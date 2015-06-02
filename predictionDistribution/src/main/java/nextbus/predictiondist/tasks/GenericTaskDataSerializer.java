//package nextbus.predictiondist.tasks;
//
//import nextbus.predictiondist.data.StopKey;
//import nextbus.predictiondist.data.StopPrediction;
//import nextbus.predictiondist.data.StopPredictionInfo;
//
//import com.hazelcast.nio.serialization.DataSerializableFactory;
//import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
//
//public class GenericTaskDataSerializer implements DataSerializableFactory {
//
//	
//	public static final int GENERIC_TASK_TYPE_FACTORY_ID = 5;
//	public static final int STOP_KEY_FACTORY_ID = 5;
//	public static final int GENERIC_TASK_CONFIG_FACTORY_ID = 5;
//	public static final int STOP_PREDICTION_FACTORY_ID = 5;
//	public static final int STOP_PREDICTION_INFO_FACTORY_ID = 5;
//	
//	public static final int GENERIC_TASK_TYPE_ID = 5;
//	public static final int GENERIC_TASK_CONFIG_ID = 6;
//	public static final int STOP_KEY_ID = 7;
//	public static final int STOP_PREDICTION_ID = 8;
//	public static final int STOP_PREDICTION_INFO_ID = 9;
//
////	@Override
////	public IdentifiedDataSerializable create(int typeId) {
////		if (typeId == GENERIC_TASK_TYPE_ID) {
////			return new GenericTask();
////		} else if (typeId == GENERIC_TASK_CONFIG_ID) {
////			return new GenericTaskConfig();
////		} else if (typeId == STOP_KEY_ID) {
////			return new StopKey();
////		}else if (typeId == STOP_PREDICTION_ID) {
////			return new StopPrediction();
////		} else if (typeId == STOP_PREDICTION_INFO_ID) {
////			return new StopPredictionInfo();
////		}else {
////			return null;
////		}
////	}
//
//}
