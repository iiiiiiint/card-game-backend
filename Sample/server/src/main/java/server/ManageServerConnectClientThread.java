package server;

import common.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理线程的类
 */
public class ManageServerConnectClientThread {
    public static ConcurrentHashMap<String, ServerConnectClientThread> map = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Vector<Message>> messageMap = new ConcurrentHashMap<>();

    public static String getOnlineFriends() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, ServerConnectClientThread> entry : map.entrySet()) {
            builder.append(entry.getKey() + " ");
        }
        return builder.toString();
    }

    public static Socket getSocketById(String userId) {
        ServerConnectClientThread thread = map.get(userId);
        if (thread == null) {
            return null;
        } else {
            return thread.getSocket();
        }
    }

    /**
     * 向所有的Socket发送消息
     * @param socket 除了这个
     * @param oos
     */
    public static void sendAll(Socket socket, ObjectOutputStream oos, Message message) {
        try {
            for (Map.Entry<String, ServerConnectClientThread> entry : map.entrySet()) {
                Socket socket1 = getSocketById(entry.getKey());
                if (socket1 != socket) {
                    oos = new ObjectOutputStream(socket1.getOutputStream());
                    oos.writeObject(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务器暂存离线信息
     * @param userId 接受者ID
     * @param message 需要发送的信息
     */
    public static void addMessage(String userId, Message message) {
        Vector<Message> vector = messageMap.get(userId);
        if (vector == null) {
            vector = new Vector<>();
            messageMap.put(userId, vector);
        }
        vector.add(message);
    }

    /**
     * 尝试将服务器库存信息进行发送
     * @param userId 接受者ID
     * @param oos 输出流
     */
    public static void sendOffLineMessage(String userId, ObjectOutputStream oos) {
        Vector<Message> vector = messageMap.get(userId); //得到库存信息
        if (!(vector == null || vector.isEmpty())) {
            try {
                //说明当前用户有待发送消息
                Socket socket = getSocketById(userId);
                while (!vector.isEmpty()) {
                    Message message = vector.get(0);
                    //将消息按顺序发出去
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message);
                    vector.remove(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ConcurrentHashMap<String, ServerConnectClientThread> getMap() {
        return map;
    }

    public static void deleteSocket(String userId) {
        map.remove(userId);
    }

    public static void addThread(String useId, ServerConnectClientThread thread) {
        map.put(useId, thread);
    }
}
