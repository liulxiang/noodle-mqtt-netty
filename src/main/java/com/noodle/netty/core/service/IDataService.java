package com.noodle.netty.core.service;

import io.netty.channel.Channel;

/**
 * 数据接收，发送业务逻辑接口
 * @author liulxiang
 * @date 2025/10/25 13:15
 */
public interface IDataService {

    /**
     * 接收到的消息
     *
     * @param topic 主题
     * @param inputMessage: 接收数据
     * @return String: 向客户端响应的数据
     * @author liulxiang
     * @date 2025/10/25 13:15
     */
    String inputListening(Channel channel, String topic, String inputMessage);

    /**
     * 推送数据
     *
     * @param userOnlyCode: 用户唯一标识
     * @param message: 消息内容
     * @return
     * @author liulxiang
     * @date 2025/10/25 13:15
     */
    void push(String userOnlyCode,String message);

    /**
     * 推送主题内所有用户
     *
     * @param topics: 主题
     * @param message: 消息内容
     * @return
     * @author liulxiang
     * @date 2025/10/25 13:15
     */
    void pushTopicAll(String message,String... topics);

    /**
     * 推送全部用户
     *
     * @param message: 消息内容
     * @return
     * @author liulxiang
     * @date 2025/10/25 13:15
     */
    void pushAll(String message);


}
