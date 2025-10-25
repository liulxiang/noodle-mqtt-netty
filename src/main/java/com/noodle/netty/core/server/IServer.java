package com.noodle.netty.core.server;

import java.util.Collection;
import java.util.List;

import com.noodle.netty.core.dto.ClientDTO;
import com.noodle.netty.core.enums.ServerRunStatusEnum;


/**
 * 服务接口
 * @author liulxiang
 * @date 2025/10/25 13:15
 */
public interface IServer {
    /**
     * 启动服务
     *
     * @return
     * @author liulxiang
     * @date 2025/10/25 13:15
     */
    IServer run ();

    /**
     * 运行状态
     *
     * @param
     * @return
     * @author liulxiang
     * @date 2025/10/25 13:15
     */
    ServerRunStatusEnum status();

    /**
     * 根据主题发送消息
     *
     * @param
     * @return
     * @author liulxiang
     * @date 2025/10/25 13:15
     */
    void sendAll(String topic, String sendMessage);

    /**
     * 获取所有客户端列表
     *
     * @param
     * @return
     * @author liulxiang
     * @date 2025/10/25 13:15
     */
    Collection<ClientDTO> getClientAllList();

    /**
     * 获取客户端列表，根据主题
     *
     * @param
     * @return
     * @author liulxiang
     * @date 2025/10/25 13:15
     */
    List<ClientDTO> getClientListByTopic(String... topic);

}
