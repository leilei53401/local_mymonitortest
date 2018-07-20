package com.voole.ad.service.impl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import com.voole.ad.utils.AgentUtils;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.voole.ad.model.AdSendInfo;
import com.voole.ad.service.IAdAgentTemplete;
import com.voole.ad.utils.GlobalProperties;

@Service
public class AdAgentTemplete implements IAdAgentTemplete{

	private Logger logger = LoggerFactory.getLogger(AdAgentTemplete.class);

	@Resource
	private AdMonitorCacheService monitorCacheService;
	@Resource
	private AdTimeSendServiceImpl adSendService;
	private AtomicInteger cnt = new AtomicInteger();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String macBlackListSwitch = GlobalProperties.getProperties("macBlackListSwitch").trim();
	private String macBlackListStr = GlobalProperties.getProperties("macBlackList").trim().replaceAll(" ", "");
	private List<String> macBlackList = Arrays.asList(macBlackListStr.split(","));
	
	private String delimiter = GlobalProperties.getProperties("delimiter");

    private String md5Flag = GlobalProperties.getProperties("md5Flag");
	
	DateTimeFormatter dtformat = DateTimeFormat.forPattern("yyyyMMddHHmmss");  
	
	public AdAgentTemplete() {
		init();
	}

	/**
	 */
	public void init() {
		if(StringUtils.isBlank(delimiter)){
			delimiter = ",";
		}
	}

	public void sendSupersspAd(String ad){
		//数据样例：
		//starttime,mediaid,sessionid,oemid,creativeid,adpid,mac,planid,type,ip,provinceid,cityid,ts,duration,te,adsource,hourtime
		//20170413111334|900500|14615495910308656913|100|2000|666|BCEC234C3069|0|0|223.94.228.139|42|4208|1490673603461|15|0|0|11
//		JSONObject jsonData = JSONObject.fromObject(ad);
		JSONObject jsonData = new JSONObject();
		String[] adArray = ad.split(delimiter);
		if(adArray.length<17){
			logger.warn("数据["+ad+"]不符合要求");
			return;
		}
		String mac = adArray[6];
		
		if(!"1".equals(macBlackListSwitch) || !macBlackList.contains(mac)){
		    String starttime = adArray[0]; 
	        String mediaid = adArray[1];
	        String creativeid =  adArray[4];
	        String ip = adArray[9];
	        String proid = adArray[10];
	        String ctyid = adArray[11];
            //时间解析    
            DateTime dateTime = DateTime.parse(starttime, dtformat);    
	        String stamp = dateTime.getMillis()+"";

            //mac md5处理
            if("1".equals(md5Flag) && StringUtils.isNotBlank(mac) && mac.length()<15){
                String md5Mac = AgentUtils.guva32BitMd5Encode(mac.toUpperCase(),"").toLowerCase();
                jsonData.put("mac", md5Mac);
            }else{
                jsonData.put("mac", mac);
            }

	        jsonData.put("ip", ip);
	        //--------多种时间格式设置---------------------------------------
	        //time in millseconds  ----->tm
	        //time in second ------ts
	        //time in yyyy-mm-dd HH:mm:ss formated style ---->tf
	        //stamp 为13位的毫秒数
	        jsonData.put("tm",stamp);
	        jsonData.put("ts",stamp.substring(0,10));
	        jsonData.put("tf",sdf.format(new Date(Long.parseLong(stamp))).replaceAll(" ","%20"));

        	//根据key获取需要发送的监测模板。
        	List<AdSendInfo> monlist = monitorCacheService.getMonitorInfo(creativeid);	        
        
	        if (monlist != null && monlist.size() > 0) {
	            for (AdSendInfo adSendInfo_old : monlist) {
	                long l_stamp = stamp!=null?Long.parseLong(stamp):0L;
	                AdSendInfo adSendInfo = new AdSendInfo();
	                adSendInfo.setCreativeid(adSendInfo_old.getCreativeid());	           
	                adSendInfo.setParams(adSendInfo_old.getParams());
	                adSendInfo.setReplaceParams(adSendInfo_old.getReplaceParams());
	                adSendInfo.setDomain(adSendInfo_old.getDomain());
                    adSendInfo.setCompany(adSendInfo_old.getCompany());
	                adSendInfo.setStarttime(starttime!=null?Long.parseLong(starttime):0L);
	                adSendInfo.setStamp(l_stamp);
	  
	                adSendInfo.setCityid(ctyid); //放入原始市编码
	                adSendInfo.setProvinceid(proid);//放入原始省编码
	                
//		          	 jsonData.put("cityid",ctyid);
//	                 jsonData.put("provinceid", proid);
	                 jsonData.put("cid",ctyid);
	                 jsonData.put("pid", proid);
	                 
	                //split后的参数串以及需要替换的参数
	                adSendInfo.setParamList(adSendInfo_old.getParamList());
	                adSendInfo.setReplaceParamList(adSendInfo_old.getReplaceParamList());
	       
                    //--------unique id-------------
                    //每条信息给一个唯一的ID 毫秒数+随机的五位数
                    String id = (int)((Math.random()*8.99999+1)*10000)+""+System.currentTimeMillis();
                    jsonData.put("id", id);
                    adSendInfo.setId(id);



                    //--------unique id-------------
                    String params = adSendInfo.getParams();
//                    String replaceParams = adSendInfo.getReplaceParams();
                    //使用放在list中参数串的各个部分以及需替换参数的实际值拼接实际参数串
//                    List<String> paramsList = adSendInfo.getParamList();
                    List<String> replaceParamList = adSendInfo.getReplaceParamList();
//                    String ismd5 = adSendInfo.getIsmd5();
//                    StringBuilder sendUrl = new StringBuilder();



                    for(String partStr:replaceParamList){
                    	  String partReStr ="%"+partStr+"%";
                    	  String realRepStr = jsonData.get(partStr.toLowerCase()) != null ? jsonData.get(partStr.toLowerCase())+ "" : "";
                    	  params = params.replaceAll(partReStr, realRepStr);
                    }
                
                    adSendInfo.setParams(params);
                    //发送
                    logger.debug("发送串为："+adSendInfo.getParams());
                    adSendService.putLogToQueue(adSendInfo);
	                
	            }
	        }
		       
		} 
	}	
}
