package zeromq.custom;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Throwables;
import com.mendix.core.Core;
import com.mendix.logging.ILogNode;

import zeromq.custom.exceptionhandlers.consumer.IConsumer;
import zeromq.proxies.Enum_LogNode;
import zeromq.proxies.ZMQSubscriber;

public class ZMQSubscriptionHelper {
	
	private static Map<Long, ZMQSubscription> subscriptionMap = new ConcurrentHashMap<>();
	private static final ILogNode LOG = Core.getLogger(Enum_LogNode.ZeroMQ.getCaption());
	
	private ZMQSubscriptionHelper() {

	}

	private static class InstanceHolder {
		public static final ZMQSubscriptionHelper INSTANCE = new ZMQSubscriptionHelper();
	}
	
	public static ZMQSubscriptionHelper getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	public void initialize(List<ZMQSubscriber> zmqSubscriberList) {
		
		zmqSubscriberList.forEach(IConsumer.handledConsumer(zmqSubscriber -> {
			
			try {
				
				Long id = zmqSubscriber.getMendixObject().getId().toLong();
				
				if(! subscriptionMap.containsKey(id)) {
					// Create subscription.
					ZMQSubscription zmqSubscription = ZMQSubscription.builder()
							.subscriber(zmqSubscriber)
							.ctx(Core.createSystemContext())
							.build();
					
					zmqSubscription.subscribe();
					
					// Update subscription map.
					subscriptionMap.put(id, zmqSubscription);
					
					LOG.info(String.format("Subscription successful for the configuration %s", 
							zmqSubscriber.getName()));
				} else
					LOG.info(String.format("Subscription already exist for the configuration %s", 
							zmqSubscriber.getName()));
				
				
				
			} catch(Exception ex) {
				LOG.error(String.format("Exception encountered while subscribing for the configuration %s:%n", 
						Throwables.getRootCause(ex).getMessage()));
			}
			
		}));
		
	}
	
	public void remove(List<ZMQSubscriber> zmqSubscriberList) {
		
		zmqSubscriberList.forEach(IConsumer.handledConsumer(zmqSubscriber -> {
			
			try {
				
				Long id = zmqSubscriber.getMendixObject().getId().toLong();
				
				if(subscriptionMap.containsKey(id)) {

					// Fetch subscription object and close the resources.
					subscriptionMap.get(id).close();
					
					// Remove the subscription reference.
					subscriptionMap.remove(id);
					
					LOG.info(String.format("Subscription removed for the configuration %s", 
							zmqSubscriber.getName()));
				} else
					LOG.info(String.format("Subscription does not exist for the configuration %s", 
							zmqSubscriber.getName()));
				
			} catch(Exception ex) {
				LOG.error(String.format("Exception encountered while removing subscription for the configuration %s:%n%s", 
						zmqSubscriber.getName(), Throwables.getRootCause(ex).getMessage()));
			}
			
		}));
	}

}
