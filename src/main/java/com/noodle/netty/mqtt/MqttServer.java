package com.noodle.netty.mqtt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.noodle.netty.core.cache.IServerListCache;
import com.noodle.netty.core.dto.ClientDTO;
import com.noodle.netty.core.dto.MqttServerConfig;
import com.noodle.netty.core.enums.ServerRunStatusEnum;
import com.noodle.netty.core.enums.ServerTypeEnum;
import com.noodle.netty.core.server.AbsServer;
import com.noodle.netty.core.server.IServer;
import com.noodle.netty.mqtt.handler.MqttHandler;
import com.noodle.netty.mqtt.handler.MqttRequest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liulxiang
 * @date:2025-10-25 14:28
 * @description: netty服务
 **/
/**
 * MQTT服务器类，继承自AbsServer<MqttServerConfig>
 * 使用@Slf4j注解进行日志记录
 */
@Slf4j
public class MqttServer extends AbsServer<MqttServerConfig> {

    /**
     * 启动MQTT服务器
     * @return 返回IServer接口实例，便于链式调用
     */
    public IServer run (){
        // 检查服务器配置是否为空
        if (serverConfig == null) {
            throw new RuntimeException("启动参数错误:"+ JSONObject.toJSONString(serverConfig));
        }
        // 记录服务器启动信息，包括boss线程数和work线程数
        log.info("netty服务启动中...线程数-boss:{},work:{}",serverConfig.getBoosTread(),serverConfig.getWorkTread());
        // 构建主线程-用于分发socket请求
        EventLoopGroup boosGroup = new NioEventLoopGroup(serverConfig.getBoosTread());
        // 构建工作线程-用于处理请求处理
        EventLoopGroup workGroup = new NioEventLoopGroup(serverConfig.getWorkTread());
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boosGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
//                    .childOption(ChannelOption.SO_BACKLOG,1024)     //等待队列
                    .childOption(ChannelOption.SO_REUSEADDR,true)   //快速复用
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 这个地方注意，如果客户端发送请求体超过此设置值，会抛异常
                            socketChannel.pipeline().addLast(new NoodleMqttDecoder(1024*1024));
                            socketChannel.pipeline().addLast( MqttEncoder.INSTANCE);
                            // 加载MQTT编解码协议，包含业务逻辑对象
                            socketChannel.pipeline().addLast("mqttHandler",new MqttHandler(serverConfig));
                        }
                    });
            serverBootstrap.bind(serverConfig.getPort()).addListener(future -> {
                log.info("MQTT服务端成功绑定端口号={}",serverConfig.getPort());
            });

        }catch (Exception e){
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            log.error("mqttServer启动失败:{}",e);
        }
        // 将已启动的服务添加到 服务列表，便于以后管理使用
        IServerListCache.SERVER_LIST_MAP.put(ServerTypeEnum.MQTT.TYPE,this);
        return this;
    }

    public ChannelFuture send(Channel channel, String topic,String sendMessage ) throws InterruptedException {
        MqttRequest request = new MqttRequest((sendMessage.getBytes()));
        MqttPublishMessage pubMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBLISH,
                        request.isDup(),
                        request.getQos(),
                        request.isRetained(),
                        0),
                new MqttPublishVariableHeader(topic, 0),
                Unpooled.buffer().writeBytes(request.getPayload()));
        // 超过高水位，则采取同步模式
        if (channel.isWritable()) {
            return channel.writeAndFlush(pubMessage);
        }
        return channel.writeAndFlush(pubMessage).sync();
    }
    public void sendAll(String topic, String sendMessage){
        if (serverConfig.getClientCache() == null) {
            return;
        }
        if(serverConfig.getClientCache().count()>0){
            for (ClientDTO clientDTO : serverConfig.getClientCache().getAllList()) {
                try {
                    send(clientDTO.getChannel(),topic,sendMessage);
                }catch (Exception e){
                    log.error("主题:{}，推送用户{}失败",topic,clientDTO.getChannel().id());
                }
            }
        }
    }

    @Override
    public Collection<ClientDTO> getClientAllList() {
        if (serverConfig.getClientCache() == null || CollectionUtils.isEmpty(serverConfig.getClientCache().getAllList())){
            return new ArrayList<>();
        }
        return serverConfig.getClientCache().getAllList();
    }

    @Override
    public List<ClientDTO> getClientListByTopic(String... topic) {
        return null;
    }

    @Override
    public ServerRunStatusEnum status() {
        return null;
    }

    public MqttServer(MqttServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public MqttServer() {
    }
}
