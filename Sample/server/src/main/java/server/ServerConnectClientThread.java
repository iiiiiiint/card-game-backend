package server;


import common.Message;
import common.MessageType;
import main.GameServer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 该类的一个对象和某个客户端保持通讯
 */
public class ServerConnectClientThread extends Thread {
    private Socket socket; //这个线程对应的Socket
    private String userId; //对应客户的ID
    private boolean flag = true; //是否结束线程的标志
    private ObjectInputStream ois; //输入流
    private ObjectOutputStream oos; //输出流

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    @Override
    public void run() {
        System.out.println("服务器与客户【" + userId + "】保持通信……");
        while (flag) {
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                actionByMessageType(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void actionByMessageType(Message message) {
        try {
            switch (message.getMessageType()) {
                case MessageType.MESSAGE_GET_ONLINE_FRIEND:
                    //对方请求列表
                    System.out.println("【" + message.getSender() + "】要看在线用户列表……");
                    Message message1 = new Message();
                    message1.setMessageType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message1.setGetter(message.getSender());//发送者变接受者
                    //消息内容为找到的内容
                    message1.setContent(ManageServerConnectClientThread.getOnlineFriends());
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message1);
                    break;
                case MessageType.MESSAGE_CLIENT_EXIT:
                    ManageServerConnectClientThread.deleteSocket(userId);
                    flag = false; //此线程结束的标志
                    ManageServerConnectClientThread.deleteSocket(userId); //从线程集合中除名
                    socket.close(); //将这个Socket移除
                    System.out.println("用户【" + userId + "】断开连接！");
                    break;
                case MessageType.MESSAGE_COMM_MES:
                    if (message.getGetter().equals("All")) {
                        //说明这是群发消息
                        ManageServerConnectClientThread.sendAll(socket, oos, message);
                        break;
                    }
                    //得到目标的Socket
                    Socket socket = ManageServerConnectClientThread.getSocketById(message.getGetter());
                    Message message2 = new Message();
                    if (GameServer.isUser(message.getGetter())) {//看此用户是否在数据库中
                        //注册的用户里有这号人
                        if (socket == null) {
                            //发回原处，告知当前用户离线，已经留言
                            socket = this.socket;
                            message2.setMessageType(MessageType.MESSAGE_CLIENT_OFFLINE);
                            message2.setGetter(message.getGetter());
                            //把消息放进消息盒
                            ManageServerConnectClientThread.addMessage(message.getGetter(), message);
                        } else {
                            message2 = message;
                        }
                    } else {
                        //数据库的用户里没有这号人
                        socket = this.socket;
                        message2.setMessageType(MessageType.MESSAGE_CLIENT_NO_EXIST);
                        message2.setGetter(message.getGetter());
                    }
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);
                    break;
            }
        } catch (Exception e) {
            System.out.println("出现异常！");
        }

    }

    public Socket getSocket() {
        return socket;
    }
}
