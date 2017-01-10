package com.weibo.datasys.work;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.fetcher.ImageFetcher;
import com.weibo.datasys.model.SeedData;
import com.weibo.datasys.queue.QueueManager;
import com.weibo.datasys.queue.QueueManager.QueueType;


public class FetchWork extends BaseWork {
	
	private static Logger logger = LoggerFactory.getLogger(BaseWork.class);

	@Override
	protected void doWork() {
		SeedData seedData = (SeedData) QueueManager.poll(
						QueueType.SEED.name());
		if(seedData != null){
			SeedData newData = new SeedData();
			newData.setId(seedData.getId());
			newData.setUrl(seedData.getUrl());
			if(seedData.getUrl().startsWith("http")){
				ImageFetcher.downloadFile(seedData);
				newData.setState(seedData.getState());
				newData.setLocalPath(seedData.getLocalPath());
				newData.setSize(seedData.getSize());
			}else{
				newData.setState(-5);
			}
			QueueManager.put(QueueType.SAVE.name(), newData);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				logger.error(e.toString());
			}
		}
	}
	
}

