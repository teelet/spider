package com.weibo.datasys.work;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.model.SeedData;
import com.weibo.datasys.queue.QueueManager;
import com.weibo.datasys.queue.QueueManager.QueueType;
import com.weibo.datasys.seed.ImageSeedProvider;
import com.weibo.datasys.service.ConfigService;

/**
 * 
 * 获取种子的工作线程，调用AbstractSeedProvider实现
 * 
 * 
 */
public class SeedProvideWork extends BaseWork {
	
	private static Logger logger = LoggerFactory.getLogger(SeedProvideWork.class);

	private int getSeedBatchCount = 1000;
	private final int maxWaitUnit = 1000;
	private int getNoSeedTimes = 0;

	public SeedProvideWork() {
		getSeedBatchCount = ConfigService.getInt(
				"seedProvider.getSeedBatchCount", 1000);
	}

	@Override
	protected void doWork() {
		boolean isNeedGotSeeds = isNeedToGetSeeds(getSeedBatchCount);
		if(!isNeedGotSeeds){
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
			}
		}
		else
		{
			boolean isGotSeeds = false;
			ImageSeedProvider seedProvider = new ImageSeedProvider();
			Collection<SeedData> seedDatas = seedProvider.getSeeds(getSeedBatchCount);
			isGotSeeds = seedDatas.size()>0;
			for (SeedData seedData : seedDatas) {
				QueueManager.put(QueueType.SEED.name(),seedData);
				SeedData newSeed = new SeedData();
				newSeed.setId(seedData.getId());
				newSeed.setUrl(seedData.getUrl());
				if(seedData.getState() == 0){
					newSeed.setState(1);
				}else{
					newSeed.setState(seedData.getState());
				}
				QueueManager.put(QueueType.SAVE.name(),newSeed);
			}
			if (isGotSeeds) {
				getNoSeedTimes = 0;
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
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
	
	/**
	 * 
	 * 判断是否需要获取种子，通过检查各运行中任务队列状态判断
	 * 
	 * @param count
	 * @return
	 */
	private boolean isNeedToGetSeeds(int count) {
		int size = QueueManager.getQueueSize(QueueType.SEED.name());
		if (size < count / 10 && size < 10) {
				return true;
		} 
		return false;
	}
}

