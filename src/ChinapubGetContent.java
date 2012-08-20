
import java.io.*;
import java.nio.file.Files;

/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  将从 www.china-pub.com 爬取到的网页中的图书目录提取出来保存到文本文件中
 */

public class ChinapubGetContent {
	
	public static void toText(File file) throws IOException {
		String html = new String(Files.readAllBytes(file.toPath()), "GB2312");
		int begin = html.indexOf("<h3 id=\"ml\" class=\"brown16b\"> <b>目录</b>");
		if (begin == -1) return;
		html = html.substring(begin);
		int end = html.indexOf("</div>");
		if (end == -1) return;
		html = html.substring(0, end);
		html = html.replaceAll("<.*>", "");
		String name = file.getName().replace("html", "txt");
		File text = new File("/home/wiza/data/content/"+name);
		Files.write(text.toPath(), html.getBytes());
	}
	
	public static void main(String[] args) throws IOException {
		File dir = new File("/home/wiza/data/chinapub");
		for (File file: dir.listFiles())
			toText(file);
	}

}
