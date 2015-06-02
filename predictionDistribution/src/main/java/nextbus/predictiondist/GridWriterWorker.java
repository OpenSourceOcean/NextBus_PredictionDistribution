/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import nextbus.predictiondist.data.StopDataFactory;
import nextbus.predictiondist.data.StopKey;
import nextbus.predictiondist.data.StopPrediction;

public final class GridWriterWorker implements GridWorker {
	private final int custId;
	private final TestConfig cfg;
	private final DataGridInterface grid;
	private final AtomicLong stopPredictionCounter;
	private final AtomicInteger currRouteId;
	private final AtomicLong numOps;
	
	public GridWriterWorker(int customerId, DataGridType gridType,
			TestConfig cfg) throws IOException {
		DataGridFactory factory = new DataGridFactory();
		this.custId = customerId;
		this.cfg = cfg;
		this.grid = factory.create(gridType);
		this.stopPredictionCounter = new AtomicLong(0);
		this.currRouteId = new AtomicInteger(0);
		this.numOps = new AtomicLong(0);	
	}
	
	public void init() throws GridException {
		grid.init(cfg.getDataGridConfig());
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
	
	@SuppressWarnings("finally")
	@Override
	public Void call() {
		try {
			runPrivate();
		} finally {
			grid.close();
			return null;
		}
	}
	
	private void runPrivate() {
		// Determine how long to run test
		final long runtimeMillis = (long)cfg.getRuntimeSec() *1000L;
		final long startTimeEpoch = System.currentTimeMillis();
		final long endTimeEpoch = startTimeEpoch + runtimeMillis;

		while ((System.currentTimeMillis()) < endTimeEpoch) {
			int currRouteIdInt = currRouteId.getAndIncrement() %
					cfg.getNumRoutes();
			// Create data
			Map<StopKey, StopPrediction> sps = StopDataFactory.createDummyData(
					custId,
					currRouteIdInt,
					cfg.getNumStopsPerRoute(),
					cfg.getNumPredsPerStop());
			// Atomically write all stop data to grid
			grid.putAll(sps);
			// Log how many stops written to grid
			stopPredictionCounter.addAndGet(cfg.getNumStopsPerRoute());
			numOps.addAndGet(1);
			// Pause if configured
			if (cfg.getPauseTimeMillis() > 0) {
				try {					
					Thread.sleep(cfg.getPauseTimeMillis());
				} catch (InterruptedException e) {
					//
				}
			}
		}
	}	
}