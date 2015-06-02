/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import nextbus.predictiondist.data.StopDataFactory;
import nextbus.predictiondist.data.StopKey;
import nextbus.predictiondist.data.StopPrediction;
import nextbus.predictiondist.tasks.GenericTask;
import nextbus.predictiondist.tasks.GenericTaskConfig;
import nextbus.predictiondist.tasks.IdeaType;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;

/**
 * Tests for looking up predictions in bulk by route/customer
 * 
 * @author noky
 *
 */
public class HazelcastBulkFetchTester {
	private final IdeaType ideaType;
	private final GenericTaskConfig genericTaskConfig;
	private final HazelcastInstance hazelcastInstance;
	private final DataGridConfig dataGridConfig;
	private final int numOfCustomer;
	private final int numRoutesPerCustomer = 30;
	private final int numStopsPerRoute = 80;
	private final int numPredsPerStop = 10;
	private final long runtimeMillis = (long) 60 * 1000L;
	private static IMap<StopKey, StopPrediction> imap;

	private static final String MAP_ID = "predictions-test";

	public HazelcastBulkFetchTester(IdeaType ideaType, int numCustomers) {
		this.numOfCustomer = numCustomers;
		this.ideaType = ideaType;
		this.genericTaskConfig = new GenericTaskConfig(null, null, null,
				ideaType);
		HazelcastImpl impl = new HazelcastImpl();
		this.dataGridConfig = new DataGridConfig(MAP_ID, MapType.PREDICTIONS,
				false, DiscoveryMode.DEFAULT, TransactionsMode.OPTIMISTIC,
				IsolationMode.REPEATABLE_READ, SerializationMode.KRYO,
				LoadMode.DATA_LOAD, null, null);
		this.hazelcastInstance = impl.getInstance(dataGridConfig);
		// routesPerStopMultiMap = hazelcastInstance
		// .getMultiMap(ROUTES_STOP_MULTIMAP_ID);
		// stopsPerRouteMultiMap = hazelcastInstance
		// .getMultiMap(ROUTES_STOPS_MULTIMAP_ID);
		// customerrRouteMultiMap = hazelcastInstance
		// .getMultiMap(CUSTOMER_ROUTES_MULTIMAP_ID);
		imap = hazelcastInstance.getMap(MAP_ID);

	}

	public Map<StopKey, StopPrediction> getPredictionsForRoutes(
			String customerId, Set<String> routeIds) {

		Map<StopKey, StopPrediction> allPredictions = new HashMap<StopKey, StopPrediction>();
		Future<Map<StopKey, StopPrediction>> returnedFuture = runTheTask(
				customerId, genericTaskConfig);

		/*
		 * For some reason distributed Atomic reference is not working /* Done
		 * flag required for not proceed further to read the output or /*
		 * future.get() will throw interrupted exception and will not return all
		 * /* expected results
		 */
		boolean notDoneFlag = true;
		while (notDoneFlag) {
			// Do something here in parallel
			if (returnedFuture.isDone()) {
				notDoneFlag = false;
			}
		}
		try {
			// Not required extra map reference will clean later
			allPredictions
					.putAll((Map<? extends StopKey, ? extends StopPrediction>) returnedFuture
							.get());

		} catch (InterruptedException | ExecutionException e) {

			e.printStackTrace();
		}

		return allPredictions;

	}

	public Map<StopKey, StopPrediction> getPredictionsForRoutes(
			Map<String, Set<String>> predictionsByCustumersRoutesMap) {

		Map<StopKey, StopPrediction> allPredictions = new HashMap<StopKey, StopPrediction>();

		List<Future<Map<StopKey, StopPrediction>>> futures = new ArrayList<Future<Map<StopKey, StopPrediction>>>();
		Set<String> customerIds = predictionsByCustumersRoutesMap.keySet();

		System.out.println("###  customerIds populateLookUpMultiMaps "
				+ customerIds);

		for (String customerId : customerIds) {
			GenericTaskConfig genericTaskConfig = new GenericTaskConfig(
					new StopKey(customerId, null, null),
					predictionsByCustumersRoutesMap.get(customerId),
					dataGridConfig.getMapId(), ideaType);
			Future<Map<StopKey, StopPrediction>> returnedFuture = runTheTask(
					customerId, genericTaskConfig);
			futures.add(returnedFuture);
		}

		/**
		 * For some reason distributed Atomic reference is not working /* Done
		 * flag required for not proceed further to read the output or /*
		 * future.get() will throw interrupted exception and will not return all
		 * /* expected results
		 */

		boolean notDoneFlag = true;
		while (notDoneFlag) {

			// Do something here parallel

			int counter = 0;
			for (Future<Map<StopKey, StopPrediction>> future : futures) {
				counter++;
				try {
					allPredictions
							.putAll((Map<? extends StopKey, ? extends StopPrediction>) future
									.get());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				if (future.isDone() && counter == futures.size()) {
					notDoneFlag = false;

				}
			}
		}

		return allPredictions;

	}

	public void populateLookUpMultiMaps(Set<String> customerIds) {

		List<Future<Map<StopKey, StopPrediction>>> futures = new ArrayList<Future<Map<StopKey, StopPrediction>>>();

		for (String customerId : customerIds) {
			GenericTaskConfig genericTaskConf = new GenericTaskConfig(null,
					null, null, IdeaType.POPULATE_MULTI_MAPS);
			Future<Map<StopKey, StopPrediction>> returnedFuture = runTheTask(
					customerId, genericTaskConf);
			futures.add(returnedFuture);
		}
		/**
		 * For some reason distributed Atomic reference is not working /* Done
		 * flag required for not proceed further to read the output or /*
		 * future.get() will throw interrupted exception and will not return all
		 * /* expected results
		 */

		boolean notDoneFlag = true;
		while (notDoneFlag) {

			// Do something here parallel

			int counter = 0;
			for (Future<Map<StopKey, StopPrediction>> future : futures) {
				counter++;
				try {
					future.get();
				} catch (InterruptedException | ExecutionException e) {

					e.printStackTrace();
				}
				if (future.isDone() && counter == futures.size()) {
					notDoneFlag = false;
				}
			}
		}

	}

	/**
	 * @param customerId
	 * @param genericTaskConf
	 * @return
	 */
	private Future<Map<StopKey, StopPrediction>> runTheTask(String customerId,
			GenericTaskConfig genericTaskConf) {
		IExecutorService executorService;
		GenericTask genericTask = new GenericTask(genericTaskConf);
		// genericTaskConf.setHazelcastInstance(hazelcastInstance);
		genericTaskConf.setRouteIds(null);
		genericTaskConf.setTaskKey(new StopKey(customerId, null, null));
		genericTaskConf.setMapId(dataGridConfig.getMapId());

		executorService = hazelcastInstance.getExecutorService("executor");
		Future<Map<StopKey, StopPrediction>> returnedFuture = executorService
				.submitToKeyOwner(genericTask, new StopKey(customerId, null,
						null));
		return returnedFuture;
	}

	public void doTest() {
		System.out.println("Starting test, #customers=" + numOfCustomer
				+ " idea=" + ideaType);

		final Map<String, Set<String>> routesPerCustomer = new HashMap<String, Set<String>>();

		System.out.println("Generating data...");
		String mapId = dataGridConfig.getMapId();

		for (int custId = 0; custId <= numOfCustomer; custId++) {
			String custIdStr = StopDataFactory.getCustomerId(custId);
			Set<String> routeIds = new HashSet<String>();
			for (int routeId = 0; routeId < numRoutesPerCustomer; routeId++) {
				Map<StopKey, StopPrediction> data = StopDataFactory
						.createDummyData(custId, routeId, numStopsPerRoute,
								numPredsPerStop);
				imap.putAll(data);
				String routeIdStr = StopDataFactory.getRouteId(routeId);
				routeIds.add(routeIdStr);
			}
			routesPerCustomer.put(custIdStr, routeIds);
		}

		if (ideaType.equals(IdeaType.LOOKUP_MULTI_MAPS)) {
			System.out.println("Populating multimaps");
			populateLookUpMultiMaps(routesPerCustomer.keySet());
		}

		System.out.println("Starting test...");

		final long startTime = System.currentTimeMillis();
		final long projectedEndTime = startTime + runtimeMillis;
		DateFormat fmt = new SimpleDateFormat("HH:mm:ss.SSSZ");

		int numReadOps = 0;
		int numStopsRead = 0;

		final int numStopsPerCust = this.numRoutesPerCustomer
				* this.numStopsPerRoute;
		LOOP: while (true) {
			for (int custId = 0; custId < this.numOfCustomer; custId++) {
				String custIdStr = StopDataFactory.getCustomerId(custId);
				Map<StopKey, StopPrediction> data = null;

				if (this.ideaType.equals(IdeaType.USE_ACTUAL_STOP_KEYS)) {
					Set<StopKey> allkeys = this.getKeysForAllStops(custId);
					data = imap.getAll(allkeys);
				} else {
					data = getPredictionsForRoutes(custIdStr,
							routesPerCustomer.get(custIdStr));
				}

				if (data == null || data.size() == 0) {
					throw new RuntimeException("No data for customer="
							+ custIdStr);
				}
				if (data.size() != numStopsPerCust) {
					throw new RuntimeException(
							"not enough data returned, expecting "
									+ numStopsPerCust + " but got "
									+ data.size());
				}
				numStopsRead += data.size();
				numReadOps++;

				long now = System.currentTimeMillis();
				if (now > projectedEndTime) {
					break LOOP;
				}
			}
		}

		// Log how long it took to write all stops in route
		Date endDate = new Date();
		final String endTimeFmt = fmt.format(endDate);
		System.out.println(endTimeFmt + ": Test completed");
		long numStopsPerSec = numStopsRead / (runtimeMillis / 1000L);
		long numReadOpsPerSec = numReadOps / (runtimeMillis / 1000L);
		double msPerOp = (double) runtimeMillis / (double) numReadOps;

		System.out.println(String.format(
				"Num stop predictions %ss: %d (%d stops/sec)", ideaType,
				numStopsRead, numStopsPerSec));
		System.out.println(String.format(
				"Num route %ss:           %d (%d routes/sec)", ideaType,
				numReadOps, numReadOpsPerSec));
		System.out.println(String.format("Avg %s time per route:     %.2fms",
				ideaType, msPerOp));
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;

		System.out.println(" #### Total time taken by the program is "
				+ totalTime);

		hazelcastInstance.shutdown();
	}

	public Set<StopKey> getKeysForAllStops(int customerId) {
		Set<StopKey> allKeys = new HashSet<StopKey>();
		for (int routeId = 0; routeId < this.numRoutesPerCustomer; routeId++) {
			Set<StopKey> keys = StopDataFactory.createDummyDataKeys(customerId,
					routeId, this.numStopsPerRoute, this.numPredsPerStop);
			allKeys.addAll(keys);
		}
		return allKeys;
	}

	public static void main(String[] args) {
		IdeaType ideaType = IdeaType.MULTI_MAP; // .USE_ACTUAL_STOP_KEYS;
		int numOfCustomer = 150;
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
				switch (arg) {
				case "-ideaType":
					String ideaString = args[++i];
					ideaType = IdeaType.valueOf(ideaString);
					System.out.println(ideaString);
					break;
				case "-nc":
					numOfCustomer = Integer.parseInt(args[++i]);
					break;
				default:
					throw new IllegalArgumentException("Unknown option: " + arg);
				}
			}
		}

		System.out.println("Testing idea: " + ideaType);
		HazelcastBulkFetchTester test = new HazelcastBulkFetchTester(ideaType,
				numOfCustomer);
		test.doTest();
	}

}