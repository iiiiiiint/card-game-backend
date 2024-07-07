package client.service;


import common.Message;
import common.MessageType;
import common.User;
import common.Utility;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;

/**
 * 用于客户端发送数据给服务端
 */
public class UserClientService {
    private User user = new User(); //当前客户
    private Socket socket; //当前客户对应的Socket
    private boolean flag = false; //登录是否成功的标志
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public boolean checkUser(String userId, String pwd) {
        try {
            //封装一个User对象，发送到服务器进行检查
            Message Message_Out = new Message();
            Message_Out.setSender(userId);
            Message_Out.setContent(pwd);
            Message_Out.setMessageType(MessageType.MESSAGE_LOGIN_REQUEST);
            //连接到服务器
            socket = new Socket(InetAddress.getByName("121.199.3.200"), 9999);
            oos = new ObjectOutputStream(socket.getOutputStream());
            //将用户对象发送出去
            oos.writeObject(Message_Out);
            ois = new ObjectInputStream(socket.getInputStream());
            //对面会将消息封装为一个Message对象
            Message Message_In = (Message)ois.readObject();
            //此时登录成功
            if (Message_In.getMessageType().equals(MessageType.MESSAGE_LOGIN_SUCCESS)) {
                ClientConnectServerThread thread = new ClientConnectServerThread(socket, userId);
                thread.start();
                flag = true;
            } else {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    public boolean signupUser(String userId, String pwd) {
        try { 
            Message MessageOut = new Message();
            MessageOut.setContent(pwd);
            MessageOut.setSender(userId);
            MessageOut.setMessageType(MessageType.MESSAGE_SIGNUP_REQUEST);

            //连接到服务器
            socket = new Socket(InetAddress.getByName("121.199.3.200"), 9999);
            oos = new ObjectOutputStream(socket.getOutputStream());
            //将用户对象发送出去
            oos.writeObject(MessageOut);
            ois = new ObjectInputStream(socket.getInputStream());

            Message MessageIn = (Message)ois.readObject();

            if(MessageIn.getMessageType().equals(MessageType.MESSAGE_SIGNUP_SUCCESS)) {
                return true;
            }else{
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 拉取在线客户
     */
    public void onlineFriendList() {
        //这是一条拉取列表的信息
        Message message = new Message(user.getUserId(), MessageType.MESSAGE_GET_ONLINE_FRIEND);
        try {
            //每次使用流就要重新绑定一次
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送一条结束通道的信息给服务器
     */
    public void closedComm() {
        try {
            Message message = new Message(user.getUserId(), MessageType.MESSAGE_CLIENT_EXIT);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            socket.close(); //将当前类的socket通道关闭
            System.exit(0);//结束进程及由此进程引发的所有线程
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 与某人私聊，并发送信息
     */
    public void Send(String name, String contents) {
        try {
            Message message1 = new Message();
            message1.setMessageType(MessageType.MESSAGE_COMM_MES);//这是一条普通消息
            message1.setGetter(name);//接收人
            message1.setContent(contents);
            message1.setSender(user.getUserId());
            message1.setSendTime(formatTime());
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendAll(String contents) {
        Send("All", contents);
    }

    public String formatTime() {
        Calendar instance = Calendar.getInstance();
        int hour = instance.get(Calendar.HOUR_OF_DAY);
        int min = instance.get(Calendar.MINUTE);
        StringBuilder builder = new StringBuilder("\n");
        if (hour < 10)
            builder.append("0");
        builder.append(hour+":");
        if (min < 10)
            builder.append("0");
        builder.append(min);
        return builder.toString();
    }
}
