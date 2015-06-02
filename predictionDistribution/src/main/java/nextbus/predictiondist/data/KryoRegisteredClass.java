/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist.data;

/**
 * List of all classes to register with Kryo serializer
 * @author noky
 */
public enum KryoRegisteredClass {
	// Each enum entry should have the class to register and a unique typeId
	STOP_PREDICTION(nextbus.predictiondist.data.StopPrediction.class, 2),
	STOP_PREDICTION_INFO(nextbus.predictiondist.data.StopPredictionInfo.class, 3),
	STOP_KEY(nextbus.predictiondist.data.StopKey.class, 4),
	CUSTOMER_ROUTE_KEY(nextbus.predictiondist.data.CustomerAndRouteKey.class, 5);
	
	private final Class<?> clazz;
	private final int typeId;

	KryoRegisteredClass(Class<?> clazz, int typeId) {
		this.typeId = typeId;
		this.clazz = clazz;
	}
	
	/**
	 * Get the class to register with the serializer
	 * @return
	 */
	public Class<?> getRegisteredClass() {
		return clazz;
	}
	
	/**
	 * Get unique type id for this class, used by serializer
	 * @return
	 */
	public int getTypeId() {
		return typeId;
	}
}