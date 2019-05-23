package com.github.camque.queue.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.camque.queue.impl.InitConsumers;

public class EnquireLinkTask implements Runnable {

	private static final Logger LOG = LogManager.getLogger(EnquireLinkTask.class);

	private InitConsumers initConsumers;

	public EnquireLinkTask(final InitConsumers initConsumers) {
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
