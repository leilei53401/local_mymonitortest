package com.voole.ad.utils;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
//import com.techcenter.util.MD5;

public class AgentUtils {

	 /**
	  * MD5加密
	 * @param text
	 * @param secretKey
	 * @return
	 */
	/*public static String md5Encode(String text, String secretKey) {
	    	String text_secretKey = text+secretKey;
			String textSecrect = new MD5().getMD5ofStr(text_secretKey);
	    	return textSecrect;
		}*/

	/**
	 * 基于guva的32位Md5加密
	 * @param text 带加密文本
	 * @param secretKey 加密秘钥
	 * @return
	 */
	public static String guva32BitMd5Encode(String text, String secretKey){
			String text_secretKey = text+secretKey;
			HashCode hashCode = Hashing.md5().hashString(text_secretKey, Charsets.UTF_8);
			String md5 =hashCode.toString();
			return md5;
	}
}
