package socketconnect.callback;

/**
 * Created by wuzefeng on 2017/10/13.
 */

public @interface SocketDataType {

    /**
     * 普通的消息
     */
    String MG="MG";

    /**
     * 长消息
     */
    String ML="ML";

    /**
     * 心跳消息
     */
    String MH="MH";

    /**
     * 长消息最后一条消息
     */
    String ME="ME";
}
