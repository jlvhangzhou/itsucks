import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;


public class APITest {

	public static void main(String[] args) throws IOException {
		
		Configuration conf = HBaseConfiguration.create();
//		HBaseAdmin admin = new HBaseAdmin(conf);
//		HTableDescriptor desc = new HTableDescriptor("dota");
//		HColumnDescriptor cdesc = new HColumnDescriptor("cf");
//		admin.createTable(desc);
//		admin.close();
		
		HTable table = new HTable(conf, "dota");
		table.setAutoFlush(false);
		
//		Put put = new Put(Bytes.toBytes("hero"));
//		put.add(Bytes.toBytes("cf"), Bytes.toBytes("力量"), 1, Bytes.toBytes("老牛"));
//		put.add(Bytes.toBytes("cf"), Bytes.toBytes("敏捷"), 2, Bytes.toBytes("敌法"));
//		put.add(Bytes.toBytes("cf"), Bytes.toBytes("智力"), 3, Bytes.toBytes("火女"));
//		
//		table.put(put);
//		table.flushCommits();
		
		Get get = new Get(Bytes.toBytes("hero"));
		get.addFamily(Bytes.toBytes("cf"));
		Result result = table.get(get);
		byte[] val = result.getValue("cf".getBytes(), "敏捷".getBytes());
		System.out.println(Bytes.toString(val));
	}
}


