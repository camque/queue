package com.github.camque.queue;

public interface IInitConsumers {

	boolean validateConnection();

	void reconnect();

}
