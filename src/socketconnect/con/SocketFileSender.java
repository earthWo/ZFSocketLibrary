package socketconnect.con;

import socketconnect.callback.MessageType;
import socketconnect.exception.SocketException;
import socketconnect.message.SocketMessage;
import socketconnect.message.SocketTextMessage;
import socketconnect.model.Connecter;
import socketconnect.utils.ByteUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by wuzefeng on 2017/10/13.
 */

public class SocketFileSender {

    public static Map<Integer,SocketFileSender>sMapSender=new HashMap<>();

    private BlockingDeque<SocketMessage>mMessageDeuqe;

    private SendThread mThread;

    private Socket mSocket;

    private SocketFileSender(Connecter connecter) {
        this.mSocket=connecter.getFileSocket();
        mMessageDeuqe=new LinkedBlockingDeque<>();
        mThread=new SendThread();
        mThread.setName("发送消息线程");
        mThread.start();
        
    }
    
    
    public static void createFileSender(Connecter connecter){
         SocketFileSender sender=new SocketFileSender(connecter);
        sMapSender.put(connecter.getSocketId(), sender);
    }
    
    
    public static SocketFileSender get(int socketId){
        return sMapSender.get(socketId);
    }
    

    public void setSocket(Socket mSocket) {
        this.mSocket = mSocket;
    }

    public void addMessage(SocketMessage socketMessage){
        try {
            mMessageDeuqe.putLast(socketMessage);
        } catch (InterruptedException e) {
          
        }
    }

    private class SendThread extends Thread{

        @Override
        public void run() {
            SocketMessage message=null;
            while(true){
                try {
                    message=mMessageDeuqe.takeFirst();
                    if(message.getMessageType().equals(MessageType.MC)) {
                        break;
                    }
                    OutputStream outputStream = mSocket.getOutputStream();
                    byte[][] ms = ByteUtil.getBytesByMessage(message);
                    for (byte[] m : ms) {
                        outputStream.write(m);
                    }
                    outputStream.flush();
                } catch (InterruptedException | IOException e) {
                    SocketHelper.getInstance().sendMessageError(message, new SocketException(e.getMessage()));
                }
            }
        }
    }

    public static void closeThreads() {
        for (int key : sMapSender.keySet()) {
            SocketFileSender sender = sMapSender.get(key);
            sender.closeThread();
        }
        sMapSender.clear();

    }
    
    public static void closeThread(Connecter connceter) {
        SocketFileSender sender = sMapSender.get(connceter.getSocketId());
        if (sender != null) {
            sender.closeThread();
            sMapSender.remove(connceter.getSocketId());
        }
    }

    public void closeThread(){
        try {
            SocketMessage message=new SocketTextMessage();
            message.setMessageType(MessageType.MC);
            mMessageDeuqe.putFirst(message);
        } catch (InterruptedException e) {
           
        }
        mThread=null;       
    }





}
