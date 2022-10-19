package zeromq.custom;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import com.mendix.core.Core;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.MendixRuntimeException;
import com.mendix.systemwideinterfaces.core.IContext;

import lombok.Builder;
import zeromq.proxies.Enum_LogNode;
import zeromq.proxies.ZMQSubscriber;
import zeromq.proxies.constants.Constants;

public class ZMQSubscription {

	private ZMQ.Context context;
	private ZMQ.Socket socket;
	private ZMQSubscriber subscriber;
	private IContext ctx;
	
	private ZMQMessageHandler msgHandler;


	private static final ILogNode LOG = Core.getLogger(Enum_LogNode.ZeroMQ.getCaption());

	@Builder
	public ZMQSubscription(ZMQSubscriber subscriber, IContext ctx) {
		this.subscriber = subscriber;
		this.ctx = ctx;
	}

	
	public void subscribe() {

		context = ZMQ.context(1);
		socket = context.socket(SocketType.SUB);
		socket.connect(getConnectionUrl());
		socket.subscribe(subscriber.getTopic());

		msgHandler = ZMQMessageHandler.builder()
						.subscriber(subscriber)
						.socket(socket)
						.ctx(ctx)
						.build();
		
		CompletableFuture.runAsync(msgHandler);

	}
	

	private String getConnectionUrl() {

		String connUrl = "tcp://%s:%s";

		switch (subscriber.getSubscriptionType()) {
		case External:
			connUrl = String.format(connUrl, subscriber.getHost(), subscriber.getPort());
			break;

		case Local:
			connUrl = String.format(connUrl, "localhost", Constants.getPublisherPort());
			break;

		default:
			throw new MendixRuntimeException(String.format("Unhandled subscription type: %s", 
					subscriber.getSubscriptionType()));
		}

		return connUrl;
	}



	/**
	 * Clean up resources. 
	 */
	public void close() {

		// Stop the execution.
		if(Objects.nonNull(msgHandler))
			msgHandler.stopExecution();

		if(Objects.nonNull(socket))
			socket.close();

		if(Objects.nonNull(context))
			context.close();

		LOG.info(String.format("Cleaned up resources for the subscription: %s", 
				subscriber.getName()));

	}

}
