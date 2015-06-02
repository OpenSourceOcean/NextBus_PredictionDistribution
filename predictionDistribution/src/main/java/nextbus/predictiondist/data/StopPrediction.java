/* $Id: StopPrediction.java 40065 2015-02-26 21:42:44Z noky $ */
/*
 * Copyright 1998-2000 NextBus Information Systems, Inc.
 * All Rights Reserved.
 */

package nextbus.predictiondist.data;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Arrays;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import nextbus.predictiondist.Compare;

/**
 * IMMUTABLE data structure for storing prediction information for a stop.
 */
public class StopPrediction implements Serializable { //, IdentifiedDataSerializable {	
    // UID
    private static final long serialVersionUID = 5060809631206077672L;

    // The tuple [ _routeTag, _stopTag, _dirTag ] uniquely identifies the stop 
    
    // Testing distributed query ... We need to keep this field here ?
    private String _projectId;
    public String get_projectId() {
		return _projectId;
	}

	public void set_projectId(String _projectId) {
		this._projectId = _projectId;
	}
	

	private String _routeTag;
    private String _stopTag;
    private String _dirTag;

    // prediction times: UTC time since epoch
    private StopPredictionInfo[] _predictions;

    // Last Passing time at the route/stop/direction (headway)
    private long _passTimeRouteDirStop;
 
    /**
     * Default constructor for factory
     */
    public StopPrediction() {
    	
    }
    
   /**
    * Constructor
    * @param routeTag the tag identifier of the route
    * @param stopTag the tag identifier of the stop
    * @param dirTag the direction of the stop
    * @param stopID database ID for this stop
    * @param passingTimeRoute the time the last vehicle passed for the specific routeTag
    * @param predictions an array of StopPredictionInfo objects. Can be set to null
    */
    public StopPrediction(String routeTag, String stopTag, String dirTag,
    		long passTimeRouteDirStop,
            StopPredictionInfo[] predictions)
    {
    	
    	_routeTag = routeTag;
        _stopTag = stopTag;
        _dirTag = dirTag;
        _passTimeRouteDirStop = passTimeRouteDirStop;
        
        if (predictions == null) {
            _predictions = new StopPredictionInfo[0];
        } else {
            _predictions = predictions;
        }
    }
    
    public String getStopTag() {
    	return _stopTag;
    }
    
    /**
     * Get the route id associated with the stop.
     * @return a string containing the route tag
     */
    public String getRouteTag() {
        return _routeTag;
    }

    /**
     * Get the direction tag associated with the stop
     * (eg: inbound, outbound, etc)
     * @return a string containing the stop direction tag
     */
    public String getDirTag() {
        return _dirTag;
    }
    
	public StopPredictionInfo[] getPredictions() {
		return _predictions;
	}

	public long getPassTimeRouteDirStop() {
		return _passTimeRouteDirStop;
	}

    public String toString() {
        return toString(null, 0);
    }
    
    /**
     * Return the data in the object, nicely formatted in a string
     */
    public String toString(DateFormat fmt, long now) {
        StringBuffer desc = new StringBuffer();

        String stopTuple = _routeTag + "-" + _stopTag + "-" + _dirTag;
        desc.append("stop=").append(stopTuple);
        desc.append(",lastPassTimeRouteDirStop=").append(_passTimeRouteDirStop);
        desc.append(",preds=");
        if (_predictions == null) {
            desc.append("null");
        } else if (_predictions.length == 0) {
            desc.append("[]");
        } else {
            for (int i=0; i<_predictions.length; i++) {
                StopPredictionInfo predInfo = _predictions[i];
                desc.append("\n  " + predInfo.toStringShort());
            }
        }
        
        return desc.toString();
    }

    /**
     * Return the data in the object, nicely formatted in a string
     */
    public String toStringBrief() {
        StringBuffer desc = new StringBuffer();
        String stopTuple = _routeTag + "-" + _stopTag + "-" + _dirTag;
        desc.append("stop=").append(stopTuple);
        desc.append(",lastPassTimeRouteDirStop=").append(_passTimeRouteDirStop);
        desc.append(",preds=");
        if( _predictions == null ) {
            desc.append("null");
        } else {
            desc.append(_predictions.length);
        }
        return desc.toString();
    }
    
    /**
     * Equals for serializable
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (getClass() == obj.getClass())) {
            StopPrediction s = (StopPrediction)obj;
            if (Compare.equals(this._routeTag,    s._routeTag)
            		&& Compare.equals(this._stopTag,     s._stopTag)
            		&& Compare.equals(this._dirTag,      s._dirTag)
            		&& Arrays.equals(this._predictions, s._predictions)
                    && this._passTimeRouteDirStop == s._passTimeRouteDirStop     
            ) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * hashCode() for serializable
     */
    public final int hashCode() {
        // just use the stoptag
        return (_stopTag == null) ? -1 : _stopTag.hashCode();
    }

    /**
     * Return the predictions in the object, nicely formatted in a string
     */
    public String dumpPreds() {
        StringBuffer desc = new StringBuffer();

        if (_predictions == null) {
            desc.append("null");
        } else if (_predictions.length == 0) {
            desc.append("0");
        } else {
            for (int i=0; i<_predictions.length; i++) {
                if (i>0) { desc.append("\n"); }
                desc.append(_predictions[i]);
            }
        }
        return desc.toString();
     }

	// Hazelcast custom serialization: IdentifiedDataSerializable

	//@Override
	public int getFactoryId() {
		return StopPredictionFactory.FACTORY_ID;
	}

	//@Override
	public int getId() {
		return StopPredictionFactory.STOP_PREDICTION_TYPE;
	}

	//@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		// Other serialization results with predefined data generated
		// in StopPredictionFactory (using 10 StopPredictionInfos per stop)
		// * Standard Java Serialize : 1150 bytes
		// * Kryo (no compress)         347 bytes 
		// * Kryo (compress)            178 bytes!
		// 
		// Testing shows 9 byte header used.
		// Serialize after: 1005 bytes (normal serialize for SPI)
		// Serialize after:  943 bytes (writeData() for SPI)
		//   69 bytes + numPreds(int) + SPIs
		out.writeUTF(_routeTag);
		out.writeUTF(_stopTag);
		out.writeUTF(_dirTag);
		//out.writeObject(_predictions);
		out.writeInt(_predictions.length);
		for (StopPredictionInfo spi : _predictions) {
			spi.writeData(out);
		}
		out.writeLong(_passTimeRouteDirStop);
	}

	//@Override
	public void readData(ObjectDataInput in) throws IOException {
		_routeTag = in.readUTF();
		_stopTag = in.readUTF();
		_dirTag = in.readUTF();
		//_predictions = in.readObject();
		int numPreds = in.readInt();
		_predictions = new StopPredictionInfo[numPreds];
		for (int i=0; i<numPreds; i++) {
			StopPredictionInfo spi = new StopPredictionInfo();
			spi.readData(in);
			_predictions[i] = spi;
		}
		_passTimeRouteDirStop = in.readLong();
	}
}