package com.noodle.netty.websocket.service;

import java.util.Collection;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.noodle.netty.core.cache.IServerListCache;
import com.noodle.netty.core.dto.ClientDTO;
import com.noodle.netty.core.enums.ServerTypeEnum;
import com.noodle.netty.core.server.IServer;
import com.noodle.netty.core.service.AbsDataService;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;


/**
 * @author liulxiang
 * @date 2025/10/25 13:15
 */
@Slf4j
@Service
public class WebSocketDataServiceImpl extends AbsDataService {
    @Override
    public String inputListening(Channel channel, String topic, String inputMessage) {
        log.info("ws来货啦+====》{}",inputMessage);
        IServer iServer = IServerListCache.SERVER_LIST_MAP.get(ServerTypeEnum.WS.TYPE);
        Collection<ClientDTO> clientAllList = iServer.getClientAllList();
        if (!CollectionUtils.isEmpty(clientAllList)) {
            for (ClientDTO clientDTO : clientAllList) {
                Channel channel1 = clientDTO.getChannel();
                channel1.writeAndFlush(new TextWebSocketFrame(inputMessage));
            }
        }
//        channel.writeAndFlush(new TextWebSocketFrame(inputMessage));
        return inputMessage;
    }
}
