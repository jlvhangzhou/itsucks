package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
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

class SearchResult {
	/*
	 *  url, title, site, abstr
	 */
	public String[] fields;
	public static int total = 0;
	
	public SearchResult(String[] args) {
		fields = new String[4];
		for (int i = 0; i < 4; i++)
			fields[i] = args[i];
	}
}

public class Application extends Controller {
	
	public static final Version luceneVersion = Version.LUCENE_40;
	public static final String[] fields = {"site", "title", "abstract", "content"};
	public static final String indexDir = "/home/wiza/data/lucene/blog/";
	
	public static ArrayList<SearchResult> indexSearch(String key, Long page) throws IOException, ParseException {
		
		File file = new File(indexDir);
		Directory dir = new MMapDirectory(file);
		IndexReader reader = IndexReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer smartcn = new SmartChineseAnalyzer(luceneVersion);
		QueryParser parser = new MultiFieldQueryParser(luceneVersion, fields, smartcn);
		parser.setDefaultOperator(QueryParser.AND_OPERATOR);
		int numDocs = reader.numDocs();
		
		key = key.substring(0, Math.min(key.length(), 64));
		Query query = parser.parse(key);
		TopDocs hits = searcher.search(query, numDocs);
		int total = hits.totalHits;
		int begin = (int) (page - 1) * 10;
		int end = (int) Math.min(page * 10, total - 1);
		ArrayList<SearchResult> result = new ArrayList<SearchResult>();
		
		if (total == 0 || begin < 0 || begin >= total) {
			SearchResult.total = 0;
			return result; 
		}
		
		ScoreDoc[] docs = hits.scoreDocs;
		for (int i = begin; i <= end; i++) {
			String[] str = new String[4];
			for (int k = 0; k < 4; k++)
				str[0] = reader.document(docs[i].doc).getField(fields[k]).stringValue();
			result.add(new SearchResult(str));
		}
		
		reader.close();
		dir.close();
		return result;
	}
	
	public static Result index() {
		String html = index.render("").toString();
		return ok(html).as("text/html");
	}
	
	public static Result test(String key, Long page) {
		return ok(index.render(key+page.toString()));
	}
  
}