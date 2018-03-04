package socketconnect.con;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import socketconnect.callback.CallbackSet;
import socketconnect.callback.MessageCallback;
import socketconnect.callback.MessageType;
import socketconnect.callback.SocketCallback;
import socketconnect.exception.SocketException;
import socketconnect.message.SocketFileMessage;
import socketconnect.message.SocketImageMessage;
import socketconnect.message.SocketMessage;
import socketconnect.message.SocketTextMessage;
import socketconnect.message.SocketVideoMessage;
import socketconnect.message.SocketVoiceMessage;
import socketconnect.model.Connecter;
import socketconnect.utils.ByteUtil;

/**
 * 负责socket的连接传输
 * Created by wuzefeng on 2017/10/13.
 */

public class SocketHelper {


    private static volatile SocketHelper sInstance;

    private SocketCallback mSocketCallback;

    private MessageThread mConnectMessageThread;

    private HeartThread mConnectHeartThread;

    private ServerSocket messageServce;

    private FileThread mFileThread;

    private ServerSocket heartServce;

    private ServerSocket fileServce;

    private static final int PORT=20006;

    private static final int HEART_PORT=20007;

    private static final int FILE_PORT=20008;

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
        if(mConnectHeartThread==null){
            mConnectHeartThread=new HeartThread();
            mConnectHeartThread.setName("心跳线程");
            mConnectHeartThread.start();
        }
        if(mConnectMessageThread==null){
            mConnectMessageThread=new MessageThread();
            mConnectMessageThread.setName("消息线程");
            mConnectMessageThread.start();
        }
        if(mFileThread==null){
            mFileThread=new FileThread();
            mFileThread.setName("文件线程");
            mFileThread.start();
        }
    }

    public Connecter getConnecter(int key){
        return mConnecter.get(key);
    }


    public void setSocketCallback(SocketCallback mSocketCallback) {
        this.mSocketCallback = mSocketCallback;
    }


    private void connectMessageSuccess(Socket messageSocket){
        Connecter connecter;
        int key=messageSocket.getInetAddress().hashCode();
        if(mConnecter.containsKey(key)){
            connecter=mConnecter.get(key);
        }else{
            connecter=new Connecter();
            mConnecter.put(key,connecter);
            connecter.setSocketId(key);
        }
        connecter.setMessageSocket(messageSocket);
        SocketMessageSender.createMessageSender(connecter);
        SocketMessageReceiver.createMessageReceiver(connecter);
    }

    private void connectHeartSuccess(Socket heartSocket){
        Connecter connecter;
        int key=heartSocket.getInetAddress().hashCode();
        if(mConnecter.containsKey(key)){
            connecter=mConnecter.get(key);
        }else{
            connecter=new Connecter();
            mConnecter.put(key,connecter);
            connecter.setSocketId(key);
        }
        connecter.setHeartSocket(heartSocket);
        SocketHeartSender.getInstance();
        SocketHeartReceiver.createMessageReceiver(connecter);
        if(mSocketCallback!=null){
            mSocketCallback.connected(connecter);
        }
    }

    private void connectFileSuccess(Socket fileSocket){
        Connecter connecter;
        int key=fileSocket.getInetAddress().hashCode();
        if(mConnecter.containsKey(key)){
            connecter=mConnecter.get(key);
        }else{
            connecter=new Connecter();
            mConnecter.put(key,connecter);
            connecter.setSocketId(key);
        }
        connecter.setFileSocket(fileSocket);
        SocketFileSender.createFileSender(connecter);
        SocketFileReceiver.createFileReceiver(connecter);
    }



    public void receiverMessage(Connecter connecter,SocketMessage message){
        if (this.mSocketCallback != null) {
            this.mSocketCallback.receiveMessage(message,connecter);
        }
    }


    public void sendMessageError(SocketMessage message,SocketException e){
        if(message!=null){
            MessageCallback callback=CallbackSet.get().getCallback(message.getMessageId());
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
            mConnectMessageThread=null;
            SocketHeartSender.getInstance().closeThread();
            SocketMessageSender.closeThreads();
            SocketMessageReceiver.closeThreads();
            mConnecter.clear();
            messageServce.close();
            messageServce=null;
        } catch (IOException ex) {
            Logger.getLogger(SocketHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeConnect(Connecter connecter) {
        SocketHeartSender.getInstance().closeThread();
        SocketMessageSender.closeThread(connecter);
        SocketMessageReceiver.closeThread(connecter);
        connecter.closeConnect();
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
            SocketFileSender.get(socketId).addMessage(socketMessage);
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
            SocketFileSender.get(socketId).addMessage(socketMessage);
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
            SocketFileSender.get(socketId).addMessage(socketMessage);
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
            SocketFileSender.get(socketId).addMessage(socketMessage);
        }
    }



    public void sendHeardMessage(int socketId,int messageId) {
        if (socketId != 0) {
            SocketTextMessage socketMessage = new SocketTextMessage();
            socketMessage.setMessageId(messageId);
            socketMessage.setData("这是一个心跳".getBytes());
            socketMessage.setMessageType(MessageType.MH);
            socketMessage.setText("这是一个心跳");

            SocketHeartSender.SocketHeartMessage message=new SocketHeartSender.SocketHeartMessage();
            Connecter connecter=SocketHelper.getInstance().getConnecter(socketId);
            if(connecter!=null&&connecter.getHeartSocket()!=null){
                message.setSocket(connecter.getHeartSocket());
                message.setMessageType(MessageType.MH);
                message.setSocketMessage(socketMessage);
            }
            SocketHeartSender.getInstance().addMessage(message);
        }
    }




    private class MessageThread extends Thread{

        @Override
        public void run() {
            //未初始化或未连接
            if(messageServce==null){
                try {
                    messageServce=new ServerSocket(PORT);
                    serviceStart();
                    while(true){
                        Socket s=messageServce.accept();
                        connectMessageSuccess(s);
                    }

                } catch (IOException ex) {
                    connectFailure(ex);
                }
            }

        }
    }

    private class HeartThread extends Thread{

        @Override
        public void run() {
            //未初始化或未连接
            if(heartServce==null){
                try {
                    heartServce=new ServerSocket(HEART_PORT);
                    serviceStart();
                    while(true){
                        Socket s=heartServce.accept();
                        connectHeartSuccess(s);
                    }

                } catch (IOException ex) {
                    connectFailure(ex);
                }
            }

        }
    }

    private class FileThread extends Thread{

        @Override
        public void run() {
            //未初始化或未连接
            if(fileServce==null){
                try {
                    fileServce=new ServerSocket(FILE_PORT);
                    while(true){
                        Socket s=fileServce.accept();
                        connectFileSuccess(s);
                    }

                } catch (IOException ex) {
                    connectFailure(ex);
                }
            }

        }
    }



}
