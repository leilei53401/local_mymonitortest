package com.voole.ad.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.voole.ad.dao.IMonitorDao;
import com.voole.ad.model.AdSendInfo;
import com.voole.ad.utils.CommonUtils;

/**
 * @author shaoyl
 * 发送第三方监测模板缓存服务类
 */
@Service
public class AdMonitorCacheService implements InitializingBean {

	private Logger logger = LoggerFactory.getLogger(AdMonitorCacheService.class);
	
	private final Map<String, List<AdSendInfo>> template_map;
	
	private final ReentrantReadWriteLock readWriteLock;
	private final Lock read;
	private final Lock write;
	
	@Resource
	private IMonitorDao monitorDao;
	
    public AdMonitorCacheService() {
		//模板缓存
    	template_map = new HashMap<String, List<AdSendInfo>>();
		// lock
		readWriteLock = new ReentrantReadWriteLock();
		read = readWriteLock.readLock();
		write = readWriteLock.writeLock();
	}
    
    /**
     * 获取监控信息
     * @param key
     * @return
     */
	public List<AdSendInfo>  getMonitorInfo(String key){
		read.lock();
		List<AdSendInfo> templateInfos = null;
		try {
			templateInfos = this.template_map.get(key);
		} catch (Exception e) {
			logger.warn("key=[" + key + "] getMonitorInfo error", e);
		} finally {
			read.unlock();
		}
		return templateInfos;
	}
	
    /**
     * 缓存需要转化区域的第三方监测字典表
     */
    public void doAreaTransformCache(){}
	
    
	public void doRefresh() {
		logger.info("AdMonitorCacheService doRefresh start....");		
		/**
		 * 缓存模板信息
		 */
		Map<String, List<AdSendInfo>> amid_mp = new HashMap<String, List<AdSendInfo>>();

		List<AdSendInfo> monitorAmid = monitorDao.loadMonitorInfo();
	
		for (AdSendInfo adinfo : monitorAmid) {
			String s_amid = adinfo.getCreativeid();
			if (StringUtils.isNotBlank(s_amid)) {
				
				String paramStr = adinfo.getParams();
				String replaceParam = adinfo.getReplaceParams();
				List<String> paramList = Arrays.asList(paramStr
						.split("\\%.*?\\%"));
				List<String> replaceParamList = Arrays.asList(replaceParam
						.split(","));
				adinfo.setParamList(paramList);
				adinfo.setReplaceParamList(replaceParamList);
				//获取域名
				String domain = CommonUtils.getDomin(paramStr);
				adinfo.setDomain(domain);
				
				  List<AdSendInfo> list = null;
					if(amid_mp.containsKey(s_amid)){
						list = amid_mp.get(s_amid);
					}else{
						list = new ArrayList<AdSendInfo>();
					}
					list.add(adinfo);
                  amid_mp.put(s_amid, list);
			}
		}		
		
		//更新缓存
		if(null!=amid_mp && amid_mp.size()>0){
			logger.debug("amid_mp="+amid_mp.toString());
			try{
				write.lock();
				//更新模板缓存
				this.template_map.clear();
				this.template_map.putAll(amid_mp);
				
				logger.info("monitor template cache updated ... size = ["+template_map.size()+"]");
				
			} catch (Exception e) {
				logger.warn("CpInfoCacher doRefresh error", e);
			} finally {
				write.unlock();
			}
		}
			
		 logger.info("AdMonitorCacheService doRefresh end....");
	}
	
	@Override
	public void afterPropertiesSet() {
		logger.info("AdMonitorCacheService init start....");
//		doRefresh();
//		doAreaTransformCache();
		logger.info("AdMonitorCacheService init end....");
	}

	public void refresh() {
		logger.info("AdMonitorCacheService refresh start....");
//		doRefresh();
		logger.info("AdMonitorCacheService refresh end....");
	}
	
	public void refreshTransformCache() {
		logger.info("refreshTransformCache refresh start....");
//		doAreaTransformCache();
		logger.info("refreshTransformCache refresh end....");
	}
	
}
