package com.voole.ad.dao;

import java.util.List;
import java.util.Map;

import com.voole.ad.model.AdSendInfo;

/**
 * @author Administrator
 *
 */
public interface IMonitorDao {

	/**
	 * 加载监控节目信息
	 * @return
	 */
	public List<AdSendInfo> loadMonitorInfo();
	
}
