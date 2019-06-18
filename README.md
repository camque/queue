Queue by ZeroMetal
================================================


Installation
------------

Library is available via maven central

    <dependency>
        <groupId>com.github.camque</groupId>
        <artifactId>queue</artifactId>
        <version>1.0.0</version>
    </dependency>

Tutorial
---------------------

**Configuration file:** In the configuration folder of your application container create a file called queue.properties, which will contain the configuration of your queues and how to connect. If you are not sure which is the configuration folder at the start of your application container look for a line similar to jboss.server.config.dir, this will indicate which is the desired folder.

	brokerName=localhost
	brokerUrl=tcp://localhost:61616
	consumers.default.size=4
	consumers.name=queue1,queue2
	consumers.queue1.receiver.class.name=com.foo.async.Queue1Receiver
	consumers.queue1.size=20
	consumers.queue2.receiver.class.name=com.foo.async.Queue2Receiver
	consumers.queue2.size=30

- brokerName: Name of your ActiveMQ broker.
- brokerUrl: Connection URL to your broker.
- default.size: Default size of the consumer, this is used if not defined in the following sections.
- consumers.name: Name of the queues that you will define. This name must be the same as the one defined in the keys of the following lines.
- consumers.[queueName].receiver.class.name: Name of the class that will act as the consumer of the queue.
- consumers.[queueName].size: Number of consumers that will have the queue.

**Startup consumers:** Create a boot singleton in the following way to boot the consumers
```java
@Startup
@Singleton
@ConcurrencyManagement(BEAN)
@LocalBean
public class InitQueue {

	@Inject
	private IInitConsumers initConsumers;

}
```

**Defining consumers: ** Create a class that extends from the *QueueReceiver* class and overrides the *proccessMessage* method. The constructor of this class must always receive the 2 parameters of the example.

```java
public class Queue1Receiver extends QueueReceiver {

	private static final Logger LOG = LogManager.getLogger(Queue1Receiver.class);

	public Queue1Receiver(Connection connection, String queueName) {
		super(connection, queueName);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void proccessMessage(Message message) {
		LOG.info("Message Arrived");
		Map<String, Object> mapMessage = null;

		try {

			if ( message instanceof ObjectMessage ){
				mapMessage = (Map<String, Object>) ((ObjectMessage) message).getObject();

				//Business Logic
			}

		} catch (final JMSException e) {
			LOG.error( StringUtils.getStackTrace(e) );
		}
	}

}
```
After getting the message the business logic depends on what you want to do, for example you could do a bean lookup to execute a method.

**Defining producers: ** To define a producer, create a class that injects the *IInitQueue* interface into an attribute of its class, then take the *sendMessage* method as a reference to send a message to your queue.

```java
public class Queue1Producer {

	private static final Logger LOG = LogManager.getLogger(Queue1Producer.class);

	@Inject
	private IInitQueue initQueue;

	@Asynchronous
	public void sendMessage(final Object obj) {
		Session session = null;
        MessageProducer producer = null;
		try {
			session = this.initQueue.getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer( session.createQueue("queue1") );
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            final Map<String, Object> mapMessage = this.mappingSubmit(obj);

            final ObjectMessage objectMessage = session.createObjectMessage();
            objectMessage.setObject((Serializable) mapMessage);

            producer.send(objectMessage);

            LOG.debug("Message Send to queue1");

        } catch (final JMSException e) {
        	LOG.error( StringUtils.getStackTrace(e) );
        } finally {
        	try {

        		if ( producer != null ){
                	producer.close();
                }
                if ( session != null ){
                	session.close();
                }

			} catch (final JMSException e) {
				LOG.error( StringUtils.getStackTrace(e) );
			}
		}
	}

	private Map<String, Object> mappingSubmit(Object obj) {
		// Business Logic to map
		return null;
	}

}
```

License
-------
GNU General Public License v3
