package socketconnect.message;


import com.alibaba.fastjson.JSON;



import socketconnect.callback.MessageType;
import socketconnect.proto.SocketDataProtos;

/**
 * Created by wuzefeng on 2017/10/17.
 */

public class SocketVoiceMessage extends SocketMessage{


    public SocketVoiceMessage() {
        setMessageType(MessageType.MV);
    }

    public SocketVoiceMessage(SocketDataProtos.SocketData socketData) {
        this();
        setSocketData(socketData);
    }

}
