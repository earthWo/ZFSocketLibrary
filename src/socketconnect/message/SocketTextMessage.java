package socketconnect.message;

import com.alibaba.fastjson.JSON;
import socketconnect.callback.MessageType;
import socketconnect.proto.SocketDataProtos;

/**
 * Created by wuzefeng on 2017/10/17.
 */

public class SocketTextMessage extends SocketMessage{

    public SocketTextMessage() {
        setMessageType(MessageType.MT);
    }

    public SocketTextMessage(SocketDataProtos.SocketData socketData) {
        this();
        setSocketData(socketData);
    }

}
