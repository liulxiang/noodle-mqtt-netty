package com.noodle.netty.mqtt.service;

import org.springframework.stereotype.Service;

import com.noodle.netty.core.service.AbsDataService;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * MQTT服务业务实现对象
 * @author liulxiang
 * @date 2025/10/25 13:15
 */
@Slf4j
@Service
public class MqttDataServiceImpl extends AbsDataService {
    @Override
    public String inputListening(Channel channel, String topic, String inputMessage) {
        log.info("MQTT监听到了数据:{}",inputMessage);
        super.pushTopicAll(inputMessage,topic);
        log.info("MQTT返回的数据:{}",inputMessage);
        return inputMessage;
    }

}
