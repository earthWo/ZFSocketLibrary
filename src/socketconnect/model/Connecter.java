/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketconnect.model;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author wuzefeng
 */
public class Connecter {
    
    
    private int socketId;
    
    private Socket messageSocket;

    private Socket heartSocket;

    private Socket fileSocket;
    
    
    public Connecter() {
        
    }
    
    public Socket getMessageSocket() {
        return messageSocket;
    }

    public int getSocketId() {
        return socketId;
    }

    public void setMessageSocket(Socket socket) {
        this.messageSocket = socket;
    }

    public void setSocketId(int socketId) {
        this.socketId = socketId;
    }

    public Socket getHeartSocket() {
        return heartSocket;
    }

    public void setHeartSocket(Socket heartSocket) {
        this.heartSocket = heartSocket;
    }

    public Socket getFileSocket() {
        return fileSocket;
    }

    public void setFileSocket(Socket fileSocket) {
        this.fileSocket = fileSocket;
    }

    public void closeConnect(){
        if(messageSocket!=null){
            try {
                messageSocket.close();
            } catch (IOException ex) {
            }
        }
        if(heartSocket!=null){
            try {
                heartSocket.close();
            } catch (IOException ex) {
            }
        }
        if(fileSocket!=null){
            try {
                fileSocket.close();
            } catch (IOException ex) {
            }
        }
    }
    
    public String getConnectorName(){
        return "连接者"+hashCode();
    }
    
    
}
