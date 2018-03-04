package socketconnect.con;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import socketconnect.callback.MessageType;
import socketconnect.exception.SocketException;
import socketconnect.message.SocketMessage;
import socketconnect.model.Connecter;
import socketconnect.utils.ByteUtil;


/**
 * 心跳
 * Created by wuzefeng on 2017/10/17.
 */

public class SocketHeartSender {


    private SendThread mThread;

    private BlockingDeque<SocketHeartMessage>mMessageDeuqe;

    private volatile static SocketHeartSender instance;

    public static SocketHeartSender getInstance(){
        if(instance==null){
            synchronized (SocketHeartSender.class){
                if(instance==null){
                    instance=new SocketHeartSender();
                }
            }
        }
        return instance;
    }



    private SocketHeartSender(){
        mThread=new SendThread();
        mThread.setName("发送心跳线程");
        mMessageDeuqe=new LinkedBlockingDeque<>();
        mThread.start();
    }

    public void addMessage(SocketHeartMessage message) {
        if(mMessageDeuqe!=null){
            mMessageDeuqe.add(message);
        }
    }


    private class SendThread extends Thread{

        @Override
        public void run() {
            SocketHeartMessage message=null;
            while(true){
                try {
                    message=mMessageDeuqe.takeFirst();
                    if(message.getMessageType().equals(MessageType.MC)) {
                        break;
                    }
                    OutputStream outputStream = message.getSocket().getOutputStream();
                    byte[][] ms = ByteUtil.getBytesByMessage(message.getSocketMessage());
                    for (byte[] m : ms) {
                        outputStream.write(m);
                    }
                    outputStream.flush();
                    System.out.println("发送心跳");
                } catch (InterruptedException | IOException e) {
                    SocketHelper.getInstance().sendMessageError(message.getSocketMessage(), new SocketException(e.getMessage()));
                }
            }
        }
    }



    public void closeThread(){
        if (mThread != null) {
            mThread.interrupt();
        }
        mMessageDeuqe.clear();
        mThread = null;
        instance=null;
    }



    public static class SocketHeartMessage{

        Socket socket;

        String messageType;

        SocketMessage socketMessage;

        public Socket getSocket() {
            return socket;
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
        }

        public String getMessageType() {
            return messageType;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }

        public SocketMessage getSocketMessage() {
            return socketMessage;
        }

        public void setSocketMessage(SocketMessage socketMessage) {
            this.socketMessage = socketMessage;
        }
    }
    

}
