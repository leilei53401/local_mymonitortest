package com.voole.ad.dao.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.voole.ad.dao.BaseDao;
import com.voole.ad.dao.IMonitorDao;
import com.voole.ad.model.AdSendInfo;
import com.voole.ad.utils.datasources.DataSourceType;



/**
 * @author Administrator
 *
 */
@Repository
public class MonitorDaoImpl extends BaseDao implements IMonitorDao {

	/* (non-Javadoc)
	 * @see com.voole.ad.dao.IMonitorDao#loadMonitorAmid()
	 */
	@Override
	public List<AdSendInfo> loadMonitorInfo() {
		return this.getSqlSession().selectList("monitor.loadMonitorInfo");
	}

}
