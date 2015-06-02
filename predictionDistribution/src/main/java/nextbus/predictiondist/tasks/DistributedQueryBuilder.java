package nextbus.predictiondist.tasks;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nextbus.predictiondist.data.StopKey;
import nextbus.predictiondist.data.StopPrediction;

public class DistributedQueryBuilder {
	
	public static String buildQuery(String customerId, Set<String> routeIds,IdeaType ideaType ) {
    String projectfieldName = null ;
    String routefieldName = null;
   
		if(ideaType.equals(IdeaType.DIST_QUERY_STOP_KEY)){
			projectfieldName = "projectId";
			routefieldName = "routeId";
		}else if (ideaType.equals(IdeaType.DIST_QUERY_STOP_PRED)) {
			projectfieldName = "_projectId";
			routefieldName = "_routeTag";
		}
		StringBuilder query = new StringBuilder("");
		if (customerId != null) {
			query.append(" " + projectfieldName+    " =  " + customerId);
		}

		if (routeIds != null && routeIds.toArray().length > 0) {
			String[] routeIdsArray = routeIds.toArray(new String[routeIds
					.size()]);
			if (customerId == null) {
				query.append(" " +routefieldName+  " in (");
			} else {
				query.append(" and " +routefieldName +  " in (");
			}
			for (int i = 0; i < routeIdsArray.length; i++) {
				if (i > 0) {
					query.append(" , ");
				}
				query.append(routeIdsArray[i]);
			}
			query.append(")");
		}
		query.append("");

		// Final build query is
		System.out.println("  #### Final build query is  ####  " + query);
		return query.toString();
	}
	
	
	public static Map<StopKey, StopPrediction> convertToStopPredictionMap(Collection<?> collection,
			String custId) {
		final Map<StopKey, StopPrediction> sps = new HashMap<StopKey, StopPrediction>();
		for (Object stopPrediction :  collection) {
			final StopKey key = new StopKey(custId, ((StopPrediction) stopPrediction).getRouteTag(), ((StopPrediction) stopPrediction).getStopTag());
			sps.put(key, (StopPrediction) stopPrediction);
		}
		return sps;
	}

}
