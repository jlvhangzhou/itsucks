
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  检验 redis:apply 中的网站是否 IT-Blog 
 */

public class SiteEstimation {
	
	public static void main(String[] args) throws Exception {
		
		JedisPool pool = new JedisPool(Util.MasterHost);
		Jedis jedis = pool.getResource();
		
		while (jedis.scard(Util.applydb) != 0) {
			String member = jedis.srandmember(Util.applydb);
			
			if ( jedis.sismember(Util.interviewdb, member) || jedis.sismember(Util.acceptdb, member) || 
					jedis.sismember(Util.rejectdb, member) ) {
			//	pass
			} else {
				String[] crawlArgs = { member };
				System.out.println("estimate: " + member);
				ProbeCrawlController.main(crawlArgs);
			}
			
			jedis.srem(Util.applydb, member);
			System.out.println("remove " + member + " from redis:" + Util.applydb);
		}
		
		pool.returnResource(jedis);
		pool.destroy();
	}
	
}
