package com.voole.ad.main;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.voole.ad.dao.IMonFileInfoDao;
import com.voole.ad.ftp.FTPClientService;
import com.voole.ad.ftp.FTPClientTools;
import com.voole.ad.model.MonFileInfo;


/**
 * 下载文件任务
 * @author shaoyl
 *
 */
@Component("StartDownLoadJob")
public class StartDownLoadJob {
	protected static Logger logger = Logger.getLogger(StartDownLoadJob.class);
	
	@Autowired
	public FTPClientService ftpClientService;
	@Resource
	private IMonFileInfoDao monFileInfoDao;
	
	//下载任务入口
	public void start(){
		
//		List<String> toDownFileIds = new ArrayList<String>();
		//获取要下载的文件		
//		List<MonFileInfo> toDownFiles = monFileInfoDao.getFiles();
		List<MonFileInfo> toDownFiles = monFileInfoDao.getFilesAndChangeStatusToDownloading();
		
		//分ftp host 处理文件
		Map<String,Object> toDownMap = new HashMap<String,Object>();
		if(null!=toDownFiles && toDownFiles.size()>0){
			logger.info("获取到要下载的文件["+toDownFiles.size()+"]个，toDownFiles="+toDownFiles);
			for (MonFileInfo  toDownFile: toDownFiles) {
				//记录ids
	//			toDownFileIds.add(toDownFile.getId()+"");
			
				String host = toDownFile.getFhost();
				
				if(null!=toDownMap.get(host)){
					List<MonFileInfo> tmpFiles = (List<MonFileInfo>)toDownMap.get(host);
					tmpFiles.add(toDownFile);
				}else{
					List<MonFileInfo> tmpFiles = new ArrayList<MonFileInfo>();
					tmpFiles.add(toDownFile);
					toDownMap.put(host, tmpFiles);
				}
			}	
		}else{
			logger.info("未获取到要下载的文件！");
		}
//		logger.info("toDownFileIds="+toDownFileIds);
		//更新文件的状态为正在下载
//		if(null!=toDownFileIds && toDownFileIds.size()>0){
//			monFileInfoDao.updateFilesToDownLoading(toDownFileIds);
//		}
		logger.info("toDownMap="+toDownMap);
		//执行下载
		List<String> expDownloadFiles = new ArrayList<String>();
		Map<String,Integer> downLoadStatusMapAll = ftpClientService.doDownLoad(toDownMap);
		Iterator<String> itStatus = downLoadStatusMapAll.keySet().iterator();
		while(itStatus.hasNext()){
			String name = itStatus.next();
			Integer status = downLoadStatusMapAll.get(name);
			if(FTPClientTools.DOWNLOAD_ERR==status){
				expDownloadFiles.add(name);
			}
		}
		//将异常下载的文件更新状态
		if(null!=expDownloadFiles&&expDownloadFiles.size()>0){
			monFileInfoDao.updateFilesToException(expDownloadFiles);
		}
	
	}
	
}
