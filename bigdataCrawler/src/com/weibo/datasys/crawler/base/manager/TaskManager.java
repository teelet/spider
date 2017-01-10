/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.crawler.base.dao.TaskDAO;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.entity.Task.TaskStatus;
import com.weibo.datasys.crawler.base.exception.TaskException;
import com.weibo.datasys.crawler.base.factory.TaskFactory;

public class TaskManager {

	private static Logger logger = LoggerFactory.getLogger(TaskManager.class);

	private static Map<String, Task> allTaskMap = new ConcurrentSkipListMap<String, Task>();

	private static Map<String, Task> runningTaskMap = new ConcurrentSkipListMap<String, Task>();

	private static String taskDS = ConfigFactory.getString(
			"taskManager.taskDB.dsname", "crawlDS");

	private static String taskDB = ConfigFactory.getString(
			"taskManager.taskDB.db", "govcn");

	private static String taskTable = ConfigFactory.getString(
			"taskManager.taskDB.table", "taskdb");

	private static boolean taskDBEnable = ConfigFactory.getBoolean(
			"taskManager.taskDB.enable", true);

	/**
	 * 初始化任务管理器
	 * 
	 * @throws TaskException
	 */
	public static void init() throws TaskException {
		try {
			loadLocalTasks();
			refresh();
		} catch (Exception e) {
			throw new TaskException("init TaskManager error.", e);
		}

	}

	private static void loadLocalTasks() throws Exception {
		String home = System.getProperty("home.dir");
		File taskDir = new File(home + "/conf/tasks");
		logger.info("[loadLocalTasks] - {}", taskDir.getPath());
		if (taskDir.exists() && taskDir.isDirectory()) {
			for (File taskFile : taskDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith(".xml")) {
						return true;
					} else {
						return false;
					}
				}
			})) {
				logger.info("[loadLocalTasks] - {}", taskFile.getPath());
				BufferedReader reader = new BufferedReader(new FileReader(
						taskFile));
				String tmp = "";
				String xml = "";
				while (null != (tmp = reader.readLine())) {
					xml += tmp + "\n";
				}
				reader.close();
				logger.info("[loadLocalTasks] - {}", xml);
				Task task = TaskFactory.buildTask(xml);
				addTask(task);
			}
		}
	}

	/**
	 * 
	 * 添加任务，本地缓存+DB，任务添加后处于STOP状态
	 * 
	 * @param task
	 */
	public static void addTask(Task task) {
		synchronized (TaskManager.class) {
			String taskId = task.getTaskId();
			if (!allTaskMap.containsKey(taskId)) {
				if (taskDBEnable) {
					TaskDAO.getInstance().save(task, taskDS, taskDB, taskTable);
				}
				allTaskMap.put(taskId, task);
				logger.info("[AddTask] - {}", task);
			} else {
				logger.error("[addTaskError] - task already add. id={}", taskId);
			}
		}
	}

	/**
	 * 
	 * 移除任务，本地缓存+DB，若任务正在运行则报警
	 * 
	 * @param taskId
	 */
	public static void removeTask(String taskId) {
		synchronized (TaskManager.class) {
			Task task = getTask(taskId);
			if (task.isRunning()) {
				logger.warn(
						"[removeTaskWarn] - task is running, stop it first. id={}",
						taskId);
			} else {
				allTaskMap.remove(taskId);
				if (taskDBEnable) {
					TaskDAO.getInstance().remove(taskId, taskDS, taskDB,
							taskTable);
				}
				logger.info("[RemoveTask] - {}", task);
			}
		}
	}

	/**
	 * 
	 * 更新任务，本地缓存+DB，若任务正在运行则报警
	 * 
	 * @param task
	 */
	public static void updateTask(Task task) {
		synchronized (TaskManager.class) {
			String taskId = task.getTaskId();
			Task origTask = getTask(taskId);
			if (origTask.isRunning()) {
				logger.warn(
						"[updateTaskWarn] - task is running, stop it first. id={}",
						taskId);
			} else {
				allTaskMap.put(taskId, task);
				if (taskDBEnable) {
					TaskDAO.getInstance().save(task, taskDS, taskDB, taskTable);
				}
				logger.info("[UpdateTask] - {}", task);
			}
		}
	}

	/**
	 * 
	 * 启动任务
	 * 
	 * @param taskId
	 */
	public static boolean runTask(String taskId) {
		boolean result = false;
		synchronized (TaskManager.class) {
			Task task = getTask(taskId);
			if (task != null) {
				if (!task.isRunning()) {
					task.setStatus(TaskStatus.RUNNING);
					task.setStartTime(System.currentTimeMillis());
					runningTaskMap.put(taskId, task);
					result = true;
					logger.info("[RunTask] - {}", task);
				} else {
					logger.warn("[runTaskWarn] - task already running. {}",
							task);
				}
			} else {
				logger.error("[runTaskError] - task not found. id={}", taskId);
			}
		}
		return result;
	}

	/**
	 * 
	 * 停止任务
	 * 
	 * @param taskId
	 */
	public static boolean stopTask(String taskId) {
		boolean result = false;
		synchronized (TaskManager.class) {
			Task task = runningTaskMap.remove(taskId);
			if (task != null) {
				task.setStatus(TaskStatus.STOP);
				CrawlingSetManager.removeCrawlingSeedsOfTask(taskId);
				QueueManager.removeTaskQueue(taskId);
				ConcurrentManager.clearTaskConcurrent(taskId);
				result = true;
				logger.info("[StopTask] - {}", task);
			} else {
				logger.warn("[stopTaskWarn] - task not running. id={}", taskId);
			}
		}
		return result;
	}

	/**
	 * 停止所有任务
	 */
	public static void stopAllTask() {
		for (String taskId : runningTaskMap.keySet()) {
			stopTask(taskId);
		}

	}

	/**
	 * 从DB刷新所有task定义
	 */
	public static void refresh() {
		if (taskDBEnable) {
			// 从数据库读取所有Task定义
			List<Task> tasks = TaskDAO.getInstance().getAllTasks(taskDS,
					taskDB, taskTable);
			synchronized (TaskManager.class) {
				for (Task newTask : tasks) {
					String taskId = newTask.getTaskId();
					Task origTask = allTaskMap.get(taskId);
					if (origTask != null) {
						// 原任务正在运行，则设置新任务为运行，同时新任务放进runningTaskMap
						if (origTask.isRunning()) {
							newTask.setStatus(TaskStatus.RUNNING);
							newTask.setStartTime(origTask.getStartTime());
							runningTaskMap.put(taskId, newTask);
						}
					}
					// 将原任务替换为新任务
					allTaskMap.put(taskId, newTask);
					logger.info("[RefreshingTask] - load - {}", newTask);
				}
			}
		}
	}

	/**
	 * @param taskId
	 * @return
	 */
	public static Task getTask(String taskId) {
		Task task = allTaskMap.get(taskId);
		if (task == null) {
			refresh();
			task = allTaskMap.get(taskId);
		}
		return task;
	}

	/**
	 * @return
	 */
	public static List<Task> getExistTasks() {
		List<Task> tasks = new ArrayList<Task>();
		tasks.addAll(allTaskMap.values());
		return tasks;
	}

	/**
	 * @return
	 */
	public static List<Task> getRunningTasks() {
		List<Task> tasks = new ArrayList<Task>();
		tasks.addAll(runningTaskMap.values());
		return tasks;
	}

}
