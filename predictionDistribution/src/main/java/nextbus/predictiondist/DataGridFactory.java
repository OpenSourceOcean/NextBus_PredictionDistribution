/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist;

import java.io.IOException;

/**
 * Factory
 * @author noky
 */
public class DataGridFactory {
	public DataGridFactory() {
		
	}
	
	public DataGridInterface create(DataGridType type) throws IOException {
		switch (type) {
		case HAZELCAST:
			return new HazelcastImpl();
//		case IGNITE:
//			return new IgniteImpl();
//		case GRIDGAIN:
//			return new GridGainImpl();
/*
		case INFINISPAN:
			return new InfinispanImpl();
		case MEMCHACHED:		
				return new MemchachedImpl();
		case EHCACHE:
		    return new EhCacheImpl();
		case JEDIS:
			return new JedisRedisClientImpl();
*/
		default:
			throw new IllegalArgumentException();
		}
	}
}
