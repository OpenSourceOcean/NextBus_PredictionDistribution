package nextbus.predictiondist.data;

import java.io.Serializable;

import com.hazelcast.core.PartitionAware;

public class CustomerAndRouteKey implements Serializable,
		PartitionAware<String>, Comparable<CustomerAndRouteKey> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6917264641799084623L;
	private final String projectId;
	private final String routeId;

	/**
	 * Default constructor required for Spring
	 * 
	 */
	public CustomerAndRouteKey() {
		this(null, null);
	}

	/**
	 * @param projectId
	 * @param routeId
	 * 
	 */

	public CustomerAndRouteKey(String projectId, String routeId) {
		this.projectId = projectId;
		this.routeId = routeId;

	}

	/**
	 * Generate unique key (no commas)
	 * 
	 * @return
	 */
	public String genKey() {
		return projectId + "---" + routeId;
	}

	public String getProjectId() {
		return projectId;
	}

	public String getRouteId() {
		return routeId;
	}

	/**
	 * Hazelcast optimization to cluster data by customer (so all data for one
	 * customer is on one node)
	 */
	public String getPartitionKey() {
		return projectId;
	}

	@Override
	public String toString() {
		return "projectId=" + projectId + ", routeId=" + routeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((projectId == null) ? 0 : projectId.hashCode());
		result = prime * result + ((routeId == null) ? 0 : routeId.hashCode());

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
		CustomerAndRouteKey other = (CustomerAndRouteKey) obj;
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

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CustomerAndRouteKey o) {
		String key = this.genKey();
		String k2 = o.genKey();
		return key.compareTo(k2);
	}

}
