package socketconnect.utils;

import com.google.protobuf.ByteString;
import socketconnect.callback.SocketDataType;
import socketconnect.message.SocketMessage;
import socketconnect.proto.SocketDataProtos;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by wuzefeng on 2017/10/13.
 */

public class ByteUtil {

    /**
     * 将byte数组拼接
     * @param byte1
     * @param byte2
     * @return
     */
    public static byte[] unitByteArray(byte[] byte1,byte[] byte2){
        byte[] unitByte = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, unitByte, 0, byte1.length);
        System.arraycopy(byte2, 0, unitByte, byte1.length, byte2.length);
        return unitByte;
    }


    /**
     * 将byte数组分成多个数组
     * @param bytes
     * @param copies
     * @return
     */
    public static byte[][] splitBytes(byte[] bytes, int copies) {
        double split_length = Double.parseDouble(copies + "");
        int array_length = (int) Math.ceil(bytes.length / split_length);
        byte[][] result = new byte[array_length][];
        int from, to;
        for (int i = 0; i < array_length; i++) {
            from = (int) (i * split_length);
            to = (int) (from + split_length);
            if (to > bytes.length)
                to = bytes.length;
            result[i] = Arrays.copyOfRange(bytes, from, to);
        }
        return result;
    }

    public static byte[] cutBytes(byte[] bytes, int from,int to){
       return Arrays.copyOfRange(bytes,from,to);
    }

    public static int bytesToInt(byte[] b){
       return  b[3] & 0xFF |
               (b[2] & 0xFF) << 8 |
               (b[1] & 0xFF) << 16 |
               (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }
    
    public static byte[] fileToByte(String filePath) throws IOException{
        FileInputStream fis = null;
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }   
           
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
           
        } catch (FileNotFoundException ex) {
            
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                System.out.println(ex.getLocalizedMessage());
            }
        }
        return buffer;
    }


    public static byte[][] getBytesByMessage(SocketMessage message){
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


}
