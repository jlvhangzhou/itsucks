import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.TimestampsFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
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


public class APITest {

	public static void main(String[] args) throws IOException, ParseException {
		
		Configuration conf = HBaseConfiguration.create();
//		HBaseAdmin admin = new HBaseAdmin(conf);
//		HTableDescriptor desc = new HTableDescriptor("dota");
//		HColumnDescriptor cdesc = new HColumnDescriptor("cf");
//		admin.createTable(desc);
//		admin.close();
		
		HTable table = new HTable(conf, "blog");
		table.setAutoFlush(false);
		
//		Put put = new Put("race".getBytes());
//		put.add(Bytes.toBytes("cf"), Bytes.toBytes("力量"), 2, Bytes.toBytes("老牛"));
//		put.add(Bytes.toBytes("cf"), Bytes.toBytes("敏捷"), 2, Bytes.toBytes("敌法"));
//		put.add(Bytes.toBytes("cf"), Bytes.toBytes("智力"), 4, Bytes.toBytes("双头龙"));
//		
//		put.add(Bytes.toBytes("cf"), Bytes.toBytes("人族"), 3, Bytes.toBytes("火枪手"));
//		put.add(Bytes.toBytes("cf"), Bytes.toBytes("兽族"), 2, Bytes.toBytes("风骑士"));
//		put.add(Bytes.toBytes("cf"), Bytes.toBytes("不死族"), 4, Bytes.toBytes("食尸鬼"));
//		
//		table.put(put);
//		table.flushCommits();
		
		Scan scan = new Scan();
		ArrayList<Long> ts = new ArrayList<Long>();
		ts.add(Long.valueOf(2));
//		TimestampsFilter fliter = new TimestampsFilter(ts);
//		scan.setFilter(fliter);
		ResultScanner scanner = table.getScanner(scan);
		int count = 0;
		while (true) {
			Result res = scanner.next();
			if (res == null) break;
			for (KeyValue key: res.list()) {
				count++;
				System.out.println(Util.getTitle(new String(key.getValue())));
			}
		}
		System.out.println(count);
		scanner.close();
		
//		Get get = new Get(Bytes.toBytes("hero"));
//		get.addFamily(Bytes.toBytes("cf"));
//		Result result = table.get(get);
//		System.out.println(result.containsColumn("cf".getBytes(), "敏捷".getBytes()));
//		byte[] val = result.getValue("cf".getBytes(), "敏捷".getBytes());
//		System.out.println(Bytes.toString(val));
		
//		Analyzer smartcn = new SmartChineseAnalyzer(Util.luceneVersion);
//		IndexWriterConfig conf = new IndexWriterConfig(Util.luceneVersion, smartcn);
//		
//		Directory dir = new MMapDirectory(new File(Util.blogIndexDir));
//		IndexWriter writer = new IndexWriter(dir, conf);
//		
//		Document doc = new Document();
//		doc.add(new Field(Util.fields[0], "blog.yufeng.info/about", Store.YES, Index.ANALYZED));
//		doc.add(new Field(Util.fields[1], "余峰", Store.YES, Index.ANALYZED));
//		doc.add(new Field(Util.fields[2], "erlang非专业余研究", Store.YES, Index.NO));
//		doc.add(new Field(Util.fields[3], "14年c开发经验, 12年网络开发经验, 3年Linux内核开发", Store.NO, Index.ANALYZED));
//		
//		writer.updateDocument(new Term(Util.fields[0], "blog.yufeng.info"), doc);
//		writer.close();
		
//		IndexReader reader = Util.getIndexReader();
//		IndexSearcher searcher = new IndexSearcher(reader);
//		Analyzer smartcn = new SmartChineseAnalyzer(Util.luceneVersion);
//		QueryParser parser = new MultiFieldQueryParser(Util.luceneVersion, Util.fields, smartcn);
//		parser.setDefaultOperator(QueryParser.AND_OPERATOR);
//		Query query = parser.parse("c开发经验");
//		TopDocs hits = searcher.search(query, 10);
//		
//		for (ScoreDoc d: hits.scoreDocs) {
//			if (reader.document(d.doc).getField(Util.fields[0]) != null) {
//				System.out.println(reader.document(d.doc).getField(Util.fields[0]).stringValue());
//				System.out.println(reader.document(d.doc).getField(Util.fields[1]).stringValue());
//				System.out.println(reader.document(d.doc).getField(Util.fields[2]).stringValue());
//			}
//			
//		}
	}
}


