package com.voole.ad.service.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.voole.ad.model.AdSendInfo;
import com.voole.ad.queue.AdBlockQueue;
import com.voole.ad.utils.GlobalProperties;
import com.voole.ad.utils.HttpConnectionMgrUtils;
import com.voole.ad.utils.LimitedBlockingQueue;

/**
 * 
 * 发送广告联盟处理
 * 
 * @author Administrator
 *
 */
@Service
public class AdTimeSendServiceImpl extends Thread {
    // logger
    private Logger logger = LoggerFactory.getLogger(AdTimeSendServiceImpl.class);
    // 读数据的阻塞队列
    private AdBlockQueue<AdSendInfo> adqueue;
    // 失败备份服务
    @Resource
    private AdTimeOutRecordFileServiceImpl recordFileImpl;

    // @Resource
    // private AdTimeOutRecordFileServiceImpl1 recordFileImpl1;
    // http管理类
    private HttpConnectionMgrUtils httpMgr;
    // 备份服务、备份根目录、备份开关、备份逻辑
    @Resource
    private DataBakService dataBakService;
    private String bakRootPath;
    private String bakSwitch;
    private String bakLogic;
    // 主发送http请求的线程池参数
    private ExecutorService threadPoolMain;
    private int corePoolSize_main;
    private int maximumPoolSize_main;
    private long threadKeepAliveTime_main;
    private int threadPoolQueueSize_main;
    // ---------------------------
   
    public AdTimeSendServiceImpl() {
        // ----------------------
        // 主发送线程池参数
        corePoolSize_main = GlobalProperties.getInteger("thread.main.pool.corePoolSize");
        maximumPoolSize_main = GlobalProperties.getInteger("thread.main.pool.maxPoolSize");
        threadKeepAliveTime_main = GlobalProperties.getInteger("thread.main.pool.keepAliveTime");
        threadPoolQueueSize_main = GlobalProperties.getInteger("thread.main.pool.queueSize");
        threadPoolMain = new ThreadPoolExecutor(corePoolSize_main, maximumPoolSize_main, threadKeepAliveTime_main,
                TimeUnit.MILLISECONDS, new LimitedBlockingQueue<Runnable>(threadPoolQueueSize_main),
                new ThreadFactoryBuilder().setNameFormat("Sending Pool").build());
        
        // 文件备份根目录、开关
        bakRootPath = GlobalProperties.getProperties("data.to.third.bak.path").trim();
        int length = bakRootPath.length();
        if (!"/".equals(bakRootPath.substring(length - 1, length))) {
            bakRootPath = bakRootPath + "/";
        }
        
        bakLogic = GlobalProperties.getProperties("data.backup.dir.logic").trim();
        bakSwitch = GlobalProperties.getProperties("dataToThirdBakSwitch").trim();
        adqueue = new AdBlockQueue<AdSendInfo>();
        httpMgr = new HttpConnectionMgrUtils();
        this.start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
    public void run() {
        while (true) {
            this.handler();
        }
    }

    /**
     * 
     */
    public void handler() {

        try {
            // read queue
            final AdSendInfo adinfo = adqueue.getData();
            // 1、source data bakup
            // --------------------文件备份start
            if ("1".equals(bakSwitch)) {
            	String domain =adinfo.getDomain();
                String company = adinfo.getCompany();
                String s_amid = adinfo.getCreativeid();
//                String ven = adinfo.getVen();
                String bakPath = "";
                if ("1".equals(bakLogic)) {// 按监测机构备份
                    bakPath = bakRootPath  + s_amid + "/" + company;
                } else {
                    bakPath = bakRootPath + s_amid + "/" + domain;// 备份路径
                }
                long starttime = adinfo.getStamp();
                if (starttime == 0) {
                    starttime = adinfo.getStarttime();
                    if (Long.toString(starttime).length() == 10) {
                        starttime = Long.parseLong(Long.toString(starttime) + "000");
                    }
                }
                String bakName = new DateTime(starttime).toString("yyyyMMdd");
                String hour = new DateTime(starttime).toString("yyyyMMddHHmmss");
                //String bakName1 = (new SimpleDateFormat("yyyyMMdd").format(new Date(starttime))).toString() + ".txt";
                String params = hour+" , "+ adinfo.getParams();
                // 文件路径、文件名、广告内容，保存
                dataBakService.saveBakData(bakPath, bakName, params);
            }
            // --------------------文件备份end
            // --------------------------------------------
            // 2、send data
            threadPoolMain.execute(new Runnable() {
                @Override
                public void run() {
                	boolean issuccess = true;
                	try{
                		int statusCode =  httpMgr.invokeGetUrl(adinfo);
//                        int statusCode = 0;
	                    if (statusCode != 200 && statusCode != 302) {
	                        recordFileImpl.writeFile1(adinfo,statusCode);
	                        int code = httpMgr.invokeGetUrl(adinfo);
//                            int code = 0;
                            if (code != 200 && code != 302) {
	                            recordFileImpl.writeFile2(adinfo, code);
	                            issuccess = false;
	                        }
	                    }
                	 }catch(Exception e){
                     	logger.error("send ad info exception ",e);
                     	recordFileImpl.exceptionWriteFile(adinfo,e.getLocalizedMessage());
                     	issuccess = false;
                     }
                  /*  if(issuccess){
                    	//发送成功
                    	adSendCountUtils.sendDaySucCount(adinfo);
                    } else{
                    	//发送失败
                    	adSendCountUtils.sendDayFailCount(adinfo);
                    }*/
                }
            });
        } catch (InterruptedException e) {
            logger.error("read queue data exception", e);
        }
    }

    /**
     * 添加到阻塞队列
     * 
     * @param adplaystat
     */
    public void putLogToQueue(AdSendInfo adinfo) {
        try {
            adqueue.putqueue(adinfo);
        } catch (InterruptedException e) {
            logger.error("put adUrl To queue exception,data=" + adinfo.getDomain() + adinfo.getParams(), e);
        }
    }
}
