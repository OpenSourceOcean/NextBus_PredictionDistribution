/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist;

import java.util.Map;
import java.util.Set;

import nextbus.predictiondist.data.StopKey;
import nextbus.predictiondist.data.StopPrediction;

/**
 * Common interface for data grid
 * @author noky
 *
 */
public interface DataGridInterface {
	public DataGridType getType();
	
	public void init(DataGridConfig cfg) throws GridException;
		
	public void putAll(Map<StopKey, StopPrediction> map);
	
	public Map<StopKey, StopPrediction> getAll(Set<StopKey> keys);
	
	public void putString(String key, String value);
	
	public int size();
	
	public void close();
}
