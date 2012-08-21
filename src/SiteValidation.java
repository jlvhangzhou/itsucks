
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  检验 redis:apply 中的网站是否 IT-Blog 
 */

public class SiteValidation {
	
	public static void main(String[] args) throws Exception {
		
		JedisPool pool = new JedisPool(Util.MasterHost);
		Jedis jedis = pool.getResource();
		
		while (jedis.scard(Util.applydb) != 0) {
			String member = jedis.srandmember(Util.applydb);
			if ( jedis.sismember(Util.interviewdb, member) || jedis.sismember(Util.acceptdb, member) || 
					jedis.sismember(Util.rejectdb, member) )
				continue;
			
			String[] crawlArgs = { member, member };
			ProbeCrawlController.main(crawlArgs);
			jedis.srem(Util.applydb, member);
		}
		
		pool.returnResource(jedis);
		pool.destroy();
	}
	
}
