/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist;

/**
 * Enum to configure transactions  Modes
 * 
 * @author Onkarr
 *
 */

public enum  TransactionsMode {
	PESSIMISTIC,// Pessimistic transactions with repeatable read isolation	
	OPTIMISTIC,// Optimistic transactions with repeatable read isolation
}
