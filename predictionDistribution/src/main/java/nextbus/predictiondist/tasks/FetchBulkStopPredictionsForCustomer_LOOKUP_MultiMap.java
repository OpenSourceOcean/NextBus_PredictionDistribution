package nextbus.predictiondist.tasks;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nextbus.predictiondist.data.CustomerAndRouteKey;
import nextbus.predictiondist.data.StopKey;
import nextbus.predictiondist.data.StopPrediction;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class FetchBulkStopPredictionsForCustomer_LOOKUP_MultiMap implements
		GenericTaskInterface,DataSerializable {
	GenericTaskConfig genericTaskConfig = null;

	@Override
	public Map<StopKey, StopPrediction> runTheLogic(
			GenericTaskConfig genericTaskConfig, HazelcastInstance hazelcastInstance) {

		this.genericTaskConfig = genericTaskConfig;
		// Get the multimap routesPerStop

		MultiMap<String, CustomerAndRouteKey> routesPerStopMultiMap = hazelcastInstance.getMultiMap("routesPerStop");

		Set<CustomerAndRouteKey> finalCustemerKeySet = new HashSet<CustomerAndRouteKey>();
		Set<String> routes = genericTaskConfig.getRouteIds();
		String custStrId = genericTaskConfig.getTaskKey().getProjectId();

		// Check if routes information is given if given
		if (genericTaskConfig.getRouteIds() != null) {
			for (String route : routes) {
				CustomerAndRouteKey customerAndRouteKey = new CustomerAndRouteKey(
						custStrId, route);
				finalCustemerKeySet.add(customerAndRouteKey);

			}
		} else {
			finalCustemerKeySet = (Set<CustomerAndRouteKey>) routesPerStopMultiMap
					.get(custStrId);

		}
//		System.out.println("FetchBulkStopPredictionsForCustomer_MultiMaps:"
//				+ getThePredictions(finalCustemerKeySet).toString());
		return getThePredictions(finalCustemerKeySet,hazelcastInstance);

	}

	/**
	 * @param finalCustemerKeySet
	 * @return Map<StopKey, StopPrediction>
	 */
	private Map<StopKey, StopPrediction> getThePredictions(
			Set<CustomerAndRouteKey> finalCustemerKeySet, HazelcastInstance hazelcastInstance) {
		IMap<StopKey, StopPrediction> map = hazelcastInstance.getMap(genericTaskConfig.getMapId());
		MultiMap<CustomerAndRouteKey, StopKey> stopsPerRouteMultiMap = hazelcastInstance.getMultiMap("stopsPerRoute");
		Set<StopKey> finalStopKeySet = new HashSet<StopKey>();
		Map<StopKey, StopPrediction> predictions = new HashMap<StopKey, StopPrediction>();
		for (CustomerAndRouteKey customKey : finalCustemerKeySet) {
			finalStopKeySet.addAll(stopsPerRouteMultiMap.get(customKey));
		}
		predictions = (Map<StopKey, StopPrediction>) map
				.getAll(finalStopKeySet);
		
		return predictions;
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeObject(genericTaskConfig);
		
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		this.genericTaskConfig = (GenericTaskConfig) in.readObject();
		
	}

}
