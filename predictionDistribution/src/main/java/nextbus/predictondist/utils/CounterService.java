package nextbus.predictondist.utils;

import java.util.concurrent.atomic.AtomicInteger;


public class CounterService {
	
	public static AtomicInteger counterClients = new AtomicInteger(0);
	public static String count ;
	

	public static String getCount() {
		count = String.valueOf(counterClients.getAndIncrement());
		return count;
	}

	public static void setCount(String count) {
		CounterService.count = count;
	}

	public static AtomicInteger getCounterClients() {
		return  counterClients;
	}

	public static void setCounterClients(AtomicInteger counterClients) {
		CounterService.counterClients = counterClients;
	}
	
	

}
