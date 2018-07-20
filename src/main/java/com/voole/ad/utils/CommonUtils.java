package com.voole.ad.utils;

import java.net.URI;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtils {
	private static Logger logger = LoggerFactory.getLogger(CommonUtils.class);
	
	public static String getDomin(String url) {
		if (!(StringUtils.startsWithIgnoreCase(url, "http://") || StringUtils
				.startsWithIgnoreCase(url, "https://"))) {
			url = "http://" + url;
		}

		String returnVal = StringUtils.EMPTY;
		
		try {
//			URI uri = new URI(url);
//			returnVal = uri.getHost();
			URL urlObj = new URL(url);
			returnVal = urlObj.getHost();
		} catch (Exception e) {
			logger.error("get url["+url+"] domain error:",e);
		}
	/*	try {
			Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
			Matcher m = p.matcher(url);
			if (m.find()) {
				returnVal = m.group();
			}

		} catch (Exception e) {
			logger.error("get url["+url+"] domain error:",e);
		}*/
//		if ((StringUtils.endsWithIgnoreCase(returnVal, ".html") || StringUtils
//				.endsWithIgnoreCase(returnVal, ".htm"))) {
//			returnVal = StringUtils.EMPTY;
//		}
		return returnVal;
	}
	
	public static void main(String[] args) {
		String s = "http://v.admaster.com.cn/i/a65044,b963843,c3006,i0,m202,n__MAC__,0d__DEVICEID__,t__TS__,f__IP__,0a__CID__,0b__PID__,0i__SIZE__,h";
//		String s = "https://www.Admaster.com?username=goadongming";
		String host = CommonUtils.getDomin(s);
		System.out.println(host);
	}

}
