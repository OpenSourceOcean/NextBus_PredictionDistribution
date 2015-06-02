package nextbus.predictiondist.tasks;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

import nextbus.predictiondist.data.StopKey;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class GenericTaskConfig implements DataSerializable {

	/**
	 * 
	 */
	// private static final long serialVersionUID = 1781720557892959970L;
	private StopKey taskKey;
	private Set<String> routeIds;
	private String mapId;
	private IdeaType ideaType;

	// private transient HazelcastInstance hazelcastInstance;

	public GenericTaskConfig(StopKey taskKey, Set<String> routeIds,
			String mapId, IdeaType ideaType) {
		this.taskKey = taskKey;
		this.routeIds = routeIds;
		this.mapId = mapId;
		this.ideaType = ideaType;
		// this.hazelcastInstance = hazelcastInstance;

	}

	// public HazelcastInstance getHazelcastInstance() {
	// return hazelcastInstance;
	// }
	//
	// public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
	// this.hazelcastInstance = hazelcastInstance;
	// }

	public StopKey getTaskKey() {
		return taskKey;
	}

	public void setTaskKey(StopKey taskKey) {
		this.taskKey = taskKey;
	}

	public Set<String> getRouteIds() {
		return routeIds;
	}

	public void setRouteIds(Set<String> routeIds) {
		this.routeIds = routeIds;
	}

	public String getMapId() {
		return mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public IdeaType getIdeaType() {
		return ideaType;
	}

	public void setIdeaType(IdeaType ideaType) {
		this.ideaType = ideaType;
	}

	public GenericTaskConfig() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ideaType == null) ? 0 : ideaType.hashCode());
		result = prime * result + ((mapId == null) ? 0 : mapId.hashCode());
		result = prime * result
				+ ((routeIds == null) ? 0 : routeIds.hashCode());
		result = prime * result + ((taskKey == null) ? 0 : taskKey.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GenericTaskConfig)) {
			return false;
		}
		GenericTaskConfig other = (GenericTaskConfig) obj;
		if (ideaType != other.ideaType) {
			return false;
		}
		if (mapId == null) {
			if (other.mapId != null) {
				return false;
			}
		} else if (!mapId.equals(other.mapId)) {
			return false;
		}
		if (routeIds == null) {
			if (other.routeIds != null) {
				return false;
			}
		} else if (!routeIds.equals(other.routeIds)) {
			return false;
		}
		if (taskKey == null) {
			if (other.taskKey != null) {
				return false;
			}
		} else if (!taskKey.equals(other.taskKey)) {
			return false;
		}
		return true;
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeObject(ideaType);
		out.writeObject(routeIds);
		out.writeObject(taskKey);
		out.writeUTF(mapId);

	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		ideaType = in.readObject();
		routeIds = in.readObject();
		taskKey = in.readObject();
		mapId = in.readUTF();
	}

//	@Override
//	public int getFactoryId() {
//
//		return GenericTaskDataSerializer.GENERIC_TASK_TYPE_FACTORY_ID;
//	}
//
//	@Override
//	public int getId() {
//		return GenericTaskDataSerializer.GENERIC_TASK_TYPE_ID;
//
//	}

}
