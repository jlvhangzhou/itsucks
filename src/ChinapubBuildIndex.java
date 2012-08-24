
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import java.math.*;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.queryparser.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.*;

public class ChinapubBuildIndex {

	public static void main(String[] args) throws IOException, ParseException {
		
		File textDir = new File("/home/wiza/data/itbooks/contents");
		Directory dir = new MMapDirectory(new File(Util.chinapubIndexDir));
//		
//		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, new SmartChineseAnalyzer(Version.LUCENE_40));
//		IndexWriter writer = new IndexWriter(dir, config);
//	
//		for (File f: textDir.listFiles()) {
//			String content = new String(Files.readAllBytes(f.toPath()), "GB2312");
//			Document doc = new Document();
//			doc.add(new Field("content", content, Store.NO, Index.ANALYZED));
//			writer.addDocument(doc);
//		}
//		writer.close();
		
		IndexReader reader = Util.getIndexReader();
		String[] fields = {"url", "title", "abstract", "content"};
		
		
		MultiFieldQueryParser parser = 
				new MultiFieldQueryParser(Version.LUCENE_40, fields, new SmartChineseAnalyzer(Version.LUCENE_40));
		
		parser.setDefaultOperator(QueryParser.AND_OPERATOR);
		
		Query query = parser.parse("算法");
//		Query query = parser.parse(Version.LUCENE_40, "java c++", fields, occurs, new SmartChineseAnalyzer(Version.LUCENE_40));
		
		
		IndexSearcher searcher = new IndexSearcher(reader);
		
		TopDocs hits = searcher.search(query, Integer.MAX_VALUE);
		System.out.println(hits.totalHits);
		double rate = (double) hits.totalHits / reader.numDocs();
//		for (ScoreDoc doc: hits.scoreDocs) {
//			System.out.println(doc.toString());
//		}
//		hits = searcher.searchAfter(hits.scoreDocs[0], query, 5);
//		for (ScoreDoc doc: hits.scoreDocs) {
//			System.out.println(doc.toString());
//		}
//		System.out.println(reader.docFreq(new Term("content", "java c")));
//		System.out.println(reader.numDocs());
//		System.out.println(Math.sin(Math.PI * Math.sqrt((double)reader.docFreq(new Term("content", "java c")) / reader.numDocs()) ) * 2);
		System.out.println(Util.compute(rate));
		
		
		
	}

}
