package com.voole.ad.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.voole.ad.model.AdSendInfo;
import com.voole.ad.queue.AdTimeOutQueue;
import com.voole.ad.utils.HttpConnectionMgrUtils;

/**
 * 
 * 发送广告超时处理
 * @author Administrator
 *
 */
@Service
public class AdTimeOutSendServiceImpl extends Thread{
	private Logger logger = LoggerFactory.getLogger(AdTimeOutSendServiceImpl.class);
	private AdTimeOutQueue<AdSendInfo> adqueue;
	private HttpConnectionMgrUtils httpMgr;
	@Resource
	private AdTimeOutRecordFileServiceImpl recordFileImpl;
	public AdTimeOutSendServiceImpl(){
		adqueue = new AdTimeOutQueue<AdSendInfo>();
		httpMgr = new HttpConnectionMgrUtils();
		this.start();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		while(true){
			this.handler();
		}
	}
	
	/**
	 * 
	 */
	public void handler(){
		try {
			//read queue 
			AdSendInfo adinfo = adqueue.getData();
			//TODO 1.重试发送   2.备份文件
			int statusCode = httpMgr.invokeGetUrl(adinfo);
//			int statusCode = httpMgr.invokeGetHttpUrl(adinfo);
			if(statusCode != 200){
				recordFileImpl.writeFile2(adinfo,statusCode);
			}
		} catch (InterruptedException e) {
			logger.error("read queue data exception", e);
		}
	}
	
	
	/**
	 * 添加到阻塞队列
	 * @param adplaystat
	 */
	public void putLogToQueue(AdSendInfo adinfo){
		try {
			adqueue.putqueue(adinfo);
		} catch (InterruptedException e) {
			logger.error("put adUrl To queue exception,data=" + adinfo.getDomain() +  adinfo.getParams(),e);
		}
	}
}
