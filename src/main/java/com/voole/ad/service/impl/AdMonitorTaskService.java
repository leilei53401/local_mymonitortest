package com.voole.ad.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("AdMonitorTaskService")
public class AdMonitorTaskService {
	
	private Logger logger = LoggerFactory.getLogger(AdMonitorTaskService.class);
	
	@Autowired
	private AdMonitorCacheService adMonitorCacheService;
	/**
	 * 监测模板缓存刷新
	 */
	public void refreshCache() {
		try {
			adMonitorCacheService.refresh();
		} catch (Exception e) {
			logger.error("监测模板缓存刷新失败", e);
		}
	}
		
	/**
	 * 需要转化区域编码的第三方监测字典表缓存更新
	 */
	public void refreshAreaCache() {
		try {
			//adMonitorCacheService.refreshTransformCache();
		} catch (Exception e) {
			logger.error("监测模板缓存刷新失败", e);
		}
	}

}
