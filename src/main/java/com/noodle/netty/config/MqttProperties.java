package com.noodle.netty.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * MQTT配置属性类
 * @author liulxiang
 * @date 2025/10/25 13:45
 */
@Data
@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {
    /**
     * 服务端口
     */
    private Integer port = 8888;
    
    /**
     * 主线程数
     */
    private Integer bossThread = 1;
    
    /**
     * 工作线程数
     */
    private Integer workerThread = 1;
    
    /**
     * 认证配置
     */
    private Auth auth = new Auth();
    
    /**
     * QoS配置
     */
    private Qos qos = new Qos();
    
    /**
     * 消息配置
     */
    private Message message = new Message();
    
    @Data
    public static class Auth {
        /**
         * 登录权限开关
         */
        private Boolean loginEnabled = false;
        
        /**
         * 用户名
         */
        private String username = "admin";
        
        /**
         * 密码
         */
        private String password = "123456";
        
        /**
         * 客户端ID验证开关
         */
        private Boolean clientIdValidation = false;
        
        /**
         * 允许的客户端ID列表
         */
        private List<String> allowedClientIds = Arrays.asList("c1", "c2", "c3");
    }
    
    @Data
    public static class Qos {
        /**
         * 默认QoS级别 (0=AT_MOST_ONCE, 1=AT_LEAST_ONCE, 2=EXACTLY_ONCE)
         */
        private Integer defaultLevel = 1;
    }
    
    @Data
    public static class Message {
        /**
         * JSON格式验证开关
         */
        private Boolean jsonValidation = false;
    }
    
}