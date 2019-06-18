package com.github.camque.queue;

import javax.ejb.Remote;

@Remote
public interface IInitConsumers {

	void init();

}
