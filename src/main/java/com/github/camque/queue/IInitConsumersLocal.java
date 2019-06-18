package com.github.camque.queue;

import javax.ejb.Local;

@Local
public interface IInitConsumersLocal {

	void init();
	
	boolean validateConnection();

	void reconnect();

}
