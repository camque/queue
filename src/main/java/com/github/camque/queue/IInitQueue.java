package com.github.camque.queue;

import javax.ejb.Remote;
import javax.jms.Connection;

@Remote
public interface IInitQueue {

	void init();

	Connection getConnection();

}
