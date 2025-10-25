package com.noodle.netty.mqtt.handler;

import static io.netty.handler.codec.mqtt.MqttQoS.AT_LEAST_ONCE;
import static io.netty.handler.codec.mqtt.MqttQoS.EXACTLY_ONCE;

import java.io.UnsupportedEncodingException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.noodle.netty.core.dto.MqttServerConfig;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubAckPayload;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import lombok.extern.slf4j.Slf4j;

/**
* @author liulxiang
* @date: 2020/7/29 13:22
* @description: MQTT业务类
*/
@Slf4j
@ChannelHandler.Sharable
public class MqttHandler extends ChannelInboundHandlerAdapter {

    private MqttServerConfig serverConfig;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof MqttMessage) {
            Channel channel = ctx.channel();
            MqttMessage message = (MqttMessage) msg;

            MqttMessageType messageType = message.fixedHeader().messageType();
            log.info("MQTT接收到的发送类型===》{}",messageType);

            switch (messageType) {
                // 建立连接
                case CONNECT:
                        try {
                            this.connect(channel, (MqttConnectMessage) message);
                        }catch (Exception e){
                            //如果用户密码，客户端ID校验不成功，会二次建立CONNECT类型连接
                            //但是没有实际意义
                        }
                    break;
                // 推送数据
                case PUBLISH:
                    this.publish(channel, (MqttPublishMessage) message);
                    break;
                // 订阅主题
                case SUBSCRIBE:
                    this.subscribe(channel, (MqttSubscribeMessage) message);
                    break;
                // 退订主题
                case UNSUBSCRIBE:
                    this.unSubscribe(channel, (MqttUnsubscribeMessage) message);
                    break;
                // 心跳包
                case PINGREQ:
                    this.pingReq(channel, message);
                    break;
                // 断开连接
                case DISCONNECT:
                    this.disConnect(channel, message);
                    break;
                // 确认收到响应报文,用于服务器向客户端推送qos1后，客户端返回服务器的响应
                case PUBACK:
                    this.puback(channel, (MqttPubAckMessage) message);
                    break;
                default:
                    if (log.isDebugEnabled()) {
                        log.debug("Nonsupport server message  type of '{}'.", messageType);
                    }
                    break;
            }
        }
    }

    /**
     * 处理MQTT客户端连接请求
     * @param channel 客户端通信通道
     * @param msg MQTT连接消息对象
     */
    public void connect(Channel channel, MqttConnectMessage msg) {
        // 客户端登录校验
        if (serverConfig.getAuthLoginOnOff()) {
            String userName = msg.payload().userName();  // 获取用户名
            byte[] passwordBytes = msg.payload().passwordInBytes();  // 获取密码字节数组
            String password = new String(passwordBytes);  // 将密码字节数组转换为字符串
            // 验证用户名和密码是否正确
            if(! serverConfig.getName().equals(userName) || !serverConfig.getPassword().equals(password)){
                log.info("账号密码不正确，通道关闭===>{}:{}",userName,password);
                // 向客户端返回账号密码不正确消息，其实客户端有重连机制，可直接关闭
                // 以下代码被注释，因为直接关闭通道更高效
//            MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(new MqttFixedHeader(
//                            MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, false, 0),
//                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, true), null);
//            channel.writeAndFlush(okResp);
                disConnect(channel,msg);  // 断开连接
                return;
            }
        }

        //客户端ID校验
        if (serverConfig.getAuthClientIdOnOff()) {
            String clientId = msg.payload().clientIdentifier();  // 获取客户端ID
            // 检查客户端ID是否在允许的列表中
            if(serverConfig.getClientIds().stream().noneMatch(e->e.equals(clientId))){
                log.info("客户端id不匹配，通道关闭===>{}",clientId);
                // 向客户端返回账号密码不正确消息，其实客户端有重连机制，可直接关闭
//            MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(new MqttFixedHeader(
//                            MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, false, 0),
//                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, true), null);
//            channel.writeAndFlush(okResp);
                disConnect(channel,msg);
                return;
            }
        }

        //连接业务校验完成,Qos1类型，需要答复
        MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(new MqttFixedHeader(
                        MqttMessageType.CONNACK, false, AT_LEAST_ONCE, false, 0),
                new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, true), null);
        channel.writeAndFlush(okResp);

        addClient(channel,msg.toString());
    }

    public void pingReq(Channel channel, MqttMessage msg) {
        if (log.isDebugEnabled()) {
            log.debug("MQTT pingReq received.");
        }
        MqttMessage pingResp = new MqttMessage(new MqttFixedHeader(MqttMessageType.PINGRESP, false,
                AT_LEAST_ONCE, false, 0));
        channel.writeAndFlush(pingResp);
    }

    public void disConnect(Channel channel, MqttMessage msg) {
        if (channel.isActive()) {
            channel.close();
            if (log.isDebugEnabled()) {
                log.debug("MQTT channel '{}' was closed.", channel.id().asShortText());
            }
            removeClient(channel,msg.toString());
        }
    }

    public void puback(Channel channel, MqttPubAckMessage msg){
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader = msg.variableHeader();
    }

    public void publish(Channel channel, MqttPublishMessage msg) {
        if(serverConfig.getCheckQos() != msg.fixedHeader().qosLevel()){
            log.info("qos类型不是{}，而是{}", serverConfig.getCheckQos().value(),msg.fixedHeader().qosLevel());
            disConnect(channel,msg);
            return;
        }
        ByteBuf buf = msg.content().duplicate();
        byte[] tmp = new byte[buf.readableBytes()];
        buf.readBytes(tmp);
        String content = null;
        try {
            content = new String(tmp,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //校验传入的数据是否符合要求
        if(StringUtils.isBlank(content)){
            log.error("MQTT接收到的数据包为空===》{}",content);
            puback(channel,msg,"MQTT接收到的数据包为空");
            return;
        }
        // 校验接收的数据是否是JSON格式
        if (serverConfig.getAuthFormatToJson()) {
            if(!isJsonObject(content)){
                log.error("MQTT接收到的数据包不为JSON格式===》{}",content);
                puback(channel,msg,"MQTT接收到的数据包不为JSON格式");
                return;
            }
        }
        log.info("MQTT读取到的客户端发送信息===>{}",content);
        // 校验是否需要返回应答
        if (AT_LEAST_ONCE == serverConfig.getCheckQos() || EXACTLY_ONCE == serverConfig.getCheckQos()) {
            String resultMessage = null;
            // 如果需要加载业务逻辑
            if (serverConfig.getDataService() != null) {
                String topic = msg.variableHeader().topicName();    //主题名称
                resultMessage = serverConfig.getDataService().inputListening(channel,topic,content);
            }
            puback(channel,msg,resultMessage);
        }
    }

    public void subscribe(Channel channel, MqttSubscribeMessage msg) {
        MqttSubAckMessage subAckMessage = (MqttSubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.SUBACK, false, AT_LEAST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()),
                new MqttSubAckPayload(0));
        channel.writeAndFlush(subAckMessage);
    }

    public void unSubscribe(Channel channel, MqttUnsubscribeMessage msg) {
        MqttUnsubAckMessage unSubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBACK, false, AT_LEAST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()), null);
        channel.writeAndFlush(unSubAckMessage);
    }

    // 客户端远程关闭
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        log.error("MQTT客户端被强制关闭:{}:{}",ctx.channel().id().asShortText(),cause);
        if (ctx.channel().isActive()) {
            ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            removeClient(ctx.channel(),ctx.channel().toString());
        }
    }
    // 客户端新增
    private void addClient(Channel channel,String message){
        if (serverConfig.getClientCache() != null) {
        	serverConfig.getClientCache().add(channel,message);
        }
    }
    // 删除客户端
    private void removeClient(Channel channel,String message){
        if (serverConfig.getClientCache() != null) {
        	serverConfig.getClientCache().remove(channel,message);
        }
    }
    // 客户端订阅主题
    private void addTopic(Channel channel,String topic,String message){
        if (serverConfig.getITopicCache() != null) {
        	serverConfig.getITopicCache().add(channel,topic,message);
        }
    }
    // 客户端取消订阅主题
    private void removeTopic(Channel channel,String topic,String message){
        if (serverConfig.getITopicCache() != null) {
        	serverConfig.getITopicCache().remove(channel,topic,message);
        }
    }

    // 客户端QOS1消息类型( MqttQoS.AT_LEAST_ONCE = qos1)，需要服务器响应包
    private void puback(Channel channel, MqttPublishMessage msg, String payLoad){
        MqttPubAckMessage sendMessage = (MqttPubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_LEAST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().packetId()),
                (String) null);
        channel.writeAndFlush(sendMessage);
    }

    public MqttHandler(MqttServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    /**
     * 判断字符串是否可以转化为json对象
     * @param content
     * @return
     */
    public static boolean isJsonObject(String content) {
        if(StringUtils.isBlank(content))
            return false;
        try {
            JSONObject jsonStr = JSONObject.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
