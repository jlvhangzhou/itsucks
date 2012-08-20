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

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  爬取指定blog站内的所有post页面
 */

public class BlogCrawlController {

	public static void main(String[] args) throws Exception {
		
		int maxPagesToFetch = 2000;
		CrawlConfig config = Util.getGlobalCrawlCongig(maxPagesToFetch);
		
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		
		JedisPool pool = new JedisPool("localhost");
		BlogCrawler.pool = pool;
		
		String site = Util.URLCrawlFormat(args[0]);
		String seed = Util.URLCrawlFormat(args[1]);
		BlogCrawler.site = site;
		controller.addSeed(seed);
		
		controller.start(BlogCrawler.class, Util.numberOfCrawlers);
		
		pool.destroy();
	}
}
