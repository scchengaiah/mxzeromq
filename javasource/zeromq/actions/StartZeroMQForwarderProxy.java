// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package zeromq.actions;

import com.google.common.base.Throwables;
import com.mendix.core.Core;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import zeromq.custom.ZMQForwarderProxyHelper;
import zeromq.proxies.Enum_LogNode;
import zeromq.proxies.constants.Constants;

/**
 * Start ZeroMQ forwarder proxy.
 */
public class StartZeroMQForwarderProxy extends CustomJavaAction<java.lang.Boolean>
{
	public StartZeroMQForwarderProxy(IContext context)
	{
		super(context);
	}

	@java.lang.Override
	public java.lang.Boolean executeAction() throws Exception
	{
		// BEGIN USER CODE
		
		// If not enabled then it means the module is to be utilized for external subscriptions.
		if(! Constants.getForwarderProxyEnabled())
			return true;
		
		// If enabled then it means the module is to be utilized for local and external subscriptions.
		try {
			
			ZMQForwarderProxyHelper.getInstance().start();
			
			LOG.info(String.format("Started ZMQ forwarder proxy - Publisher port: %s | Subscriber port: %s", 
					Constants.getPublisherPort(), Constants.getSubscriberPort()));
			
			return true;
			
		} catch (Exception ex) {
			LOG.critical(String.format("Exception encountered while starting ZMQ forwarder:%n%s", 
					Throwables.getRootCause(ex).getMessage()));
			
			return false;
		}
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "StartZeroMQForwarderProxy";
	}

	// BEGIN EXTRA CODE
	private static final ILogNode LOG = Core.getLogger(Enum_LogNode.ZeroMQ.getCaption());
	// END EXTRA CODE
}