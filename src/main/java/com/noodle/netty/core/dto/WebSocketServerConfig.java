package com.noodle.netty.core.dto;

import com.noodle.netty.config.WebsocketProperties;
import com.noodle.netty.core.cache.IClientCache;
import com.noodle.netty.core.cache.ITopicCache;
import com.noodle.netty.core.service.IDataService;

import lombok.Data;

/**
 * WebSocket服务配置参数
 * @author liulxiang
 * @date 2025/10/25 13:55
 */
@Data
public class WebSocketServerConfig {
    // 端口号
    private Integer port;
    // 主线程
    private Integer boosTread;
    // 子线程
    private Integer workTread;
    // 业务逻辑
    private IDataService dataService;
    // 客户端
    private IClientCache clientCache;
    // 主题
    private ITopicCache iTopicCache;

    public WebSocketServerConfig() {
    }
    
    /**
     * 从配置属性初始化
     * @param properties MQTT配置属性
     */
    public WebSocketServerConfig(WebsocketProperties properties) {
        // WebSocket配置
        this.port = properties.getPort();
        this.boosTread = properties.getBossThread();
        this.workTread = properties.getWorkerThread();
    }

    public static WebSocketServerConfig base(Integer port){
        return base(port,1,4);
    }

    public static WebSocketServerConfig base(Integer port, IDataService dataService){
        return base(port,1,4,dataService,null);
    }

    public static WebSocketServerConfig base(Integer port, Integer boosTread, Integer workTread){
        return base(port,boosTread,workTread,null,null);
    }

    public static WebSocketServerConfig base(Integer port, Integer boosTread, Integer workTread, IDataService dataService, IClientCache clientCache){
        WebSocketServerConfig config = new WebSocketServerConfig();
        config.setPort(port);
        config.setWorkTread(workTread);
        config.setBoosTread(boosTread);
        config.setDataService(dataService);
        config.setClientCache(clientCache);
        return config;
    }
    
    /**
     * 从配置属性创建配置参数
     * @param properties MQTT配置属性
     * @param dataService 数据服务
     * @param clientCache 客户端缓存
     * @return 配置参数
     */
    public static WebSocketServerConfig fromConfig(WebsocketProperties properties, IDataService dataService, IClientCache clientCache) {
        WebSocketServerConfig config = new WebSocketServerConfig(properties);
        config.setDataService(dataService);
        config.setClientCache(clientCache);
        return config;
    }
}