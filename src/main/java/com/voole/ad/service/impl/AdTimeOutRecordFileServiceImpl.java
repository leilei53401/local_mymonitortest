package com.voole.ad.service.impl;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.voole.ad.model.AdSendInfo;
import com.voole.ad.utils.GlobalProperties;

/**
 * 
 * 发送超时的记录文件
 * @author Administrator
 *
 */
@Service
public class AdTimeOutRecordFileServiceImpl {

	private Logger logger = LoggerFactory.getLogger(AdTimeOutRecordFileServiceImpl.class);
	//private Collection<String> adUrlBackList = new ArrayList<String>();
	private String sendFailBakPath = GlobalProperties.getProperties("data.send.fail.bak.path").trim();
	private String bakLogic = GlobalProperties.getProperties("data.backup.dir.logic").trim();
	private File bakFile;
	private String sysSeparator = System.getProperty("line.separator"); 
	/**
	 * 重发失败后的备份
	 */
	public void writeFile2(AdSendInfo adInfo,int statusCode){
		if(adInfo != null && StringUtils.isNotBlank(adInfo.getDomain())){
			final String failUrl =new DateTime().toString("yyyyMMddHH")+" , "+statusCode+" , "+ adInfo.getParams()+sysSeparator;
            String catalogName = "";
            if ("1".equals(bakLogic)){
                catalogName = sendFailBakPath+"2/"+adInfo.getCompany() + "/" +adInfo.getCreativeid()+ "/" ;
            }else{
                 catalogName = sendFailBakPath+"2/"+adInfo.getDomain() + "/" +adInfo.getCreativeid()+ "/" ;
            }

			File file = FileUtils.getFile(new File(catalogName),new DateTime(adInfo.getStamp()).toString("yyyyMMdd")+".txt");
			try {
				FileUtils.writeStringToFile(file, failUrl, true);
			} catch (IOException e1) {
					logger.error("写发送失败备份文件发生错误！"+e1);
			}
		}
	}
	/**
	 * 
	 * 第一次发送失败备份
	 */
	public void writeFile1(AdSendInfo adInfo,int statusCode){
        if(adInfo != null && StringUtils.isNotBlank(adInfo.getDomain())){
            final String failUrl =new DateTime().toString("yyyyMMddHH")+" , "+statusCode+" , " + adInfo.getParams()+sysSeparator;
            String catalogName = "";
            if ("1".equals(bakLogic)){
                catalogName = sendFailBakPath+"1/"+adInfo.getCompany() + "/" +adInfo.getCreativeid()+ "/" ;
            }else{
                catalogName = sendFailBakPath+"1/"+adInfo.getDomain() + "/" +adInfo.getCreativeid()+ "/" ;
            }
            File file = FileUtils.getFile(new File(catalogName),new DateTime(adInfo.getStamp()).toString("yyyyMMdd")+".txt");
            try {
                FileUtils.writeStringToFile(file, failUrl, true);
            } catch (IOException e1) {
                    logger.error("写发送失败备份文件发生错误！"+e1);
            }
        }
    }
	/**
	 * 异常数据备份
	 * @param
	 */
	public void exceptionWriteFile(AdSendInfo adInfo,String msg) {
		// TODO Auto-generated method stub
		if(adInfo != null && StringUtils.isNotBlank(adInfo.getDomain())){
            final String failUrl =new DateTime().toString("yyyyMMddHH") + "   ||   " + msg + "   ||   " + adInfo.getParams()+sysSeparator;

            String catalogName ="";
            if ("1".equals(bakLogic)){
                catalogName = sendFailBakPath+"-Exception/"+adInfo.getCompany() + "/" +adInfo.getCreativeid()+ "/" ;
            }else{
                catalogName = sendFailBakPath+"-Exception/"+adInfo.getDomain() + "/" +adInfo.getCreativeid()+ "/" ;
            }

            File file = FileUtils.getFile(new File(catalogName),new DateTime(adInfo.getStamp()).toString("yyyyMMdd")+".txt");
            try {
                FileUtils.writeStringToFile(file, failUrl, true);
            } catch (IOException e1) {
                    logger.error("写发送失败备份文件发生错误！"+e1);
            }
        }
	}


    public void writeFileBase(String bakpath,String loginfo,int statusCode){

        final String failUrl =new DateTime().toString("yyyyMMddHH")+" , "+statusCode+" , " + loginfo+"\n";
            String catalogName = "";
            File file = FileUtils.getFile(new File(bakpath),new DateTime().toString("yyyyMMdd")+".txt");
            try {
                FileUtils.writeStringToFile(file, failUrl, true);
            } catch (IOException e1) {
                logger.error("写发送失败备份文件发生错误！"+e1);
            }

    }
}
