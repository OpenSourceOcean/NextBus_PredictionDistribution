/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;

import nextbus.predictiondist.data.StopDataFactory;
import nextbus.predictiondist.data.StopKey;
import nextbus.predictiondist.data.StopPredictionInfo;
import nextbus.predictiondist.data.StopPrediction;
import nextbus.predictondist.utils.SimpleTimeLimiterTest.Sample;
import nextbus.predictondist.utils.SimpleTimeLimiterTest.SampleImpl;

/**
 * Class to allow one thread to read from grid. Randomly selects customer/route
 * to read all stops
 * 
 * @author noky
 */
public final class GridReaderWorker implements GridWorker {
	private final TestConfig cfg;
	private final DataGridInterface grid;
	private final AtomicLong stopPredictionCounter;
	private final AtomicLong numOps;
	private static final ExecutorService executor = Executors
			.newFixedThreadPool(1);
	private TimeLimiter service;

	public GridReaderWorker(DataGridType gridType, TestConfig cfg)
			throws IOException {
		DataGridFactory factory = new DataGridFactory();
		this.cfg = cfg;
		this.grid = factory.create(gridType);
		this.stopPredictionCounter = new AtomicLong(0);
		this.numOps = new AtomicLong(0);
		service = new SimpleTimeLimiter(executor);
	}

	public void init() throws GridException {
		DataGridConfig dgcfg = cfg.getDataGridConfig();
		grid.init(dgcfg);
	}

	public void close() {
		grid.close();
	}

	@Override
	public long getNumReadOrWriteOps() {
		return numOps.get();
	}

	@Override
	public long getNumStopsAccessed() {
		return this.stopPredictionCounter.get();
	}

	@Override
	public Void call() {
		try {
			runPrivate();
		} finally {
			grid.close();
			return null;
		}
	}

	public void runPrivate() throws GridException {

		Random randCust = new Random();
		Random randRoute = new Random();

		// Determine how long to run test
		final long runtimeMillis = (long) cfg.getRuntimeSec() * 1000L;
		final long startEpochTime = System.currentTimeMillis();
		final long endEpochTime = startEpochTime + runtimeMillis;

		while ((System.currentTimeMillis()) < endEpochTime) {
			// select random customer and route
			int currRouteId = randRoute.nextInt(cfg.getNumRoutes());
			int currCustId = randCust.nextInt(cfg.getNumCustomers());

			final String customerId = StopDataFactory.getCustomerId(currCustId);
			final String routeId = StopDataFactory.getRouteId(currRouteId);

			Set<StopKey> keys = new HashSet<StopKey>();
			for (int i = 0; i < cfg.getNumStopsPerRoute(); i++) {
				final String stopId = StopDataFactory.getStopId(i);
				StopKey key = new StopKey(customerId, routeId, stopId);
				keys.add(key);
			}

			// Atomically read data from grid
			Map<StopKey, StopPrediction> preds = null;
			// SampleImpl target = new SampleImpl();
			DataGridInterface proxy = service.newProxy(grid,
					DataGridInterface.class, endEpochTime, TimeUnit.MILLISECONDS);
			try {
				preds = proxy.getAll(keys);

			} catch (UncheckedTimeoutException expected) {
				throw new GridException(" Get operation timed out ");
			}

			if (cfg.getVerifyData()) {
				testConsistency(preds);
			}
			int numPreds = (preds == null) ? 0 : preds.size();
			// Log how many stops read from grid
			stopPredictionCounter.addAndGet(numPreds);
			// Log how long it took to write all stops in route
			numOps.addAndGet(1);
			// Pause if configured
			int pauseTimeMillis = cfg.getPauseTimeMillis();
			if (pauseTimeMillis > 0) {
				try {
					Thread.sleep(pauseTimeMillis);
				} catch (InterruptedException e) {
					//
				}
			}
		}
	}

	// Test to make sure all stops are consistent.
	// Dummy data is created in a specific way and all stop predictions
	// have consistent spacing (in time).
	// This tests to make sure that the reads are all consistent
	// and that route updates are done in an atomic fashion.
	public void testConsistency(Map<StopKey, StopPrediction> map) {
		long veryFirstPredTime = 0;

		int num = -1;
		TreeSet<StopKey> sortedKeys = new TreeSet<StopKey>(map.keySet());
		for (StopKey stopKey : sortedKeys) {
			num++;
			StopPrediction pred = map.get(stopKey);
			String stopTag = pred.getStopTag();
			StopPredictionInfo[] infos = pred.getPredictions();

			long predTime = infos[0].getPredictionTime();
			if (veryFirstPredTime == 0) {
				veryFirstPredTime = predTime;
				continue;
			}
			long expectedDelta = (long) num
					* StopDataFactory.PRED_DELTA_BETWEEN_STOPS_MILLIS;
			long delta = predTime - veryFirstPredTime;
			if (delta != expectedDelta) {
				StringBuilder b = new StringBuilder();
				for (StopKey k : sortedKeys) {
					StopPrediction spi = map.get(k);
					b.append(k.genKey() + " -> " + spi + "\n");
				}
				String err = "Inconsistent prediction data, stop=" + num + " ("
						+ stopTag + ") " + " delta=" + delta + "\n"
						+ b.toString();
				System.err.println(err);
				throw new java.lang.AssertionError(err);
			}
		}
	}
}