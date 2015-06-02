/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist;

public final class TestConfig {
	final DataGridType dataGridType;
	final int numCustomers;
	final int numRoutes;
	final int numStopsPerRoute;
	final int numPredsPerStop;
	final int numThreads;
	final int runtimeSec;
	final int pauseTimeMillis;
	final int startCustomerNum;
	final boolean verifyData;
	final DataGridConfig dataGridConfig;
	final boolean waitToStart;
	
	/**
	 * @param numCustomers
	 * @param numRoutes
	 * @param numStopsPerRoute
	 * @param numPredsPerStop
	 * @param optimizedDataStruct
	 */
	public TestConfig(DataGridType gridType,
			int numCustomers, int numRoutes,
			int numStopsPerRoute, int numPredsPerStop, 
			int numThreads,
			int runtimeSec,
			int pauseTimeMillis,
			boolean verifyData,
			int startCustomerNum,
			boolean waitToStart,
			DataGridConfig dgCfg) {			
		this.dataGridType = gridType;
		this.numCustomers = numCustomers;
		this.numRoutes = numRoutes;
		this.numStopsPerRoute = numStopsPerRoute;
		this.numPredsPerStop = numPredsPerStop;
		this.numThreads = numThreads;
		this.runtimeSec = runtimeSec;
		this.pauseTimeMillis = pauseTimeMillis;
		this.verifyData = verifyData;
		this.startCustomerNum = startCustomerNum;
		this.dataGridConfig = dgCfg;
		this.waitToStart = waitToStart;
	}
	public DataGridType getDataGridType() {
		return dataGridType;
	}
	public int getNumCustomers() {
		return numCustomers;
	}
	public int getNumRoutes() {
		return numRoutes;
	}
	public int getNumStopsPerRoute() {
		return numStopsPerRoute;
	}
	public int getNumPredsPerStop() {
		return numPredsPerStop;
	}
	public int getRuntimeSec() {
		return this.runtimeSec;
	}
	public int getPauseTimeMillis() {
		return this.pauseTimeMillis;
	}
	public int getNumThreads() {
		return this.numThreads;
	}
	public boolean getVerifyData() {
		return verifyData;
	}
	public int getStartCustomerNum() {
		return this.startCustomerNum;
	}
	
	public DataGridConfig getDataGridConfig() {
		return dataGridConfig;
	}
	public boolean getWaitToStart() {
		return waitToStart;
	}

	@Override
	public String toString() {
		return 
				"dataGridType=" + dataGridType 
				+ "\nnumCustomers=" + numCustomers 
				+ "\nstartCustomerNum=" + startCustomerNum 
				+ "\nnumRoutes=" + numRoutes
				+ "\nnumStopsPerRoute=" + numStopsPerRoute
				+ "\nnumPredsPerStop=" + numPredsPerStop 
				+ "\nnumThreads=" + numThreads 
				+ "\nruntimeSec="+runtimeSec
				+ "\npauseTimeMillis="+pauseTimeMillis 
				+ "\nverifyData=" + verifyData
				+ "\ndataGridConfig=" + dataGridConfig 
				+ "\nwaitToStart=" + waitToStart;
	}
}