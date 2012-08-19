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
 *  爬取指定blog站内的所有post页面
 */

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import org.w3c.dom.stylesheets.LinkStyle;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class BlogCrawler extends WebCrawler {
	
	public static JedisPool pool;
	public static String site;
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
			+ "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	
	public int commentHead(String org) {
		String html = org.toLowerCase();
		int ret = html.length();
		double rate = 0.15;
		int last = html.indexOf("comment", 0);
		while (last != -1) {
			double numberOfComments = html.substring(last).split("comment").length-1;
			double numberOfLines = html.substring(last).split("\n").length-1;
			if (numberOfComments/numberOfLines > rate) {
				rate = numberOfComments/numberOfLines;
				ret = last;
			}
			last = html.indexOf("comment", last+1);
		}
		return ret;
	}
	
	public int getYears(String text) {
		String[] lines = text.split("\n");
		int count = 0;
		Pattern p = Pattern.compile("\\D20\\d{2}\\D");
		for (String line: lines) {
//			if (line.split("\\D20\\d{2}\\D").length > 1)
			if (line.replaceFirst(p.pattern(), "").length() < line.length())
				count++;
		}
		return count;
	}
	
	public boolean shouldAdd(WebURL url) {
		String href = url.getURL().toLowerCase();
		if (!href.startsWith(site)) return false;
		String path = href.substring(site.length());
		return href.startsWith(site) && !path.contains("#") && !path.contains("&")// && !path.contains("?")
				&& (!path.contains(".") || path.endsWith(".html") || path.endsWith(".htm")) 
				&& !path.endsWith("/feed") && !path.endsWith("/feed/")
				&& !path.endsWith("/rss") && !path.endsWith("/rss/")
				&& path.split("/tag/").length == 1;
				
	}
	
	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(WebURL url) {
		if (shouldAdd(url)) {
//			if (jedis.sismember(site, url.getURL()))
//				jedis.sadd(site, url.getURL());
			return true;
		} else 
			return false;
//		return !FILTERS.matcher(href).matches() && href.startsWith(start);
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		String domain = page.getWebURL().getDomain();
		String path = page.getWebURL().getPath();
		String subDomain = page.getWebURL().getSubDomain();
		String parentUrl = page.getWebURL().getParentUrl();

		if (page.getParseData() instanceof HtmlParseData) {
			
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();
			Jedis jedis = pool.getResource();
			jedis.set("html", html);
			pool.returnResource(jedis);
			
//			System.out.println("Docid: " + docid);
//			System.out.println("URL: " + url);
			System.out.println(Util.isPost(html) + ", "); 
/*	
			System.out.println(htmlParseData.getHtml().replaceAll("<script[^>]*>[^<]*</script>", "").replaceAll("<[^<>]*>", ""));

			List<WebURL> links = htmlParseData.getOutgoingUrls();
			
			int index = commentHead(html);
			int linesBeforeComment = html.substring(0, index).split("\n").length;
			String[] lines = htmlParseData.getText().split("\n");
			String text = lines[0] + "\n";
			for (int i = 1; i < linesBeforeComment; i++)
				text += lines[i] + "\n";
//			String text = html.substring(0, index).replaceAll("<.*>", "").replaceAll("<.*>", "").replaceAll("<.*>", "");
			double numberOfTitles = html.split("title").length-1;
			double numberOfLines = html.split("\n").length-1;
			double numberOfYears = (text.split("\\D20\\d{2}\\D").length-1);
			double rate = numberOfTitles/numberOfLines;
			Double score = rate * rate * numberOfYears * 10;
			CRC32 crc32 = new CRC32();
			crc32.update(htmlParseData.getTitle().getBytes());
			Long crc32value = crc32.getValue(); 
			
//			System.out.println("CRC32: " + crc32value);
//			Jedis jedis = pool.getResource();
//			if (!jedis.sismember("crc32", crc32value.toString()))
//				jedis.sadd("crc32", crc32value.toString());
//			pool.returnResource(jedis);
			
			System.out.println("Year occurs " + numberOfYears);
			System.out.println("Title rate: " + rate);
			System.out.println(score);
//			System.out.println(html.substring(0, index));
			System.out.println(htmlParseData.getText());
			System.out.println(GetMainBody.getResult(htmlParseData.getText()));
			*/
//			System.out.println("=============\n");
			
//			System.out.println(score.toString() + ", ");
		} 


	}
}
