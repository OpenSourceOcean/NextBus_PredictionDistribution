/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist.data;



/**
 * StreamSerializer for CustomerAndRouteKey
 * @author Onkarr
 */
public class KryoSerializerCustomerAndRouteKey extends KryoSerializerBase<CustomerAndRouteKey> {
	/**
	 * Default constructor
	 */
	public KryoSerializerCustomerAndRouteKey(boolean compress) {
		super(compress);
	}

	

	@Override
	public KryoRegisteredClass getRegisteredClass() {
		return KryoRegisteredClass.CUSTOMER_ROUTE_KEY;
	}
}