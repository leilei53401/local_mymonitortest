package com.voole.ad.ftp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.voole.ad.model.MonFileInfo;

/**
 * ftp下载服务入口类
 * 多个ftp连接在此处管理
* @author shaoyl
* @date 2017-4-13 下午5:05:51 
* @version V1.0
 */
public class FTPClientService {
	private static Logger logger = Logger.getLogger(FTPClientService.class);
	/*FTPClientTools ftpClientTools_1;
	FTPClientTools ftpClientTools_2;*/
	
	Map<String,FTPClientTools> ftpClientToolsMap;
	
	/**
	 * 下载文件
	 * @param toDownMap
	 * @return
	 */
	public Map<String,Integer> doDownLoad(Map<String,Object> toDownMap){
		//记录所有文件下载状态
		Map<String,Integer> downLoadStatusMapAll = new HashMap<String,Integer>();

		
		Iterator<String> ftpToolsMapIt = ftpClientToolsMap.keySet().iterator();
		while(ftpToolsMapIt.hasNext()){
			String host = ftpToolsMapIt.next();
			FTPClientTools ftpClientTools = ftpClientToolsMap.get(host);
			
            //处理下载文件
			List<MonFileInfo>  toDownFiles = (List<MonFileInfo>)toDownMap.get(host);
			
			if(null!=toDownFiles && toDownFiles.size()>0){
				List<String> files = getFiles(toDownFiles);
				//记录下载状态
				Map<String,Integer> downLoadStatusMap = new HashMap<String,Integer>();
				//执行下载
				try {
					downLoadStatusMap = ftpClientTools.downFiles(files,false);
				} catch (FTPClientException e) {
					logger.error("下载ftp host=["+host+"]上文件出错:",e);
				}
				//下载正常的文件可删除ftp上文件
				List<String> toDelFiles = new ArrayList<String>();
				
				Iterator<String> itStatus = downLoadStatusMap.keySet().iterator();
				while(itStatus.hasNext()){
					String name = itStatus.next();
					Integer status = downLoadStatusMap.get(name);
					if(FTPClientTools.DOWNLOAD_OK==status){
						toDelFiles.add(name);
					}
				}
			
				//删除
			/*	try {
					String[] delFiles = new String[toDelFiles.size()];
					toDelFiles.toArray(delFiles);
					ftpClientTools_1.delete(delFiles, true);
				} catch (FTPClientException e) {
					logger.error("删除ftp文件出错:",e);
				}*/
				
				//备份到backup目录下
				ftpClientTools.backupFiles(toDelFiles, true);
				//记录下载状态
				downLoadStatusMapAll.putAll(downLoadStatusMap);
				
			}else{
				logger.warn("未获取到 host=["+host+"]下要下载的文件!");
			}
			
		}
		return downLoadStatusMapAll;
	}
		
	/*
	 public Map<String,Integer> doDownLoad_bak(Map<String,Object> toDownMap){
		
		Map<String,Integer> downLoadStatusMapAll = new HashMap<String,Integer>();
		//Map<String,ArrayList<MonFileInfo>>
		Iterator<String> itDown = toDownMap.keySet().iterator();
		while(itDown.hasNext()){
			String host = itDown.next();
			if(host.equals(ftpClientTools_1.getHost())){
				List<MonFileInfo>  toDownFiles = (List<MonFileInfo>)toDownMap.get(host);
				List<String> files = getFiles(toDownFiles);
				//记录下载状态
				Map<String,Integer> downLoadStatusMap = new HashMap<String,Integer>();
				//执行下载
				try {
					downLoadStatusMap = ftpClientTools_1.downFiles(files,false);
				} catch (FTPClientException e) {
					logger.error("下载ftp host=["+host+"]上文件出错:",e);
				}
				//下载正常的文件可删除ftp上文件
				List<String> toDelFiles = new ArrayList<String>();
				
				Iterator<String> itStatus = downLoadStatusMap.keySet().iterator();
				while(itStatus.hasNext()){
					String name = itStatus.next();
					Integer status = downLoadStatusMap.get(name);
					if(FTPClientTools.DOWNLOAD_OK==status){
						toDelFiles.add(name);
					}
				}
			
				//删除
				try {
					String[] delFiles = new String[toDelFiles.size()];
					toDelFiles.toArray(delFiles);
					ftpClientTools_1.delete(delFiles, true);
				} catch (FTPClientException e) {
					logger.error("删除ftp文件出错:",e);
				}
				
				//备份到backup目录下
				ftpClientTools_1.backupFiles(toDelFiles, true);
				//记录下载状态
				downLoadStatusMapAll.putAll(downLoadStatusMap);
				
			}else if(host.equals(ftpClientTools_2.getHost())){
				List<MonFileInfo>  toDownFiles = (List<MonFileInfo>)toDownMap.get(host);
				List<String> files = getFiles(toDownFiles);
				//记录下载状态
				Map<String,Integer> downLoadStatusMap = new HashMap<String,Integer>();
				//执行ftp2下载
				try {
					downLoadStatusMap =	ftpClientTools_2.downFiles(files,false);
				} catch (FTPClientException e) {
					logger.error("下载ftp host=["+host+"]上文件出错:",e);
				}
				
				//下载正常的文件可删除ftp上文件
				List<String> toDelFiles = new ArrayList<String>();
				
				Iterator<String> itStatus = downLoadStatusMap.keySet().iterator();
				while(itStatus.hasNext()){
					String name = itStatus.next();
					Integer status = downLoadStatusMap.get(name);
					if(FTPClientTools.DOWNLOAD_OK==status){
						toDelFiles.add(name);
					}
				}
				
				//删除
				try {
					String[] delFiles = new String[toDelFiles.size()];
					toDelFiles.toArray(delFiles);
					ftpClientTools_2.delete(delFiles, true);
				} catch (FTPClientException e) {
					logger.error("删除ftp文件出错:",e);
				}
				//备份
				//备份到backup目录下
				ftpClientTools_2.backupFiles(toDelFiles, true);
				
				//记录下载状态
				downLoadStatusMapAll.putAll(downLoadStatusMap);
			}else{
				logger.warn("no ftp host to match host ["+host+"]");
			}
		}
		
		return downLoadStatusMapAll;
	}
	*/
	
	private List<String> getFiles(List<MonFileInfo> toDownFiles){
		List<String> files= new ArrayList<String>();
		for(MonFileInfo mf:toDownFiles){
			files.add(mf.getFname());
		}
		return files;
	}

	public Map<String, FTPClientTools> getFtpClientToolsMap() {
		return ftpClientToolsMap;
	}

	public void setFtpClientToolsMap(Map<String, FTPClientTools> ftpClientToolsMap) {
		this.ftpClientToolsMap = ftpClientToolsMap;
	}

	/*public FTPClientTools getFtpClientTools_1() {
		return ftpClientTools_1;
	}

	public void setFtpClientTools_1(FTPClientTools ftpClientTools_1) {
		this.ftpClientTools_1 = ftpClientTools_1;
	}

	public FTPClientTools getFtpClientTools_2() {
		return ftpClientTools_2;
	}

	public void setFtpClientTools_2(FTPClientTools ftpClientTools_2) {
		this.ftpClientTools_2 = ftpClientTools_2;
	}*/
	
	
	
}
