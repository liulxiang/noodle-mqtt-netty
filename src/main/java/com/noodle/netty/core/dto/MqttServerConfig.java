package com.noodle.netty.core.dto;

import java.util.List;

import com.noodle.netty.config.MqttProperties;
import com.noodle.netty.core.cache.IClientCache;
import com.noodle.netty.core.cache.ITopicCache;
import com.noodle.netty.core.service.IDataService;

import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Data;

/**
 * MQTT服务配置参数
 * @author liulxiang
 * @date 2025/10/25 13:15
 */
@Data
public class MqttServerConfig {
    // 端口号
    private Integer port;
    // 主线程
    private Integer boosTread;
    // 子线程
    private Integer workTread;
    // 账号
    private String name;
    // 密码
    private String password;
    // 客户端id
    private List<String> clientIds;
    // qos类型
    private MqttQoS checkQos;
    // 登录权限开关（默认关闭）
    private Boolean authLoginOnOff;
    // 客户端连接id验证开关（默认关闭）
    private Boolean authClientIdOnOff;
    // 校验数据是否是JSON格式
    private Boolean authFormatToJson;
    // 业务逻辑
    private IDataService dataService;
    // 客户端
    private IClientCache clientCache;
    // 主题
    private ITopicCache iTopicCache;

    public MqttServerConfig() {
    }
    
    /**
     * 从配置属性初始化
     * @param properties MQTT配置属性
     */
    public MqttServerConfig(MqttProperties properties) {
    	// MQTT配置
        this.port = properties.getPort();
        this.boosTread = properties.getBossThread();
        this.workTread = properties.getWorkerThread();
        this.name = properties.getAuth().getUsername();
        this.password = properties.getAuth().getPassword();
        this.clientIds = properties.getAuth().getAllowedClientIds();
        this.authLoginOnOff = properties.getAuth().getLoginEnabled();
        this.authClientIdOnOff = properties.getAuth().getClientIdValidation();
        this.authFormatToJson = properties.getMessage().getJsonValidation();
        
        // 设置QoS级别
        switch (properties.getQos().getDefaultLevel()) {
            case 0:
                this.checkQos = MqttQoS.AT_MOST_ONCE;
                break;
            case 1:
                this.checkQos = MqttQoS.AT_LEAST_ONCE;
                break;
            case 2:
                this.checkQos = MqttQoS.EXACTLY_ONCE;
                break;
            default:
                this.checkQos = MqttQoS.AT_LEAST_ONCE;
        }
    }

    public static MqttServerConfig base(Integer port){
        return base(port,1,1);
    }

    public static MqttServerConfig base(Integer port, IDataService dataService){
        return base(port,1,1,dataService,null);
    }

    public static MqttServerConfig base(Integer port, Integer boosTread, Integer workTread){
        return base(port,boosTread,workTread,null,null);
    }

    public static MqttServerConfig base(Integer port, Integer boosTread, Integer workTread, IDataService dataService, IClientCache clientCache){
        MqttServerConfig config = new MqttServerConfig();
        config.setPort(port);
        config.setWorkTread(workTread);
        config.setBoosTread(boosTread);
        config.setDataService(dataService);
        config.setClientCache(clientCache);
        return config;
    }
    
    /**
     * 从配置属性创建MQTT配置参数
     * @param properties MQTT配置属性
     * @param dataService 数据服务
     * @param clientCache 客户端缓存
     * @return 配置参数
     */
    public static MqttServerConfig fromConfig(MqttProperties properties, IDataService dataService, IClientCache clientCache) {
        MqttServerConfig config = new MqttServerConfig(properties);
        config.setDataService(dataService);
        config.setClientCache(clientCache);
        return config;
    }
}