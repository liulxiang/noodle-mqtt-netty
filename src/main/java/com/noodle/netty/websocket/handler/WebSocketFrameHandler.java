package com.noodle.netty.websocket.handler;

import com.noodle.netty.core.dto.WebSocketServerConfig;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;


/**
 * @author liulxiang
 * @date 2025/10/25 13:15
 */
@Slf4j
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private WebSocketServerConfig serverConfig;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("收到WebSocket消息: {}", msg.text());
        String s = serverConfig.getDataService().inputListening(ctx.channel(), null, msg.text());
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("新WebSocket用户加入: {}", ctx.channel().id().asLongText());
        serverConfig.getClientCache().add(ctx.channel(),null);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("WebSocket用户退出: {}", ctx.channel().id().asLongText());
        serverConfig.getClientCache().remove(ctx.channel(),null);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("WebSocket连接异常", cause);
        if (ctx.channel().isActive()) {
            serverConfig.getClientCache().remove(ctx.channel(),"异常发生");
            ctx.close();
        }
    }

    public WebSocketFrameHandler(WebSocketServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public WebSocketFrameHandler() {
    }
}
