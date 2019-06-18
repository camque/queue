package com.github.camque.queue.impl;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.DependsOn;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.camque.queue.IConsumer;
import com.github.camque.queue.IInitConsumers;
import com.github.camque.queue.IInitConsumersLocal;
import com.github.camque.queue.IInitQueueLocal;
import com.github.camque.queue.commons.constants.IConstants;
import com.github.camque.queue.commons.constants.IParameterConstants;
import com.github.camque.queue.commons.utils.ParameterUtils;
import com.github.camque.queue.commons.utils.StringUtils;
import com.github.camque.queue.thread.EnquireLinkTask;

@DependsOn("InitQueue")
@Startup
@Singleton
@ConcurrencyManagement(BEAN)
@LocalBean
public class InitConsumers implements IInitConsumersLocal, IInitConsumers {

	private static final Logger LOG = LogManager.getLogger(InitConsumers.class);

	@Inject
	private IInitQueueLocal initQueue;

	private Map<String, List<IConsumer>> consumers;

	private ScheduledExecutorService enquireLinkExecutor;
	private ScheduledFuture<?> enquireLinkTask;

	private String appName;

	public InitConsumers() {
		try {
			this.appName = InitialContext.doLookup(IConstants.JNDI_APP_NAME);
			this.enquireLinkExecutor = Executors.newSingleThreadScheduledExecutor();
			this.runEnquireLinkTask();
		} catch (final NamingException e) {
			LOG.error( StringUtils.getStackTrace(e) );
		}
	}

	@PostConstruct
	@Override
	public void init() {
		LOG.info("Starting Consumers");
		
		int defaultSizeConsumer = Integer.parseInt( ParameterUtils.getParam(IParameterConstants.CONSUMERS_DEFAULT_SIZE) );

		if ( this.validateConnection() ) {
			this.consumers = new HashMap<>();

			try {
				List<String> queueNames = StringUtils.separatedListToList( ParameterUtils.getParam(IParameterConstants.CONSUMERS_NAME), ",");
				for (String queueName : queueNames) {
					
					String queueAppName = ParameterUtils.getParam( MessageFormat.format(IParameterConstants.CONSUMERS_QUEUE_NAME, queueName) );
					if ( this.appName.equals(queueAppName) ) {
						String receiverName = ParameterUtils.getParam( MessageFormat.format(IParameterConstants.CONSUMERS_RECEIVER_CLASS_NAME, queueName ) );
						
						this.consumers.put(queueName, new ArrayList<IConsumer>() );
						
						int sizeConsumer = Integer.parseInt( ParameterUtils.getParam( MessageFormat.format(IParameterConstants.CONSUMERS_QUEUE_SIZE, queueName) ) );
						
						if ( sizeConsumer == 0 ) {
							sizeConsumer = defaultSizeConsumer;
						}
						
						for(int i=0; i<sizeConsumer; i++) {

							@SuppressWarnings("rawtypes")
							final Class classConsumer = Class.forName( receiverName );

							@SuppressWarnings("unchecked")
							final IConsumer consumer = (IConsumer) classConsumer
								.getDeclaredConstructor(Connection.class, String.class)
								.newInstance(this.initQueue.getConnection(), queueName);

							this.consumers.get( queueName ).add( consumer );
						}
						
					}
					
				}

			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
				LOG.error( StringUtils.getStackTrace(e) );
			}
		}

	}

	@PreDestroy
	private void close() {
		LOG.info("Closing Consumers");

		this.enquireLinkTask.cancel(true);
		this.closeConsumers();
	}

	private void closeConsumers() {
		if ( this.consumers!=null && !this.consumers.isEmpty() ) {
			this.consumers.forEach((key, listConsumers) -> {
				if ( listConsumers!=null && !listConsumers.isEmpty() ) {
					listConsumers.forEach( consumer -> consumer.closeConsumer() );
				}
			});
		}
	}

	/**
	 * @see com.mobiera.smart.queue.IInitConsumers#validateConnection()
	 */
	@Override
	public boolean validateConnection() {
		boolean response = true;
		try {
			if ( this.initQueue.getConnection()==null || this.initQueue.getConnection().getClientID()==null ) {
				response = false;
			}
		} catch (final JMSException e) {
			LOG.error( StringUtils.getStackTrace(e) );
			response = false;
		}
		return response;
	}

	private void runEnquireLinkTask() {
		this.enquireLinkTask = this.enquireLinkExecutor.scheduleWithFixedDelay(new EnquireLinkTask(this), 5, 15, TimeUnit.SECONDS);
	}

	/**
	 * @see com.mobiera.smart.queue.IInitConsumers#scheduleReconnect()
	 */
	@Override
	public void reconnect() {
		this.closeConsumers();
		if ( this.initQueue.reconnect() ) {
			this.init();
		}
	}

}
