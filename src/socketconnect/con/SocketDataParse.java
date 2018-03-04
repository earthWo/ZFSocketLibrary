package socketconnect.con;


import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import socketconnect.callback.CallbackSet;
import socketconnect.callback.MessageType;
import socketconnect.callback.SocketDataType;
import socketconnect.message.SocketFileMessage;
import socketconnect.message.SocketImageMessage;
import socketconnect.message.SocketMessage;
import socketconnect.message.SocketTextMessage;
import socketconnect.message.SocketVideoMessage;
import socketconnect.message.SocketVoiceMessage;
import socketconnect.model.ReceiveInterface;
import socketconnect.proto.SocketDataProtos;
import socketconnect.utils.ByteUtil;

/**
 * Created by wuzefeng on 2017/10/13.
 */

public class SocketDataParse {


    private ParserThread mParserThread;

    private byte[]currentMessage;

    private int lessDataLength;

    private String currentMessageType;

    private byte[] longMessage;

    private final ReceiveInterface mReceiver;


    public SocketDataParse(ReceiveInterface receiver) {
        mParserThread=new ParserThread();
        mParserThread.start();
        mParserThread.setName("解析线程");
        mReceiver=receiver;
    }

    public void closeThread(){
        flag=false;
        mParserThread.interrupt();
        mParserThread=null;
    }





    private class ParserThread extends Thread{

        @Override
        public void run() {
            while(flag){
                try {
                    byte[]message=mReceiver.getReceiveDeque().take();
                    if(message.length!=0) {
                        parseMessage(message);
                    }
                } catch (InterruptedException ex) {
                }
            }
        }

    }

    private boolean flag=true;


    private void parseMessage(byte[] message) {


        //小于2，完全无法解析
        if (message.length < 2 && message.length > 0) {

            if (currentMessage == null) {//不需要补足
                currentMessage = message;
            } else if (currentMessage != null && lessDataLength == 0) {//有不足的数据，但是没有缺少数据的量

                //两个消息连起来能否解析出长度
                if (currentMessage.length + message.length > 6) {//能够解析出长度，将两个加在一起，重新解析
                    message = ByteUtil.unitByteArray(currentMessage, message);
                    currentMessage = null;
                    currentMessageType = null;
                    parseMessage(message);
                } else {//加起来都无法解析
                    currentMessage = ByteUtil.unitByteArray(currentMessage, message);
                }

            } else if (lessDataLength < 2) {//数据组合在一起足够解析出一条消息
                currentMessage = ByteUtil.unitByteArray(currentMessage, message);

                if (SocketDataType.ML.equals(currentMessageType)) {
                    lessDataLength = 0;
                } else {
                    parseCompleteData(currentMessage);
                    currentMessage = null;
                    lessDataLength = 0;
                    currentMessageType = null;
                }

            } else if (lessDataLength >= 2) {//数据组合在一起还是不足
                currentMessage = ByteUtil.unitByteArray(currentMessage, message);
                lessDataLength -= message.length;
            }
        } else {//大于2，可以解析

            //截取前两个
            String head = new String(ByteUtil.cutBytes(message, 0, 2));

            int length;
            //有消息头的代表之前的消息完全解析完
            //是心跳消息
            if (SocketDataType.MH.equals(head) || SocketDataType.MG.equals(head)) {//是普通消息或心跳消息

                //是否可以解析出消息长度
                if (message.length > 6) {
                    //解析出消息长度
                    length = ByteUtil.bytesToInt(ByteUtil.cutBytes(message, 2, 6));

                    //能否解析出整个消息
                    if (message.length >= length + 6) {
                        //截取出整条消息
                        currentMessage = ByteUtil.cutBytes(message, 0, length + 6);
                        //解析完整的消息
                        parseCompleteData(currentMessage);

                        currentMessage = null;
                        currentMessageType = null;
                        lessDataLength = 0;

                        //剩余消息重新解析
                        message = ByteUtil.cutBytes(message, length + 6, message.length);
                        parseMessage(message);

                    } else {//解析不出整条消息
                        //设置当前的消息
                        currentMessage = message;
                        //设置当前的消息类型
                        currentMessageType = head;
                        //设置剩余消息数量
                        lessDataLength = length + 6 - message.length;



                    }

                } else {//消息长度都解析不出

                    currentMessage = message;
                    //设置当前的消息类型
                    currentMessageType = head;


                }

            } else if (SocketDataType.ML.equals(head)) {//是长消息

                //是否可以解析出消息长度
                if (message.length >= 6) {
                    //解析出消息长度
                    length = ByteUtil.bytesToInt(ByteUtil.cutBytes(message, 2, 6));

                    //能否解析出整个消息
                    if (message.length >= length + 6) {
                        //截取出整条消息
                        byte[] msg = ByteUtil.cutBytes(message, 0, length + 6);
                        //设置当前的消息类型
                        currentMessageType = head;
                        //是长消息的第一条
                        if (longMessage == null) {
                            longMessage = msg;
                            //剩余消息重新解析
                            message = ByteUtil.cutBytes(message, length + 6, message.length);
                            parseMessage(message);
                        } else {//不是第一条
                            longMessage = ByteUtil.unitByteArray(longMessage, ByteUtil.cutBytes(message, 6, length + 6));
                            //剩余消息重新解析
                            message = ByteUtil.cutBytes(message, length + 6, message.length);
                            parseMessage(message);
                        }

                    } else {//解析不出整条消息
                        //设置当前的消息
                        currentMessage = message;
                        //设置当前的消息类型
                        currentMessageType = head;
                        //设置剩余消息数量
                        lessDataLength = length + 6 - message.length;
                    }

                } else {//消息长度都解析不出

                    currentMessage = message;
                    //设置当前的消息类型
                    currentMessageType = head;


                }

            } else if (SocketDataType.ME.equals(head)) {//是最后的长消息


                //是否可以解析出消息长度
                if (message.length > 6) {
                    //解析出消息长度
                    length = ByteUtil.bytesToInt(ByteUtil.cutBytes(message, 2, 6));

                    //能否解析出整个消息
                    if (message.length >= length + 6) {
                        //截取出整条消息
                        byte[] msg = ByteUtil.cutBytes(message, 6, length + 6);
                        //设置当前的消息类型
                        currentMessageType = head;
                        //是长消息的第一条
                        if (longMessage == null) {
                            longMessage = msg;
                        } else {//不是第一条
                            longMessage = ByteUtil.unitByteArray(longMessage, ByteUtil.cutBytes(message, 6, length + 6));
                        }
                        //解析完整的消息
                        parseCompleteData(longMessage);
                        longMessage = null;

                        currentMessage = null;
                        currentMessageType = null;
                        lessDataLength = 0;

                        //剩余消息重新解析
                        byte[] msge = ByteUtil.cutBytes(message, length + 6, message.length);
                        parseMessage(msge);

                    } else {//解析不出整条消息
                        //设置当前的消息
                        if (currentMessage == null) {
                            currentMessage = message;
                        } else {
                            currentMessage = ByteUtil.unitByteArray(currentMessage, ByteUtil.cutBytes(message, 6, message.length));
                        }
                        //设置当前的消息类型
                        currentMessageType = head;
                        //设置剩余消息数量
                        lessDataLength = length + 6 - message.length;
                    }

                } else {//消息长度都解析不出

                    currentMessage = message;
                    //设置当前的消息类型
                    currentMessageType = head;

                }

            } else if (currentMessage != null&&lessDataLength>0) {//不是消息的头

                if (message.length >= lessDataLength) {

                    //解析出消息
                    byte[] msg = ByteUtil.cutBytes(message, 0, lessDataLength);
                    currentMessage = ByteUtil.unitByteArray(currentMessage, msg);

                    //如果是长消息，添加长消息
                    if (SocketDataType.ML.equals(currentMessageType)) {

                        if (longMessage == null) {
                            longMessage = currentMessage;
                        } else {
                            currentMessage = ByteUtil.cutBytes(currentMessage, 6, currentMessage.length);
                            longMessage = ByteUtil.unitByteArray(longMessage, currentMessage);
                        }

                    } else if (SocketDataType.ME.equals(currentMessageType)) {//长消息的最后一个消息，将长消息补足后解析
                        longMessage = ByteUtil.unitByteArray(longMessage, ByteUtil.cutBytes(currentMessage, 6, currentMessage.length));
                        parseCompleteData(longMessage);
                        longMessage = null;
                    } else {//如果是其他消息，则消息补足后直接解析
                        parseCompleteData(currentMessage);
                    }

                    currentMessage = null;
                    currentMessageType = null;
                    message = ByteUtil.cutBytes(message, lessDataLength, message.length);
                    lessDataLength = 0;
                    parseMessage(message);

                } else {//消息不足以补足现在的消息
                    currentMessage = ByteUtil.unitByteArray(currentMessage, message);
                    lessDataLength=lessDataLength-message.length;
                }

            }else if(currentMessage != null && lessDataLength ==0&&currentMessage.length<6){//数据无法解析头和长度


                if(message.length>=6-currentMessage.length){//将头补全
                    int len=6 - currentMessage.length;
                    currentMessage=ByteUtil.unitByteArray(currentMessage, ByteUtil.cutBytes(message, 0, 6-currentMessage.length));
                    lessDataLength=ByteUtil.bytesToInt(ByteUtil.cutBytes(currentMessage, 2, 6));
                    message=ByteUtil.cutBytes(message, len, message.length);
                    parseMessage(message);
                }else{//添加之后还是解析出长度
                    currentMessage = ByteUtil.unitByteArray(currentMessage,message);
                }


            }

        }

    }


    /**
     * 将已经完整的数据解析出来
     * @param bytes
     */
    private void parseCompleteData(byte[]bytes){

        byte[] msg=ByteUtil.cutBytes(bytes,6,bytes.length);

        String type = new String(ByteUtil.cutBytes(bytes, 0, 2));
        if(!SocketDataType.MH.equals(type)){
            SocketDataProtos.SocketData data= null;
            try {
                data = SocketDataProtos.SocketData.parseFrom(msg);
                SocketMessage message =null;
                if(MessageType.MT.equals(data.getMessageType())){
                    message=new SocketTextMessage(data);
                }else if (MessageType.MV.equals(data.getMessageType())) {
                    //音频数据
                    message=new SocketVoiceMessage(data);
                } else if (MessageType.MM.equals(data.getMessageType())) {
                    //音频数据
                    message=new SocketVideoMessage(data);
                } else if (MessageType.MP.equals(data.getMessageType())) {
                    //图片数据
                    message=new SocketImageMessage(data);
                }else if (MessageType.MF.equals(data.getMessageType())) {
                    //文件数据
                        message = new SocketFileMessage(data);
                }

                if (message != null) {
                    SocketHelper.getInstance().receiverMessage(mReceiver.getReceiver(), message);
                }

            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }else {
            SocketHelper.getInstance().sendHeardMessage(mReceiver.getReceiver().getSocketId(), CallbackSet.decodeMessageId(System.currentTimeMillis() + ""));
        }




    }


}
