/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nextbus.predictiondist.data.KryoSerializerStopPrediction;
import nextbus.predictiondist.data.StopDataFactory;
import nextbus.predictiondist.data.StopKey;
import nextbus.predictiondist.data.StopPrediction;

//import org.apache.ignite.IgniteCheckedException;
//import org.apache.ignite.internal.MarshallerContextAdapter;
//import org.apache.ignite.marshaller.MarshallerContext;
//import org.apache.ignite.marshaller.optimized.OptimizedMarshaller;

import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.serialization.SerializationService;
import com.hazelcast.nio.serialization.SerializationServiceBuilder;

/**
 * Test different methods of serialization
 * @author noky
 */
public class SerializationTester {
	private final int stopsPerRoute;
	private final int predsPerStop;
	private final int numTrials;
	
	public SerializationTester() {
		stopsPerRoute = DataGridTester.DEF_NUM_STOPS_PER_ROUTE;
		predsPerStop = DataGridTester.DEF_NUM_PREDS_PER_STOP;
		numTrials = 100000;
	}
	
	public void testSerialize() {
		// Create data
		Map<StopKey, StopPrediction> preds = StopDataFactory.createDummyData(
				200, // customer id
				100, // route id
				stopsPerRoute, predsPerStop);

		System.out.println("Serialization test results. Number of trials: " + numTrials);
		
		Entry<StopKey, StopPrediction> first = preds.entrySet().iterator().next();
		StopPrediction pred = first.getValue();
		int baselineSize = 0;
		
		System.out.println("Serializer                  Size    Savings  Serialize");
		System.out.println("                            (bytes)          Time");
		System.out.println("----------------------------------------------------");
		for (int testId=0; testId<5; testId++) {
			Serializer s;
			switch (testId) {
			case 0:
				s = new JavaSerializer();
				break;
			case 1:
				//s = new GridGainSerializer();
				s = null;
				break;
			case 2:
				s = new KryoSerializer(false);
				break;
			case 3:
				s = new KryoSerializer(true);
				break;
			case 4:
				s = new IdentifiedDataSerializableSerializer();
				break;
			default: throw new RuntimeException("Invalid test id: "+testId);
			}
		
			final byte[] bytes = null;
			//= s.serialize(pred);
			final int length = bytes.length;
			if (baselineSize == 0) baselineSize = length;
			final long now = System.currentTimeMillis();
			for (int i=0; i<numTrials; i++) {
				//s.serialize(pred);
			}
			final long end = System.currentTimeMillis();
			final long time = end - now;
			final double savings = 1.0 - ((double)length/(double)baselineSize);
			System.out.println(String.format("%-28s %5d    %.1f%% %6d ms",
					s.getName(), bytes.length, savings * 100.0, time));
		}
	}
	
	// Needed for GridGain serializing, not sure if this is right, but it works.
	// There doesn't seem to be a default MarshallerContext impl available
//    private static class TestMarshallerContext extends MarshallerContextAdapter {
//        private Map<Integer, String> classes = new HashMap<Integer, String>();
//        @Override protected boolean registerClassName(int id, String clsName) {
//        	classes.put(id, clsName);
//            return true;
//        }
//        @Override protected String className(int id) {        	
//            String clsName = classes.get(id);
//            return clsName;
//        }
//    }

    public static interface Serializer {
    	public byte[] serialize(Serializable s) throws IOException;
    	public String getName();
    }
    
    public static class JavaSerializer implements Serializer {
    	SerializationService ss;
    	
    	public JavaSerializer() {    		
    	}
    	public String getName() { return "Java Serialize"; }
    	public byte[] serialize(Serializable s) throws IOException {    	
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(s);
			byte[] b = bos.toByteArray();
			oos.close();
			return b;
    	}
    }
    
//    public static class GridGainSerializer implements Serializer {
//    	OptimizedMarshaller marsh;
//    	GridGainSerializer() {
//    		marsh = new OptimizedMarshaller(true);
//    		MarshallerContext ctx = new TestMarshallerContext();
//    		marsh.setContext(ctx);
//    	}
//    	public String getName() { return "GridGainOptimizedMarshaller"; }
//    	public byte[] serialize(Serializable s) throws IOException {
//	        try {
//	        	byte[] b = marsh.marshal(s);
//				return b;
//			} catch (IgniteCheckedException e) {
//				throw new IOException("err: " + e.getMessage(), e);
//			}
//    	}
//    }
    
    public static class KryoSerializer implements Serializer {
    	private final SerializationService ss;
    	private final boolean compress;
    	
    	public KryoSerializer(boolean compress) {
    		this.compress = compress;
    		SerializationConfig serzCfg = new SerializationConfig();
			SerializerConfig serCfg = new SerializerConfig()
				.setTypeClass(StopPrediction.class)
				.setImplementation(new KryoSerializerStopPrediction(compress));
			List<SerializerConfig> scfgs = new ArrayList<SerializerConfig>();
			scfgs.add(serCfg);
			serzCfg.setSerializerConfigs(scfgs);
	    	SerializationServiceBuilder b = new DefaultSerializationServiceBuilder();
			b.setConfig(serzCfg);
			ss = b.build();
		}	
    	public String getName() { return "Kryo (compress="+compress+")"; }
	
    	public byte[] serialize(Serializable s) {
    		Data data = ss.toData(s);
    		byte[] bytes = data.getData();
    		return bytes;
    	}
    }
    
    // Hazelcast. Classes must implement manual IdentifiedDataSerializable
    public static class IdentifiedDataSerializableSerializer implements Serializer {
    	private final SerializationService ss;
    	
    	public IdentifiedDataSerializableSerializer() {
	    	SerializationServiceBuilder b = new DefaultSerializationServiceBuilder();
			ss = b.build();
		}	
    	public String getName() { return "IdentifiedDataSerializable"; }
	
    	public byte[] serialize(Serializable s) {    
    		Data data = ss.toData(s);
    		byte[] bytes = data.getData();
    		return bytes;
    	}
    }
    	
	public static void main(String[] args) {
		SerializationTester s = new SerializationTester();
		s.testSerialize();
	}
}