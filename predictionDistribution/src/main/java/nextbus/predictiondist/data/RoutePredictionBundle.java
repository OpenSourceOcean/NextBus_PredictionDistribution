/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist.data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Structure for storing all StopPredictions for a Route
 * @author noky
 */
public class RoutePredictionBundle implements Serializable {
	static final long serialVersionUID = 4228217859802708126L;

	private final long creationTimestamp;
	private final String routeTag;
	private final LinkedHashMap<String, StopPrediction> stopPredictions;
	
	/**
	 * Constructor
	 * @param creationTimestamp
	 * @param stopPredictions
	 */
	public RoutePredictionBundle(
			String routeTag,
			long creationTimestamp,
		    LinkedHashMap<String, StopPrediction> stopPredictions) {
		this.routeTag = routeTag;
		this.creationTimestamp = creationTimestamp;
		this.stopPredictions = stopPredictions;
	}
	
	public StopPrediction getStopPrediction(String stopTag) {
		return stopPredictions.get(stopTag);
	}
	
	public Map<String, StopPrediction> getStopPredictions() {
		return stopPredictions;
	}
	
	public long getCreationTimestamp() {
		return creationTimestamp;
	}
	
	public int getSize() {
		return stopPredictions.size();
	}
	
	public String getRouteTag() {
		return routeTag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (creationTimestamp ^ (creationTimestamp >>> 32));
		result = prime * result
				+ ((routeTag == null) ? 0 : routeTag.hashCode());
		result = prime * result
				+ ((stopPredictions == null) ? 0 : stopPredictions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoutePredictionBundle other = (RoutePredictionBundle) obj;
		if (creationTimestamp != other.creationTimestamp)
			return false;
		if (routeTag == null) {
			if (other.routeTag != null)
				return false;
		} else if (!routeTag.equals(other.routeTag))
			return false;
		if (stopPredictions == null) {
			if (other.stopPredictions != null)
				return false;
		} else if (!stopPredictions.equals(other.stopPredictions))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "creationTimestamp=" + creationTimestamp + 
				", routeTag=" + routeTag +
				", stopPredictions=" + stopPredictions;
	}
}
