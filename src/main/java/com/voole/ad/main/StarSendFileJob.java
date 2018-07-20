package com.voole.ad.main;


import com.voole.ad.model.AdSendInfo;
import com.voole.ad.service.IAdAgentTemplete;
import com.voole.ad.service.impl.MonitorSendServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 广汽菲克 补发任务
 * @author shaoyl
 *
 */
//@Component("StarSendFileJob")
public class StarSendFileJob {
	protected static Logger logger = Logger.getLogger(StarSendFileJob.class);
	
	@Resource
	private IAdAgentTemplete adAgent;


    @Resource
    private MonitorSendServiceImpl monitorSendService;

    private int sendType = 1; // 1: 6省 ; 1: 7省


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
        int sleepNum = 10;
        boolean toExec = false;

        sendParam = "http://www.baidu.com?x=%MAC%&B=%IP%";
        theFile = new File("/opt/data/gqfk3/gqfk3_22.txt");

	   /* if(1==sendType){
             sendParam = "http://g.dtv.cn.miaozhen.com/x/k=4010343&p=2iEyq&ns=%IP%&nx=1&sn=&ni=&m1=&m1a=&m4=&m6=&m6a=%MAC%&rt=2&nd=&nn=&ng=&nc=&nt=%TS%&rfm=&tdt=&tdr=&pro=n&vv=1&tvrm=&o=";

            if("20180406".equals(currDateStr)){
                toExec =  true;
                theFile = new File("/opt/data/gqfk3/pro6_2.txt");

            }else if("20180407".equals(currDateStr)){
                toExec =  true;
                theFile = new File("/opt/data/gqfk3/pro6_3.txt");

            }else if("20180414".equals(currDateStr)){
                toExec =  true;
                theFile = new File("/opt/data/gqfk3/pro6_4.txt");

            }else if("20180415".equals(currDateStr)){
                toExec =  true;
                theFile = new File("/opt/data/gqfk3/pro6_5.txt");
            }

        }else{
            sleepNum = 30;
            sendParam = "http://g.dtv.cn.miaozhen.com/x/k=4010344&p=2iEyw&ns=%IP%&nx=1&sn=&ni=&m1=&m1a=&m4=&m6=&m6a=%MAC%&rt=2&nd=&nn=&ng=&nc=&nt=%TS%&rfm=&tdt=&tdr=&pro=n&vv=1&tvrm=&o=";

            if("20180406".equals(currDateStr)){
                toExec =  true;
                theFile = new File("/opt/data/gqfk3/pro7_2.txt");

            }else if("20180407".equals(currDateStr)){
                toExec =  true;
                theFile = new File("/opt/data/gqfk3/pro7_3.txt");

            }else if("20180414".equals(currDateStr)){
                toExec =  true;
                theFile = new File("/opt/data/gqfk3/pro7_4.txt");

            }else if("20180415".equals(currDateStr)){
                toExec =  true;
                theFile = new File("/opt/data/gqfk3/pro7_5.txt");
            }
        }*/


//        if(!toExec){
//            logger.info("今日【"+currDateStr+"】无发送任务，该任务结束！");
//            return false;
//        }

        String fileName  =  theFile.getName();

        logger.info("### 开始处理日期为【"+currDateStr+"】 文件为【"+fileName+"】 的任务！");


        String regx = ".*d=(.*)&e=.*f=(.*)h=.*i=(.*)&l=.*starttime=(.*)&cip=.*";

        long all = 0L;

        try {
            LineIterator it = FileUtils.lineIterator(theFile, "UTF-8");
            try {
                while (it.hasNext()) {
                    String line = it.nextLine();
                    //数据样例
                    //"GET /v1/a/1.gif?a=900102&b=&c=900102101&d=1050001608&e=10151010&f=1255c3bbf41034b7022745e617eb74a4&h=0&i=114.98.132.154&l=1522145167&m=&n=0&p=1&z=1HTTP/1.0&starttime=20180327180704&cip=114.98.132.154, 123.207.58.240, 101.37.145.163, 192.168.10.3"

                    try {
                        Pattern r = Pattern.compile(regx);

                        Matcher m = r.matcher(line);
                        if (m.find( )) {
//                            System.out.println("Found value: " + m.group(0) );
//                            System.out.println("Found value: " + m.group(1) );
//                            System.out.println("Found value: " + m.group(2) );
//                            System.out.println("Found value: " + m.group(3) );

                            //处理解析数据

                            String creativeid = m.group(1);
                            String mac =  m.group(2);
                            String ip =  m.group(3);
                            String starttime =  m.group(4);

                         /*   DateTime dateTime = DateTime.parse(starttime, dtformat);
                            String stamp = dateTime.getMillis()+"";*/
                            String stamp = System.currentTimeMillis()+"";
                            String ts = stamp.substring(0,10);


                            String sendParamNew = sendParam.replaceAll("%IP%",ip)
                                                  .replaceAll("%MAC%",mac)
                                                  .replaceAll("%TS%",ts);


                            AdSendInfo adSendInfo = new AdSendInfo();
                            adSendInfo.setCreativeid(creativeid);
                            adSendInfo.setParams(sendParamNew);

                            adSendInfo.setCompany("Miaozhen2");
                            adSendInfo.setStarttime(starttime!=null?Long.parseLong(starttime):0L);
                            long l_stamp = stamp!=null?Long.parseLong(stamp):0L;
                            adSendInfo.setStamp(l_stamp);



                            if (all % 10000 == 0) {
                                logger.debug("发送串为："+adSendInfo.getParams());
                            }

                            monitorSendService.putLogToQueue(adSendInfo);

                            if (all % sleepNum == 0) {
                                try {
                                    //线程等待0.5秒，防止加入数据过多。
                                    Thread.sleep(1000L);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }


                        } else {
                            System.out.println("NO MATCH");
                        }
                    } catch (Exception e) {
                       logger.error("处理line["+line+"]出错：",e);
                    }

                    all++;

                    if (all % 10000 == 0) {
                        System.out.println((new Date()) + ",allnum=" + all);

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

    public int getSendType() {
        return sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }
}
