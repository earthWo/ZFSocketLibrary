package socketconnect.callback;

/**
 * Created by wuzefeng on 2017/10/13.
 */

public @interface OrderType {

    int CONNECT = 10001;

    int SEND_TEXT_MESSAGE = 10002;

    int CLOSE_CONNECTION = 10004;

    int SEND_HEART_MESSAGE = 10005;

    int SEND_VOICE_MESSAGE = 10006;

    int SEND_VIDEO_MESSAGE = 10007;

    int SEND_IMAGE_MESSAGE = 10008;

    int SEND_FILE_MESSAGE = 10009;
}
