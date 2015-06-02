/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist.data;

import java.io.Serializable;

import com.hazelcast.core.PartitionAware;


/**
 * Key for Key-Value lookups.
 * Implements PartitionAware as Hazelcase optimization.
 * @author noky
 * Onkarr added @AffinityKeyMapped annotation for Ignite
 */
public class StopKey implements Serializable, PartitionAware<String>, Comparable<StopKey> {
	private static final long serialVersionUID = 6312395450304283247L;

	
	private final String projectId;
	private final String routeId;
	private final String stopId;
	
	/** Default constructor required for Spring 
	 * 
	 */
	public StopKey(){
		this(null,null,null);
	}
	
	/**
	 * @param projectId
	 * @param routeId
	 * @param stopId
	 */
	
	
	public StopKey(String projectId, String routeId, String stopId) {
		this.projectId = projectId;
		this.routeId = routeId;
		this.stopId = stopId;
	}
	
	public StopKey(String projectId) {
		String[] array = projectId.split("---", -1);
		this.projectId = array[0];
		this.routeId = array[1];
		this.stopId = array[2];
	}
	
	
	public String getProjectId() {
		return projectId;
	}
	public String getRouteId() {
		return routeId;
	}
	public String getStopId() {
//		Object personKey1 = new GridCacheAffinityKey("myPersonId1", "myCompanyId");
		return stopId;
	}
	
	/**
	 * Hazelcast optimization to cluster data by customer
	 * (so all data for one customer is on one node)
	 */
	@Override
	public String getPartitionKey() {
		return projectId;
	}
	
	/**
	 * Generate unique key (no commas)
	 * @return
	 */
	public String genKey() {
		return projectId + "---" + routeId + "---" + stopId;
	}
	
	@Override
	public String toString() {
		return "projectId=" + projectId + ", routeId=" + routeId + ", stopId="
				+ stopId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((projectId == null) ? 0 : projectId.hashCode());
		result = prime * result + ((routeId == null) ? 0 : routeId.hashCode());
		result = prime * result + ((stopId == null) ? 0 : stopId.hashCode());
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
		StopKey other = (StopKey) obj;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		if (routeId == null) {
			if (other.routeId != null)
				return false;
		} else if (!routeId.equals(other.routeId))
			return false;
		if (stopId == null) {
			if (other.stopId != null)
				return false;
		} else if (!stopId.equals(other.stopId))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(StopKey o) {
		String key = this.genKey();
		String k2 = o.genKey();
		return key.compareTo(k2);
	}
}