package socketconnect.message;

import com.alibaba.fastjson.JSON;

/**
 * Created by wuzefeng on 2017/10/18.
 */

public class SocketData {

    private String messageType;

    private byte[] data;

    public SocketData() {
    }

    public SocketData(SocketMessage message) {
        this.messageType=message.getMessageType();
        this.data=message.toByteArray();
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] toByteArray() {
        return JSON.toJSONBytes(this);
    }
}
