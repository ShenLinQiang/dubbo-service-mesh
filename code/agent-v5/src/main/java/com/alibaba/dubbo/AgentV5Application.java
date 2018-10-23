package com.alibaba.dubbo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alibaba.dubbo.consumer.http.server.HttpServer;
import com.alibaba.dubbo.provider.tcp.server.TcpServer;

@SpringBootApplication
public class AgentV5Application {

	public static void main(String[] args) throws  Exception {
		SpringApplication.run(AgentV5Application.class, args);
		
		//根据type启动相应的服务
		if("consumer".equals(System.getProperty("type"))) {
			new HttpServer().start();
		}else if("provider".equals(System.getProperty("type"))) {
			new TcpServer().start();
		}else {
			System.out.println("Environment variable type is needed to set to provider or consumer.");
		}
		
		
	}
}
