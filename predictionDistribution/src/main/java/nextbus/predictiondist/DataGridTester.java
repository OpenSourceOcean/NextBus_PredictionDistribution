package nextbus.predictiondist;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import nextbus.predictiondist.data.StopDataFactory;
import nextbus.predictiondist.data.StopKey;
import nextbus.predictiondist.data.StopPrediction;

/**
 * Test some data grid solutions for prediction distribution schemes.
 * 
 * Default: 80 stops per route, 10 preds per stop
 * * 64K data with Java serialization
 */
public class DataGridTester {
	// Default values. 1000 * 30 * 80 = 2.4 Million stops
	private static final int DEF_START_CUSTOMER_NUM = 0;
	private static final int DEF_NUM_CUSTOMERS = 1;
	private static final int DEF_NUM_ROUTES_PER_CUST = 30;
	public static final int DEF_NUM_STOPS_PER_ROUTE = 80;
	public static final int DEF_NUM_PREDS_PER_STOP = 10;
	private static final int DEF_NUM_THREADS = 1;	
	
	private static final int DEF_RUNTIME_SEC = 60;
	private static final int DEF_PAUSETIME_MILLIS = 0;
	
	// spacing of pred deltas in demo data
	public static final long PRED_DELTA_FOR_STOP_MILLIS = 30L * 60L * 1000L;
	
	public static final long PRED_DELTA_BETWEEN_STOPS_MILLIS = 1L * 60L * 1000L;
		
	public DataGridTester() {
		
	}
	
	public static String getGridId(DataGridType gridType, TestType testType) {
		if (testType.equals(TestType.SIMPLE_STRING)) {
			return gridType + "-strings"; 
		}
		return gridType + "-predictions";
	}
	


    // Simply write some key/value string pairs for basic testing
	public static void testStrings(final TestConfig cfg, int numKeys) throws IOException, GridException {
		// Create data grid
		final DataGridType gridType = cfg.getDataGridType();
		final DataGridFactory factory = new DataGridFactory();
		final DataGridInterface dg = factory.create(gridType);
		
		// Init for strings
		DataGridConfig dgCfg = cfg.getDataGridConfig();
		dg.init(dgCfg);
		int size = dg.size();
		System.out.println("Initial map size: " + size);
		if (size < numKeys) {
			System.out.println("Populating map: " + dgCfg.getMapId());
			for (int i=0; i<numKeys; i++) {
				dg.putString("key"+i, "value"+i);
			}
		}
		System.out.println("Final map size: " + dg.size());
		dg.close();
	}

	// See how big map can grow: <StopKey, StopPredictionInterface>
	public static void testPredictionWrites(final TestConfig cfg) throws IOException, GridException {
		// Create data grid
		final DataGridType gridType = cfg.getDataGridType();
		final DataGridFactory factory = new DataGridFactory();
		final DataGridInterface dg = factory.create(gridType);
		
		DataGridConfig dgcfg = cfg.getDataGridConfig();
		dg.init(dgcfg);
		//System.out.println("Initial map size: " + dg.size());
		final int startCustomerId = cfg.getStartCustomerNum();
		final int endCustomerId   = startCustomerId + cfg.getNumCustomers();
		for (int custIdNum=startCustomerId; custIdNum < endCustomerId; custIdNum++) {
			System.out.println("Adding data for customer: " + custIdNum);
			for (int routeId=0; routeId < cfg.getNumRoutes(); routeId++) {
				// Create data
				Map<StopKey, StopPrediction> sps = StopDataFactory.createDummyData(
						custIdNum, 
						routeId, 
						cfg.getNumStopsPerRoute(),
						cfg.getNumPredsPerStop());
				
				// atomically add data
				dg.putAll(sps);
			}
		}
		//System.out.println("Final map size: " + dg.size());
		dg.close();
	}

	public static void testParallelWrites(
			final TestConfig cfg) throws IOException, GridException {
		testParallel(cfg, true);
	}
	
	public static void testParallelReads(
			final TestConfig cfg) throws IOException, GridException {
		testParallel(cfg, false);
	}
	
	public static void testParallel(
			final TestConfig cfg, final boolean writes) throws IOException, GridException {
		
		String readWrite = writes ? "write" : "read";
		final DataGridType gridType = cfg.getDataGridType();
		DateFormat fmt = new SimpleDateFormat("HH:mm:ss.SSSZ");
		final long runtimeMillis = (long)cfg.getRuntimeSec() *1000L;
		
		// Create all the workers
		final int numWorkers = cfg.getNumThreads();
		System.out.println("Creating workers: " + numWorkers);
		List<GridWorker> workers = new ArrayList<GridWorker>(numWorkers);
		
		final int startCustomerId = cfg.getStartCustomerNum();
		final int endCustomerId   = startCustomerId + numWorkers;
		for (int custId=startCustomerId; custId< endCustomerId; custId++) {
			GridWorker worker = null;
			if (writes) {
				worker = new GridWriterWorker(custId, gridType, cfg);
			} else {
				GridReaderWorker	worker2 = new GridReaderWorker(gridType, cfg);
				worker2.init();
				worker2.runPrivate();
			}
			//worker.init();
			//workers.add(worker);
		}

		// Normally just wait a few seconds before starting		
		long delay = 5000L;
		if (cfg.getWaitToStart()) {
			// Wait until the "top of the minute" so multiple tests can start
			// at the same time and workers have a chance to initialize
			final Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, 0);
			cal.add(Calendar.SECOND, 60);
			final String startFmt = fmt.format(cal.getTime());
			System.out.println("Waiting until "+startFmt+" for workers to initialize...");
			delay = cal.getTimeInMillis() - System.currentTimeMillis();
		}
		try { Thread.sleep(delay); } catch (InterruptedException ie) {}

		Date startDate = new Date();
		final String startTimeFmt = fmt.format(startDate);
		System.out.println(startTimeFmt + ": Starting workers...");
		// Run all the workers and wait for them to finish
//		ExecutorService execService = Executors.newFixedThreadPool(numWorkers);
//		try {
//			
//			execService.invokeAll(workers);
//			execService.shutdown();
//			execService.awaitTermination(runtimeMillis + 10000L, TimeUnit.MILLISECONDS);
//		} catch (InterruptedException e) {
//			System.err.println(e);
//		}
		
		
		
	
		
		
		Date endDate = new Date();
		final String endTimeFmt = fmt.format(endDate);
		System.out.println(endTimeFmt + ": Test completed");
		
		// Tally up all the route/stop statistics, close() properly to cleanup
		long numStopsAccessed = 0;
		long numReadOrWriteOps = 0;
		for (GridWorker w : workers) {
			w.close();
			numReadOrWriteOps += w.getNumReadOrWriteOps();
			numStopsAccessed += w.getNumStopsAccessed();			
		}
		long numStopsPerSec = numStopsAccessed / (runtimeMillis/1000L);
		long numReadOrWriteOpsPerSec = numReadOrWriteOps / (runtimeMillis/1000L);
		double msPerOp = (double)runtimeMillis / (double)numReadOrWriteOps;
		System.out.println(String.format(
				"Num stop predictions %ss: %d (%d stops/sec)",
				readWrite, numStopsAccessed, numStopsPerSec));
		System.out.println(String.format("Num route %ss:           %d (%d routes/sec)", readWrite, 
				numReadOrWriteOps, numReadOrWriteOpsPerSec));
		System.out.println(String.format("Avg %s time per route:     %.2fms", readWrite, msPerOp));
	}
	
	public enum TestType {
		SIMPLE_STRING,
		WRITE_ONCE,
		PARALLEL_WRITE,
		PARALLEL_READ,
	}
	
	public static void usage() {
		String s = 
			"Usage: gridtest OPTIONS\n" +
		    "Where OPTIONS:\n" +
		    "[-t testType] - which test to run\n" +
			"  testType: "+ Arrays.asList(TestType.values()) + "\n" +
		    "  DEF: " + TestType.PARALLEL_WRITE + "\n" +
			"[-g gridType] - which Data Grid to use\n" +
		    "  gridType: " + Arrays.asList(DataGridType.values()) + "\n" +
		    "  DEF: " + DataGridType.HAZELCAST + "\n" +
		    "[-sc startCustomer]- # start customer #, DEF: " +DEF_START_CUSTOMER_NUM +"\n" +
		    "[-nc numCustomers] - # customers to simulate, DEF: " +DEF_NUM_CUSTOMERS +"\n" +
		    "[-nr numRoutes]    - # routes to simulate per customer, DEF: " +DEF_NUM_ROUTES_PER_CUST +"\n"+
		    "[-ns numStops]     - # stops to simulate per route, DEF: " +DEF_NUM_STOPS_PER_ROUTE +"\n"+
		    "[-np numPreds]     - # predictions to simulate per stop, DEF: " +DEF_NUM_PREDS_PER_STOP +"\n"+
		    "[-nt numThreads]   - # threads for parallel read/write, DEF: " +DEF_NUM_THREADS +"\n"+
		    "[-time runtimeSec] - runtime for longer tests in seconds, DEF: " + DEF_RUNTIME_SEC + "\n"+
		    "[-pause pauseTimeMillis] - # millises to pause between writing routes, DEF: " + DEF_PAUSETIME_MILLIS +"\n" +
		    "[-discoveryMode mode] - How to discover cluster\n" +
		    "  mode: " + Arrays.asList(DiscoveryMode.values()) + "\n" +
		    "  DEF: " + DiscoveryMode.DEFAULT + "\n" +
		    "[-serializationMode mode] - Serialization config\n" +
		    "  mode: " + Arrays.asList(SerializationMode.values()) + "\n" +
		    "  DEF: " + SerializationMode.DEFAULT + "\n" +
		    "[-awsAccessKey]  - AWS access key\n" +
		    "[-awsSecurityKey]  - AWS security key\n" +
		    "[-noAtomic]        - disable atomic get/put operations\n" +
		    "[-waitToStart]     - wait until the top of the minute to start running tests,\n" +
		    "                     helpful to sync multiple copies of test\n" +
		    "[-vd]              - verifyData for Read operations, check data, DEF: " +false +"\n"+
		    "";
		System.out.println(s);
	}
	
	public static void main(String[] args) throws Exception {
		// Select test type and data grid implementation
		DataGridType gridType = DataGridType.HAZELCAST;
		//TestType testType = TestType.WRITE_ONCE;
		TestType testType = TestType.PARALLEL_READ;
		// Allows testing at AWS
		DiscoveryMode discoveryMode = DiscoveryMode.DEFAULT;
		LoadMode loadMode = LoadMode.CACHE_MODE;
		
		// Testing Different transactional  operations of the cache
		TransactionsMode transactionsMode = TransactionsMode.OPTIMISTIC;
		IsolationMode isolationMode = IsolationMode.REPEATABLE_READ;
		SerializationMode serializationMode = SerializationMode.KRYO;
		// Config params
		int numStringKeys = 10000; // for simple test

		int numCustomers = DEF_NUM_CUSTOMERS;
		int numRoutes = DEF_NUM_ROUTES_PER_CUST;
		int numStopsPerRoute = DEF_NUM_STOPS_PER_ROUTE;
		int numPredsPerStop = DEF_NUM_PREDS_PER_STOP;
		int numThreads = DEF_NUM_THREADS;
		int startCustomerNum = DEF_START_CUSTOMER_NUM;
		
		int runtimeSec = DEF_RUNTIME_SEC;
		// ms to wait between route updates
		int pauseTimeMillis = DEF_PAUSETIME_MILLIS;
		boolean verifyData = false;
		boolean atomicMode = true;
		String awsAccessKey = "unset";
		String awsSecurityKey = "unset";
		boolean waitToStart = false;
		
		// Read command line
		if (args != null && args.length > 0) {
			for (int i=0; i<args.length; i++) {
				String arg = args[i];
				switch (arg) {
				case "-g":
					String grid = args[++i];
					gridType = DataGridType.valueOf(grid);
					break;
				case "-t":
					String test = args[++i];
					testType = TestType.valueOf(test);
					break;
				case "-nc":
					numCustomers = Integer.parseInt(args[++i]);
					break;
				case "-nr":
					numRoutes = Integer.parseInt(args[++i]);
					break;
				case "-ns":
					numStopsPerRoute = Integer.parseInt(args[++i]);
					break;
				case "-np":
					numPredsPerStop = Integer.parseInt(args[++i]);
					break;
				case "-nt":
					numThreads = Integer.parseInt(args[++i]);
					break;
				case "-time":
					runtimeSec = Integer.parseInt(args[++i]);
					break;
				case "-sc":
					startCustomerNum = Integer.parseInt(args[++i]);
					break;
				case "-vd":
					verifyData = true;
					break;
				case "-waitToStart":
					waitToStart = true;
					break;
				case "-discoveryMode":
					String mode = args[++i];
					discoveryMode = DiscoveryMode.valueOf(mode);
					System.out.println(discoveryMode);
					break;
				case "-isolationMode":
					String islnMode = args[++i];					
					isolationMode = IsolationMode.valueOf(islnMode);
					System.out.println(isolationMode);
					break;
				case "-transactionsMode":
					String prtnMode = args[++i];
					transactionsMode = TransactionsMode.valueOf(prtnMode);
					System.out.println(transactionsMode);
					break;
				case "-serializationMode":
					String serMode = args[++i];
					serializationMode = SerializationMode.valueOf(serMode);
					break;
				case "-awsAccessKey":
					awsAccessKey = args[++i];
					break;
				case "-awsSecurityKey":
					awsSecurityKey = args[++i];
					break;
				case "-loadMode":
					String ldMode = args[++i];
					loadMode = LoadMode.valueOf(ldMode);
					System.out.println(loadMode);
					break;
				case "-noAtomic":
					atomicMode = false;
					break;
				case "-pause":
					pauseTimeMillis = Integer.parseInt(args[++i]);
					break;
				case "-h":
					usage();
					System.exit(0);
				default:
					throw new IllegalArgumentException("Unknown option: " + arg);
				}
			}
		}
		
		// Build DataGridConfig
		String gridId = DataGridTester.getGridId(gridType, testType);
		MapType mapType = testType.equals(TestType.SIMPLE_STRING) ? MapType.STRINGS : MapType.PREDICTIONS;
		DataGridConfig dgCfg = new DataGridConfig(gridId, mapType, atomicMode, 
				discoveryMode, transactionsMode, isolationMode, serializationMode,
				loadMode, awsAccessKey, awsSecurityKey);
	
		TestConfig cfg = new TestConfig(
				gridType,
				numCustomers,
				numRoutes, 
				numStopsPerRoute, 
				numPredsPerStop, 
				numThreads,
				runtimeSec, 
				pauseTimeMillis,
				verifyData,
				startCustomerNum,
				waitToStart,
				dgCfg);

		System.out.println("Running test: " + testType);
		System.out.println("Config:       \n" + cfg);
		
		switch (testType) {
		case SIMPLE_STRING:
			testStrings(cfg, numStringKeys);
			break;
		case WRITE_ONCE:
			testPredictionWrites(cfg);
			break;
		case PARALLEL_WRITE:
			testParallelWrites(cfg);
			break;
		case PARALLEL_READ:
			testParallelReads(cfg);
			break;
		default:
			System.out.println("Invalid test");
		}
		
		System.out.println("Done with test: " + testType + " for: " + gridType);
	}
}