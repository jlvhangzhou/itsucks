/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  分布式无协作爬虫
 */

import java.io.IOException;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class MapOnlyCrawler {
	
	enum Result { SUCCESS, EXCEPTION }
	public static final int MaxNumberOfURLs = 24;
	public static final int MapSlotCapacity = 3;
	public static final String inputDir = "input/";
	public static Path[] inputFiles;
	
	public static void getInputReady() throws IOException {
		JedisPool pool = new JedisPool("localhost");
		Jedis jedis = pool.getResource();
		Set<String> list = jedis.smembers(Util.interviewdb);
		pool.returnBrokenResource(jedis);
		pool.destroy();
		
		if (list.size() == 0) return;
		
		inputFiles = new Path[MapSlotCapacity];
		for (Integer i = 1; i <= MapSlotCapacity; i++)
			inputFiles[i-1] = new Path(inputDir + "/url_list_" + i.toString());
		
		Configuration conf = new Configuration();
		FileSystem hdfs = FileSystem.get(conf);
		FSDataOutputStream[] out = new FSDataOutputStream[MapSlotCapacity];
		for (int i = 0; i < MapSlotCapacity; i++)
			out[i] = hdfs.create(inputFiles[i]);
		
		int count = 0;
		for (String url: list) {
			int i = count % MapSlotCapacity;
			out[i].write((url + "\n").getBytes());
			count++;
			if (count == MaxNumberOfURLs) break;
		}
		
		for (int i = 0; i < MapSlotCapacity; i++)
			out[i].close();
	}
	
	public static class CrawlMapper extends Mapper<LongWritable, Text, NullWritable, NullWritable> {
		public void map(LongWritable key, Text value, Context context) {
			String[] args = { value.toString(), value.toString() };
			try {
				BlogCrawlController.main(args);
				context.getCounter(Result.SUCCESS).increment(1);
			} catch (Exception e) {
				e.printStackTrace();
				context.getCounter(Result.EXCEPTION).increment(1);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		getInputReady();

		Configuration conf = new Configuration();
		Job job = new Job(conf, "Crawling Job");
		job.setJarByClass(MapOnlyCrawler.class);
		
		TextInputFormat.setMinInputSplitSize(job, 0);
		for (Path p: inputFiles)
			TextInputFormat.addInputPath(job, p);
		
		job.setMapperClass(CrawlMapper.class);
		job.setNumReduceTasks(0);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(NullOutputFormat.class);
		
		System.exit(job.waitForCompletion(true)? 0:1);
	}
}
