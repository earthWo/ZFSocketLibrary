package socketconnect.message;


import com.alibaba.fastjson.JSON;



import socketconnect.callback.MessageType;

/**
 * Created by wuzefeng on 2017/10/17.
 */

public class SocketVoiceMessage extends SocketMessage{

    private String voiceName;


    public String getVoiceName() {
        return voiceName;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }

    public SocketVoiceMessage() {
        setMessageType(MessageType.MV);
    }

    @Override
    public byte[] toByteArray() {
       return JSON.toJSONBytes(this);
    }
}
