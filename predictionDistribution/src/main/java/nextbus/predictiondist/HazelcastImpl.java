/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import nextbus.predictiondist.data.CustomerAndRouteKey;
import nextbus.predictiondist.data.KryoSerializerCustomerAndRouteKey;
import nextbus.predictiondist.data.KryoSerializerStopKey;
import nextbus.predictiondist.data.KryoSerializerStopPrediction;
import nextbus.predictiondist.data.KryoSerializerStopPredictionInfo;
import nextbus.predictiondist.data.StopKey;
import nextbus.predictiondist.data.StopPrediction;
import nextbus.predictiondist.data.StopPredictionInfo;


import nextbus.predictondist.utils.SimpleTimeLimiterTest.Sample;
import nextbus.predictondist.utils.SimpleTimeLimiterTest.SampleImpl;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientAwsConfig;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * Hazelcast impl: simulate one client accessing distributed map
 * 
 * @author noky
 */
public class HazelcastImpl implements DataGridInterface {
	private HazelcastInstance hazelcastInstance;
	private IMap<StopKey, StopPrediction> imap;	
	private IMap<String, String> imapString;
	private DataGridConfig _dataGridConfig;
	private int _id;

	private static AtomicInteger _idCounter = new AtomicInteger();

	private static final String AWS_REGION = "us-west-2b";
	private static final String AWS_SECURITY_GROUP = "launch-wizard-1";
	private static final String AWS_TAG_KEY = "type";
	private static final String AWS_TAG_VALUE = "hz-nodes";
	
	
	 private TimeLimiter service;

	/**
	 * Constructor
	 */
	public HazelcastImpl() {
		_id = _idCounter.getAndIncrement();
	}

	public HazelcastInstance getInstance(DataGridConfig dgCfg) {
		if (hazelcastInstance != null) {
			return hazelcastInstance;
		}
		// disable logging, this does work but is lame
		final String logprop = "hazelcast.logging.type";
		final String logtype = "none"; // "log4j2"
		System.setProperty(logprop, logtype);
		System.setProperty("hazelcast.operation.call.timeout.millis ", String.valueOf(1));
		

		// this doesn't work to disable logging!
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.setProperty(logprop, logtype);
		clientConfig.setProperty("hazelcast.operation.call.timeout.millis ", String.valueOf(1));

		// AWS credentials for noky's EC2 testing
		final DiscoveryMode discoveryMode = dgCfg.getDiscoveryMode();
		final boolean aws = (discoveryMode.equals(DiscoveryMode.AWS_INSIDE) || discoveryMode
				.equals(DiscoveryMode.AWS_OUTSIDE));
		final boolean awsInside = discoveryMode
				.equals(DiscoveryMode.AWS_INSIDE);
		final boolean isKryo = SerializationMode.KRYO.equals(dgCfg
				.getSerializationMode())
				|| SerializationMode.KRYO_COMPRESSED.equals(dgCfg
						.getSerializationMode());
		final boolean isKryoCompressed = SerializationMode.KRYO_COMPRESSED
				.equals(dgCfg.getSerializationMode());

		System.out.println("Hazelcast Client " + _id + ": aws=" + aws
				+ " awsInside=" + awsInside + " kryo=" + isKryo
				+ " compressed=" + isKryoCompressed);
		if (aws) {
			// Setup AWS config
			ClientAwsConfig clientAwsConfig = new ClientAwsConfig();
			clientAwsConfig.setAccessKey(dgCfg.getAwsAccessKey());
			clientAwsConfig.setSecretKey(dgCfg.getAwsSecurityKey());
			clientAwsConfig.setRegion(AWS_REGION);
			clientAwsConfig.setSecurityGroupName(AWS_SECURITY_GROUP);
			clientAwsConfig.setTagKey(AWS_TAG_KEY);
			clientAwsConfig.setTagValue(AWS_TAG_VALUE);
			clientAwsConfig.setInsideAws(awsInside);
			clientAwsConfig.setEnabled(true);
			clientConfig.getGroupConfig().setName("nextbus")
					.setPassword("nextbus");
			// Setup network config
			ClientNetworkConfig clientNetCfg = new ClientNetworkConfig();
			clientNetCfg.setAwsConfig(clientAwsConfig);
			clientConfig.setNetworkConfig(clientNetCfg);
		}

		// Alternate serialization with Kryo
		if (isKryo) {
			SerializationConfig serializationConfig = new SerializationConfig();
			Config config = new Config();
			
			List<SerializerConfig> scfgs = new ArrayList<SerializerConfig>(4);
			
			
			// StopPrediction
			SerializerConfig serCfg = new SerializerConfig().setTypeClass(
					StopPrediction.class).setImplementation(
					new KryoSerializerStopPrediction(isKryoCompressed));
			config.getSerializationConfig().addSerializerConfig(serCfg);
			scfgs.add(serCfg);
			
			// StopPredictionInfo
			
			SerializerConfig serCfgInfo = new SerializerConfig().setTypeClass(
					StopPredictionInfo.class).setImplementation(
					new KryoSerializerStopPredictionInfo(isKryoCompressed));
			config.getSerializationConfig().addSerializerConfig(serCfgInfo);
			scfgs.add(serCfgInfo);
			
			// stopkey 
			
			SerializerConfig serCfgStopKey = new SerializerConfig().setTypeClass(
					StopKey.class).setImplementation(
					new KryoSerializerStopKey(isKryoCompressed));
			config.getSerializationConfig().addSerializerConfig(serCfgStopKey);
			scfgs.add(serCfgStopKey);
			
			
			// customer and route key 
			
			SerializerConfig serCfgCustomerRoute = new SerializerConfig().setTypeClass(
					CustomerAndRouteKey.class).setImplementation(
					new KryoSerializerCustomerAndRouteKey(isKryoCompressed));
			config.getSerializationConfig().addSerializerConfig(serCfgCustomerRoute);
			scfgs.add(serCfgCustomerRoute);
			
			
			serializationConfig.setSerializerConfigs(scfgs);
			clientConfig.setSerializationConfig(serializationConfig);
			
		}

		// Create client
		// todo: this can throw a runtime exception
		// java.lang.IllegalStateException
		// if the cluster is not reachable! Undocumented, ugly!
		//clientConfig.setConnectionTimeout(1);
		hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);
		
		return hazelcastInstance;
	}

	@Override
	public DataGridType getType() {
		return DataGridType.HAZELCAST;
	}

	@Override
	public void init(DataGridConfig cfg) {
		_dataGridConfig = cfg;
		MapType mapType = cfg.getMapType();
		String mapId = cfg.getMapId();
		HazelcastInstance instance = getInstance(cfg);
		if (mapType.equals(MapType.STRINGS)) {
			imapString = instance.getMap(mapId);
		} else {
			imap = instance.getMap(mapId);
			
		  
			
		}
	}

	@Override
	public int size() {
		if (imap != null) {
			return imap.size();
		}
		return imapString.size();
	}

	@Override
	public void putAll(Map<StopKey, StopPrediction> map) {
		if (_dataGridConfig.isAtomicModeOn()) {
			// atomic put
			imap.putAll(map);

		} else {
			// non-atomic put
			for (Entry<StopKey, StopPrediction> entry : map.entrySet()) {
				StopKey key = entry.getKey();
				StopPrediction value = entry.getValue();
				// More efficient than put()
				imap.set(key, value);
			}
		}
	}

	@Override
	public Map<StopKey, StopPrediction> getAll(Set<StopKey> keys) {
		
	
		
		
		Map<StopKey, StopPrediction> map = new HashMap<StopKey, StopPrediction>();
		if (_dataGridConfig.isAtomicModeOn()) {
			map = imap.getAll(keys);
		} else {
			// This is unsafe, ie non-atomic
			
			for (StopKey key : keys) {
				StopPrediction pred = imap.get(key);
				if (pred != null) {
					map.put(key, pred);
				}
			}
		}
		return map;
	}

	@Override
	public void putString(String key, String value) {
		imapString.put(key, value);
	}
	
	@Override
	public void close() {
		hazelcastInstance.shutdown();
	}
	
	  
	
}