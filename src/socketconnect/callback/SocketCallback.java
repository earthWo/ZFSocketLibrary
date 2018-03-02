package socketconnect.callback;

import socketconnect.exception.SocketException;
import socketconnect.message.SocketMessage;
import socketconnect.model.Connecter;

/**
 * Created by wuzefeng on 2017/10/13.
 */

public interface SocketCallback {
    
    void startService(String ip);

    void connected(Connecter connecter);

    void connectError(SocketException e);
    
    void disconnected(Connecter connecter);
    
    void receiveMessage(SocketMessage message, Connecter connecter);


}
