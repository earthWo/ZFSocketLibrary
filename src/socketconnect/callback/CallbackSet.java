package socketconnect.callback;

import java.util.HashMap;
import java.util.Map;
import socketconnect.con.SocketConnect;

/**
 * Created by wuzefeng on 2017/10/13.
 */

public class CallbackSet {

    private static volatile CallbackSet sInstance;


    private final Map<Integer,MessageCallback>mMessageCallbackMap;


    private CallbackSet(){
        mMessageCallbackMap=new HashMap<>();
    }

    public static CallbackSet get(){
        CallbackSet instance = CallbackSet.sInstance;
        if(instance==null){
            synchronized (SocketConnect.class){
                instance = CallbackSet.sInstance;
                if(instance==null){
                    CallbackSet.sInstance=sInstance = new CallbackSet();
                }
            }
        }
        return sInstance;
    }


    public void addCallback(int key,MessageCallback callback){
        mMessageCallbackMap.put(key,callback);
    }

    public MessageCallback getCallback(int key){
        MessageCallback  callback =mMessageCallbackMap.get(key);
        if(callback!=null){
            mMessageCallbackMap.remove(key);
        }
        return callback;
    }




    public static int decodeMessageId(String message){
        if(message!=null){
            return message.hashCode();
        }else{
            return 0;
        }
    }
}
