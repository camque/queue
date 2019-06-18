package com.github.camque.queue;

import javax.ejb.Local;
import javax.jms.Connection;

@Local
public interface IInitQueueLocal {

	void init();

	Connection getConnection();

	boolean reconnect();

}
