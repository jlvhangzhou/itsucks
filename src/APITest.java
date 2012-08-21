import java.io.*;
import java.nio.Buffer;
import java.util.*;
import java.util.regex.*;

import javax.swing.text.html.parser.Parser;

import redis.clients.jedis.*;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.apache.http.HttpEntity;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.apache.tika.parser.html.HtmlParser;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;


public class APITest {

	public static void main(String[] args) throws ParseException, UnsupportedEncodingException {
//		String str = new String("刘翔加油");
//		str = new String(str.getBytes("UTF-8"), "UTF-8");
//		System.out.println(str);
//		FileWriter file = new FileWriter("/home/wiza/workspace/itsucks/src/log4j.propertie");
//		BufferedWriter w = new BufferedWriter(file);
//		w.write(str);
//		w.close();
//		System.out.println();
		
//		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
//		Jedis jedis = pool.getResource();
//		System.out.println(jedis.sismember("itbooks", "www.china-pub.com"));
//		jedis.disconnect();
//		pool.destroy();
		
//		Pattern p = Pattern.compile("http://product.china-pub.com/\\d{1,}(&\\?ref=\\w{1,})?");
//		Pattern tail = Pattern.compile("&\\?ref=\\w{1,}");
//		 Matcher m = p.matcher("http://product.china-pub.com/3682539#");
//		 System.out.println(m.matches());
//		String str =  "http://product.china-pub.com/8182&?ref=buyand";
//		Matcher m = p.matcher(str);
//		str = str.replaceFirst(tail.pattern(), "");
//		System.out.println(m.matches() + " " + str);
		
//		File file = new File("/home/wiza/some.html");
//		FileInputStream in = new FileInputStream(file);
//		ArrayList<Byte> bylist = new ArrayList<Byte>();
//		int b = 0;
//		while (true) {
//			b = in.read();
//			if (b == -1) break;
//			else bylist.add((byte)b);
//		}
//		int size = bylist.size();
//		byte[] bytes = new byte[size];
//		int i = 0;
//		for (Byte x: bylist) {
//			bytes[i] = x;
//			i++;
//		}
//		String html = new String(bytes, "GB2312");
//		System.out.println(html);
//		String pat = "<a class='blue13' href='http://list.china-pub.com/cache/browse2/59/1_1_59_0.html'>计算机</a>";
//		System.out.println(html.contains(pat));
		
//		String str = "<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"\" />";
//		System.out.println(str.split("name").length);
//		System.out.println(str.replaceAll("<.*>", ""));
//		String f = "hello我们";
//		System.out.println(str.split("wiza").length-1);
//		System.out.println(f.toLowerCase().length());
//		System.out.println(str.indexOf("2000", 21));
		
//		WebURL url = new WebURL();
//		String site = "http://blog.coly.li";
//		url.setURL("http://blog.coly.li/p/81#3");
//		System.out.println(url.getURL());
//		System.out.println(url.getDomain());
//		System.out.println(url.getSubDomain());
//		System.out.println(url.getURL().substring(site.length()));
//		ArrayList<Integer> a = new ArrayList<Integer>();
//		a.add(3);
//		a.add(1);
//		a.add(2);
//		Integer[] b = new Integer[a.size()];
//		a.toArray(b);
//		Arrays.sort(b);
//		for (Integer i: b)
//			System.out.println(i);
//		JedisPool pool = new JedisPool("localhost");
//		Jedis jedis = pool.getResource();
//		System.out.println(jedis.srandmember("itbooks"));
		
//		Arrays.sort(tt);
//		double[] avg = new double[tt.length]; 
//		avg[0] = tt[0];
//		double temp = tt[0];
//		double fence = 0;
//		for (int i = 1; i < avg.length; i++) {
//			temp += tt[i];
//			avg[i] = temp / (i+1);
//			if (i * 5 > avg.length && tt[i] > avg[i] * 2) {
//				fence = i;
//				break;
//			}
//		}
//		System.out.println(fence + "/" + tt.length);
		
		/*
		int beg = tt.length * 0 / 100;
		int end = tt.length * 100 / 100;
		double[] scores = Arrays.copyOfRange(tt, beg, end);
		int len = scores.length;
		double[] sum = new double[scores.length];
		double[] ssum = new double[scores.length];
		sum[0] = scores[0];
		ssum[0] = scores[0] * scores[0];
		for (int i = 1; i < len; i++) {
			sum[i] = sum[i-1] + scores[i];
			ssum[i] = ssum[i-1] + scores[i] * scores[i];
		}
		int fence = len * 20 / 100;
		end = len - len * 10 / 100;
		double minProduct = Double.MAX_VALUE;
		double tsum = sum[len-1];
		double tssum = ssum[len-1];
		double val = scores[fence];
		double var1 = 0, var2 = 0;
		for (int i = fence; i < end; i++) {
			double v1 = ssum[i]/(i+1) - sum[i]/(i+1) * sum[i]/(i+1);
			double v2 = (tssum - ssum[i])/(len-i-1) - (tsum - sum[i]) * (tsum - sum[i]) /(len-i-1) /(len-i-1);
//			double v1 = sum[i]/(i+1);
//			double v2 = (tsum - sum[i]) /(len-i-1);
			System.out.println("&&&" + v1 * v2);
			if (v1 * v2 != 0 && v1 * v2 < minProduct * 1.05) {
				minProduct = v1 * v2;
				var1 = v1;
				var2 = v2;
				fence = i;
			}
		}
		System.out.println(fence + " / " + len);
		System.out.println(scores[fence]);
		System.out.println(minProduct);
		System.out.println(var1);
		System.out.println(var2);
		*/
		
//		String site = "blog.linezing.com";
//		String seed = "blog.linezing.com";
////		String site = "www.cppblog.com/vczh";
////		String seed = "http://www.cppblog.com/vczh/archive/2010/07/07/119562.html";
//		String[] arg = { site, seed };
//		BlogCrawlController.main(arg);
		
		String str = "我们都喜欢Java，但是不喜欢C++，因为后者很恶心XML的���则表";
		str = new String(str.getBytes("UTF-16"), "UTF-16");
		System.out.println(str);
		
		QueryParser parser = 
				new QueryParser(Version.LUCENE_40, "_@_#_", new SmartChineseAnalyzer(Version.LUCENE_40));
		
		parser.setDefaultOperator(QueryParser.AND_OPERATOR);
		
		Query query = parser.parse(str);
//		System.out.println(query.toString());
		for (String i: query.toString().split("\\+_@_#_:")) {
			System.out.println(i);
		}
		
	}
}

/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  
 */

/*
 * double titleRate = 1.0;
		double partRate = 1;
		int temp = 0, totalTitles = 0;
		for (int i = 0; i < totalLines; i++) {
			totalTitles += Math.min(1, lines[i].split("[Tt]itle").length - 1);
		}
		for (int i = 1; i < totalLines; i++) {
			temp += Math.min(1, lines[i].split("[Tt]itle").length - 1);
			if (i % blockHeight == 0) {
				titleRate /= totalTitles;
				if (temp != 0) titleRate *= 10.0 * temp;
				temp = 0;
			}
		}
 */

/*

switch (1) {
			case 1: {
				site = new String("http://blog.yufeng.info").toLowerCase();
//				seed = new String("http://blog.yufeng.info/archives/2234");
				seed = new String("http://blog.yufeng.info/archives/category/erlang");
				break;
			}
			case 2: {
				site = new String("http://www.cppblog.com/vczh/").toLowerCase();
//				seed = new String("http://www.cppblog.com/vczh/archive/2010/07/07/119562.html");
				seed = new String("http://www.cppblog.com/vczh/category/6885.html");
				break;
			}
			case 3: {
				site = new String("http://www.guwendong.com/").toLowerCase();
				seed = new String("http://www.guwendong.com/post/2012/xlvector_recsys_book.html");
				break;
			}
			case 4: {
				site = new String("http://dinglin.iteye.com/").toLowerCase();
				seed = new String("http://dinglin.iteye.com/?from=yufeng");
				break;
			}
			case 5: {
				site = new String("http://www.searchtb.com").toLowerCase();
//				seed = new String("http://www.searchtb.com/2012/08/");
				seed = new String("http://www.searchtb.com/2012/08/zeromq-primer.html");
				break;
			}
			case 6: {
				site = new String("http://www.yankay.com").toLowerCase();
				seed = new String("http://www.yankay.com/java-fast-byte-comparison/");
				break;
			}
			case 7: {
				site = new String("http://verypig.com/").toLowerCase();
				seed = new String("http://verypig.com/?p=393");
				break;
			}
		}


 * 
 */

