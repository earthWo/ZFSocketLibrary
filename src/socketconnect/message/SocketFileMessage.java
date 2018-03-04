package socketconnect.message;


import com.alibaba.fastjson.JSON;

import socketconnect.callback.MessageType;
import socketconnect.proto.SocketDataProtos;

/**
 * Created by wuzefeng on 2017/10/17.
 */

public class SocketFileMessage extends SocketMessage{


    public SocketFileMessage() {
        setMessageType(MessageType.MF);
    }

    public SocketFileMessage(SocketDataProtos.SocketData socketData) {
        this();
        setSocketData(socketData);
    }

}
