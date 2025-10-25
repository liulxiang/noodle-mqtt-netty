package com.noodle.netty.mqtt.handler;

import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * @author liulxiang
 * @date:2025-10-25 15:58
 * @description: 请求消息体
 **/
/**
 * MQTT请求类，用于封装MQTT消息的相关属性和行为
 */
public class MqttRequest {
    // 标记请求是否可变的标志位，默认为true
    private boolean mutable = true;
    // 消息的负载内容，即实际传输的数据
    private byte[] payload;
    // 消息的质量等级，默认为至少一次交付(AT_LEAST_ONCE)
    private MqttQoS qos = MqttQoS.AT_LEAST_ONCE;
    // 标记消息是否需要被保留，默认为false
    private boolean retained = false;
    // 标记消息是否为重复消息，默认为false
    private boolean dup = false;
    // 消息的唯一标识符
    private int messageId;

    /**
     * 默认构造函数，初始化一个空负载的MQTT请求
     */
    public MqttRequest() {
        this.setPayload(new byte[0]);
    }

    /**
     * 带负载的构造函数
     * @param payload 消息的负载内容
     */
    public MqttRequest(byte[] payload) {
        this.setPayload(payload);
    }
    /**
     * 带负载和QoS等级的构造函数
     * @param payload 消息的负载内容
     * @param qos 消息的质量等级
     */
    public MqttRequest(byte[] payload,MqttQoS qos) {
        this.setPayload(payload);
        this.setQos(qos);
    }

    /**
     * 获取消息的负载内容
     * @return 消息的负载内容
     */
    public byte[] getPayload() {
        return this.payload;
    }

    /**
     * 清空消息负载内容
     */
    public void clearPayload() {
        this.checkMutable();
        this.payload = new byte[0];
    }

    /**
     * 设置消息的负载内容
     * @param payload 要设置的消息负载内容
     * @throws NullPointerException 如果传入的payload为null
     */
    public void setPayload(byte[] payload) {
        this.checkMutable();
        if (payload == null) {
            throw new NullPointerException();
        } else {
            this.payload = payload;
        }
    }

    /**
     * 获取消息是否被保留的标记
     * @return 如果消息被保留则返回true，否则返回false
     */
    public boolean isRetained() {
        return this.retained;
    }

    /**
     * 设置消息的保留标记
     * @param retained 是否保留消息的标记
     */
    public void setRetained(boolean retained) {
        this.checkMutable();
        this.retained = retained;
    }

    /**
     * 获取消息的质量等级
     * @return 消息的质量等级
     */
    public MqttQoS getQos() {
        return qos;
    }

    /**
     * 设置消息的质量等级
     * @param qos 要设置的消息质量等级
     */
    public void setQos(MqttQoS qos) {
        this.qos = qos;
    }

    /**
     * 获取请求是否可变的标记
     * @return 如果请求可变则返回true，否则返回false
     */
    public boolean isMutable() {
        return mutable;
    }

    /**
     * 设置请求的可变性标记
     * @param mutable 请求是否可变的标记
     */
    public void setMutable(boolean mutable) {
        this.mutable = mutable;
    }

    /**
     * 检查请求是否可变，如果不可变则抛出异常
     * @throws IllegalStateException 如果请求不可变
     */
    protected void checkMutable() throws IllegalStateException {
        if (!this.mutable) {
            throw new IllegalStateException();
        }
    }

    /**
     * 获取消息的重复标记
     * @return 如果消息是重复的则返回true，否则返回false
     */
    public boolean isDup() {
        return dup;
    }

    /**
     * 设置消息的重复标记
     * @param dup 是否为重复消息的标记
     */
    public void setDup(boolean dup) {
        this.dup = dup;
    }

    /**
     * 获取消息的唯一标识符
     * @return 消息的唯一标识符
     */
    public int getMessageId() {
        return messageId;
    }

    /**
     * 设置消息的唯一标识符
     * @param messageId 要设置的消息唯一标识符
     */
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    /**
     * 将消息负载转换为字符串表示
     * @return 消息负载的字符串表示
     */
    @Override
    public String toString() {
        return new String(this.payload);
    }
}
