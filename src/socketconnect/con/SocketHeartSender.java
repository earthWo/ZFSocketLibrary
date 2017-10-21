package socketconnect.con;

import java.util.HashMap;
import java.util.Map;
import socketconnect.model.Connecter;


/**
 * 心跳
 * Created by wuzefeng on 2017/10/17.
 */

public class SocketHeartSender {


    private HeartThread mHeardThread;

    private long lastTime;

    private static final int SLEEP_TIME=10000;
    
    private Connecter mConnceter;
    
    public static Map<Integer, SocketHeartSender> sMapSender = new HashMap<>();


    public static void createHeardMessageSender(Connecter connecter) {
        SocketHeartSender sender = new SocketHeartSender(connecter);
        sMapSender.put(connecter.getSocketId(), sender);
        sender.updateReceiveTime();
    }

    public static SocketHeartSender get(int socketId) {
        return sMapSender.get(socketId);
    }
    


    private SocketHeartSender(Connecter connecter){
        this.mConnceter=connecter;
        mHeardThread=new HeartThread();
        mHeardThread.start();
    }

    public void updateReceiveTime(){
        lastTime=System.currentTimeMillis();
    }


    private class HeartThread extends Thread{

        @Override
        public void run() {

            try {
                while (true){
                    Thread.sleep(SLEEP_TIME);
                    if (System.currentTimeMillis() > lastTime + 1000 * 60) {//离上次收到时间大于1分钟，认为断开
                        SocketHelper.getInstance().disconncted(mConnceter);
                    }else if(System.currentTimeMillis()>lastTime+8000){//离上次收到时间大于8秒，发送心跳
                        SocketConnect.get().sendHeardMessage(mConnceter.getSocketId());
                    }
                }
            } catch (InterruptedException e) {
            }
        }
    }


    public static void closeThreads(){
        for(int key:sMapSender.keySet()){
            SocketHeartSender sender=sMapSender.get(key);
            sender.closeThread();
        }
      
    }
    
    
    private void closeThread(){
        if (mHeardThread != null) {
            mHeardThread.interrupt();
        }
        mHeardThread = null;       
    }
    
    public static void closeThread(Connecter connceter) {
        SocketHeartSender sender = sMapSender.get(connceter.getSocketId());
        if(sender!=null){
            sender.closeThread();
            sMapSender.remove(connceter.getSocketId()); 
        }     
    }


}
