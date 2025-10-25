package com.noodle.netty.core.cache;

import java.util.List;
import java.util.Map;

import com.noodle.netty.core.dto.ClientDTO;

import io.netty.channel.Channel;

/**
 * 主题缓存
 * @author liulxiang
 * @date 2025/10/25 13:15
 */
public interface ITopicCache {
    // 主题总数量
    int count();
    // 添加主题
    void add(Channel channel, String topic, String message);
    // 删除主题
    void remove(Channel channel,String topic,String message);
    // 提取主题内所有客户端
    Map<String, List<ClientDTO>> get(String topic);
}
