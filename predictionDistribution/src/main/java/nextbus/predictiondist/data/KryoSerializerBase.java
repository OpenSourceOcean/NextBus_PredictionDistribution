/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

/**
 * Generic base class for Hazelcast StreamSerializer
 * @author noky
 */
public abstract class KryoSerializerBase<E> implements StreamSerializer<E> {
    private final boolean compress;
    
    // Kryo is not threadsafe, so use ThreadLocal
    private static final ThreadLocal<Kryo> kryoThreadLocal 
           = new ThreadLocal<Kryo>() {
        
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            KryoRegisteredClass[] values = 
            		KryoRegisteredClass.values();
            if (values != null) {
            	for (KryoRegisteredClass value : values) {
            		Class<?> clazz = value.getRegisteredClass();
            		kryo.register(clazz);	
            	}
            }
            return kryo;
        }
    };

    /**
     * Constructor
     * @param compress enable compression
     */
    public KryoSerializerBase(boolean compress) {
        this.compress = compress;
    }

    /**
     * Subclasses should override this to specify the class that is registered
     * @return
     */
    public abstract KryoRegisteredClass getRegisteredClass();

    @Override
    public int getTypeId() {
    	KryoRegisteredClass reg = getRegisteredClass();
    	return reg.getTypeId();
    }

    @Override
    public void write(ObjectDataOutput objectDataOutput, E obj) 
           throws IOException {
        Kryo kryo = kryoThreadLocal.get();

        if (compress) {
        	// This seems rather slow/inefficient in testing...
        	// There might be ways to improve this.
        	// Note magic number here...
            ByteArrayOutputStream byteArrayOutputStream = 
                new ByteArrayOutputStream(256);
            DeflaterOutputStream deflaterOutputStream = 
                    new DeflaterOutputStream(byteArrayOutputStream);
            Output output = new Output(deflaterOutputStream);
            kryo.writeObject(output, obj);
            output.close();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            objectDataOutput.write(bytes);
        	/*
        	DeflaterOutputStream deflaterOutputStream = 
                new DeflaterOutputStream((OutputStream)objectDataOutput);            
            Output output = new Output(deflaterOutputStream);
            kryo.writeObject(output, obj);
            objectDataOutput.write(0);
            deflaterOutputStream.flush();
            output.close();
            */
        } else {
            Output output = new Output((OutputStream) objectDataOutput);
            kryo.writeObject(output, obj);
            output.flush();
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public E read(ObjectDataInput objectDataInput) 
           throws IOException {
        InputStream in = (InputStream) objectDataInput;
        if (compress) {
            in = new InflaterInputStream(in);
        }
        Input input = new Input(in);
        Kryo kryo = kryoThreadLocal.get();
        return ((E) kryo.readObject(input,  this.getRegisteredClass().getRegisteredClass()));
    }

    @Override
    public void destroy() {
    	// no-op?
    }
}