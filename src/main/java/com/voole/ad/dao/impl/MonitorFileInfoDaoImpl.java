package com.voole.ad.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.voole.ad.dao.BaseDao;
import com.voole.ad.dao.IMonFileInfoDao;
import com.voole.ad.model.MonFileInfo;



/**
 * @author shayl
 *
 */
@Repository
public class MonitorFileInfoDaoImpl extends BaseDao implements IMonFileInfoDao {
	
	private Logger logger = LoggerFactory.getLogger(MonitorFileInfoDaoImpl.class);

	/* (non-Javadoc)
	 * @see com.voole.ad.dao.IMonitorDao#loadMonitorAmid()
	 */
	@Override
	public List<MonFileInfo> getFiles() {
		return this.getSqlSession().selectList("monitorfile.getFiles");
	}

	@Override
	public int updateFilesToDownLoading(List<String> ids) {
		return this.getSqlSession().update("monitorfile.updateFilesToDownLoading", ids);
	}
	
	@Override
	public List<MonFileInfo> getFilesAndChangeStatusToDownloading() {
//		return this.getSqlSession().update("monitorfile.cutFilesToHistory", names);
		SqlSession sessionnew =	this.getSqlSession().getSqlSessionFactory().openSession(false);
		List<MonFileInfo> toDownFiles = null;
		int size = 0;
		try {
			toDownFiles = sessionnew.selectList("monitorfile.getFiles");
			List<String> toDownFileIds = new ArrayList<String>();
			for (MonFileInfo  toDownFile: toDownFiles) {
				//记录ids
				toDownFileIds.add(toDownFile.getId()+"");
			}
			logger.info("toDownFileIds="+toDownFileIds);
		
			if(null!=toDownFileIds && toDownFileIds.size()>0){
				size = sessionnew.update("monitorfile.updateFilesToDownLoading", toDownFileIds);
				logger.info("update size = " + size);
			}
			sessionnew.commit();
		} catch (Exception e) {
			logger.error("获取文件并更新状体出错：",e);
			return null;
		}finally{
			sessionnew.close();
		}
		
		if(size>0){
			return toDownFiles;
		}else{
			return null;
		}
	
	
	}
	
	@Override
	public int updateFilesDownException(List<String> names) {
		return this.getSqlSession().update("monitorfile.updateFilesDownException", names);
	}

	@Override
	public int updateFilesToParsed(List<String> names) {
		return this.getSqlSession().update("monitorfile.updateFilesToParsed", names);
	}
	
	@Override
	public int updateFilesToException(List<String> names) {
		return this.getSqlSession().update("monitorfile.updateFilesToException", names);
	}

	@Override
	public int cutFilesToHistory(List<String> names) {
//		return this.getSqlSession().update("monitorfile.cutFilesToHistory", names);
		SqlSession sessionnew =	this.getSqlSession().getSqlSessionFactory().openSession(false);
		int size = 0;
		try {
			sessionnew.update("monitorfile.copyFilesToHistory", names);
			size = sessionnew.update("monitorfile.delOldFiles", names);
			sessionnew.commit();
		} catch (Exception e) {
			logger.error("剪切解析完成文件出错：",e);
		}finally{
			sessionnew.close();
		}
		return size;
	}
	
}
