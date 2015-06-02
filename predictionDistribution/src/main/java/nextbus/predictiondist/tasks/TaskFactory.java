package nextbus.predictiondist.tasks;

import java.io.IOException;
import java.io.Serializable;

public class TaskFactory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -279997833873430652L;

	public TaskFactory() {

	}

	public GenericTaskInterface create(IdeaType type, GenericTaskConfig genericTaskConfig)
			throws IOException {
		switch (type) {
		
		case TEMP_MAP:
			return new FetchBulkStopPredictionsForCustomer_TempMap();
		case DIST_QUERY_STOP_KEY:
			return new FetchBulkStopPredictionsForCustomer_TempMap();
		case DIST_QUERY_STOP_PRED:
			return new FetchBulkStopPredictionsForCustomer_TempMap();
		case LOOKUP_MULTI_MAPS:
			return new FetchBulkStopPredictionsForCustomer_LOOKUP_MultiMap();
		case POPULATE_MULTI_MAPS:
			return new Populate_CustomerRoutesMultiMaps();

		default:
			throw new IllegalArgumentException();
		}
	}

}
