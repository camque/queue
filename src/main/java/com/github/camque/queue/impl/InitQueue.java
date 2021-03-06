package com.github.camque.queue.impl;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.camque.queue.IInitQueue;
import com.github.camque.queue.IInitQueueLocal;
import com.github.camque.queue.commons.constants.IParameterConstants;
import com.github.camque.queue.commons.utils.ParameterUtils;
import com.github.camque.queue.commons.utils.StringUtils;

/**
 * Session Bean implementation class InitQueue
 */
@Startup
@Singleton
@ConcurrencyManagement(BEAN)
@LocalBean
public class InitQueue implements IInitQueueLocal, IInitQueue {

	private static final Logger LOG = LogManager.getLogger(InitQueue.class);

	private ConnectionFactory connectionFactory = null;
	private Connection connection = null;

	private String urlBroker;
	
	@PostConstruct
	@Override
	public void init() {
		this.urlBroker = ParameterUtils.getParam(IParameterConstants.BROKER_URL);
		LOG.debug("ULR_BROKER=" + this.urlBroker);
		
		if ( this.urlBroker != null ) {
			this.reconnect();
		}
	}

	private boolean startConnection() {
		LOG.info("Starting Queue Connection");
		boolean response = false;
		try {
			//Start conection
			this.connectionFactory = new ActiveMQConnectionFactory(this.urlBroker);
			this.connection = this.connectionFactory.createConnection();
			this.connection.start();

			response = true;

		} catch (final JMSException e) {
			LOG.error( StringUtils.getStackTrace(e) );
		}

		return response;
	}

	@PreDestroy
	private void close() {
		LOG.info("Closing Queue Connection");
		try {

			if ( this.connection != null ){
	        	this.connection.close();
	        }
			this.connectionFactory = null;

		} catch (final JMSException e) {
			LOG.error( StringUtils.getStackTrace(e) );
		}
	}

	@Override
	public Connection getConnection() {
		return this.connection;
	}

	@Override
	public boolean reconnect() {
		this.close();
		return this.startConnection();
	}

}
