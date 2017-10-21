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
    
    private Socket socket;
    
    
    public Connecter() {
        
    }
    
    public Connecter(Socket socket){
        this.socket=socket;
        this.socketId=socket.hashCode();
    }

    public Socket getSocket() {
        return socket;
    }

    public int getSocketId() {
        return socketId;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setSocketId(int socketId) {
        this.socketId = socketId;
    } 
    
    
    public void closeSocket(){
        if(socket!=null){
            try {
                socket.close();
            } catch (IOException ex) {
            }
        }
    }
    
    public String getConnectorName(){
        return "连接者"+socketId;
    }
    
    
}
