package nextbus.predictiondist.tasks;

import java.util.Map;
import java.util.Set;
import java.util.List;

import nextbus.predictiondist.data.StopKey;
import nextbus.predictiondist.data.StopPrediction;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;

public class FetchBulkStopPredictionsForCustomer_TempMap implements
		GenericTaskInterface {

	@SuppressWarnings("unchecked")
	@Override
	public Map<StopKey, StopPrediction> runTheLogic(
			GenericTaskConfig genericTaskConfig,HazelcastInstance hazelcastInstance) {
		Map<StopKey, StopPrediction> predictions;
		IMap<StopKey, ?> map = hazelcastInstance.getMap(
				genericTaskConfig.getMapId());

		Set<StopKey> localKeys = map.localKeySet();

		// This is very lame solution should not be used at all
		if (genericTaskConfig.getIdeaType()
				.equals(IdeaType.DIST_QUERY_STOP_KEY)) {
			IMap<StopKey, StopKey> tempMap = hazelcastInstance.getMap("map");
			for (StopKey key : localKeys) {
				tempMap.put(key, key);

			}

			// Query on StopKeys
			Set<StopKey> returnedKeys = (Set<StopKey>) tempMap
					.values(new SqlPredicate(DistributedQueryBuilder
							.buildQuery(genericTaskConfig.getTaskKey()
									.getProjectId(), genericTaskConfig
									.getRouteIds(), genericTaskConfig
									.getIdeaType().DIST_QUERY_STOP_KEY)));

			predictions = (Map<StopKey, StopPrediction>) map
					.getAll(returnedKeys);
			tempMap.evictAll();
			
		} else if (genericTaskConfig.getIdeaType().equals(
				IdeaType.DIST_QUERY_STOP_PRED)) {
			
			// This is query on stopPredictions
			predictions = DistributedQueryBuilder.convertToStopPredictionMap(
					map.values(new SqlPredicate(DistributedQueryBuilder
							.buildQuery(genericTaskConfig.getTaskKey()
									.getProjectId(), genericTaskConfig
									.getRouteIds(), genericTaskConfig
									.getIdeaType().DIST_QUERY_STOP_PRED))),
					genericTaskConfig.getTaskKey().getProjectId());
		} else {
			predictions = null;
		}

		return predictions;

	}

}
