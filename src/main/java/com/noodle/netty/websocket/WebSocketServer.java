package com.noodle.netty.websocket;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;

import com.noodle.netty.core.cache.IServerListCache;
import com.noodle.netty.core.dto.ClientDTO;
import com.noodle.netty.core.dto.WebSocketServerConfig;
import com.noodle.netty.core.enums.ServerRunStatusEnum;
import com.noodle.netty.core.enums.ServerTypeEnum;
import com.noodle.netty.core.server.AbsServer;
import com.noodle.netty.core.server.IServer;
import com.noodle.netty.websocket.handler.WebSocketFrameHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * WS服务
 * @author liulxiang
 * @date 2025/10/25 13:15
 */
/**
 * WebSocket服务器类，继承自抽象服务器AbsServer
 * 使用@Slf4j注解提供日志支持
 */
@Slf4j
public class WebSocketServer extends AbsServer<WebSocketServerConfig> {
    /**
     * 重写run方法，启动WebSocket服务器
     * @return 返回IServer接口对象
     */
    @Override
    public IServer run() {
        // 创建NIO线程组，boosGroup用于接收连接，workGroup用于处理业务
        EventLoopGroup boosGroup = new NioEventLoopGroup(serverConfig.getBoosTread());
        EventLoopGroup workGroup = new NioEventLoopGroup(serverConfig.getWorkTread());
        try {
            // 创建服务器引导对象
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            WebSocketServerConfig initParamDTO = this.serverConfig;
            serverBootstrap.group(boosGroup, workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new ChunkedWriteHandler());

                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            // 这个地方注意，如果客户端发送请求体超过此设置值，会抛异常
                            pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                            pipeline.addLast(new WebSocketFrameHandler(initParamDTO));
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(this.serverConfig.getPort()))
                    .addListener(future -> {
                     log.info("WEBSOCKET服务端成功绑定端口号={}", this.serverConfig.getPort());
                    });
        }catch (Exception e){
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            log.error("websocketServer启动失败:{}",e);
        }
        // 将已启动的服务添加到 服务列表，便于以后管理使用
        IServerListCache.SERVER_LIST_MAP.put(ServerTypeEnum.WS.TYPE,this);
        return this;
    }

    @Override
    public ServerRunStatusEnum status() {
        return null;
    }

    @Override
    public void sendAll(String topic, String sendMessage) {

    }

    @Override
    public Collection<ClientDTO> getClientAllList() {
        return serverConfig.getClientCache().getAllList();
    }

    @Override
    public List<ClientDTO> getClientListByTopic(String... topic) {
        return null;
    }

    public WebSocketServer(WebSocketServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public WebSocketServer() {
    }
}
