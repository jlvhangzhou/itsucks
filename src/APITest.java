import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import java.nio.file.Path;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.TimestampsFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.uci.ics.crawler4j.parser.HtmlParseData;
import org.xml.sax.SAXException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class APITest {

	public static void main(String[] args) throws IOException, ParseException, SAXException {
		
//		Configuration conf = HBaseConfiguration.create();
//		HTable table = new HTable(conf, "blog");
//		table.setAutoFlush(false);
//		
//		Scan scan = new Scan();
////		ArrayList<Long> ts = new ArrayList<Long>();
////		ts.add(Long.valueOf(2));
////		TimestampsFilter fliter = new TimestampsFilter(ts);
////		scan.setFilter(fliter);
//		ResultScanner scanner = table.getScanner(scan);
//		int count = 0;
//		while (true) {
//			Result res = scanner.next();
//			if (res == null) break;
//			for (KeyValue key: res.list()) {
//				count++;
//				String text = new String(key.getValue());
//				System.out.println(new String(key.getRow()) + new String(key.getQualifier()));
//				System.out.println(Util.getTitle(text));
//				System.out.println(Util.getAbstract(text));
//				System.out.println("===");
//			}
//		}
//		System.out.println(count);
//		scanner.close();
		
//		Get get = new Get(Bytes.toBytes("hero"));
//		get.addFamily(Bytes.toBytes("cf"));
//		Result result = table.get(get);
//		System.out.println(result.containsColumn("cf".getBytes(), "敏捷".getBytes()));
//		byte[] val = result.getValue("cf".getBytes(), "敏捷".getBytes());
//		System.out.println(Bytes.toString(val));
		
//		Analyzer smartcn = new SmartChineseAnalyzer(Util.luceneVersion);
//		IndexWriterConfig conf = new IndexWriterConfig(Util.luceneVersion, smartcn);
////		
//		Directory dir = new MMapDirectory(new File(Util.blogIndexDir));
//		IndexWriter writer = new IndexWriter(dir, conf);
////		
////		QueryParser parser = new MultiFieldQueryParser(Util.luceneVersion, Util.fields, smartcn);
////		parser.setDefaultOperator(QueryParser.AND_OPERATOR);
////		Query query = parser.parse("site:yufeng");
//		writer.deleteAll();
//		writer.commit();
////		writer.deleteDocuments(query);
//		System.out.println(writer.numDocs());
//		
////		Document doc = new Document();
////		doc.add(new Field(Util.fields[0], "blog.yufeng.info/about", Store.YES, Index.ANALYZED));
////		doc.add(new Field(Util.fields[1], "余峰", Store.YES, Index.ANALYZED));
////		doc.add(new Field(Util.fields[2], "erlang 非业余研究", Store.YES, Index.NO));
////		doc.add(new Field(Util.fields[3], "14年c开发经验, 12年网络开发经验, 3年Linux内核开发", Store.NO, Index.ANALYZED));
////		writer.updateDocument(new Term(Util.fields[2], "erlang 非业余研究"), doc);
//		
//		writer.commit();
//		writer.close();
		
		IndexReader reader = Util.getIndexReader();
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer smartcn = new SmartChineseAnalyzer(Util.luceneVersion);
		QueryParser parser = new MultiFieldQueryParser(Util.luceneVersion, Util.fields, smartcn);
		parser.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query query = parser.parse("java");
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		
//		Term t = new Term("id", "1148516621552470229");
//		Query q = new TermQuery(t);
//		TopDocs hits = searcher.search(q, 10);
		
		for (ScoreDoc d: hits.scoreDocs) {
			if (reader.document(d.doc).getField(Util.fields[0]) != null) {
				System.out.println(reader.document(d.doc).getField(Util.fields[0]).stringValue());
				System.out.println(reader.document(d.doc).getField(Util.fields[1]).stringValue());
				System.out.println(reader.document(d.doc).getField(Util.fields[2]).stringValue());
				System.out.println(d.doc);
			}
			
		}
		
		File path = new File("/home/wiza/193.html");
		String html = new String( Files.readAllBytes(path.toPath()) );
		String content = Util.getMainBody(html);
		String abstr = Util.getAbstract(content);
		System.out.println(content);
		System.out.println(abstr);
	}
}


