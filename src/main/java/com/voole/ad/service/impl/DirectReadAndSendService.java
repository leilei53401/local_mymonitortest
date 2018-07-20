package com.voole.ad.service.impl;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.voole.ad.dao.IImgDao1;
import com.voole.ad.dao.IImgDao2;
import com.voole.ad.dao.IImgDao3;

@Service
public class DirectReadAndSendService {
	@Resource
	IImgDao1 imgDao1;
	@Resource
	IImgDao2 imgDao2;
	@Resource
	IImgDao3 imgDao3;
	
	BufferedWriter bw24 = null;
	BufferedWriter bw25 = null;
	BufferedWriter bw26 = null;
	BufferedWriter bw27 = null;
	BufferedWriter bw28 = null;
	private int pauseCal=0;
	private int existCal=0;
	ArrayList<BufferedWriter> bw_list = new ArrayList<BufferedWriter>();
	Logger logger = LoggerFactory.getLogger(DirectReadAndSendService.class);
	public DirectReadAndSendService(){
		
	}
	
	public void createDF(){
		//创建保存暂停数据的文件
				File path = new File("/opt/tiepian/");
				if(!path.exists()){
					path.mkdirs();
				}
				//-----保存24-28日的数据文件-------------------
				File file24 = new File("/opt/tiepian/24.txt");
				if(!file24.exists()){
					try {
						file24.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				File file25 = new File("/opt/tiepian/25.txt");
				if(!file25.exists()){
					try {
						file25.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				File file26 = new File("/opt/tiepian/26.txt");
				if(!file26.exists()){
					try {
						file26.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				File file27 = new File("/opt/tiepian/27.txt");
				if(!file27.exists()){
					try {
						file27.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				File file28 = new File("/opt/tiepian/28.txt");
				if(!file28.exists()){
					try {
						file28.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				try {
					bw24 = new BufferedWriter(new FileWriter(file24,true));
					bw25 = new BufferedWriter(new FileWriter(file25,true));
					bw26 = new BufferedWriter(new FileWriter(file26,true));
					bw27 = new BufferedWriter(new FileWriter(file27,true));
					bw28 = new BufferedWriter(new FileWriter(file28,true));
					bw_list.add(bw24);
					bw_list.add(bw25);
					bw_list.add(bw26);
					bw_list.add(bw27);
					bw_list.add(bw28);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	public void readAndSend() {
		// TODO Auto-generated method stub
		createDF();
		
		//保存所有的表名
		ArrayList<String> tableNames = new ArrayList<String>();
		tableNames.add("rt_og_play_log5");
		tableNames.add("rt_og_play_log6");
		tableNames.add("rt_og_play_log7");
		tableNames.add("rt_og_play_log1");
		tableNames.add("rt_og_play_log2");
		
		
		//传递给mybatis的参数放在这里
		Map<String,Object> tn_map = new HashMap<String,Object>();
		//迭代读几张表
		for(int i=0;i<tableNames.size();i++){
			//得到bw/表名：
			String tableName = tableNames.get(i);
			logger.info("reading table **************"+ tableName+"*************");
			BufferedWriter bw = bw_list.get(i);
			//--------第一次查询--------------------------
			int offset=0;
			tn_map.put("tn",tableName);
			tn_map.put("limitation",5000);
			tn_map.put("offset",offset);//设置表offset
			logger.info("reading form db1");
			List<Map> list1 = imgDao1.loadImgData(tn_map);
			logger.info("size "+list1.size());

			logger.info("reading form db2");
			List<Map> list2 = imgDao2.loadImgData(tn_map);
			logger.info("size "+list2.size());
			
			logger.info("reading form db3");
			List<Map> list3 = imgDao3.loadImgData(tn_map);
			logger.info("size "+list3.size());
			
			//-------第一次查询结束------------------------

			//解析查询到的list，并写入文件
			
			while(!list1.isEmpty()||!list2.isEmpty()||!list3.isEmpty()){ //
				//解析一个库的查询结果
				logger.info("phrase data from db1");
				phraseSaveList(list1,bw);
				
				logger.info("phrase data from db2");
				phraseSaveList(list2,bw);
				
				logger.info("phrase data from db3");
				phraseSaveList(list3,bw);
				//解析第二个库的查询结果
				//设置数据库的分页偏移，执行下一次查询.
				offset=offset+1;
				tn_map.put("offset", offset*5000);
				
				logger.info("reading form db1");
				list1 = imgDao1.loadImgData(tn_map);
				logger.info("size "+list1.size());
				
				logger.info("reading form db2");
				list2 = imgDao2.loadImgData(tn_map);
				logger.info("size "+list2.size());
				
				logger.info("reading form db3");
				list3 = imgDao3.loadImgData(tn_map);
				logger.info("size "+list3.size());
			
			}
			logger.info("continue ...");
		}
		
		logger.info("pause cal = "+pauseCal +", existCal = "+existCal);
		try {
			for(BufferedWriter bw:bw_list){
				bw.close();
				logger.info("closing the buffered writer!!");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("close bufferedwriter error!");
		}
		
		logger.info("==========================task finish!!=============================");
	}
	
	
	private void phraseSaveList(List<Map> list,BufferedWriter bw) {
		// TODO Auto-generated method stub
		
		if(!list.isEmpty()){
			for(Map map : list){
				String hid = map.get("HID").toString();
				String ip = map.get("IP").toString();
				String starttime = map.get("STARTTIME").toString();
				String amid = map.get("AMID").toString();
				try {
					bw.write(hid+","+ip+","+starttime.substring(0,starttime.length()-2));
					bw.newLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("write file error! "+e);
				}
			}
			try {
				bw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			logger.info("this db data has been consumed !");
		}
		
	}

}
