// package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.ldap.UnsolicitedNotificationListener;

import common.Message;
import common.MessageType;
import common.User;
import common.Gameround;
import common.Player;
import common.Room;

/*

 * @title: Server

 * @description: 服务器主线程 
 *
 */
public class Server{
    private static ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<>();
    public static Room[] rooms;
    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket =  new ServerSocket(9999);
        Socket client;
        System.out.println("Server start...\n");
        
        while(true){
            System.out.println("Waiting for connect...\n");
            client = serverSocket.accept();
            ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream());
            Message inputMessage;
            try{
                inputMessage = (Message) inputStream.readObject();
            }catch(Exception e){
                continue;
            }
            Message outMessage = (Message) new Message();
            
            switch(inputMessage.getMessageType()){
                
                case MessageType.MESSAGE_SIGNUP_REQUEST:
                    String UserID = inputMessage.getUserName();
                    String PassWd = inputMessage.getPassWord();
                    if(isUser(UserID)){
                        outMessage.setMessageType(MessageType.MESSAGE_SIGNUP_FAIL);
                        outMessage.setReason("This user already exist!");
                    }else{
                        userMap.put(UserID, new User(UserID, PassWd));
                        outMessage.setMessageType(MessageType.MESSAGE_SIGNUP_SUCCESS);
                    }
                    outputStream.writeObject(outMessage);
                    client.close();
                    break;
                
                case MessageType.MESSAGE_LOGIN_REQUEST:
                    UserID = inputMessage.getUserName();
                    PassWd = inputMessage.getPassWord();
                    if(!isUser(UserID)){
                        outMessage.setMessageType(MessageType.MESSAGE_LOGIN_FAIL);
                        outMessage.setReason("The user is not exist!");
                        outputStream.writeObject(outMessage);
                        client.close();
                    }else{
                        User temp = userMap.get(UserID);
                        if(temp.getPasswd() != PassWd){
                            outMessage.setMessageType(MessageType.MESSAGE_LOGIN_FAIL);
                            outMessage.setReason("Password wrong!");
                            outputStream.writeObject(outMessage);
                            client.close();
                        }else{
                            outMessage.setMessageType(MessageType.MESSAGE_LOGIN_SUCCESS);
                            outputStream.writeObject(outMessage);
                            new PlayerMsg(client, UserID).start();
                        }
                    }
            }
        }
    }


    public static boolean isUser(String userId){
        User user = userMap.get(userId);
        if(user == null){
            return false;
        }
        return true;
    }
}

