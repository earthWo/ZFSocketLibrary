package socketconnect.message;

import java.io.Serializable;

/**
 * Created by wuzefeng on 2017/10/13.
 */

public abstract class SocketMessage implements Serializable {
    
    
    private int messageId;

    private String messageType;

    private byte[] data;

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
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


    public abstract byte[] toByteArray();
  
}
