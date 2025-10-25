package com.noodle.netty;


import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.noodle.netty.config.MqttProperties;
import com.noodle.netty.config.WebsocketProperties;
import com.noodle.netty.core.cache.ClientCache;
import com.noodle.netty.core.dto.ClientDTO;
import com.noodle.netty.core.dto.MqttServerConfig;
import com.noodle.netty.core.dto.WebSocketServerConfig;
import com.noodle.netty.core.server.IServer;
import com.noodle.netty.core.service.IDataService;
import com.noodle.netty.mqtt.MqttServer;
import com.noodle.netty.websocket.WebSocketServer;
import com.noodle.netty.websocket.service.WebSocketDataServiceImpl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liulxiang
 * @date:2025-10-25 14:28
 * @description: netty启动服务
 **/
/**
 * Netty服务器初始化类
 * 负责初始化并启动MQTT和WebSocket服务器
 */
@Slf4j
@Component
public class NettyServerInit {
    // 注入MQTT数据服务实现
    @Resource(name = "mqttDataServiceImpl")
    private IDataService mqttService;
    // 注入WebSocket数据服务实现
    @Resource(name = "webSocketDataServiceImpl")
    private WebSocketDataServiceImpl webSocketDataService;
    
    // 注入MQTT配置属性
    @Autowired
    // 注入WebSocket配置属性
    private MqttProperties mqttProperties;
    @Autowired
    private WebsocketProperties websocketProperties;

    /**
     * 初始化方法
     * 使用@PostConstruct注解，在Bean实例化后自动执行
     */
    @PostConstruct
    public void init(){
        // 创建mqtt服务，使用配置文件中的参数
        MqttServer mqttServer = new MqttServer(MqttServerConfig.fromConfig(mqttProperties, mqttService, new ClientCache()));
        // 创建websocket服务，使用配置文件中的参数
        WebSocketServer wsServer = new WebSocketServer(WebSocketServerConfig.fromConfig(websocketProperties, webSocketDataService, new ClientCache()));
        // 启动服务
        IServer mqttRun = mqttServer.run();
        IServer wsRun = wsServer.run();
        // todo 测试代码-测试监听服务客户端状态
        Thread thread = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (true) {
                    Collection<ClientDTO> clientList = mqttRun.getClientAllList();
                    log.info("mqtt客户端连接数:{}", clientList.size());

                    Collection<ClientDTO> clientAllList = wsRun.getClientAllList();
                    log.info("ws客户端连接数:{}", clientAllList.size());
                    Thread.sleep(1000*10*1);
                }
            }
        });
        thread.run();

    }
}
