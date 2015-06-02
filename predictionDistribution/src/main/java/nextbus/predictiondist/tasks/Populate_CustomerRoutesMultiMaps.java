package nextbus.predictiondist.tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;

import nextbus.predictiondist.data.CustomerAndRouteKey;
import nextbus.predictiondist.data.StopKey;
import nextbus.predictiondist.data.StopPrediction;

public class Populate_CustomerRoutesMultiMaps implements GenericTaskInterface {
	

	@Override
	public Map<StopKey, StopPrediction> runTheLogic(
			GenericTaskConfig genericTaskConfig, HazelcastInstance hazelcastInstance) {

		IMap<StopKey, StopPrediction> map = hazelcastInstance.getMap(
				genericTaskConfig.getMapId());

		Set<StopKey> localKeys = map.localKeySet();

		
		// Multimap for storing Customer keys for routes 
		MultiMap<CustomerAndRouteKey, StopKey> stopsPerRouteMultiMap = hazelcastInstance.getMultiMap("stopsPerRoute");
		MultiMap<String, CustomerAndRouteKey> routesPerStopMultiMap  = hazelcastInstance.getMultiMap("routesPerStop");

		for (StopKey key : localKeys) {
			
			// Generate a customer key
			CustomerAndRouteKey customerAndRouteKey = new CustomerAndRouteKey(
					key.getProjectId(), key.getRouteId());
			
			// Populate  stopsPerRouteMultiMap
			stopsPerRouteMultiMap.put(customerAndRouteKey, key);
			
			// Populate  routesPerStopMultiMap
			routesPerStopMultiMap.put(key.getProjectId(), customerAndRouteKey);
		}
		
		System.out.println("Populate_CustomerRoutesMultiMaps #########" + routesPerStopMultiMap.keySet().toString() + stopsPerRouteMultiMap.keySet().toString());
		return new HashMap<StopKey, StopPrediction>();
	}
	
	
	
	
	
	
}
