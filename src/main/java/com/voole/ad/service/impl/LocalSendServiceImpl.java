package com.voole.ad.service.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.voole.ad.model.AdSendInfo;
import com.voole.ad.queue.AdBlockQueue;
import com.voole.ad.utils.GlobalProperties;
import com.voole.ad.utils.HttpConnectionMgrUtils;
import com.voole.ad.utils.LimitedBlockingQueue;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 本地发送处理
 * 
 * @author shaoyl
 *
 */
@Service
public class LocalSendServiceImpl extends Thread {
    // logger
    private Logger logger = LoggerFactory.getLogger(LocalSendServiceImpl.class);
    // 读数据的阻塞队列
    private AdBlockQueue<String> adqueue;
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

    private String bakSwitch;
    private String bakLogic;
    // 主发送http请求的线程池参数
    private ExecutorService threadPoolMain;
    private int corePoolSize_main;
    private int maximumPoolSize_main;
    private long threadKeepAliveTime_main;
    private int threadPoolQueueSize_main;
    // ---------------------------

    public LocalSendServiceImpl() {
        // ----------------------
        // 主发送线程池参数
        corePoolSize_main = GlobalProperties.getInteger("thread.main.pool.corePoolSize");
        maximumPoolSize_main = GlobalProperties.getInteger("thread.main.pool.maxPoolSize");
        threadKeepAliveTime_main = GlobalProperties.getInteger("thread.main.pool.keepAliveTime");
        threadPoolQueueSize_main = GlobalProperties.getInteger("thread.main.pool.queueSize");
        threadPoolMain = new ThreadPoolExecutor(corePoolSize_main, maximumPoolSize_main, threadKeepAliveTime_main,
                TimeUnit.MILLISECONDS, new LimitedBlockingQueue<Runnable>(threadPoolQueueSize_main),
                new ThreadFactoryBuilder().setNameFormat("Sending Pool").build());
        

        
        bakLogic = GlobalProperties.getProperties("data.backup.dir.logic").trim();
        bakSwitch = GlobalProperties.getProperties("dataToThirdBakSwitch").trim();
        adqueue = new AdBlockQueue<String>();
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
            final String adinfo = adqueue.getData();
            // 1、source data bakup
            // --------------------文件备份start
//            if ("1".equals(bakSwitch)) {
//            	String domain =adinfo.getDomain();
//                String company = adinfo.getCompany();
//                String s_amid = adinfo.getCreativeid();
//                String ven = adinfo.getVen();
                final  String bakPath = "/opt/webapps/exp_file/log/";
//                if ("1".equals(bakLogic)) {// 按监测机构备份
//                    bakPath = bakRootPath  + s_amid + "/" + company;
//                } else {
//                    bakPath = bakRootPath + s_amid + "/" + domain;// 备份路径
//                }

//                String bakName = new DateTime(starttime).toString("yyyyMMdd");
//                String hour = new DateTime(starttime).toString("yyyyMMddHHmmss");
//                //String bakName1 = (new SimpleDateFormat("yyyyMMdd").format(new Date(starttime))).toString() + ".txt";
//                String params = hour+" , "+ adinfo.getParams();

//                String params = adinfo;
                // 文件路径、文件名、广告内容，保存
                dataBakService.saveBakData(bakPath, "sendlog_20180516_1", adinfo+"\n");

//            }
            // --------------------文件备份end
            // --------------------------------------------
            // 2、send data
            threadPoolMain.execute(new Runnable() {
                @Override
                public void run() {
                	boolean issuccess = true;
                	try{
                		int statusCode =  httpMgr.invokeGetUrlString(adinfo);
//                        int statusCode = 0;
	                    if (statusCode != 200 && statusCode != 302) {
	                        recordFileImpl.writeFileBase(bakPath,adinfo,statusCode);
//	                        int code = httpMgr.invokeGetUrlString(adinfo);
////                            int code = 0;
//                            if (code != 200 && code != 302) {
//	                            recordFileImpl.writeFile2(adinfo, code);
//	                            issuccess = false;
//	                        }
	                    }
                	 }catch(Exception e){
                     	logger.error("send ad info exception ",e);
//                     	recordFileImpl.exceptionWriteFile(adinfo,e.getLocalizedMessage());
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
     * @param adinfo
     */
    public void putLogToQueue(String adinfo) {
        try {
            adqueue.putqueue(adinfo);
        } catch (InterruptedException e) {
            logger.error("put adUrl To queue exception,data=" + adinfo, e);
        }
    }
}
