package com.voole.ad.main;


import com.voole.ad.model.AdSendInfo;
import com.voole.ad.service.IAdAgentTemplete;
import com.voole.ad.service.impl.LocalSendServiceImpl;
import com.voole.ad.service.impl.MonitorSendServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 本地发送监测串测试
 *
 */
//@Component("StarSendFileJob")
public class LocalSendFileJob {
	protected static Logger logger = Logger.getLogger(LocalSendFileJob.class);
	
	@Resource
	private IAdAgentTemplete adAgent;


    @Resource
    private LocalSendServiceImpl localSendServiceImpl;



    DateTimeFormatter dtformat = DateTimeFormat.forPattern("yyyyMMddHHmmss");


    //下载任务入口
	public void start(){

        boolean result = parseFile2();
	}


	public  boolean parseFile2(){

	    logger.info("################## 开始处理文件任务 ####################");

	    DateTime currDate = new DateTime();
        String currDateStr = currDate.toString("yyyyMMdd");
        String sendParam = "";
        File theFile = null;
        int sleepNum = 600;
        boolean toExec = false;

        sendParam = "http://192.168.3.5:8081/v1/a/1.gif?";
        theFile = new File("/opt/webapps/exp_file/20180501_22_top10W.txt");



        String fileName  =  theFile.getName();

        logger.info("### 开始处理日期为【"+currDateStr+"】 文件为【"+fileName+"】 的任务！");



        long all = 0L;

        try {
            LineIterator it = FileUtils.lineIterator(theFile, "UTF-8");
            try {
                while (it.hasNext()) {
                    String line = it.nextLine();
                    //数据样例
                    //"GET /v1/a/1.gif?a=900102&b=&c=900102101&d=1050001608&e=10151010&f=1255c3bbf41034b7022745e617eb74a4&h=0&i=114.98.132.154&l=1522145167&m=&n=0&p=1&z=1HTTP/1.0&starttime=20180327180704&cip=114.98.132.154, 123.207.58.240, 101.37.145.163, 192.168.10.3"

                    try {
                            //处理解析数据
                            /*String creativeid = m.group(1);
                            String mac =  m.group(2);
                            String ip =  m.group(3);
                            String starttime =  m.group(4);*/

                         /*   DateTime dateTime = DateTime.parse(starttime, dtformat);
                            String stamp = dateTime.getMillis()+"";*/
                            String stamp = System.currentTimeMillis()+"";
                            String ts = stamp.substring(0,10);

                        String param = StringUtils.substringBetween(line,"?","HTTP");
                        if(StringUtils.isBlank(param)){
                            param = line;
                        }
                        param = StringUtils.trimToEmpty(param);



                        String sendParamNew = sendParam+param;


                        /*    AdSendInfo adSendInfo = new AdSendInfo();
                            adSendInfo.setCreativeid(creativeid);
                            adSendInfo.setParams(sendParamNew);

                            adSendInfo.setCompany("Miaozhen2");
                            adSendInfo.setStarttime(starttime!=null?Long.parseLong(starttime):0L);
                            long l_stamp = stamp!=null?Long.parseLong(stamp):0L;
                            adSendInfo.setStamp(l_stamp);
*/


                            if (all % 5000 == 0) {
                                logger.info("发送串为："+sendParamNew);
//                                logger.info("已发送："+all+"条!");

                            }

                            localSendServiceImpl.putLogToQueue(sendParamNew);

                            if (all % sleepNum == 0) {
                                try {
                                    //线程等待0.5秒，防止加入数据过多。
                                    Thread.sleep(500L);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }



                    } catch (Exception e) {
                       logger.error("处理line["+line+"]出错：",e);
                    }

                    all++;

                    if (all % 10000 == 0) {

                        logger.info("已处理数据【" + all + "】条!");
                    }
                }

            } finally {
                LineIterator.closeQuietly(it);
            }
            System.out.println("共处理数据["+all+"]条!");
            logger.info("处理日期为【"+currDateStr+"】文件为【"+fileName+"】的数据完成! 共处理数据["+all+"]条!");
            logger.info("###################################################");
        } catch (IOException e) {
            logger.error("prcessFile  error:" , e);
            return false;
        }

        return true;
    }

}
