package socketconnect.message;


import com.alibaba.fastjson.JSON;

import socketconnect.callback.MessageType;

/**
 * Created by wuzefeng on 2017/10/17.
 */

public class SocketVideoMessage extends SocketMessage{

    private String vedioName;

    public String getVedioName() {
        return vedioName;
    }

    public void setVedioName(String vedioName) {
        this.vedioName = vedioName;
    }

    public SocketVideoMessage() {
        setMessageType(MessageType.MM);
    }

    @Override
    public byte[] toByteArray() {
       return JSON.toJSONBytes(this);
    }
}
