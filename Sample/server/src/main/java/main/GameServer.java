package main;

import common.Message;
import common.MessageType;
import common.User;
import server.ManageServerConnectClientThread;
import server.PlayerMsg;
import server.ServerConnectClientThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    ServerSocket serverSocket;
    //模拟用户数据库
    private static ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<>();

    static {
        userMap.put("123", new User("123", "123"));
        userMap.put("tom", new User("tom", "123"));
        userMap.put("捉妖龙", new User("捉妖龙", "123"));
    }

    public static void main(String[] args) {
        new GameServer();
    }


    public GameServer() {
        try {
            System.out.println("在9999端口监听……");
            serverSocket = new ServerSocket(9999);
            while (true) {
                Socket client = serverSocket.accept();
                
                ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());

                
                Message MessageIn = (Message) ois.readObject();

                //构建一个Message对象，准备回复
                Message MessageOut = new Message();

                switch (MessageIn.getMessageType()) {
                    case MessageType.MESSAGE_SIGNUP_REQUEST:

                        // 注册
                        String UserID = MessageIn.getUserName();
                        String PassWd = MessageIn.getPassWord();

                        if (isUser(UserID)) {
                            MessageOut.setMessageType(MessageType.MESSAGE_SIGNUP_FAIL);
                        } else {
                            userMap.put(UserID, new User(UserID, PassWd));
                            MessageOut.setMessageType((MessageType.MESSAGE_SIGNUP_SUCCESS));
                        }

                        oos.writeObject(MessageOut);
                        client.close();

                        break;

                    case MessageType.MESSAGE_LOGIN_REQUEST:

                        //登陆
                        if (isUser(MessageIn.getUserName(), MessageIn.getPassWord())) {

                            MessageOut.setMessageType((MessageType.MESSAGE_LOGIN_SUCCESS));
                            oos.writeObject((MessageOut));
                            new PlayerMsg(client).start();

                        } else {
                            MessageOut.setMessageType(MessageType.MESSAGE_LOGIN_FAIL);
                            oos.writeObject(MessageOut);
                            client.close();
                        }

                        break;
                    
                
                    default:
                        client.close();
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ConcurrentHashMap<String, User> getUserMap() {
        return userMap;
    }

    public boolean isUser(String userId, String pw) {
        User user = userMap.get(userId);
        //没有这个用户
        if (user == null) {
            return false;
        }
        //密码不正确
        if (!user.getPasswd().equals(pw)) {
            return false;
        }
        return true;
    }

    public static boolean isUser(String userId) {
        User user = userMap.get(userId);
        if (user == null) {
            return false;
        }
        return true;
    }
}
