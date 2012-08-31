package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;

import play.mvc.*;
import views.html.index;

/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 */

class SearchResult {
	/*
	 *  URL, title, site, abstract
	 */
	public String[] fields;
	public static Integer total = 0;
	
	public SearchResult(String[] args) {
		fields = new String[4];
		for (int i = 0; i < 4; i++)
			fields[i] = args[i];
	}
}

public class Application extends Controller {
	
	/*
	 *  搜索 Lucene 的索引
	 */
	
	public static final Version luceneVersion = Version.LUCENE_40;
	public static final String[] indexFields = {"site", "title", "abstract", "content"};
	public static final String indexDir = "/home/wiza/data/lucene/blog/";
	
	public static ArrayList<SearchResult> indexSearch(String key, Long page) throws IOException, ParseException {
		
		File file = new File(indexDir);
		Directory dir = new MMapDirectory(file);
		IndexReader reader = IndexReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer smartcn = new SmartChineseAnalyzer(luceneVersion);
		QueryParser parser = new MultiFieldQueryParser(luceneVersion, indexFields, smartcn);
		parser.setDefaultOperator(QueryParser.AND_OPERATOR);
		int numDocs = reader.numDocs();
		
		key = key.substring(0, Math.min(key.length(), 64));
		Query query = parser.parse(key);
		TopDocs hits = searcher.search(query, numDocs);
		int total = hits.totalHits;
		int begin = (int) (page - 1) * 10;
		int end = (int) Math.min(page * 10, total);
		ArrayList<SearchResult> result = new ArrayList<SearchResult>();
		
		if (total == 0 || begin < 0 || begin >= total) {
			SearchResult.total = 0;
			return result; 
		}
		
		ScoreDoc[] docs = hits.scoreDocs;
		for (int i = begin; i < end; i++) {
			Document doc = reader.document(docs[i].doc);
			String[] str = new String[4];
			//  URL
			str[0] = "http://" + doc.getField(indexFields[0]).stringValue();
			//  title
			str[1] = doc.getField(indexFields[1]).stringValue();
			str[1] = getPrefix(str[1], lengthOfTitle);
			//  site
			str[2] = doc.getField(indexFields[0]).stringValue();
			str[2] = getPrefix(str[2], lengthOfSite);
			//  abstract
			str[3] = doc.getField(indexFields[2]).stringValue();
			str[3] = toEntity(str[3]);
			
			result.add(new SearchResult(str));
		}
		SearchResult.total = total;
		
		reader.close();
		dir.close();
		return result;
	}
	
	/*
	 *  省略显示 title 和 site
	 */
	
	public static final int lengthOfTitle = 32;
	public static final int lengthOfSite = 40;
	
	public static String getPrefix(String text, int k) {
		text = text.replaceAll("[\r\n]", "");
		int length = text.length();
		double count = 0;
		String result = "";
		for (int i = 0; i < length; i++) {
			char ch = text.charAt(i);
			result += ch;
			if (ch == ' ') count += 0.5;
			else if (Character.isUpperCase(ch)) count += 0.8;
			else if (Character.isLowerCase(ch)) count += 0.7;
			else if (Character.isDigit(ch)) count += 0.7;
			else count += 1;
			if (count >= k) {
				if (i + 1 != length) result += "...";
				break;
			}
		}
		return result;
	}
	
	/*
	 *  转换abstract中的部分符号
	 */
	
	public static String toEntity(String text) {
		text = text.replaceAll("<", "&lt;");
		text = text.replaceAll(">", "&gt;");
		text = text.replaceAll("\"", "&quot;");
		text = text.replaceAll("\'", "&apos;");
		return text;
	}
	
	/*
	 *  响应请求
	 */
	
	public static Result index() {
		String html = index.render("").toString();
		return ok(html).as("text/html");
	}
	
	public static Result search(String key, Long page) throws IOException, ParseException {
		String html = index.render(key).toString().replaceAll("\n", "");
		ArrayList<SearchResult> result = indexSearch(key, page);
		String regex, replacement;
		
		if (SearchResult.total == 0) {
			regex = "<!-- result [^>]* -->.*<!-- end result [^>]* -->";
			replacement = "";
			html = html.replaceAll(regex, replacement);
		} else {
			Integer totalPages = (SearchResult.total - 1) / 10 + 1;
			
			regex = "<!-- button 1 -->.*<!-- end button 1 -->";
			replacement = "<a href=\"/" + key + "/1\">&laquo;</a>";
			html = html.replaceAll(regex, replacement);
			
			Integer prev = (int) Math.max(1, page - 1);
			regex = "<!-- button 2 -->.*<!-- end button 2 -->";
			replacement = "<a href=\"/" + key + "/" + prev.toString() + "\">&lsaquo;</a>";
			html = html.replaceAll(regex, replacement);
			
			Integer next = (int) Math.min(totalPages, page + 1);
			regex = "<!-- button 3 -->.*<!-- end button 3 -->";
			replacement = "<a href=\"/" + key + "/" + next.toString() + "\">&rsaquo;</a>";
			html = html.replaceAll(regex, replacement);
			
			regex = "<!-- button 4 -->.*<!-- end button 4 -->";
			replacement = "<a href=\"/" + key + "/" + totalPages.toString() + "\">&raquo;</a>";
			html = html.replaceAll(regex, replacement);
			
			for (int i = 0; i < 10; i++) {
				String num = Integer.valueOf(i + 1).toString(); 
				regex = "<!-- result " + num + " -->.*<!-- end result " + num + " -->";
				int index = html.indexOf("<!-- result ");
				html = html.replaceAll(regex, "");
				if (i < result.size()) {
					replacement = "<li><h4><a href=\"" + 
							result.get(i).fields[0] + "\">" +
							result.get(i).fields[1] + "</a></h4><h6>" +
							result.get(i).fields[2] + "</h6><h5>" +
							result.get(i).fields[3] + "</h5></li>";
					html = html.substring(0, index) + replacement + html.substring(index);
				} 
			}
		}
		return ok(html).as("text/html");
	}
  
}
