/**
 * Copyright 2015 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictondist.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import nextbus.predictiondist.data.StopPrediction;

/**
 * Utilitary class for JaxB . 	 This class methods could be synchronized. This not the best way to create singleton using double checking and  volatile / Enum  is better 
 * For POC it's acceptable
 * 
 * @author Onkarr
 *
 */

public class JaxbUtil {

	private static JAXBContext jaxbContext;
	private static Marshaller jaxbMarshaller;
	private static Unmarshaller jaxbUnmarshaller;
	
	/*
	 * Get the Jxb Marshaller to marshall the objects
	 * @return Marshaller
	 */

	public synchronized static Marshaller generateXmlMarshaller() throws JAXBException {
		if (jaxbMarshaller == null) {
			jaxbContext = JAXBContext.newInstance(StopPrediction.class);
			jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		}
		return jaxbMarshaller;
	}

	/*
	 * Get the Jxb Unmarshaller to Unmarshaller the bytes
	 * @return Unmarshaller
	 */
	public synchronized static Unmarshaller generateXmlUnMarshaller() throws JAXBException {
		if (jaxbUnmarshaller == null) {
			jaxbContext = JAXBContext.newInstance(StopPrediction.class);
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		}
		return jaxbUnmarshaller;
	}
}
