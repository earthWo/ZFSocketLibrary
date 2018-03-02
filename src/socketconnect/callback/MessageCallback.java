package socketconnect.callback;

import socketconnect.exception.SocketException;
import socketconnect.message.SocketMessage;

/**
 * Created by wuzefeng on 2017/10/13.
 */

public interface MessageCallback {


    void response(int messageId, String message);
    
    
    void requestError(SocketMessage message, SocketException e);


}
