package socketconnect.con;

import com.google.protobuf.ByteString;
import socketconnect.callback.MessageType;
import socketconnect.callback.SocketDataType;
import socketconnect.exception.SocketException;
import socketconnect.message.SocketMessage;
import socketconnect.message.SocketTextMessage;
import socketconnect.model.Connecter;
import socketconnect.proto.SocketDataProtos;
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

public class SocketMessageSender {
    
    public static Map<Integer,SocketMessageSender>sMapSender=new HashMap<>();

    private BlockingDeque<SocketMessage>mMessageDeuqe;

    private SendThread mThread;

    private Socket mSocket;

    private SocketMessageSender(Connecter connecter) {
        this.mSocket=connecter.getSocket();
        mMessageDeuqe=new LinkedBlockingDeque<>();
        mThread=new SendThread();
        mThread.setName("发送线程");
        mThread.start();
        
    }
    
    
    public static void createMessageSender(Connecter connecter){
         SocketMessageSender sender=new SocketMessageSender(connecter);
        sMapSender.put(connecter.getSocketId(), sender);
    }
    
    
    public static SocketMessageSender get(int socketId){
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
                    byte[][] ms = getBytesByMessage(message);
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


    private byte[][] getBytesByMessage(SocketMessage message){
        byte[][]ms;
        SocketDataProtos.SocketData socketData=SocketDataProtos.SocketData.newBuilder()
                .setMessageId(message.getMessageId())
                .setMessageType(message.getMessageType())
                .setText(message.getText())
                .setData(ByteString.copyFrom(message.getData()))
                .build();
        byte[]messageByte=socketData.toByteArray();
        int messageLength=messageByte.length;
        int len = 0;
        //普通消息
        if(message.getMessageType() == null ? SocketDataType.MH != null : !message.getMessageType().equals(SocketDataType.MH)){
            //大于1000分包发送
            if(messageLength>=256){
               
               
                byte[][]msb=ByteUtil.splitBytes(messageByte,256);
                ms=new byte[msb.length][];

                for(int i=0;i<msb.length;i++){
                    if(i==msb.length-1){
                        ms[i]= ByteUtil.unitByteArray(ByteUtil.unitByteArray(SocketDataType.ME.getBytes(),ByteUtil.intToByteArray(msb[i].length)),msb[i]);
                    }else{
                        ms[i]= ByteUtil.unitByteArray(ByteUtil.unitByteArray(SocketDataType.ML.getBytes(),ByteUtil.intToByteArray(msb[i].length)),msb[i]);
                    }  
                    len+=  ms[i].length;
                }
            }else{
                ms=new byte[1][];
                ms[0]= ByteUtil.unitByteArray(ByteUtil.unitByteArray(SocketDataType.MG.getBytes(),ByteUtil.intToByteArray(messageLength)),messageByte);
                return ms;
            }
        }else{//心跳消息

            ms=new byte[1][];
            ms[0]= ByteUtil.unitByteArray(ByteUtil.unitByteArray(SocketDataType.MH.getBytes(),ByteUtil.intToByteArray(messageLength)),messageByte);
            return ms;
        }
       
        return ms;
    }
    
    public static void closeThreads() {
        for (int key : sMapSender.keySet()) {
            SocketMessageSender sender = sMapSender.get(key);
            sender.closeThread();
        }
        sMapSender.clear();

    }
    
    public static void closeThread(Connecter connceter) {
        SocketMessageSender sender = sMapSender.get(connceter.getSocketId());
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
