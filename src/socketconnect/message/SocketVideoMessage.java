package socketconnect.message;


import com.alibaba.fastjson.JSON;

import socketconnect.callback.MessageType;
import socketconnect.proto.SocketDataProtos;

/**
 * Created by wuzefeng on 2017/10/17.
 */

public class SocketVideoMessage extends SocketMessage{


    public SocketVideoMessage() {
        setMessageType(MessageType.MM);
    }

    public SocketVideoMessage(SocketDataProtos.SocketData socketData) {
        this();
        setSocketData(socketData);
    }

}
