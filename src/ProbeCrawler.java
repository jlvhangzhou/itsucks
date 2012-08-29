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

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  爬取指定网站若干个页面
 */

public class ProbeCrawler extends WebCrawler {
	
	public boolean shouldVisit(WebURL url) {
		return Util.shouldVisit(url, ProbeCrawlController.site);
	}

	@Override
	public void visit(Page page) {
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = Util.getMainBody(htmlParseData.getHtml());
			if (text == null) return;
			if (ProbeCrawlController.totalFetchPages < ProbeCrawlController.maxPagesToFetch / 2) {
				ProbeCrawlController.texts.add(text);
			}
			ProbeCrawlController.totalFetchPages++;
		} 
	}
}
