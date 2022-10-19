# ZeroMQ

ZeroMQ is a minimalistic in memory message queuing library.

The connector is aimed at supporting **PUB-SUB** sockets and a forwarder component that acts as an interface for external systems to publish and subscribe messages.

Refer to the [docs](https://zeromq.org/) for more information.

## Features

Refer to the **UseMe** folder for configuration related information.

### Subscription configuration

Multiple Subscription configuration is supported by persisting the connection information via entity **ZMQSubscriber**.

Use snippet **SNIP_ZMQSubscriberConfiguration** to visualize subscription configuration overview.

Configurations are created with the page **ZMQSubscriber_NewEdit.**

> The **Save** action performs necessary validations and initiate or remove subscription for the configuration via **ACT_SaveZMQSubscriber** microflow. If you wish to use your own New/Edit page, please configure the microflow **ACT_SaveZMQSubscriber** for the **Save** action to avoid issues.

#### Name

Unique name for the configuration.

#### Subscription type

**Local**

Use this when the forwarder component is enabled. Refer Forwarder component section for more information. By this way, the configuration is setup to receive messages from the local publisher. **Host** and **Port** fields will be hidden for this configuration type.

**External**

This can be used with the forwarder component enabled or disabled. This configuration type connects to the external publishers.

#### Host

Host if the subscription type is **External**.

#### Port

Port if the subscription type is **External**.

#### Topic

Topic to subscribe.

#### Active

**Yes** - Initiates subscription for the specified topic.

**No** - Removes subscription for the specified topic.

### Forwarder component

Forwarder component receives messages from several publishers and forwards them to multiple subscribers.

This component will be enabled if the constant **ForwarderProxyEnabled** is set to **True**.

It requires subscriber port to receive messages from several publishers and a publisher port to forward those messages for multiple subscribers.

The ports are configured via the following constants.

**SubscriberPort** - Defaulted to 5559

**PublisherPort** - Defaulted to 5560

> **Note:**
>
> With the forwarder component enabled and the subscription type for the configuration set to Local, subscription sockets are opened to listen on localhost and **PublisherPort**.

### After startup

Please ensure to place the **ASU_Startup** microflow in your existing after startup microflow as sub microflow (or) in the Runtime settings of the project.

This ensures to start the forwarder component if enabled and open sockets for the active subscriptions configured during the application startup.

### Before shutdown

Please ensure to place the **BSD_Shutdown** microflow in your existing before shutdown microflow as sub microflow (or) in the Runtime settings of the project.

This ensures to close the sockets initialized for the active configured subscriptions and shutdown the forwarder component if enabled.

### On Message microflow

Each message is persisted in the form of file document and consolidated into a list. On message reception, microflow **IVK_OnZMQMessage** will be invoked with the following parameters.

**ZMQSubscriber** - Configuration responsible for the subscription.

**MessageList** - List of messages as specialized file document objects for the configured subscription.

> **Important Note:**
>
> 1. The first message should always contain the topic information as per the ZeroMQ standards, hence it shall be used for comparison with the configured topic and will not be considered for the message preparation. All the subsequent messages are captured as specialized file document objects.
> 2. This implementation supports multipart message reception, where the sender can publish multiple messages as a block and it would be combined together as list of file document objects.
> 3. The microflow is invoked asynchronously to avoid performance problems on long running operations. If the messages are to be processed in queue based fashion, it is up to the implementor to handle this behavior.
> 4. Delete MessageList objects after completing the operations to avoid stale data and files being persisted.

## Limitations

-   Supports PUB-SUB sockets at the moment.
-   The publisher(s) or subscriber(s) are not notified during disconnection. Connection will be re-established when the components are up again. This is the behavior of the ZeroMQ component itself which makes it lightweight. However, we can employ customized timeouts to handle such situations (or) choose a message broker like RabbitMQ 😉.
