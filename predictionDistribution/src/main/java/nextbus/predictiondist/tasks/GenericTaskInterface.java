package nextbus.predictiondist.tasks;

import java.util.Map;

import com.hazelcast.core.HazelcastInstance;

import nextbus.predictiondist.data.StopKey;
import nextbus.predictiondist.data.StopPrediction;

public interface GenericTaskInterface {

	public Map<StopKey, StopPrediction> runTheLogic(
			GenericTaskConfig genericTaskConfig, HazelcastInstance hazelcastInstance);

}
