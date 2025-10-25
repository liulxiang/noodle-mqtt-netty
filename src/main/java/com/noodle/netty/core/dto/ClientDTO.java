package com.noodle.netty.core.dto;

import java.util.List;

import io.netty.channel.Channel;
import lombok.Data;

/**
 * 客户端对象
 * @author liulxiang
 * @date 2025/10/25 13:15
 */
@Data
public class ClientDTO {
    // 客户端id
    private String id;
    // 对应通道
    private Channel channel;
    // 订阅主题
    private List<String> topic;

    public static ClientDTO build(String id, Channel channel){
        return build(id,channel,null);
    }
    public static ClientDTO build(String id, Channel channel, List<String> topic){
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(id);
        clientDTO.setChannel(channel);
        clientDTO.setTopic(topic);
        return clientDTO;
    }
}
