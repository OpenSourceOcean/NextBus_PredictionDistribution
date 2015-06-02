/* $Id: StopPredictionInfo.java 39454 2014-11-19 18:46:16Z noky $ */
/*
 * Copyright 1998-2006 NextBus Information Systems, Inc.
 * All Rights Reserved.
 * 
 * Created on May 22, 2006
 */

package nextbus.predictiondist.data;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import nextbus.predictiondist.Compare;

/**
 * IMMUTABLE data structure.
 * Contains a single prediction plus information to describe it. It
 * is used in multiple places whether it is predictions for stop,
 * for route, or for vehicles. Therefore there is a lot of info
 * associated with the prediction since it is used in several different
 * ways. The goal was to have a single class to represent a prediction
 * whereas before we had about 4 of them.
 * 
 * NOTE: If this class changes then also need to update Predictor2Comm.StopPrediction
 * class because it serializes and unserializes the StopPredictionInfo objects.
 */
public class StopPredictionInfo implements Serializable { //, IdentifiedDataSerializable {
    // UID
    private static final long serialVersionUID = 8469140216274905660L;

    private static final byte BITMASK_IS_DEPARTURE = 1;
    private static final byte BITMASK_IS_AFFECTED_BY_TIMEPOINT = 2;
    private static final byte BITMASK_IS_SCHEDULE_BASED = 4;
    
    // Vehicle prediction is for
    private String _vehicleTag;
           
    // Destination
    private String _dirDestTag;
    // Job vehicle is on
    private String _jobTag;
    private short _pathIndex;
    private String _tripTag;
    // Actual prediction time (epoch millis)
    private long   _predictionTime;
    
    // If true, prediction is for departure.  If false, prediction is for arrival.
    private byte _flags;

    private float _badness;
    // The interpolated time of day in seconds the vehicle is scheduled to be at the stop
    private int _scheduledTimeForStop;
    // The overall order of the stop in the trip pattern, starting at 1.
    // Only available if "new style" config is uses.
    // Allows disambiguation when stop appears more than once in trip pattern.
    private Short _orderOfStopInTripPattern;
    
    /**
     * Constructor
     * 
     * @param vehicleTag
     * @param routeTag StopTuple: route
     * @param stopTag StopTuple: stop
     * @param dirTag StopTuple: dir tag for the stop tuple
     * @param predictionDirTag
     * Specificially: for "new style config" this is the tripPatternTag
	 * and for "old style config" this is the dirTag.
	 * The direction/destination tag associated with the prediction.
	 * Helps differentiate a prediction for a stop which services a route
	 * that has multiple directions/destinations.
     * @param jobTag
     * @param pathIndex
     * @param tripTag
     * @param predictionTime
     * @param isDeparture
     * @param affectedByTimepointaffectedByTimepoint if true, stop is at a timepoint or downstream
     * of a timepoint (thus, prediction time is affected by a timepoint)
     * @param scheduleBased specifies if in schedule based predictions mode
	 * @paramm badness - specifies if vehicle traveling as fast as expected. 
	 * @param scheduledTimeForStop - The interpolated time of day in seconds the vehicle is scheduled to be at the stop 
     */
    public StopPredictionInfo(String vehicleTag, 
    		              String predictionDirTag,
    		              String jobTag,
    		              short pathIndex,
    		              String tripTag,
    		              long predictionTime, 
    		              boolean isDeparture,
    		              boolean affectedByTimepoint,
    		              boolean scheduleBased,
    		              float badness,
    		              int scheduledTimeForStop,
    		              Short orderOfStopInTripPattern) {
    	_vehicleTag = vehicleTag;
        _dirDestTag = predictionDirTag;
        _jobTag = jobTag;
        _pathIndex = pathIndex;
        _tripTag = tripTag;
        _predictionTime = predictionTime;
        byte flags = 0;
        if (isDeparture) {
        	flags = (byte)(flags | BITMASK_IS_DEPARTURE);
        }
        if (affectedByTimepoint) {
        	flags = (byte)(flags | BITMASK_IS_AFFECTED_BY_TIMEPOINT);
        }
        if (scheduleBased) {
        	flags = (byte)(flags | BITMASK_IS_SCHEDULE_BASED);
        }
        _flags = flags;
        _badness = badness;
        _scheduledTimeForStop = scheduledTimeForStop;
        _orderOfStopInTripPattern = orderOfStopInTripPattern;
    }
    
    /**
     * Default constructor for factory
     */
    public StopPredictionInfo() {
    	
    }
    
    public String getVehicleTag() {
        return _vehicleTag;
    }
    
    /**
     * Specificially: for "new style config" this is the tripPatternTag
	 * and for "old style config" this is the dirTag.
	 * The direction/destination tag associated with the prediction.
	 * Helps differentiate a prediction for a stop which services a route
	 * that has multiple directions/destinations.
     * @return
     */
    public String getDirDestTag() {
        return _dirDestTag;
    }

    public String getJobTag() {
    	return _jobTag;
    }
    
    /**
     * Get pathIndex (same as jobsequence number, but 0 to n-1 instead of 1 to n)
     * @return Returns the index of the path in the job that prediction is associated with
     */
    public short getPathIndex() {
    	return _pathIndex;
    }
        
    /**
     * Identifies which trip a prediction is associated with. 
     * Only valid for automatically configured agencies.
     * @return
     */
    public String getTripTag() {
    	return _tripTag;
    }
    
    /**
     * Epoch time, milliseconds
     * @return
     */
    public long getPredictionTime() {
        return _predictionTime;
    }
    
	private boolean isFlagSet(byte flag) {
		return (_flags & flag) == flag;
	}
	
    /**
     * if true, stop is at a timepoint or downstream
     * of a timepoint (thus, prediction time is affected by a timepoint)
     * @return true if prediction was affected by upstream timepoint
     */
    public boolean isAffectedByTimepoint() {
    	return isFlagSet(BITMASK_IS_AFFECTED_BY_TIMEPOINT);
    }   
    
    /**
     * Returns true if predictor currently creating schedule based, as
     * opposed to AVL based, predictions.
     * @return
     */
    public boolean isScheduleBased() {
		return isFlagSet(BITMASK_IS_SCHEDULE_BASED);
    }
    

    public boolean isDeparture() {
		return isFlagSet(BITMASK_IS_DEPARTURE);
    }
        
    public int getPredictionInSecs() {
    	return (int) (_predictionTime - System.currentTimeMillis())/1000;
    }
    
    /**
     * Returns prediction in minutes.
     * Note: rounding down wait time instead of rounding off so that displayed times
     * will always be less than calculated so that people don't miss their bus.
     * @return
     */
    public int getPredictionInMinutes() {
    	return getPredictionInSecs()/60;
    }
    
    /**
     * Returns the latest badness value for the vehicle. Indicates how fast the
     * vehicle is traveling compared to what is expected. A value of 1.0 
     * indicates traveling as fast as expected. If value < 1.0 then vehicle
     * traveling more slowly. If > 1.0 then traveling faster. This can be
     * used by the UI to tell passengers if vehicle is delayed.
     * @return
	 */
    public float getBadness() {
    	return _badness;
    }
    
    /**
     * The interpolated time when vehicle is scheduled to be at the stop.
     * Returns -1 if not valid.
     * @return
     */
    public int getScheduledTimeForStop() {
    	return _scheduledTimeForStop;
    }
    
    /**
     * Order of stop in trip pattern, starting at 1
     * @return null if old style config is used
     */
    public Short getOrderOfStopInTripPattern() {
    	return this._orderOfStopInTripPattern;
    }
    
    /**
     * For serializable.  Just uses truncated arrival time for hashing.
     */
    public int hashCode() {
        return (int)_predictionTime;
    }

    /**
     * Needed for Serializable
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && (getClass() == o.getClass())) {
            StopPredictionInfo p = (StopPredictionInfo)o;
            if (this._predictionTime == p._predictionTime
                    && this._flags == p._flags
                    && Compare.equals(this._dirDestTag, p._dirDestTag)
                    && Compare.equals(this._vehicleTag, p._vehicleTag)) {
                return true;
            }
        }
        return false;
    }
       
    public String toString() {
        return toString(null);
    }
    
    public String toString(DateFormat dateFmt) {
        String type = (isDeparture()) ? "departure" : "arrival";
        String predTime = null;
        if (dateFmt != null) {
            predTime = dateFmt.format(new Date(_predictionTime));
        } else {
            predTime = _predictionTime+"";
        }
        
        String tp = "";
        if (isAffectedByTimepoint()) {
            tp = " (affected by timepoint)";
        }
        
        String schedBased = "";
        if (isScheduleBased())
        	schedBased = " (schedule based)";
        
        String str =
            "type=" + type + " predTime=" + predTime +
            " tripPat=" + _dirDestTag + " pathIndex=" + _pathIndex +")" + tp + schedBased;
        str += " vehicle=" + _vehicleTag + " job=" + _jobTag + " trip=" + _tripTag +
        	" badness=" + _badness + "orderInTP=" + this._orderOfStopInTripPattern; 
        return str;
    }
    
    public String toStringShort() {
        String type = (isDeparture()) ? "dep" : "arr";
        String predTime = _predictionTime+"";
        
        String str =
            "type=" + type + "t=" + predTime;
        return str;
    }

    // Hazelcast serialization optimization: IdentifiedDataSerializable
    //@Override
	public int getFactoryId() {
		return StopPredictionFactory.FACTORY_ID;
	}

	// @Override
	public int getId() {
		return StopPredictionFactory.STOP_PREDICTION_INFO_TYPE;
	}

	//@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeUTF(_vehicleTag);
		out.writeUTF(_dirDestTag);
		out.writeUTF(_jobTag);
		out.writeShort(_pathIndex);
		out.writeUTF(_tripTag);
		out.writeLong(_predictionTime);
		out.writeByte(_flags);
		out.writeFloat(_badness);
		out.writeInt(_scheduledTimeForStop);
		out.writeObject(_orderOfStopInTripPattern);
	}
	
	// @Override
	public void readData(ObjectDataInput in) throws IOException {
		_vehicleTag = in.readUTF();
		_dirDestTag = in.readUTF();
		_jobTag = in.readUTF();
		_pathIndex = in.readShort();
		_tripTag = in.readUTF();		
		_predictionTime = in.readLong();
		_flags = in.readByte();
		_badness = in.readFloat();
		_scheduledTimeForStop = in.readInt();
		_orderOfStopInTripPattern = in.readObject();
	}
}