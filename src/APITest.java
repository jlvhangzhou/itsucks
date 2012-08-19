import java.io.*;
import java.nio.Buffer;
import java.util.*;
import java.util.regex.*;

import javax.swing.text.html.parser.Parser;

import redis.clients.jedis.*;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.apache.http.HttpEntity;
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

import edu.uci.ics.crawler4j.url.WebURL;

public class APITest {

	public static void test(int[] a) {
		a[0] = 99;
	}
	
	public static void main(String[] args) throws IOException {
		
		
		
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
		
//		Vector<HashSet<Double>> all = new Vector<HashSet<Double>>();
		
		double[] scores = {
				1.2156721350880357, 
				0.897253269048141, 
				0.2777864766648195, 
				0.6910936337549155, 
				0.2953787596981613, 
				0.20000000000000004, 
				0.29252794292508916, 
				1.969778030521271, 
				0.387396694214876, 
				0.24928204228128678, 
				0.10973936899862825, 
				0.09279563116992227, 
				0.5728099954175201, 
				0.12512368731845896, 
				0.1489345254966631, 
				0.2558106575963719, 
				0.6873719945010239, 
				0.5748838877125881, 
				0.10687049006531923, 
				0.10501995379122034, 
				0.14289688007400358, 
				0.078125, 
				0.09930566950407853, 
				0.10563142239048134, 
				0.6397403307300297, 
				0.2773668639053255, 
				0.12190388997800754, 
				0.08473092678827772, 
				0.46242273845981374, 
				0.10624824690392609, 
				0.06650072188283622, 
				0.7827781594716033, 
				0.238503519138846, 
				0.5728099954175201, 
				0.08915886257975826, 
				0.2683746066875688, 
				0.19691917313210752, 
				1.036750637755102, 
				0.30945523061739066, 
				0.09114018228036455, 
				0.10471620860306483, 
				0.09438178007889547, 
				0.09377966499607017, 
				0.07896279561897682, 
				0.19793815809680243, 
				0.20718813329966523, 
				0.1478472047255116, 
				0.238503519138846, 
				0.10624824690392609, 
				0.08888641646248994, 
				0.09930566950407853, 
				0.08729450170891612, 
				0.06414342540994712, 
				0.2174503169111891, 
				1.5363511659807956, 
				0.1190204081632653, 
				0.06793631676013398, 
				0.14546032478823298, 
				0.1580246913580247, 
				0.19691917313210752, 
				0.07694754887039076, 
				0.5008317136075006, 
				0.08473092678827772, 
				0.1953125, 
				0.7827781594716033, 
				0.2871158869375013, 
				0.18714805181687333, 
				0.25826446280991733, 
				0.10624824690392609, 
				0.06814519078529782, 
				0.101749894688859, 
				1.2767244127962587, 
				0.34128723317912507, 
				0.20718813329966523, 
				0.09259259259259259, 
				0.21799770578695465, 
				0.09582661845692683, 
				1.6645056671415994, 
				0.2037038481945008, 
				0.09667903784881479, 
				0.8639588647959183, 
				0.12302960399846213, 
				0.14506098297307088, 
				0.18281486823040816, 
				0.0, 
				0.47583861976163017, 
				0.24926507054604002, 
				0.8436020933675277, 
				0.34128723317912507, 
				0.22769674094415948, 
				1.0243739705177122, 
				0.14626397146254458, 
				0.07718089990817263, 
				0.5975307183364837, 
				0.09377966499607017, 
				0.897253269048141, 
				0.15556852471330287, 
				0.09318329343989615, 
				0.7204937043070725, 
				0.10380622837370242, 
				0.6147233789411065, 
				0.22769674094415948, 
				0.07765079775685739, 
				0.14127320395126663, 
				0.09288723868355403, 
				0.5411816110608283, 
				0.30302265094315806, 
				0.387396694214876, 
				0.09249681683862507, 
				0.17547218412570229, 
				0.10441377927762371, 
				0.2892164717192758, 
				0.2271327863843169, 
				0.9500328731097963, 
				0.10266396490184412, 
				0.12257570435211385, 
				0.1648481910014421, 
				0.1244197579845269, 
				0.06691073942149471, 
				0.29252794292508916, 
				0.30686563919952814, 
				0.09639363863076524, 
				0.29252794292508916, 
				0.21640082567085756, 
				0.10073571334590588, 
				0.048811923620259996, 
				0.16617671369736003, 
				0.9500328731097963, 
				0.06731542450906124, 
				0.09318329343989615, 
				0.07224573360980108, 
				0.06835502958579881, 
				0.08473092678827772, 
				0.06899039437756951, 
				0.09377966499607017, 
				0.29252794292508916, 
				0.12503955332809358, 
				0.10501995379122034, 
				0.1313624562250936, 
				0.09318329343989615, 
				0.20896527457594213, 
				0.12938293606593895, 
				0.143088728784695, 
				0.21799770578695465, 
				0.09554498269896196, 
				0.11630867967496504, 
				0.13429783950617283, 
				0.07765079775685739, 
				0.11647619663528608, 
				0.06629935720844812, 
				0.07185971751992065, 
				0.06793631676013398, 
				0.4772757299511165, 
				0.28539821366258566, 
				0.9500328731097963, 
				0.06030939891075546, 
				0.15512009297520662, 
				0.1866533959601606, 
				0.8436020933675277, 
				0.18519990327397312, 
				0.24811073524497418, 
				0.897253269048141, 
				0.1589087418877087, 
				0.5728099954175201, 
				0.14466328341245782, 
				0.17247201704458237, 
				0.07579651723795867, 
				0.14586131791240733, 
				0.0767152545157492, 
				0.1053250224868923, 
				0.14546032478823298, 
				0.09171701743746397, 
				0.28844358869053655, 
				0.08703294529646723, 
				0.09667903784881479, 
				0.08677256265911293, 
				0.5728099954175201, 
				0.09114018228036455, 
				0.12583360752433612, 
				0.15734720149979228, 
				0.06856583911244275, 
				0.09318329343989615, 
				0.08548801246994564, 
				0.16905105341813226, 
				0.3278608638022698, 
				0.16984079190033496, 
				0.10128215918974273, 
				0.12547789432408588, 
				0.16573209392680044, 
				0.2230186016095928, 
				0.078125, 
				0.12512368731845896, 
				0.1941840528651545, 
				0.12372175230400201, 
				0.06835502958579881, 
				0.10381283791078741, 
				0.09498542324197418, 
				0.07788735755208513, 
				0.08625529071062596, 
				0.10441377927762371, 
				0.06856583911244275, 
				0.12739976636029007, 
				0.12372175230400201, 
				0.07602464808638136, 
				0.16573209392680044, 
				0.1866533959601606, 
				0.06920415224913495, 
				0.06877762536198749, 
				0.5728099954175201, 
				0.5728099954175201, 
				0.08548801246994564, 
				0.5728099954175201, 
				0.5728099954175201, 
				0.07444905045351473, 
				0.11531104013924351, 
				0.10643912626703499, 
				0.1498224293928557, 
				0.13504705891481286, 
				0.35094436825140457, 
				0.08189778892438866, 
				0.5728099954175201, 
				0.2683746066875688, 
				0.09554498269896196, 
				0.09068070048685972, 
				0.36615727062651693, 
				0.06899039437756951, 
				0.09639363863076524, 
				0.11113287084018271
		};
		Arrays.sort(scores);
		int len = scores.length;
		double[] sum = new double[scores.length];
		double[] ssum = new double[scores.length];
		sum[0] = scores[0];
		ssum[0] = scores[0] * scores[0];
		for (int i = 1; i < len; i++) {
			sum[i] = sum[i-1] + scores[i];
			ssum[i] = ssum[i-1] + scores[i] * scores[i];
		}
		int fence = len / 5;
		int end = len - len/10;
		double minProduct = Double.MAX_VALUE;
		double tsum = sum[len-1];
		double tssum = ssum[len-1];
		double val = scores[fence];
		double var1 = 0, var2 = 0;
		for (int i = fence; i < end; i++) {
			double v1 = ssum[i]/(i+1) - sum[i]/(i+1) * sum[i]/(i+1);
			double v2 = (tssum - ssum[i])/(len-i-1) - (tsum - sum[i]) * (tsum - sum[i]) /(len-i-1) /(len-i-1);
			if (v1 * v2 != 0 && v1 * v2 < minProduct * 1.08) {
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

		System.out.println("==x==x=");
		String zxc = "2010J我的anuary 2010 2110 2010";
		System.out.println(zxc.replaceAll("\\D20\\d{2}\\D", ""));
		System.out.println("=====");
		
		
	}
}

/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  
 */
