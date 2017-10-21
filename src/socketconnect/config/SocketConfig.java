package socketconnect.config;

/**
 * Created by wuzefeng on 2017/10/13.
 */

public class SocketConfig {

    private static volatile SocketConfig sInstance;

    public static void init(String ip,int port){
        if(sInstance==null){
            synchronized (SocketConfig.class){
                if(sInstance==null){
                    sInstance=new SocketConfig(ip,port);
                }
            }
        }
    }

    public static SocketConfig get(){
        return sInstance;
    }

    private SocketConfig(String ip,int port){
        this.ip=ip;
        this.port=port;
    }

    private String ip;


    private int port;


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
