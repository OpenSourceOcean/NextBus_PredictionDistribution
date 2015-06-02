/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist.data;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

/**
 * Factory
 * @author noky
 */
public class StopPredictionFactory implements DataSerializableFactory {

	public static final int FACTORY_ID = 1;

	public static final int STOP_PREDICTION_TYPE = 1;
	public static final int STOP_PREDICTION_INFO_TYPE = 2;

	@Override
	public IdentifiedDataSerializable create(int typeId) {
		switch (typeId) {
	    //case STOP_PREDICTION_TYPE: return new StopPrediction();
	    //case STOP_PREDICTION_INFO_TYPE: return new StopPredictionInfo();
	    default: return null;
		}
	}
}
