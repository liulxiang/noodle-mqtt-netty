package com.noodle;

import java.lang.management.ManagementFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication {
	public static void main(String[] args) {
		String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		System.out.println("当前项目进程号：" + jvmName.split("@")[0]);
		SpringApplication.run(MainApplication.class, args);
		
	}

}
