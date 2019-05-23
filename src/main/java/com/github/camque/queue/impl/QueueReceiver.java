package com.github.camque.queue.impl;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.camque.queue.IConsumer;
import com.github.camque.queue.IConsumerMXBean;
import com.github.camque.queue.commons.utils.StringUtils;

public abstract class QueueReceiver implements IConsumer, IConsumerMXBean {

	private static final Logger LOG = LogManager.getLogger(QueueReceiver.class);

	protected Connection connection = null;
	protected Session session = null;
	protected MessageConsumer consumer = null;

	public QueueReceiver(final Connection connection, final String queueName) {
		this.connection = connection;
		this.initConsumer(queueName);
	}

	protected void initConsumer(final String queueName) {
		LOG.info("Starting Consumer [" + this.getClass().getSimpleName() + "]");

		try {

			this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			this.consumer = this.session.createConsumer( this.session.createQueue(queueName) );

			//Listener
			final MessageListener listener = new MessageListener() {

				@Override
				public void onMessage(final Message message) {
					QueueReceiver.this.proccessMessage(message);
				}

			};
			this.consumer.setMessageListener(listener);

		} catch (final JMSException e) {
			LOG.error( StringUtils.getStackTrace(e) );
			this.closeConsumer();
        }

	}

	protected abstract void proccessMessage(Message message);

	public String getComponent() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void closeConsumer() {
		LOG.info("Closing Consumer [" + this.getClass().getSimpleName() + "]");
    	try {

    		if ( this.consumer != null ){
    			this.consumer.close();
            }
            if ( this.session != null ){
            	this.session.close();
            }

		} catch (final JMSException e) {
			LOG.error( StringUtils.getStackTrace(e) );
		}

	}

}
