
package nextbus.predictiondist;
/**
 * Enum to configure Isolation Modes
 * 
 * @author Onkarr
 *
 */

public enum IsolationMode {
	REPEATABLE_READ, // Repeatable read uses same value for the operations after loaded of for the fistime
	READ_COMMITTED // Read committed uses the latest value that is persisted in the data store 
	

}
