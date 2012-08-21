
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.url.WebURL;

public class Util {
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  定义文件夹位置
	 */
	
	public static final String userHome = "/home/wiza/";
	public static final String luceneIndexDir = userHome + "data/lucene/blog";
	public static final String crawler4jDataDir = userHome + "data/crawler4j";
	
	/** 
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  定义 redis 数据库名称
	 */
	
	public static final String applydb = "apply";
	public static final String interviewdb = "interview";
	public static final String acceptdb = "accept";
	public static final String rejectdb = "reject";
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  定义爬虫参数
	 */

	public static final int numberOfCrawlers = 8;
	public static final String MasterHost = "meepo-0";
	
	public static CrawlConfig getGlobalCrawlCongig(int maxPagesToFetch) {
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawler4jDataDir);
		config.setPolitenessDelay(125);
		config.setMaxDepthOfCrawling(-1);
		config.setMaxPagesToFetch(maxPagesToFetch);
		config.setResumableCrawling(false);
		
		return config;
	}
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  从parse后的网页纯文本提取正文内容
	 */
	
	public static final int minStart = 10;
	public static final int blockThickness = 3;
	public static final int minChars = 80;
	
	public static String getMainBody(String text) {
		
		String[] lines = text.split("\n");
		int totalLines = lines.length;
		int[] lineLength = new int[totalLines];
		for (int i = 0; i < totalLines; i++)
			lineLength[i] = lines[i].length();

		int[] blockLength = new int[totalLines - blockThickness + 1];
		
		for (int i = 0; i < blockLength.length; i++) {
			int sum = 0;
			for (int j = 0; j < blockThickness; j++) {
				sum += lineLength[i + j];
			}
			blockLength[i] = sum;
		}

		int start = minStart, end = totalLines;
		for (int i = start; i < blockLength.length - 1; i++) {
			if (blockLength[i] < minChars) continue;
			if (blockLength[i + 1] == 0) continue;
			start = i;
			for (int j = i; j < totalLines; j++)
				if (blockLength[j] == 0) {
					end = j;
					break;
				}
			break;
		}

		String result = lines[start] + "\n";
		for (int i = start + 1; i < end; i++) {
			result += lines[i] + "\n";
		}
		/*
		 *  转换成 UTF-8 编码
		 */
		return toUTF8String(result);
	}
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  计算页面是post页面的概率
	 */
	
	private static int numberOfCommentsWithin(int first, int last, int[] sumOfComments) {
		return sumOfComments[last] - sumOfComments[first - 1];
	}
	
	public static double isPost(String origHtml) {
			
		int bodyIndex = Math.max(origHtml.indexOf("<body>"), 0);
		String body = origHtml.substring(bodyIndex);
		String[] lines = body.split("\n");
		int totalLines = lines.length;
		int[] numberOfComments = new int[totalLines];
		int[] sumOfComments = new int[totalLines];
		int lastLine = totalLines - 1;
		int firstComment = -1, lastComment = -1;
		
		numberOfComments[0] = sumOfComments[0] = 0;
		for (int i = 1; i <= lastLine; i++) {
			/*
			 *  numberOfComment[i] 可以等于 0, 1, 2
			 */
			numberOfComments[i] = Math.min(1, lines[i].split("[Cc]omment").length - 1);
			numberOfComments[i] += Math.min(1, lines[i].split("[Rr]eply").length - 1);
			sumOfComments[i] = sumOfComments[i-1] + numberOfComments[i];
			if (numberOfComments[i] == 0) continue;
			if (firstComment == -1) {
				firstComment = i; 
			}
			lastComment = i;
		}

		int beginOfCommentBlock = lastLine + 1;
		int endOfCommentBlock = lastLine + 1;
		
		if (firstComment != -1) {
			/*
			 *  假设 body = content + LineX + comment + LineY + footer
			 *  行数分别为 Rl, Sl, Tl
			 *  带 comment 的行数为 Rc, Sc, Tc
			 *  枚举 LineX 和 LineY 使 Sl * Rc * Tc / Sc^2 / Rl / Tl 最小
			 */
			double minProduct = Double.MAX_VALUE;
			
			for (int i = firstComment; i <= lastComment; i++) {
				for (int j = i; j <= lastComment; j++) {
					double product = (double)(j - i + 1) / Math.pow(numberOfCommentsWithin(i, j, sumOfComments), 2)
							* (numberOfCommentsWithin(1, i-1, sumOfComments) + 1)
							* (numberOfCommentsWithin(j+1, lastLine, sumOfComments) + 1)
							/ i / (lastLine - j);
					
					if (product < minProduct) {
						beginOfCommentBlock = i;
						endOfCommentBlock = j + 1;
						minProduct = product;
					}
				}
			}
		}
		
//		System.out.println("begin comment blog: " + beginOfCommentBlock);				// useless code
//		System.out.println("end comment blog: " + endOfCommentBlock);					// useless code
//		System.out.println("total lines: " + totalLines);								// useless code
		
		/*
		 *  删除 Comment Block 包含的年份
		 */
		for (int i = beginOfCommentBlock; i < endOfCommentBlock; i++)
			lines[i] = removeYears(lines[i]);
		
		String main = lines[0] + "\n";
		for (int i = 1; i <= lastLine; i++) {
			/*
			 *  删除正文中的年份
			 */
			if (!lines[i].contains("<") && !lines[i].contains(">"))
				lines[i] = removeYears(lines[i]);
			
			main += lines[i] + "\n";
		}
		
		String mainText = removeEmptyLines(fliterTag(main));
		/*
		 *  lines[] 写入删除Tag和空行后的内容 
		 */
		lines = mainText.split("\n");
		int[] numberOfYears = new int[lines.length];
		for (int i = 0; i < lines.length; i++) {
			numberOfYears[i] = Math.min(1, numberOfYears(lines[i]));
		}
		
		int countYears = 0;
		/*
		 *  删除可能的年份列表
		 *  防止页脚的版权年份干扰, 只取 9/10 的内容
		 */
		for (int i = 2; i <= lines.length * 9 / 10; i++) {
			if (numberOfYears[i] == 0) continue;
			/*
			 *  不统计局部出现的大量年份
			 */
			if (numberOfYears[i-2] + numberOfYears[i-1] + numberOfYears[i] >= 3) continue;
			if (numberOfYears[i+1] + numberOfYears[i-1] + numberOfYears[i] >= 3) continue;
			if (numberOfYears[i+2] + numberOfYears[i+1] + numberOfYears[i] >= 3) continue;
			countYears++;
		}
		countYears = Math.max(2, countYears);
		
		return (double)countYears;
	}
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  计算字符串中年份个数
	 */
	
	private static final char Y = '2';
	private static final char E = '0';
	private static final char A = ' ';
	private static final char R = ' ';
	
	public static int numberOfYears(String line) {
		char[] chars = line.toCharArray();
		int result = 0;
		for (int i = 0; i < chars.length - 3; i++) {
			boolean ok = true;
			ok &= Character.isDigit(chars[i]) & chars[i] == Y;
			ok &= Character.isDigit(chars[i+1]) & chars[i+1] == E;
			ok &= Character.isDigit(chars[i+2]);
			ok &= Character.isDigit(chars[i+3]);
			if (i != 0) ok &= !Character.isDigit(chars[i-1]);
			if (i + 4 < chars.length) ok &= !Character.isDigit(chars[i+4]); 
			if (ok) result++;
		}	
		return result;
	}
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  删除字符串中的所有年份
	 */
	
	public static String removeYears(String line) {
		char[] chars = line.toCharArray();
		String result = "";
		for (int i = 0; i < chars.length - 3; i++) {
			boolean ok = true;
			ok &= Character.isDigit(chars[i]) & chars[i] == Y;
			ok &= Character.isDigit(chars[i+1]) & chars[i+1] == E;
			ok &= Character.isDigit(chars[i+2]);
			ok &= Character.isDigit(chars[i+3]);
			if (i != 0) ok &= !Character.isDigit(chars[i-1]);
			if (i + 4 < chars.length) ok &= !Character.isDigit(chars[i+4]); 
			if (ok) i += 3;
			else result += chars[i];
		}	
		return result;
	}
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  对(不)规则html除去标签
	 */
	
	public static String fliterTag(String html) {
		String result = html.replaceAll("<script[^>]*>[^<]*</script>", "");
		result = result.replaceAll("<[^>]*>", "");
		return result;
	}
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  删除空行
	 */
	
	public static String removeEmptyLines(String text) {
		String result = text.replaceAll("[ \t]+\n", "\n").replaceAll("\n+", "\n");
		return result;
	}
	
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  转换URL的格式
	 */
	
	public static String URLCrawlFormat(String url) {
		String newFormat= url;
		if (!newFormat.contains("://")) {
			newFormat = "http://" + newFormat;
		}
		return newFormat;
	}
	
	public static String URLDBFormat(String url) {
		String newFormat = url;
		while (newFormat.endsWith("/")) {
			/*
			 *  该转换会稍稍降低爬虫的效率
			 */
			newFormat = newFormat.substring(0, newFormat.length() - 1);
		}
		int index = newFormat.indexOf("://");
		if (index != -1) newFormat = newFormat.substring(index + 3);
		return newFormat;
	}
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  计算评分数据的临界值(临界值在后半区)
	 */
	
	public static double getThreshold(double[] scores) {
		Arrays.sort(scores);
		double total = 0;
		int length = scores.length;
		int fence = length - 1;
		for (int i = 0; i < length; i++) {
			total += scores[i];
			/*
			 *  前 20% 数据默认合格
			 */
			if (i < length / 5) continue;
			if (scores[i] > total/(i+1) * 5 / 3) {
				fence = i;
				break;
			}
		}
		return scores[fence];
	}
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  Crawler 用来判断URL是否访问
	 */
	
	public static boolean shouldVisit(WebURL url, String site) {
		String href = url.getURL().toLowerCase();
		if ( ! href.startsWith(site.toLowerCase()) ) return false;
		
		String path = href.substring(site.length());
		
		return !path.contains("#") && !path.contains("&") 
				&& (!path.contains(".") || path.endsWith(".html") || path.endsWith(".htm")) 
				&& !path.endsWith("/feed") && !path.endsWith("/feed/")
				&& !path.endsWith("/rss") && !path.endsWith("/rss/")
				&& !path.contains("/tag/");
	}
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  Crawler 用来判断URL是否出链
	 */
	
	public static boolean isOutLink(WebURL url, String site) {
		String href = url.getURL().toLowerCase();
		if ( href.contains(Util.URLDBFormat(site.toLowerCase())) ) return false;
		
		String root = Util.getRoot(href);
		String path = href.substring(root.length());
		
		return !path.contains("#") && !path.contains("&") 
				&& (!path.contains(".") || path.endsWith(".html") || path.endsWith(".htm")) 
				&& !path.endsWith("/feed") && !path.endsWith("/feed/")
				&& !path.endsWith("/rss") && !path.endsWith("/rss/")
				&& !path.contains("/tag/");
	}
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  返回 URL 的父路径
	 */
	
	public static String getSecondRoot(String url) {
		int index = url.indexOf("://");
		String protocol = "";
		if (index != -1) protocol = url.substring(0, index + 3);
		String ret = URLDBFormat(url);
		if (!ret.contains("/")) return null;
		while (ret.split("/").length > 2) {
			ret = ret.substring(0, ret.length() - 1);
		}
		if (ret.endsWith("/")) ret = ret.substring(0, ret.length() - 1);
		return protocol + ret;
	}
	
	public static String getRoot(String url) {
		int index = url.indexOf("://");
		String protocol = "";
		if (index != -1) protocol = url.substring(0, index + 3);
		String ret = URLDBFormat(url);
		while (ret.contains("/")) {
			ret = ret.substring(0, ret.length() - 1);
		}
		return protocol + ret;
	}
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  将任何编码的字符串转为 UTF-8
	 */
	
	public static String toUTF8String(String str) {
		try {
			return new String(str.getBytes("UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  评定 texts 是否相似 IT-blog 的内容
	 */
	
	public static boolean isQualifiedITblog(ArrayList<String> texts) {
		double maxGrade = 0;
		double threshold = 0.1;
		for (String t: texts) {
			maxGrade = Math.max(maxGrade, grade(t));
		}
		return maxGrade > threshold;
	}
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  返回 text 的评分
	 */
	
	public static double grade(String text) {
		
		return 0;
	}
}

