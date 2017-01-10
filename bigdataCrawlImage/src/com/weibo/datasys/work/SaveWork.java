package com.weibo.datasys.work;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.dao.SeedDataDAO;
import com.weibo.datasys.model.SeedData;
import com.weibo.datasys.queue.QueueManager;
import com.weibo.datasys.queue.QueueManager.QueueType;
import com.weibo.datasys.service.ConfigService;

public class SaveWork extends BaseWork {

	private static Logger logger = LoggerFactory
			.getLogger(SaveWork.class);
	
	private int saveBatch = 1000;
	private final int maxWaitUnit = 1000;
	private int getNoSeedTimes = 0;


	public SaveWork() {
	}

	@Override
	protected void doWork() {
		// 执行具体save操作
		doSaveWork();
	}

	/**
	 * 执行具体save操作，子类需重写
	 * 
	 */
	protected void doSaveWork() {
		boolean isGotSeeds = false;
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		for (int i = 0; i < saveBatch; i++) {
			SeedData seedData = (SeedData) QueueManager.poll(QueueType.SAVE.name());
			if (seedData == null) {
				break;
			}
			seedDatas.add(seedData);
		}
		isGotSeeds = seedDatas.size()>0;
		if (isGotSeeds) {
			getNoSeedTimes = 0;
			// 批量保存seed
			SeedDataDAO.getInstance().saveBatch(seedDatas,
								ConfigService.getSeedDS(), ConfigService.getSeedDB(),
								ConfigService.getSeedTable(), true, true);
			
		} else {
			getNoSeedTimes++;
			int wait = (1 + Math.min(getNoSeedTimes, 10)) * maxWaitUnit;
			try {
				Thread.sleep(wait);
			} catch (Exception e) {
			}
		}
		
	}
}

