package com.voole.ad.main;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartUp {

	public static void main(String[] args) throws IOException {
		final Logger log = LoggerFactory.getLogger(StartUp.class);
		log.info("程序开始启动...");
		long beginTime = System.currentTimeMillis();
		try {
			final ApplicationContext app = new ClassPathXmlApplicationContext("applicationContext.xml");


//            StarSendFileJob sendFileJob = (StarSendFileJob) app.getBean("StarSendFileJob");
//
//            sendFileJob.start();

		} catch (Exception e) {
			e.printStackTrace();
			log.error("加载spring配置文件失败！");
			System.exit(0);
		}



		long startUpTime = System.currentTimeMillis() - beginTime;
		log.info("程序启动成功,耗时" + startUpTime + "ms.");

	}
}