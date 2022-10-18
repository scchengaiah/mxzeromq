package zeromq.custom;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import com.mendix.core.Core;
import com.mendix.logging.ILogNode;

import zeromq.proxies.Enum_LogNode;
import zeromq.proxies.constants.Constants;

public class ZMQForwarderProxyHelper {

	private static final ILogNode LOG = Core.getLogger(Enum_LogNode.ZeroMQ.getCaption());

	private ZMQ.Context zmqCtx;
	private ZMQ.Socket frontEndSocket;
	private ZMQ.Socket backEndSocket;

	private ZMQForwarderProxyHelper() {

	}

	private static class InstanceHolder {
		public static final ZMQForwarderProxyHelper INSTANCE = new ZMQForwarderProxyHelper();
	}

	public static ZMQForwarderProxyHelper getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public void start() {

		zmqCtx = ZMQ.context(1);

		// Socket facing clients - Receive message from Publisher.
		frontEndSocket = zmqCtx.socket(SocketType.SUB);
		frontEndSocket.bind(String.format("tcp://*:%s", Constants.getSubscriberPort()));

		// Empty topic value receives all messages from all publishers for forwarding.
		frontEndSocket.subscribe("");

		// Socket facing services - Publish messages to subscribers.
		backEndSocket = zmqCtx.socket(SocketType.PUB);
		backEndSocket.bind(String.format("tcp://*:%s", Constants.getPublisherPort()));

		// Start the device in dedicated thread.
		CompletableFuture.runAsync(() -> ZMQ.proxy(frontEndSocket, backEndSocket, null));

	}

	public void shutDown() {

		LOG.info("Shutting down ZMQ forwarder started.");

		if(Objects.nonNull(frontEndSocket))
			frontEndSocket.close();
		if(Objects.nonNull(backEndSocket))
			backEndSocket.close();
		if(Objects.nonNull(zmqCtx))
			zmqCtx.close();

		LOG.info("Shutting down ZMQ forwarder completed.");
	}
}
