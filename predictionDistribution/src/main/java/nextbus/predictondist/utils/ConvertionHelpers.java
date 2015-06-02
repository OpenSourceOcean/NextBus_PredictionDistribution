/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictondist.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

//import org.apache.commons.lang.SerializationUtils;

import nextbus.predictiondist.data.StopPrediction;


/**
 * Utilitary class for DataConversion.
 * 
 * @author Onkarr
 *
 */

public class ConvertionHelpers {

	public static <T> StopPrediction convertInstanceOfObject(Object o,
			Class<T> clazz) {
		return (StopPrediction) clazz.cast(o);

	}

	public static Object deserialize(byte[] bytes) throws Exception {

		// DataInputStream din = new DataInputStream(din);
		// int size = din.readInt();
		// byte[] bytes2 = new byte[size];
		// din.readFully(bytes);
		System.out.println("DeSerializing..." + bytes);
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		Object o = ois.readObject();
		ois.close();
		return o;
	}

	/*
	public static Object deserializeWithCommons(byte[] bytes) throws Exception {
		Object obj = SerializationUtils.deserialize(bytes);
		return obj;
	}

	public static byte[] serializeWithCommons(Serializable s) throws Exception {
		byte[] b = SerializationUtils.serialize(s);
		return b;
	}
	*/

	
	/*
	 * Marshall the StopPrediction
	 * @Param StopPrediction
	 * @return String
	 */
	public synchronized static String xmlSerialize(StopPrediction sp) throws JAXBException,
			IOException {
		StringWriter sw = new StringWriter();
		Marshaller jaxbMarshaller = JaxbUtil.generateXmlMarshaller();
		jaxbMarshaller.marshal(sp, sw);
		return sw.toString();

	}

	/*
	 * UnMarshall the xml string to StropPrdictionObject 
	 * @Param String (XML String)
	 * @return StopPrediction 
	 */
	public static StopPrediction jaxbUnmarshaller(String xml)
			throws JAXBException, IOException {
		StringReader sr = new StringReader(xml);
		Unmarshaller jaxbUnmarshaller = JaxbUtil.generateXmlUnMarshaller();
		StopPrediction sp = (StopPrediction) jaxbUnmarshaller.unmarshal(sr);
		return sp;

	}

}
