package socketconnect.con;

import socketconnect.callback.CallbackSet;
import socketconnect.callback.MessageCallback;
import socketconnect.callback.MessageType;
import socketconnect.callback.SocketCallback;
import socketconnect.exception.SocketException;
import socketconnect.message.*;
import socketconnect.model.Connecter;
import socketconnect.utils.ByteUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 负责socket的连接传输
 * Created by wuzefeng on 2017/10/13.
 */

public class SocketHelper {


    private static volatile SocketHelper sInstance;

    private SocketCallback mSocketCallback;

    private ConnectThread mConnectThread;
    
    private ServerSocket servce;
    
    private static final int PORT=20006;
    
    
    private Map<Integer,Connecter>mConnecter;


    private SocketHelper(){
        mConnecter=new HashMap<>();
    }
    
    


    public static SocketHelper getInstance(){
        if(sInstance==null){
            synchronized (SocketHelper.class){
                if(sInstance==null){
                    sInstance=new SocketHelper();
                }
            }
        }
        return sInstance;
    }
    
    
    public void disconncted(Connecter connceter) {
        if (mSocketCallback != null) {
            mSocketCallback.disconnected(connceter);
        }
    }



    /**
     * 连接socket
     */
    public void connect(){
        if(mConnectThread==null){
            mConnectThread=new ConnectThread();
            mConnectThread.setName("连接线程");
            mConnectThread.start();
        }
    }


    public void setSocketCallback(SocketCallback mSocketCallback) {
        this.mSocketCallback = mSocketCallback;
    }


    private void connectSuccess(Socket socket){
        Connecter connecter=new Connecter(socket);
        SocketMessageSender.createMessageSender(connecter);
        SocketMessageReceiver.createMessageReceiver(connecter);
        SocketHeartSender.createHeardMessageSender(connecter);
        mConnecter.put(connecter.getSocketId(),connecter);
        if(mSocketCallback!=null){
            mSocketCallback.connected(connecter);
        }
        
    }
    
    public void receiverMessage(Connecter connecter, SocketMessage message){
        if (this.mSocketCallback != null) {
            this.mSocketCallback.receiveMessage(message,connecter);
        } 
    }
    
    
    public void sendMessageError(SocketMessage message, SocketException e){
        if(message!=null){
            MessageCallback callback= CallbackSet.get().getCallback(message.getMessageId());
            if(callback!=null){
                callback.requestError(message, e);
            }
        }
        
    }


    private void connectFailure(IOException e){
        if(mSocketCallback!=null){
            mSocketCallback.connectError(new SocketException(e.getMessage()));
        }
    }
    
    private void serviceStart() {
        if (mSocketCallback != null) {
            try {
                InetAddress addr = InetAddress.getLocalHost();
                mSocketCallback.startService(addr.getHostAddress());
            } catch (UnknownHostException ex) {
            }
        }
    }


    public void closeAllConnect(){
        try {
            mConnectThread=null;
            SocketHeartSender.closeThreads();
            SocketMessageSender.closeThreads();
            SocketMessageReceiver.closeThreads();
            mConnecter.clear();
            servce.close();
            servce=null;
        } catch (IOException ex) {
            Logger.getLogger(SocketHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void closeConnect(Connecter connecter) {
       SocketHeartSender.closeThread(connecter);
       SocketMessageSender.closeThread(connecter);
       SocketMessageReceiver.closeThread(connecter);
       connecter.closeSocket();
       mConnecter.remove(connecter.getSocketId());
    }



    public void sendTextMessage(int socketId,int messageId,String message){
        if(socketId!=0) {
            SocketTextMessage socketMessage = new SocketTextMessage();
            socketMessage.setMessageId(messageId);
            socketMessage.setData(message.getBytes());
            socketMessage.setText(message);
            socketMessage.setMessageType(MessageType.MT);
            SocketMessageSender.get(socketId).addMessage(socketMessage);
        }
    }
    
    public void sendVoiceMessage(int socketId,int messageId, String fileName) {
        if (socketId!=0) {
            SocketVoiceMessage socketMessage = new SocketVoiceMessage();
            socketMessage.setMessageId(messageId);
            try {
                socketMessage.setData(ByteUtil.fileToByte(fileName));
            } catch (IOException e) {
                MessageCallback callback = CallbackSet.get().getCallback(messageId);
                if (callback != null) {
                    callback.requestError(socketMessage, new SocketException(e.getMessage()));
                } else {
                    throw new SocketException(e.getMessage());
                }
            }
            socketMessage.setText(fileName.substring(fileName.lastIndexOf("/")+1));
            SocketMessageSender.get(socketId).addMessage(socketMessage);
        }
    }

    public void sendVideoMessage(int socketId,int messageId, String fileName) {
        if (socketId!=0) {
            SocketVideoMessage socketMessage = new SocketVideoMessage();
            socketMessage.setMessageId(messageId);
            try {
                socketMessage.setData(ByteUtil.fileToByte(fileName));
            } catch (IOException e) {
                MessageCallback callback = CallbackSet.get().getCallback(messageId);
                if (callback != null) {
                    callback.requestError(socketMessage, new SocketException(e.getMessage()));
                } else {
                    throw new SocketException(e.getMessage());
                }
            }
            socketMessage.setText(fileName.substring(fileName.lastIndexOf("/")+1));
            SocketMessageSender.get(socketId).addMessage(socketMessage);
        }
    }

    public void sendImageMessage(int socketId,int messageId, String fileName) {
        if (socketId != 0) {
            SocketImageMessage socketMessage = new SocketImageMessage();
            socketMessage.setMessageId(messageId);
            try {
                socketMessage.setData(ByteUtil.fileToByte(fileName));
            } catch (IOException e) {
                MessageCallback callback = CallbackSet.get().getCallback(messageId);
                if (callback != null) {
                    callback.requestError(socketMessage, new SocketException(e.getMessage()));
                } else {
                    throw new SocketException(e.getMessage());
                }
            }
            socketMessage.setText(fileName.substring(fileName.lastIndexOf("/")+1));
            SocketMessageSender.get(socketId).addMessage(socketMessage);
        }
    }

    public void sendFileMessage(int socketId,int messageId, String fileName) {
        if (socketId != 0) {
            
            SocketFileMessage socketMessage = new SocketFileMessage();
            socketMessage.setMessageId(messageId);
            try {
                socketMessage.setData(ByteUtil.fileToByte(fileName));
            } catch (IOException e) {
                MessageCallback callback = CallbackSet.get().getCallback(messageId);
                if (callback != null) {
                    callback.requestError(socketMessage, new SocketException(e.getMessage()));
                } else {
                    throw new SocketException(e.getMessage());
                }
            }
            socketMessage.setText(fileName.substring(fileName.lastIndexOf("/")+1));
            SocketMessageSender.get(socketId).addMessage(socketMessage);
        }
    }

    
    
    public void sendHeardMessage(int socketId,int messageId) {
        if (socketId != 0) {
            SocketTextMessage socketMessage = new SocketTextMessage();
            socketMessage.setMessageId(messageId);
            socketMessage.setData("这是一个心跳".getBytes());
            socketMessage.setMessageType(MessageType.MH);
            socketMessage.setText("这是一个心跳");
            SocketMessageSender.get(socketId).addMessage(socketMessage);
        }
    }


   

    private class ConnectThread extends Thread{

        @Override
        public void run() {
            //未初始化或未连接
            if(servce==null){
                try {
                    servce=new ServerSocket(PORT);
                    serviceStart();
                    while(true){
                       Socket s=servce.accept();
                       connectSuccess(s);
                    }
                                      
                } catch (IOException ex) {
                     connectFailure(ex);
                }
            }
           
        }
    }



}
