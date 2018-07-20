package com.voole.ad.main;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.voole.ad.dao.IMonFileInfoDao;
import com.voole.ad.service.IAdAgentTemplete;

/**
 * 解析本地文件任务
 * @author shaoyl
 *
 */

public class StartLocalFileJob {
	protected static Logger logger = Logger.getLogger(StartLocalFileJob.class);
	
	@Resource
	private IAdAgentTemplete adAgent;
	@Resource
	private IMonFileInfoDao monFileInfoDao;
	
	private  String localPath;//本地路径配置
	private  String backupPath;//解析完文件备份路径
	
	private int maxProcessfiles = 5;//每次任务最多处理文件个数 
	//下载任务入口
	public void start(){
		
			File f = new File(localPath);
			// 修改为过滤出文件名
			String[] listFiles = f.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.endsWith(".txt")) {
						return true;
					}
					return false;
				}
			});
			
			
			//限制每次任务最多处理文件个数。
			int allSize = 0;
			if(null != listFiles){
				allSize = listFiles.length;
			}
			
			
			List<String> processList = new ArrayList<String>();
			int toProcessSize = (allSize>maxProcessfiles)?maxProcessfiles:allSize;
			for(int i=0; i < toProcessSize;i++){
				processList.add(listFiles[i]);
			}
			
			logger.info("将要处理的文件为:"+processList.toString());
			logger.info("localpath ["+localPath+"] 过滤到 .txt 文件共["+allSize+"]个,每次任务最多处理["+maxProcessfiles+"]个!");
			
			List<String> toCutFiles = new ArrayList<String>();
			List<String> excepFiles = new ArrayList<String>();
			for (String processFile : processList) {
				boolean result = parseFile(processFile);
				if(result){
					toCutFiles.add(processFile);
				}else{
					excepFiles.add(processFile);
				}
			}
			
			//将处理完成文件剪切到历史表中
			if(null!=toCutFiles && toCutFiles.size()>0){
				monFileInfoDao.updateFilesToParsed(toCutFiles);
			}
			if(null!=excepFiles && excepFiles.size()>0){
				monFileInfoDao.updateFilesToException(excepFiles);			
			}
			if(null!=processList && processList.size()>0){
				monFileInfoDao.cutFilesToHistory(processList);		
			}
				
	}
		
	public boolean parseFile(String inputFile) {
		boolean result = true;
		File input = new File(localPath + inputFile);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(input));
			String line = null;
	//		PlayLogBean playLogBean = null;
			while ((line = br.readLine()) != null) {
				try {
					logger.debug("开始处理播放串["+line+"]");
					//数据样例：
					//starttime,mediaid,sessionid,oemid,creativeid,adpid,mac,planid,type,ip,provinceid,cityid,ts,duration,te,adsource,hourtime
					//20170413111334|900500|14615495910308656913|100|2000|666|BCEC234C3069|0|0|223.94.228.139|42|4208|1490673603461|15|0|0|11
					adAgent.sendSupersspAd(line);
				} catch (Exception e) {
					logger.error("处理数据["+line+"]异常:",e);
					continue;
				}
			}
			br.close();
		}catch (Exception ex) {
			logger.error("prcessFile ["+inputFile+"] error:" , ex);
			result = false;
		} finally {
			try {
				if (br != null){
					br.close();
					br = null;
				 }
				if(result){
					if (input != null && input.exists()) {
	//					input.delete();
						//备份到 backup目录下
						String backFilePath = "";
						if(backupPath.endsWith("/")){
							backFilePath = backupPath+inputFile;
						}else{
							backFilePath = backupPath+File.separator+inputFile;
						}
						File backFile =  new File(backFilePath);
						boolean flag = input.renameTo(backFile);
						logger.info("备份文件["+inputFile+"]到["+backFile+"]下完成，flag="+flag);
					}
				}
			} catch (Exception e) {
				logger.error("流关闭异常:", e);
			}
		}
		
		return result;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getBackupPath() {
		return backupPath;
	}

	public void setBackupPath(String backupPath) {
		this.backupPath = backupPath;
	}

	public int getMaxProcessfiles() {
		return maxProcessfiles;
	}

	public void setMaxProcessfiles(int maxProcessfiles) {
		this.maxProcessfiles = maxProcessfiles;
	}
	

	
}
