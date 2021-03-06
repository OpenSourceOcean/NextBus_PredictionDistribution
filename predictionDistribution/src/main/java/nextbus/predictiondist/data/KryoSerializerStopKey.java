/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist.data;



/**
 * StreamSerializer for StopKey
 * @author Onkarr
 */
public class KryoSerializerStopKey extends KryoSerializerBase<StopKey> {
	/**
	 * Default constructor
	 */
	public KryoSerializerStopKey(boolean compress) {
		super(compress);
	}

	

	@Override
	public KryoRegisteredClass getRegisteredClass() {
		return KryoRegisteredClass.STOP_KEY;
	}
}