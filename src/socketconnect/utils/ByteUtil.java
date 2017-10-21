package socketconnect.utils;

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


}
