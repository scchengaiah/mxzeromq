package zeromq.custom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.zeromq.ZMQ;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;

import lombok.Builder;
import zeromq.proxies.Enum_LogNode;
import zeromq.proxies.Message;
import zeromq.proxies.ZMQSubscriber;


public class ZMQMessageHandler implements Runnable {

	private ZMQ.Socket socket;
	private ZMQSubscriber subscriber;
	private IContext ctx;

	private boolean executionEnabled;
	private static final ILogNode LOG = Core.getLogger(Enum_LogNode.ZeroMQ.getCaption());
	private static final String MICROFLOW_TO_EXECUTE = "ZeroMQ.IVK_OnZMQMessage";

	@Builder
	public ZMQMessageHandler(ZMQSubscriber subscriber, ZMQ.Socket socket, IContext ctx) {

		this.subscriber = subscriber;
		this.socket = socket;
		this.ctx = ctx;
		this.executionEnabled = true;

	}

	@Override
	public void run() {

		while(executionEnabled) {

			List<IMendixObject> msgList = null;

			String topicStr = null;

			try {

				// First message is always considered to be the topic.
				final byte[] topic = socket.recv(0);

				if(Objects.nonNull(topic) && topic.length > 0)
					topicStr = new String(topic, StandardCharsets.UTF_8);
				
				// We proceed only if the configured topic matches exactly with the received topic.
				if(topicMatched(topicStr)) {
					
					msgList = extractMessages();

					if(! msgList.isEmpty()) {

						LOG.info(String.format("Received %s message(s) for the topic %s.", msgList.size(), topicStr));

						Core.executeAsync(ctx, MICROFLOW_TO_EXECUTE, true, 
								Map.of("ZMQSubscriber", subscriber.getMendixObject(),
										"MessageList", msgList));

					}
					
				}
			} catch(Exception ex) {
				// Log exception messages that occurred only when the execution is enabled. 
				if(executionEnabled)
					LOG.error(String.format("Exception while processing message for the topic %s | subscription %s:%n%s",
							subscriber.getTopic(), subscriber.getName(), Throwables.getStackTraceAsString(ex)));
			}
		}

	}
	
	private boolean topicMatched(String topicStr) {
		
		if(! Strings.isNullOrEmpty(topicStr)) {
			return topicStr.equals(subscriber.getTopic());
		}
		
		return false;
	}

	private List<IMendixObject> extractMessages() throws IOException, CoreException {

		List<IMendixObject> msgList = new ArrayList<>();

		// Subsequent messages are captured.
		while(socket.hasReceiveMore()) {

			final byte[] msg = socket.recv(0);

			if(Objects.nonNull(msg) && msg.length > 0) {
				
				try (ByteArrayInputStream bais = new ByteArrayInputStream(msg)) {
					
					Message message = new Message(ctx);
					message.setName("zmq_message");
					Core.storeFileDocumentContent(ctx, message.getMendixObject(), bais);
					Core.commit(ctx, message.getMendixObject());
					
					msgList.add(message.getMendixObject());

				}
			}

		}

		return msgList;
	}
	
	public void stopExecution() {
		executionEnabled = false;
	}

}
