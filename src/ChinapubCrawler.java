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

/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  爬取购书网 www.china-pub.com 计算机分类的网页
 */

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.*;
import java.util.List;
import java.util.regex.Pattern;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class ChinapubCrawler extends WebCrawler {
	
	private final static Pattern pat = Pattern.compile("http://product.china-pub.com/\\d{1,}(&\\?ref=\\w{1,})?");
	private final static Pattern tail = Pattern.compile("&\\?ref=\\w{1,}");

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		if (pat.matcher(href).matches() == false) return false;
		
		href = href.replaceFirst(tail.pattern(), "");
		Integer i = new Integer(href.charAt(30)) % 4; 
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "meepo-"+i.toString());
		Jedis jedis = pool.getResource();
		boolean exist = jedis.sismember("itbooks", href); 
		jedis.disconnect();
		pool.destroy();
		
		if (exist) return false;
		return true;
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL().toLowerCase();
		System.out.println("Docid: " + docid);
		System.out.println("URL: " + url);
		
		url = url.replaceFirst(tail.pattern(), "");
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
		Jedis jedis = pool.getResource();
		boolean exist = jedis.sismember("itbooks", url); 
		if (exist) {
			System.out.println(url + " exist.");
			jedis.disconnect();
			pool.destroy();
			System.out.println("=============");
			return;
		}

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();

			String keyword = "<a class='blue13' href='http://list.china-pub.com/cache/browse2/59/1_1_59_0.html'>计算机</a>";
			if (html.contains(keyword)) {
				String number = url.substring(29);
				try {
					FileOutputStream out = new FileOutputStream("/home/wiza/data/itbooks/"+number+".html");
					out.write(html.getBytes("GB2312"));
					out.close();
					jedis.sadd("itbooks", url);
					System.out.println("Downloaded");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Not about IT.");
			}
		}

		System.out.println("=============");
		jedis.disconnect();
		pool.destroy();
	}
}
