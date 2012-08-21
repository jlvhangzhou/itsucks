/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  爬取指定网站若干个页面
 */

public class ProbeCrawlController {
	
	public static void main(String[] args) throws Exception {
		
		int maxPagesToFetch = 30;
		CrawlConfig config = Util.getGlobalCrawlCongig(maxPagesToFetch);
		
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		
		/*
		 *  以下两行不可遗漏
		 */
//		String site = Util.URLCrawlFormat(args[0]);
//		String seed = Util.URLCrawlFormat(args[1]);
		String site = "http://www.cppblog.com/vczh";
		String seed = "http://www.cppblog.com/vczh";
		
		
		ProbeCrawler.texts = new ArrayList<String>();
		ProbeCrawler.totalFetchPages = 0;
		ProbeCrawler.site = site;
		
		controller.addSeed(seed);
		controller.start(ProbeCrawler.class, Util.numberOfCrawlers);
		
		JedisPool pool = new JedisPool(Util.MasterHost);
		Jedis jedis = pool.getResource();
		
		if (ProbeCrawler.texts.size() < maxPagesToFetch / 2) {
			if (Util.URLDBFormat(Util.getRoot(site)) != Util.URLDBFormat(site)) {
				jedis.sadd(Util.applydb, Util.URLDBFormat(Util.getRoot(site)));
			}
		} else {
			if (Util.isQualifiedITblog(ProbeCrawler.texts))
				jedis.sadd(Util.interviewdb, Util.URLDBFormat(site));
		}
		
		pool.returnResource(jedis);
		pool.destroy();
	}
}
