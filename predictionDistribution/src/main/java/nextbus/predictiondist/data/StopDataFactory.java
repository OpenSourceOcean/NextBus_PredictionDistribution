/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Generate test data
 * @author noky
 */
public class StopDataFactory {
	// spacing of pred deltas in demo data
	public static final long PRED_DELTA_FOR_STOP_MILLIS = 30L * 60L * 1000L;
	
	public static final long PRED_DELTA_BETWEEN_STOPS_MILLIS = 1L * 60L * 1000L;

	
	public static Set<StopKey> createDummyDataKeys(
			final int custId,
			final int routeId,
			final int numStopsPerRoute,
			final int numPredsPerStop) {
		final Set<StopKey> keys = new HashSet<StopKey>();
		final String customerId = getCustomerId(custId);
		final String routeTag = getRouteId(routeId);
		for (int i=0; i<numStopsPerRoute; i++) {
			String stopTag = getStopId(i);
			StopKey key = new StopKey(customerId, routeTag, stopTag);
			keys.add(key);
		}
		return keys;
	}

	public static Set<String> createDummyDataRouteIds(
			final int numRoutes) {
		final Set<String> routeIds = new HashSet<String>();
		for (int i=0; i<numRoutes; i++) {
			final String routeTag = getRouteId(i);
			routeIds.add(routeTag);
		}
		return routeIds;
	}

	public static Map<StopKey, StopPrediction> createDummyData(
			final int custId,
			final int routeId,
			final int numStopsPerRoute,
			final int numPredsPerStop) {
		final String routeTag = getRouteId(routeId);
		long now = System.currentTimeMillis();
		LinkedHashMap<String, StopPrediction> stopPreds = 
			new LinkedHashMap<String, StopPrediction>();
		for (int i=0; i<numStopsPerRoute; i++) {
			String stopTag = getStopId(i);
			String dirTag = "IB";
            long passTimeRouteDirStop = now;
            StopPredictionInfo[] spis = new StopPredictionInfo[numPredsPerStop];
            for (int j=0; j<numPredsPerStop; j++) {
            	StopPredictionInfo spi = new StopPredictionInfo(
            		"1256",
            		dirTag, "63523", 
            		(short)10, //pathIndex
            		"130549", // tripTag
            		now + 
        			    ((long)i)*PRED_DELTA_BETWEEN_STOPS_MILLIS + 
        			    ((long)j)*PRED_DELTA_FOR_STOP_MILLIS, // predictionTime 
            		false, // isDeparture
            		false, // affectedByTimepoint
            		false, // scheduleBased,
            		3.3535F, // badness,
            		23545, // scheduledTimeForStop,
            		(short)i //orderOfStopInTripPattern
            		);
            	spis[j] = spi;
            }
            StopPrediction sp = new StopPrediction(
            		routeTag, stopTag, dirTag,
                    passTimeRouteDirStop, spis);
            stopPreds.put(stopTag, sp);
		}
		
		RoutePredictionBundle rp = new RoutePredictionBundle(routeTag, now, stopPreds);
		String custIdStr = getCustomerId(custId);
		Map<StopKey, StopPrediction> map = convertRouteBundle(rp, custIdStr);
		return map;
	}
	
	public static Map<StopKey, StopPrediction> convertRouteBundle(RoutePredictionBundle rpb,
			String custId) {
		final Map<String, StopPrediction> spsOrig = rpb.getStopPredictions();
		final String routeTag = rpb.getRouteTag();
		// convert data to map with correct keys
		final Map<StopKey, StopPrediction> sps = new HashMap<StopKey, StopPrediction>();
		for (String stopTag : spsOrig.keySet()) {
			final StopPrediction sp = spsOrig.get(stopTag);
			final StopKey key = new StopKey(custId, routeTag, stopTag);
			sps.put(key, sp);
		}
		return sps;
	}
		
	public static String getCustomerId(int id) {
		return "customer" + id;
	}
	
	public static String getRouteId(int id) {
		return "route" + String.format("%04d", id);
	}
	
	public static String getStopId(int id) {
		return "stop" + String.format("%04d", id);
	}
}
