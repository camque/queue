package com.github.camque.queue.commons.constants;

public interface IQueueNamesConstants {
	String DOMAIN_QUASAR = "quasar.";
	String TYPE_QUEUE = ".queue";
	String TYPE_TOPIC = ".topic";

	String CONNECTION_FACTORY = "ConnectionFactory";

	//Controladas por SMART
	String ESME_QUEUE = DOMAIN_QUASAR + "esme" + TYPE_QUEUE;
	String SMSC_QUEUE = DOMAIN_QUASAR + "smsc" + TYPE_QUEUE;
}