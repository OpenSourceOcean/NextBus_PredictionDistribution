package nextbus.predictiondist.tasks;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;

import nextbus.predictiondist.data.StopKey;
import nextbus.predictiondist.data.StopPrediction;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class GenericTask implements Callable<Map<StopKey, StopPrediction>>,
DataSerializable, HazelcastInstanceAware {
	/**
	 * 
	 */
	private transient HazelcastInstance hazelcastInstance;
	// private static final long serialVersionUID = -171656703006762050L;
	GenericTaskConfig genericTaskConfig;

	public GenericTask(GenericTaskConfig genericTaskConfig) {
		this.genericTaskConfig = genericTaskConfig;
	}

	@Override
	public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;

	}

	@Override
	public Map<StopKey, StopPrediction> call() throws Exception {
		TaskFactory taskFactory = new TaskFactory();
		// genericTaskConfig.setHazelcastInstance(hazelcastInstance);
		GenericTaskInterface theTask = taskFactory.create(
				genericTaskConfig.getIdeaType(), genericTaskConfig);
		return theTask.runTheLogic(genericTaskConfig, hazelcastInstance);
	}

	public GenericTask() {

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
		result = prime
				* result
				+ ((genericTaskConfig == null) ? 0 : genericTaskConfig
						.hashCode());
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
		if (!(obj instanceof GenericTask)) {
			return false;
		}
		GenericTask other = (GenericTask) obj;
		if (genericTaskConfig == null) {
			if (other.genericTaskConfig != null) {
				return false;
			}
		} else if (!genericTaskConfig.equals(other.genericTaskConfig)) {
			return false;
		}
		return true;
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		// out.writeObject(hazelcastInstance);
		out.writeObject(genericTaskConfig);

	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		// this.hazelcastInstance = (HazelcastInstance) in.readObject();
		this.genericTaskConfig = (GenericTaskConfig) in.readObject();

	}

//	//@Override
//	public int getFactoryId() {
//		return GenericTaskDataSerializer.GENERIC_TASK_TYPE_FACTORY_ID;
//	}
//
//	//@Override
//	public int getId() {
//		return GenericTaskDataSerializer.GENERIC_TASK_TYPE_ID;
//	}

}
