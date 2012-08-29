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
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  爬取指定blog站内的所有post页面
 */

public class BlogCrawlController {
	
	public static void main(String[] args) throws Exception {
		
//		args = new String[]{ "blog.yufeng.info", "blog.yufeng.info" };
		String site = Util.URLCrawlFormat(args[0]);
		String seed = Util.URLCrawlFormat(args[1]);
		
		JedisPool pool = new JedisPool("localhost");
		Jedis jedis = pool.getResource();
		if (jedis.sismember(Util.acceptdb, site) || jedis.sismember(Util.rejectdb, site)) return;
		pool.returnResource(jedis);
		pool.destroy();
		
		int maxPagesToFetch = 1600;
		CrawlConfig config = Util.getGlobalCrawlCongig(maxPagesToFetch);
		
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		
		BlogCrawler.scores = new ArrayList<Double>();
		BlogCrawler.CRC32_html = new HashMap<>();
		BlogCrawler.site = site;
		
		controller.addSeed(seed);
		controller.start(BlogCrawler.class, Util.numberOfCrawlers);
		
		/*
		 *  连接数据库
		 */
		site = Util.URLDBFormat(site);
		
		pool = new JedisPool(Util.MasterHost);
		jedis = pool.getResource();
		
		/*
		 *  网站异常
		 */
		if (BlogCrawler.scores.size() < 10) {
			jedis.srem(Util.interviewdb, site);
			jedis.sadd(Util.applydb, site);
			pool.returnResource(jedis);
			pool.destroy();
			return;
		}
		
		double[] scores = new double[BlogCrawler.scores.size()];
		int i = 0;
		for (double s: BlogCrawler.scores) {
			scores[i] = s;
			i++;
		}
		double threshold = Util.getThreshold(scores);
		
		Configuration conf = HBaseConfiguration.create();
		HTable table = new HTable(conf, Util.blogHT);
		table.setAutoFlush(false);
		Put put = new Put(site.getBytes());
		Get get = new Get(site.getBytes());
		get.addFamily("cf".getBytes());
		Result result = table.get(get);
		Long ts = new Date().getTime() / 1000;
		jedis.sadd(Util.tsdb, ts.toString());
		
		Set<Long> keys = BlogCrawler.CRC32_html.keySet();
		for (Long key: keys) {
			MyHtmlClass x = BlogCrawler.CRC32_html.get(key); 
			if (x.score + 1e-6 < threshold) {
				/*
				 *  爬虫数据持久化
				 */
				for (WebURL u: x.parser.getOutgoingUrls()) {
					if (Util.isOutLink(u, site)) {
						String link = u.getURL().toString();
						if (Util.getSecondRoot(link) == null) 
							link = Util.getRoot(link);
						else link = Util.getSecondRoot(link);
						jedis.sadd(Util.applydb, Util.URLDBFormat(link));
					}
				}
				
				String path = Util.getPath(x.url, site);
				if (path == null) continue;
				if (!result.containsColumn("cf".getBytes(), path.getBytes())) {
					String value = x.parser.getHtml(); 
					put.add("cf".getBytes(), path.getBytes(), ts, value.getBytes());
				}
			}
		}
		
		jedis.srem(Util.interviewdb, site);
		jedis.sadd(Util.acceptdb, site);
		
		/*
		 *  断开数据库连接
		 */
		table.put(put);
		table.flushCommits();
		pool.returnBrokenResource(jedis);
		pool.destroy();
	}
}
