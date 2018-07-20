package com.voole.ad.queue;

import com.voole.ad.utils.GlobalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LocalBlockQueue<T> {


	private Logger logger = LoggerFactory.getLogger(AdTimeOutQueue.class);
	private BlockingQueue<T> queue;
	private int max = GlobalProperties.getInteger("ad.block.queue.max");

	public LocalBlockQueue() {
		queue = new ArrayBlockingQueue<T>(max);
	}
	
	
	/**
	 * 队列中添加数据
	 * 
	 * @param t
	 * @throws InterruptedException 
	 */
	public boolean putqueue(T t) throws InterruptedException {
		if (t == null) {
			logger.error("put queue exception, put data is null");
			return false;
		}
		queue.put(t);
		return true;
	}
	
	
	public T getData() throws InterruptedException {
		return queue.take();
	}
}
