package com.noodle.netty.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * MQTT配置属性类
 * 
 * @author liulxiang
 * @date 2025/10/25 13:45
 */
@Data
@Component
@ConfigurationProperties(prefix = "websocket")
public class WebsocketProperties {
	/**
	 * WebSocket服务端口
	 */
	private Integer port = 9010;

	/**
	 * 主线程数
	 */
	private Integer bossThread = 1;

	/**
	 * 工作线程数
	 */
	private Integer workerThread = 4;
}