package socketconnect.message;


import com.alibaba.fastjson.JSON;

import socketconnect.callback.MessageType;
import socketconnect.proto.SocketDataProtos;

/**
 * Created by wuzefeng on 2017/10/17.
 */

public class SocketImageMessage extends SocketMessage{


    public SocketImageMessage() {
        setMessageType(MessageType.MP);
    }

    public SocketImageMessage(SocketDataProtos.SocketData socketData) {
        this();
        setSocketData(socketData);
    }

}
