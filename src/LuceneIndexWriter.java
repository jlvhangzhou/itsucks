/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  将 HBase 中的数据整理到 Lucene 的索引中
 */

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.TimestampsFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class LuceneIndexWriter {

	public static void main(String[] args) throws IOException {
		/*
		 *  连接数据库
		 */
		JedisPool pool = new JedisPool("localhost");
		Jedis jedis = pool.getResource();
		
		HBaseConfiguration hbaseConf = new HBaseConfiguration();
		HTable table = new HTable(hbaseConf, Util.blogHT);
		
		Analyzer smartcn = new SmartChineseAnalyzer(Util.luceneVersion);
		IndexWriterConfig luceneConf = new IndexWriterConfig(Util.luceneVersion, smartcn);
		Directory dir = new MMapDirectory(new File(Util.blogIndexDir));
		IndexWriter writer = new IndexWriter(dir, luceneConf);
		
		while (jedis.scard(Util.tsdb) != 0) {
			Set<String> tsdbCurrentSet = jedis.smembers(Util.tsdb);
			ArrayList<Long> tslist = new ArrayList<>();
			for (String ts: tsdbCurrentSet) {
				tslist.add(new Long(ts));
			}
			
			TimestampsFilter fliter = new TimestampsFilter(tslist);
			Scan scan = new Scan();
			scan.setFilter(fliter);
			ResultScanner scanner = table.getScanner(scan);
			
			while (true) {
				Result res = scanner.next();
				if (res == null) break;
				for (KeyValue key: res.list()) {
					String value = new String(key.getValue());
					String site = new String(key.getRow()) + new String(key.getQualifier());
					String title = Util.getTitle(value);
					String content = Util.getMainBody(value);
					if (content == null) continue;
					String abstr = Util.getAbstract(content);
					
					Document doc = new Document();
					doc.add(new Field(Util.fields[0], site, Store.YES, Index.ANALYZED));
					doc.add(new Field(Util.fields[1], title, Store.YES, Index.ANALYZED));
					doc.add(new Field(Util.fields[2], abstr, Store.YES, Index.NO));
					doc.add(new Field(Util.fields[3], content, Store.NO, Index.ANALYZED));
					writer.updateDocument(new Term(Util.fields[0], site), doc);
					
					System.out.println("site: " + site);
					System.out.println("title: " + title);
					System.out.println("abstract: " + abstr);
					System.out.println("= = = = = = = = = =");
				}
				writer.commit();
			}
			
			for (Long i: tslist) {
				jedis.srem(Util.tsdb, i.toString());
				System.out.println("remove " + i.toString() + " from redis:ts");
			}
		}
		
		/*
		 *  关闭数据库连接
		 */
		pool.returnResource(jedis);
		pool.destroy();
		table.close();
		writer.close();
	}

}
