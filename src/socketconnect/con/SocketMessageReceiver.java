package socketconnect.con;


import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import socketconnect.model.Connecter;

/**
 * Created by wuzefeng on 2017/10/13.
 */

public class SocketMessageReceiver {

    private Socket mSocket;

    private ReceiverThread mReceiverThread;
    
    private SocketDataParse mParse;
    
    private Connecter mConnecter;
    
    public static Map<Integer, SocketMessageReceiver> sMapReceiver = new HashMap<>();



    private SocketMessageReceiver(Connecter connecter) {
        this.mConnecter=connecter;
        this.mSocket = connecter.getSocket();
        mReceiveQueue=new LinkedBlockingQueue<>();
        mReceiverThread=new ReceiverThread();
        mReceiverThread.setName("接收线程");
        mReceiverThread.start();
        sMapReceiver.put(connecter.getSocketId(),this);
    }

    public void setSocket(Socket mSocket) {
        this.mSocket = mSocket;
    }
    
    public Connecter getReceiver() {
        return mConnecter;
    }

    private BlockingQueue<byte[]>mReceiveQueue;

    public BlockingQueue<byte[]> getReceiveDeque() {
        if(mReceiveQueue==null){
            mReceiveQueue=new LinkedBlockingQueue<>();
        }
        return mReceiveQueue;
    }

   
    public static void closeThreads() {
        for (int key : sMapReceiver.keySet()) {
            SocketMessageReceiver sender = sMapReceiver.get(key);
            sender.closeThread();
        }
        sMapReceiver.clear();
    }

   
    public void closeThread(){
        mParse.closeThread();
        mReceiveQueue.clear();
        mReceiveQueue=null;
        mReceiverThread=null;       
    }
    
    public static void createMessageReceiver(Connecter connecter) {
        SocketMessageReceiver receiver = new SocketMessageReceiver(connecter);
        sMapReceiver.put(connecter.getSocketId(), receiver);
    }
    
    
    public static void closeThread(Connecter connceter) {
        SocketMessageReceiver receiver = sMapReceiver.get(connceter.getSocketId());
        if(receiver!=null){
            receiver.closeThread();
            sMapReceiver.remove(connceter.getSocketId());
        }
       
    }



  

    private class ReceiverThread extends Thread{

        @Override
        public void run() {
            try {
                mParse = new SocketDataParse(SocketMessageReceiver.this);
                InputStream inputStream = mSocket.getInputStream();
                while(true){
                    byte[] buffer = new byte[256];
                    int len;
                    if (mSocket != null && (len = inputStream.read(buffer)) != -1) {
                        mReceiveQueue.put(Arrays.copyOf(buffer, len));
                        if(SocketHeartSender.get(mConnecter.getSocketId())!=null)
                            SocketHeartSender.get(mConnecter.getSocketId()).updateReceiveTime();
                    }
                }
            } catch (IOException | InterruptedException e) {
               
            }
        }

    }
    
    
    
    

}

