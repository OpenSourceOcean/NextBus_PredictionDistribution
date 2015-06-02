/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist;
/**
 * Enum to configure LoadModes to use either Transctional cache or Dataloader api ,  currently useful for ignite only 
 * 
 * @author Onkarr
 *
 */
public enum LoadMode {
	
	DATA_LOAD,// This mode uses Dataloader load mothod
	CACHE_MODE,// This mode uses cache put, putALL etc operations 

}
