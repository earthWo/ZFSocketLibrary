package socketconnect.con;




import socketconnect.callback.CallbackSet;
import socketconnect.callback.MessageCallback;
import socketconnect.callback.SocketCallback;
import socketconnect.model.Connecter;

/**
 * Created by wuzefeng on 2017/10/13.
 */

public class SocketConnect {

    private static volatile SocketConnect sInstance;

     private final SocketHelper mSocketHelper;


    private SocketConnect(){
        mSocketHelper=SocketHelper.getInstance();
    }

    public static void init(){
        if(sInstance==null){
            synchronized (SocketConnect.class){
                if(sInstance==null){
                    sInstance=new SocketConnect();
                }
            }
        }
    }

    public static SocketConnect get(){
        return sInstance;
    }

    public SocketConnect setSocketCallback(SocketCallback mSocketCallback) {
        mSocketHelper.setSocketCallback(mSocketCallback);
        return this;
    }

    public void connect(){
       mSocketHelper.connect(); 
    }

    public void sendTextMessage(int socketId,String message){
       mSocketHelper.sendTextMessage(socketId,CallbackSet.decodeMessageId(message),message);
    }
    
    public void sendVoiceMessage(int socketId, String fileName) {
        mSocketHelper.sendVoiceMessage(socketId, CallbackSet.decodeMessageId(System.currentTimeMillis()+""), fileName);
    }
    
    public void sendVideoMessage(int socketId, String fileName) {
        mSocketHelper.sendVideoMessage(socketId, CallbackSet.decodeMessageId(System.currentTimeMillis() + ""), fileName);
    }

    public void sendImageMessage(int socketId, String fileName) {
        mSocketHelper.sendImageMessage(socketId, CallbackSet.decodeMessageId(System.currentTimeMillis() + ""), fileName);
    }
    
    public void sendFileMessage(int socketId, String fileName) {
        mSocketHelper.sendFileMessage(socketId, CallbackSet.decodeMessageId(System.currentTimeMillis() + ""), fileName);
    }

    public void sendHeardMessage(int socketId) {
        mSocketHelper.sendHeardMessage(socketId, CallbackSet.decodeMessageId(System.currentTimeMillis()+""));
    }

    public void sendSyncTextMessage(int socketId,String message, MessageCallback callback){
        int messageId=CallbackSet.decodeMessageId(message);
        CallbackSet.get().addCallback(messageId,callback);
        mSocketHelper.sendTextMessage(socketId, messageId, message);
    }
    
    public void sendSyncVoiceMessage(int socketId, String fileName, MessageCallback callback) {
        int messageId = CallbackSet.decodeMessageId(System.currentTimeMillis() + "");
        CallbackSet.get().addCallback(messageId, callback);
        mSocketHelper.sendVoiceMessage(socketId,messageId, fileName);
    }
    
    public void sendSyncVideoMessage(int socketId, String fileName, MessageCallback callback) {
        int messageId = CallbackSet.decodeMessageId(System.currentTimeMillis() + "");
        CallbackSet.get().addCallback(messageId, callback);
        mSocketHelper.sendVideoMessage(socketId, messageId, fileName);
    }
    
    public void sendSyncImageMessage(int socketId, String fileName, MessageCallback callback) {
        int messageId = CallbackSet.decodeMessageId(System.currentTimeMillis() + "");
        CallbackSet.get().addCallback(messageId, callback);
        mSocketHelper.sendImageMessage(socketId, messageId, fileName);
    }

    public void sendSyncFileMessage(int socketId, String fileName, MessageCallback callback) {
        int messageId = CallbackSet.decodeMessageId(System.currentTimeMillis() + "");
        CallbackSet.get().addCallback(messageId, callback);
        mSocketHelper.sendFileMessage(socketId, messageId, fileName);
    }
    
    public void closeAllConnect(){
        mSocketHelper.closeAllConnect();
    }
    
    
    public void closeConnect(Connecter connecter){
        mSocketHelper.closeConnect(connecter);
    }

}
