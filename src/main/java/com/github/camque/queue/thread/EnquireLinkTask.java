package com.github.camque.queue.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.camque.queue.IInitConsumersLocal;

public class EnquireLinkTask implements Runnable {

	private static final Logger LOG = LogManager.getLogger(EnquireLinkTask.class);

	private IInitConsumersLocal initConsumers;

	public EnquireLinkTask(final IInitConsumersLocal initConsumers) {
		super();
		this.initConsumers = initConsumers;
	}

	@Override
	public void run() {
		LOG.debug("sending enquire_link");
		if ( !this.initConsumers.validateConnection() ) {
			LOG.warn("Enquire link failed, executing reconnect");
			this.initConsumers.reconnect();
		}
	}

}
