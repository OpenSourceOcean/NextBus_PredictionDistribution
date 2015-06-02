/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist.data;



/**
 * StreamSerializer for StopPredictionInfo
 * @author noky
 */
public class KryoSerializerStopPredictionInfo extends KryoSerializerBase<StopPredictionInfo> {
	/**
	 * Default constructor
	 */
	public KryoSerializerStopPredictionInfo(boolean compress) {
		super(compress);
	}

	

	@Override
	public KryoRegisteredClass getRegisteredClass() {
		return KryoRegisteredClass.STOP_PREDICTION_INFO;
	}
}