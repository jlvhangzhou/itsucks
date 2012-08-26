/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  分布式无协作爬虫
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.Tool;

public class MapOnlyCrawler extends Configured implements Tool {
	
	enum Result { SUCCESS, EXCEPTION }
	
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
	
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		Job job = new Job(conf, "Job " + Util.getTime());
		
		
		return 0;
	}
	
}
