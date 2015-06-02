/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist.data;

/**
 * StreamSerializer for StopPrediction
 * @author noky
 */
public class KryoSerializerStopPrediction extends KryoSerializerBase<StopPrediction> {
	/**
	 * @param compress
	 */
	public KryoSerializerStopPrediction(boolean compress) {
		super(compress);
	}

	@Override
	public KryoRegisteredClass getRegisteredClass() {
		return KryoRegisteredClass.STOP_PREDICTION;
	}
}