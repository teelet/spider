package com.weibo.datasys.seed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.dao.SeedDataDAO;
import com.weibo.datasys.model.SeedData;
import com.weibo.datasys.service.ConfigService;

public class ImageSeedProvider {

	private static Logger logger = LoggerFactory
			.getLogger(ImageSeedProvider.class);
	
	private static final String SELECT_NORMAL_SQL = "select * from db.table where "
			+ " fetchstate=0 or (fetchstate < 0 and fetchstate > -3) limit {limit}";

	public List<SeedData> getSeeds(int count) {
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		seedDatas.addAll(getBaseSeeds(count));
		// 记录本次获取到的种子数
		int seedCount = seedDatas.size();
		logger.info("[getSeeds] - count={}",
				new Object[] { seedCount});
		return seedDatas;
	}

	private List<SeedData> getBaseSeeds(int count) {
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		// build sql
		String sql = SELECT_NORMAL_SQL
						.replace("db", ConfigService.getSeedDB())
						.replace("table",
									ConfigService.getSeedTable() )
						.replace("{limit}", "" + count);
		// select 种子
		List<SeedData> tmpDatas = SeedDataDAO.getInstance()
							.getBySQL(sql, ConfigService.getSeedDS());
		seedDatas.addAll(tmpDatas);
		// 对种随机排序
		Collections.sort(seedDatas, new Comparator<SeedData>() {
			private Random random = new Random(System.currentTimeMillis());

			@Override
			public int compare(SeedData o1, SeedData o2) {
				return random.nextInt() % 2;
			}
		});
		return seedDatas;
	}

}

