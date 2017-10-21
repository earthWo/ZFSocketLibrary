package socketconnect.message;


import com.alibaba.fastjson.JSON;

import socketconnect.callback.MessageType;

/**
 * Created by wuzefeng on 2017/10/17.
 */

public class SocketImageMessage extends SocketMessage{

    private String imageName;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public SocketImageMessage() {
        setMessageType(MessageType.MP);
    }

    @Override
    public byte[] toByteArray() {
       return JSON.toJSONBytes(this);
    }
}
