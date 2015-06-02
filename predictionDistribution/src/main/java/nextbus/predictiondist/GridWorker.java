/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist;

import java.util.concurrent.Callable;

/**
 * Grid Reader or Writer
 * @author noky
 *
 */
public interface GridWorker extends Callable<Void> {
	public void init() throws GridException;

	public void close();
	
	public long getNumReadOrWriteOps();
	
	public long getNumStopsAccessed();
}
