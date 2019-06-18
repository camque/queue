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

Configuration file
In the configuration folder of your application container create a file called queue.properties, which will contain the configuration of your queues and how to connect. If you are not sure which is the configuration folder at the start of your application container look for a line similar to jboss.server.config.dir, this will indicate which is the desired folder.

	brokerName=localhost
	brokerUrl=tcp://localhost:61616
	consumers.default.size=4
	consumers.name=queue1,queue2
	consumers.queue1.app.name=test-ear
	consumers.queue1.receiver.class.name=com.foo.async.Queue1Receiver
	consumers.queue1.size=20
	consumers.queue2.app.name=test-ear
	consumers.queue2.receiver.class.name=com.foo.async.Queue1Receiver
	consumers.queue2.size=30

Inject the next bean into the class you are going to audit:

    @Startup
    @Singleton
    @ConcurrencyManagement(BEAN)
    @LocalBean
    public class InitQueue {
    	@Inject
    	private IInitConsumers initConsumers;
    }

Finally, in the desired method include the following lines of code at the beginning and end of the method, or where you want to audit the execution:

    private void methodJMX() {
        final long startTrx = System.currentTimeMillis();
    
        //Any business code
    
        this.recorder.registerEvent(this.getClass(), "methodJMX", System.currentTimeMillis() - startTrx, true);
    }




License
-------
GNU General Public License v3
