//package nextbus.predictiondist.tasks;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.atomic.AtomicLong;
//
//import nextbus.predictiondist.DataGridConfig;
//import nextbus.predictiondist.DiscoveryMode;
//import nextbus.predictiondist.HazelcastImpl;
//import nextbus.predictiondist.IsolationMode;
//import nextbus.predictiondist.LoadMode;
//import nextbus.predictiondist.MapType;
//import nextbus.predictiondist.SerializationMode;
//import nextbus.predictiondist.TransactionsMode;
//import nextbus.predictiondist.data.RoutePredictionBundle;
//import nextbus.predictiondist.data.StopDataFactory;
//import nextbus.predictiondist.data.StopKey;
//import nextbus.predictiondist.data.StopPrediction;
//import nextbus.predictiondist.data.StopPredictionInfo;
//
//public class ParallelTaskRunner {
//
//	private static AtomicLong stopPredictionCounter =  new AtomicLong(0);;
//	private static AtomicLong numOps =  new AtomicLong(0);;
//
//	public static void main(String[] args) {
//		long numStopsAccessed = 0;
//		long numReadOrWriteOps = 0;
//		final Map<String, Set<String>> predictionsByCustemersRoutesMap = new HashMap<String, Set<String>>();
//		int numOfCustomer = 10;
//		HazelcastImpl impl = new HazelcastImpl();
//		DataGridConfig dgCfg = null;
//		for (int c = 1; c <= numOfCustomer; c++) {
//			Set<String> routeIds = new HashSet<String>();
//			long now = System.currentTimeMillis();
//
//			LinkedHashMap<String, StopPrediction> stopPreds = new LinkedHashMap<String, StopPrediction>();
//			String custIdStr = StopDataFactory.getCustomerId(c);
//
//			for (int i = 0; i < 1; i++) {
//				String stopTag = String.valueOf(i);
//				String dirTag = "IB";
//				long passTimeRouteDirStop = System.currentTimeMillis();
//				StopPredictionInfo[] spis = new StopPredictionInfo[10];
//				for (int j = 0; j < 10; j++) {
//					StopPredictionInfo spi = new StopPredictionInfo("1256",
//							dirTag, "63523", (short) 10, // pathIndex
//							"130549", // tripTag
//							now + ((long) i) * j + ((long) j) * i, // predictionTime
//							false, // isDeparture
//							false, // affectedByTimepoint
//							false, // scheduleBased,
//							3.3535F, // badness,
//							23545, // scheduledTimeForStop,
//							(short) i // orderOfStopInTripPattern
//					);
//					spis[j] = spi;
//				}
//				StopPrediction sp = new StopPrediction("routeTag" + i, stopTag,
//						dirTag, passTimeRouteDirStop, spis);
//				sp.set_projectId(custIdStr);
//				stopPreds.put(stopTag, sp);
//				routeIds.add("routeTag" + i);
//
//				GenericTaskConfig genericTaskConfig = new GenericTaskConfig(
//						null, null, null, IdeaType.LOOKUP_MULTI_MAPS, null);
//
//				dgCfg = new DataGridConfig("HAZELCAST", MapType.PREDICTIONS,
//						false, DiscoveryMode.DEFAULT,
//						TransactionsMode.OPTIMISTIC,
//						IsolationMode.REPEATABLE_READ,
//						SerializationMode.DEFAULT, LoadMode.DATA_LOAD, null,
//						null, genericTaskConfig);
//				impl.init(dgCfg);
//				RoutePredictionBundle rp = new RoutePredictionBundle("routeTag"
//						+ i, now, stopPreds);
//
//				// Put all the values the distributed map
//				// Now run the test
//				Map<StopKey, StopPrediction> map = StopDataFactory
//						.convertRouteBundle(rp, custIdStr);
//				impl.putAll(map);
//			}
//			if (predictionsByCustemersRoutesMap.get(custIdStr) != null) {
//				predictionsByCustemersRoutesMap.get(custIdStr).addAll(routeIds);
//			} else {
//				predictionsByCustemersRoutesMap.put(custIdStr, routeIds);
//			}
//
//		}
//
//		populateCustomerRoutesMultiMap(dgCfg,
//				predictionsByCustemersRoutesMap.keySet());
//
//		DateFormat fmt = new SimpleDateFormat("HH:mm:ss.SSSZ");
//		// Normally just wait a few seconds before starting
//		final long runtimeMillis = (long) 60 * 1000L;
//		Date endDate = new Date();
//		final String endTimeFmt = fmt.format(endDate);
//
//		Set<String> totalCustumers = predictionsByCustemersRoutesMap.keySet();
//
//		for (String custIdStr : totalCustumers) {
//
//			Map<StopKey, StopPrediction> allPredictions;
//			allPredictions = impl.getPredictionsForRoutes(custIdStr,
//					predictionsByCustemersRoutesMap.get(custIdStr));
//			stopPredictionCounter.addAndGet(allPredictions.size());
//			// Log how long it took to write all stops in route
//			numOps.addAndGet(allPredictions.size());
//			numReadOrWriteOps += getNumReadOrWriteOps();
//			numStopsAccessed += getNumStopsAccessed();
//
//		}
//		System.out.println(endTimeFmt + ": Test completed");
//		long numStopsPerSec = numStopsAccessed / (runtimeMillis / 1000L);
//		long numReadOrWriteOpsPerSec = numReadOrWriteOps
//				/ (runtimeMillis / 1000L);
//		double msPerOp = (double) runtimeMillis / (double) numReadOrWriteOps;
//
//		// System.out.println(String.format(
//		// "Num stop predictions %ss: %d (%d stops/sec)", impl._dataGridConfig
//		// .getGenericTaskConfig().getIdeaType(),
//		// numStopsAccessed, numStopsPerSec));
//		// System.out.println(String.format(
//		// "Num route %ss:           %d (%d routes/sec)", impl._dataGridConfig
//		// .getGenericTaskConfig().getIdeaType(),
//		// numReadOrWriteOps, numReadOrWriteOpsPerSec));
//		// System.out.println(String.format(
//		// "Avg %s time per route:     %.2fms", impl._dataGridConfig
//		// .getGenericTaskConfig().getIdeaType(), msPerOp));
//
//	}
//
//	public static long getNumReadOrWriteOps() {
//		return numOps.get();
//	}
//
//	public static long getNumStopsAccessed() {
//		return stopPredictionCounter.get();
//	}
//
//	public static void populateCustomerRoutesMultiMap(DataGridConfig dgCfg,
//			Set<String> customerIds) {
//
//		HazelcastImpl impl = new HazelcastImpl();
//		impl.init(dgCfg);
//		impl.populateLookUpMultiMaps(customerIds);
//
//	}
//
//	@SuppressWarnings("unchecked")
//	public static void getPredictionsForRoutes(DataGridConfig dgCfg) {
//
//		HazelcastImpl impl = new HazelcastImpl();
//		impl.init(dgCfg);
//		Map<StopKey, StopPrediction> preds = impl
//				.getPredictionsForRoutes((Map<String, Set<String>>) StopDataFactory
//						.getPredictionsbycustemersroutesmap());
//
//		int numPreds = (preds == null) ? 0 : preds.size();
//		stopPredictionCounter.addAndGet(numPreds);
//		numOps.addAndGet(numPreds);
//
//	}
//
//}
