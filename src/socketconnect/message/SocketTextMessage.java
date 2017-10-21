package socketconnect.message;

import com.alibaba.fastjson.JSON;

/**
 * Created by wuzefeng on 2017/10/17.
 */

public class SocketTextMessage extends SocketMessage{
    

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public byte[] toByteArray() {
        return JSON.toJSONBytes(this);
    }
}
