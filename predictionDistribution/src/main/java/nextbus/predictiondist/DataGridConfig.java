/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist;

/**
 * Config for DataGridInterface
 * @author noky
 * Onkarr added TransactionsMode and IsolationMode
 *
 */
public class DataGridConfig {
	private final String mapId;
	private final MapType mapType;
	private final DiscoveryMode discoveryMode;
	private final boolean atomicModeOn;
	private final TransactionsMode transactionsMode;
	private final IsolationMode isolationMode;
	private final SerializationMode serializationMode;
	private final String awsAccessKey;
	private final String awsSecurityKey;
	private final LoadMode loadMode;
	public LoadMode getLoadMode() {
		return loadMode;
	}

	public int ingiteInstanceCounter;
	/**
	 * @param atomicModeOn if true: getAll() & putAll() should be atomic,
	 * otherwise, non-atomic
	 */
	public DataGridConfig(
			String mapId,
			MapType mapType,
			boolean atomicModeOn, 
			DiscoveryMode discoveryMode,
			TransactionsMode transactionsMode,
			IsolationMode isolationMode,
			SerializationMode serializationMode,
			LoadMode loadMode,
			String awsAccessKey,
			String awsSecurityKey) {
		this.mapId = mapId;
		this.mapType = mapType;
		this.atomicModeOn = atomicModeOn;
		this.discoveryMode = discoveryMode;
		this.transactionsMode = transactionsMode;
		this.isolationMode = isolationMode;
		this.serializationMode = serializationMode;
		this.awsAccessKey = awsAccessKey;
		this.awsSecurityKey = awsSecurityKey;
		this.loadMode = loadMode;
	}
	
	public TransactionsMode getTransactionsMode() {
		return transactionsMode;
	}

	
	public String getMapId() {
		return mapId;
	}
	
	public MapType getMapType() {
		return mapType;
	}
	
	public boolean isAtomicModeOn() {
		return atomicModeOn;
	}
	public DiscoveryMode getDiscoveryMode() {
		return discoveryMode;
	}
	public IsolationMode getIsolationMode() {
		return isolationMode;
	}
	public SerializationMode getSerializationMode() {
		return serializationMode;
	}
	public String getAwsAccessKey() {
		return awsAccessKey;
	}
	public String getAwsSecurityKey() {
		return awsSecurityKey;
	}
	
	@Override
	public String toString() {
		return "mapId=" + mapId + 
				", mapType=" + mapType + 
				", discoveryMode=" + discoveryMode + 
				", atomicModeOn=" + atomicModeOn +
				", serializationMode=" + serializationMode+
				", awsAccessKey=" + awsAccessKey +
				", awsSecurityKey=" + awsSecurityKey;
	}
}