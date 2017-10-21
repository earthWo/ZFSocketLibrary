package socketconnect.callback;

/**
 * Created by wuzefeng on 2017/10/13.
 */

public @interface MessageType {
    /**
     * 文本的消息
     */
    String MT = "MT";

    /**
     * 语音的消息
     */
    String MV = "MV";

    /**
     * 视频的消息
     */
    String MM = "MM";

    /**
     * 图片的消息
     */
    String MP = "MP";

    /**
     * 文件的消息
     */
    String MF = "MF";

    /**
     * 心跳消息
     */
    String MH = "MH";

    /**
     * 关闭发送线程消息
     */
    String MC = "MC";
}
