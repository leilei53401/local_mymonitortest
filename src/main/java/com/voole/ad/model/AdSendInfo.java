package com.voole.ad.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.type.Alias;

/**
 * 发送广告的实体
 * @author Administrator
 *
 */
@Alias(value = "AdSendInfo")
public class AdSendInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//区域限定类型
	public static final int AREA_TYPE_NO_LIMIT = 0;
	public static final int AREA_TYPE_LIMIT_PROVINCE = 1;
	public static final int AREA_TYPE_LIMIT_CITY = 2;
	//频道限定类型
	public static final int CHANNEL_TYPE_NO_LIMIT = 0;
	public static final int CHANNEL_TYPE_LIMIT = 1;
	

	private String creativeid; //节目id
	//增加监控维度
	private int monAreaType;//区域限定类型 0:不限定区域，1：限定省份，2：限定地市
	private String monProvinceIds;//监控省份id，多个省份id逗号隔开。
	private String monCityIds;//监控地市id，多个地市id逗号隔开
	private int monChannelType;//频道限定类型 0:不限定频道，1：限定频道
	private String monChannelIds;//多个频道id用逗号隔开。
	
	private String company;//监控公司名称
	private String domain;//域名
	private String port;//端口
	private String params; //参数
	private String replaceParams;// 替换参数
	private String ismd5;//md5是否加密
	private String secretKey;//密钥
	private long starttime;//时间戳
	private String ven;//厂商id
	private long stamp;
	private String mstatus;
	private Integer monCount;//发送上限数
	private Integer sendCount;//已发送数
	private Integer sendFailCount;//发送失败数
	private Integer sendDayCount;//日已发送数
	private Integer sendDayFailCount;//日发送失败数
	private Date sendStarttime;//发送开始时间
	private Date sendEndtime;//发送结束时间
	private Integer monId; //监控主键
	private Integer channelId;//频道ID
	private Integer programId;//栏目ID
	private List<String> paramList; //分段防止参数串 
	private List<String> replaceParamList;//分割后的替换参数
	private Integer areaMapperFlag;//区域映射标记位
	private String cityid;//区域市id
	private String provinceid;//区域省id
	
	
	public int getMonAreaType() {
		return monAreaType;
	}
	public void setMonAreaType(int monAreaType) {
		this.monAreaType = monAreaType;
	}
	public String getMonProvinceIds() {
		return monProvinceIds;
	}
	public void setMonProvinceIds(String monProvinceIds) {
		this.monProvinceIds = monProvinceIds;
	}
	public String getMonCityIds() {
		return monCityIds;
	}
	public void setMonCityIds(String monCityIds) {
		this.monCityIds = monCityIds;
	}
	public int getMonChannelType() {
		return monChannelType;
	}
	public void setMonChannelType(int monChannelType) {
		this.monChannelType = monChannelType;
	}
	public String getMonChannelIds() {
		return monChannelIds;
	}
	public void setMonChannelIds(String monChannelIds) {
		this.monChannelIds = monChannelIds;
	}
	
	public String getCreativeid() {
		return creativeid;
	}
	public void setCreativeid(String creativeid) {
		this.creativeid = creativeid;
	}
	public String getCityid() {
        return cityid;
    }
    public void setCityid(String cityid) {
        this.cityid = cityid;
    }
    public String getProvinceid() {
        return provinceid;
    }
    public void setProvinceid(String provinceid) {
        this.provinceid = provinceid;
    }
    public Integer getAreaMapperFlag() {
        return areaMapperFlag;
    }
    public void setAreaMapperFlag(Integer areaMapperFlag) {
        this.areaMapperFlag = areaMapperFlag;
    }
    private String id;
	
    public Integer getChannelId() {
        return channelId;
    }
    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }
    public Integer getProgramId() {
        return programId;
    }
    public void setProgramId(Integer programId) {
        this.programId = programId;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getMstatus() {
        return mstatus;
    }
    public void setMstatus(String mstatus) {
        this.mstatus = mstatus;
    }
    public String getUstatus() {
        return ustatus;
    }
    public void setUstatus(String ustatus) {
        this.ustatus = ustatus;
    }
    private String ustatus;
	public long getStamp() {
		return stamp;
	}
	public void setStamp(long stamp) {
		this.stamp = stamp;
	}
	public long getStarttime() {
		return starttime;
	}
	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}
	public String getVen() {
		return ven;
	}
	public void setVen(String ven) {
		this.ven = ven;
	}
	
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getReplaceParams() {
		return replaceParams;
	}
	public void setReplaceParams(String replaceParams) {
		this.replaceParams = replaceParams;
	}
	public String getIsmd5() {
		return ismd5;
	}
	public void setIsmd5(String ismd5) {
		this.ismd5 = ismd5;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public Integer getMonCount() {
		return monCount;
	}
	public void setMonCount(Integer monCount) {
		this.monCount = monCount;
	}
	public Integer getSendCount() {
		return sendCount;
	}
	public void setSendCount(Integer sendCount) {
		this.sendCount = sendCount;
	}
	public Integer getSendFailCount() {
		return sendFailCount;
	}
	public void setSendFailCount(Integer sendFailCount) {
		this.sendFailCount = sendFailCount;
	}
	public Date getSendStarttime() {
		return sendStarttime;
	}
	public void setSendStarttime(Date sendStarttime) {
		this.sendStarttime = sendStarttime;
	}
	public Date getSendEndtime() {
		return sendEndtime;
	}
	public void setSendEndtime(Date sendEndtime) {
		this.sendEndtime = sendEndtime;
	}
	public Integer getMonId() {
		return monId;
	}
	public void setMonId(Integer monId) {
		this.monId = monId;
	}
	public Integer getSendDayCount() {
		return sendDayCount;
	}
	public void setSendDayCount(Integer sendDayCount) {
		this.sendDayCount = sendDayCount;
	}
	public Integer getSendDayFailCount() {
		return sendDayFailCount;
	}
	public void setSendDayFailCount(Integer sendDayFailCount) {
		this.sendDayFailCount = sendDayFailCount;
	}
    public List<String> getParamList() {
        return paramList;
    }
    public void setParamList(List<String> paramList) {
        this.paramList = paramList;
    }
    public List<String> getReplaceParamList() {
        return replaceParamList;
    }
    public void setReplaceParamList(List<String> replaceParamList) {
        this.replaceParamList = replaceParamList;
    }
    
	@Override
	public String toString() {
		return "AdSendInfo [creativeid=" + creativeid + ", params=" + params
				+ ", replaceParams=" + replaceParams + "]";
	}
	
}
