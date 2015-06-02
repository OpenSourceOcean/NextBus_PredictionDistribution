//package nextbus.predictiondist.tasks;
//
//
//import nextbus.predictiondist.data.CustomerAndRouteKey;
//import nextbus.predictiondist.data.StopKey;
//import nextbus.predictiondist.data.StopPrediction;
//
//import com.hazelcast.core.EntryEvent;
//import com.hazelcast.core.EntryListener;
//import com.hazelcast.core.IMap;
//import com.hazelcast.core.MapEvent;
//import com.hazelcast.core.MultiMap;
//
//static class MapEntryListener implements EntryListener<StopKey, StopPrediction> {
//	
//	static MultiMap<String, CustomerAndRouteKey> routesPerStopMultiMap = ;
//	static MultiMap<CustomerAndRouteKey, StopKey> stopsPerRouteMultiMap;
//	
//	IMap<StopKey, StopPrediction> imap = hazelcastInstance.getMap(mapId);
//	routesPerStopMultiMap = hazelcastInstance.getMultiMap("routesPerStop");
//	stopsPerRouteMultiMap = hazelcastInstance.getMultiMap("stopsPerRoute");
//	imap.addEntryListener(new MyEntryListener(), true);
//
//	@Override
//	public void entryAdded(EntryEvent<StopKey, StopPrediction> event) {
//		CustomerAndRouteKey customerAndRouteKey = stopKeyToCutomerRouteKey(event
//				.getKey());
//		stopsPerRouteMultiMap.put(customerAndRouteKey, event.getKey());
//		routesPerStopMultiMap.put(customerAndRouteKey.getProjectId(),
//				customerAndRouteKey);
//		System.out
//				.println("Entry added ########################################");
//
//	}
//
//	@Override
//	public void entryRemoved(EntryEvent<StopKey, StopPrediction> event) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void entryUpdated(EntryEvent<StopKey, StopPrediction> event) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void entryEvicted(EntryEvent<StopKey, StopPrediction> event) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void mapEvicted(MapEvent event) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void mapCleared(MapEvent event) {
//		// TODO Auto-generated method stub
//
//	}
//
//	static CustomerAndRouteKey stopKeyToCutomerRouteKey(StopKey stopKey) {
//
//		return new CustomerAndRouteKey(stopKey.getProjectId(),
//				stopKey.getRouteId());
//
//	}
//}
