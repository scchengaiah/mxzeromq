// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package zeromq.actions;

import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import zeromq.custom.ZMQSubscriptionHelper;
import com.mendix.systemwideinterfaces.core.IMendixObject;

/**
 * Remove subscriptions based on the configuration.
 */
public class RemoveSubscriptions extends CustomJavaAction<java.lang.Void>
{
	private java.util.List<IMendixObject> __zmqSubscriberList;
	private java.util.List<zeromq.proxies.ZMQSubscriber> zmqSubscriberList;

	public RemoveSubscriptions(IContext context, java.util.List<IMendixObject> zmqSubscriberList)
	{
		super(context);
		this.__zmqSubscriberList = zmqSubscriberList;
	}

	@java.lang.Override
	public java.lang.Void executeAction() throws Exception
	{
		this.zmqSubscriberList = new java.util.ArrayList<zeromq.proxies.ZMQSubscriber>();
		if (__zmqSubscriberList != null)
			for (IMendixObject __zmqSubscriberListElement : __zmqSubscriberList)
				this.zmqSubscriberList.add(zeromq.proxies.ZMQSubscriber.initialize(getContext(), __zmqSubscriberListElement));

		// BEGIN USER CODE
		ZMQSubscriptionHelper.getInstance().remove(zmqSubscriberList);
		return null;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "RemoveSubscriptions";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}
