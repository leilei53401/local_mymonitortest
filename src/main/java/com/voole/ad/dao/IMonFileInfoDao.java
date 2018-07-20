package com.voole.ad.dao;

import java.util.List;

import com.voole.ad.model.MonFileInfo;

/**
 * 
 * @author shaoyl
 *
 */
public interface IMonFileInfoDao {
	public List<MonFileInfo> getFiles();
	//将文件修改为正在下载状态
	public int updateFilesToDownLoading(List<String> ids);
	
	public List<MonFileInfo> getFilesAndChangeStatusToDownloading() ;
	//将文件修改状态修改为异常
	public int updateFilesDownException(List<String> names);
	//将文件修改为解析完成状态
	public int updateFilesToParsed(List<String> names);
	//将文件修改状态修改为异常
	public int updateFilesToException(List<String> names);
	//将处理完文件剪切到历史表中。
	public int cutFilesToHistory(List<String> names);
}
