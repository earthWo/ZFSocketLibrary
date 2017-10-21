package socketconnect.message;


import com.alibaba.fastjson.JSON;

import socketconnect.callback.MessageType;

/**
 * Created by wuzefeng on 2017/10/17.
 */

public class SocketFileMessage extends SocketMessage{

    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public SocketFileMessage() {
        setMessageType(MessageType.MF);
    }

    @Override
    public byte[] toByteArray() {
       return JSON.toJSONBytes(this);
    }
}
