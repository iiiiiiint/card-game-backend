package main.java;

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
import java.util.Random;

import javax.management.relation.RoleNotFoundException;
import javax.naming.ldap.UnsolicitedNotificationListener;

import common.Message;
import common.MessageType;
import common.User;
import common.Gameround;
import common.Player;
import common.Room;

class PlayerMsg extends Thread {//一个玩家线程,用于选择房间到开始游戏之前的阶段
    private static ConcurrentHashMap<String, Room> roomMap = new ConcurrentHashMap<>();
    Player player;//一个玩家 
    Room[] rooms;//所有房间
    Room room;//选择的房间

    public PlayerMsg(Socket client, String UserID) {
        this.rooms = Server.rooms;
        this.player = new Player(client, UserID);
    }
    public PlayerMsg(Player player,Room room){
        this.player = player;
        this.rooms = Server.rooms;
        this.room = room;
    }

    /**

     * @title: run

     * @description: 玩家与服务器交流的多线程
     *

     */
    @Override
    public void run() {
        try {

            do {
                while(!player.isInGame()){
                    Message inMessage = player.receiveMsg();
                    if(inMessage.getMessageType() == MessageType.MESSAGE_CREATE_ROOM){
                        this.room = CreateRoom(player);
                        Message outMessage = room.getRoomMessage();
                        player.getOut().writeObject(outMessage);
                    }else if(inMessage.getMessageType() == MessageType.MESSAGE_SELECT_ROOM){
                        String RoomID = inMessage.getSelectRoomID();
                        this.room = SelectRoom(RoomID);
                    }
                }

            } while (!gameReady(room));//六个人都准备后break


            this.room.decrease();
            synchronized (room) {
                if (room.n == 0) {
                    room.n = 6;
                    new GameThread(room.getPlayers()).start();//开始游戏
                }
            }
            System.out.println("start");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Room CreateRoom(Player player){
        Room ret = new Room();
        Random rand = new Random();
        int temp = rand.nextInt(8999) + 1000;
        //这里是不是没有判断重复？
        ret.SetRoomNumber(String.valueOf(temp));
        roomMap.put(String.valueOf(temp), ret);
        ret.addPlayer(player);
        return ret;
    }

    /**

     * @title: selectRoom

     * @description: 玩家选择房间
     *

     */
    //选择房间
    public Room SelectRoom(String RoomID) throws IOException {
        String message;
        while (true){
            //？？还不上号？？？？？？？？
            //能不能快点写
            //懒狗！！！！！
        }
    }
    /**

     * @title: gameReady
     *
     * @description: 玩家进入房间后准备阶段的交流
     *
     * @param room Room

     * @return boolean 返回true表示三位玩家都准备了,可以开始游戏;false表示玩家退出这个房间

     */
    public boolean gameReady(Room room){
        String playerMessage;//放玩家消息
        room.setStart(false);//重置房间开始信号
        player.setInGame(false);//游戏退出后,设为不在游戏内
        while(true){//每隔0.01秒服务器向玩家询问准备状态,若3人都准备了,则房间信号置true
            if (room.isStart()) {//房间开始信号
                player.sendMsg("start");//提示玩家开始游戏
                player.receiveMsg();
                room.getReadyPlayer().clear();//开始游戏后重置准备玩家
                return true;//返回true
            }
            else
                player.sendMsg("ready?"+room.roomInfo(player));//房间准备信息
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            playerMessage = player.receiveMsg();
            if (playerMessage.equals("unready"))
                room.removeReady(player);//设置房间的准备玩家的list
            if (playerMessage.equals("ready")) {
                room.addReady(player);
            }
            if (playerMessage.equals("quit")) {
                player.sendMsg("quit");
                player.receiveMsg();
                room.removePlayer(player);//房间移除玩家
                player.setRoom(null);//玩家移除房间
                System.out.println("quit");
                return false;//重新进入大厅阶段,选择房间
            }

        }
    }
    /**

     * @title: getRoomNum

     * @description: 获取所有房间人数信息
     *

     * @return String 房间人数信息

     */
    public String getRoomNum(){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            str.append(rooms[i].getNum()).append(",");
        }
        return str.toString();
    }
}

/**

 * @title: GameThread
 *
 * @description: 一局游戏的多线程,用于不同玩家的游戏
 *

 */
class GameThread extends Thread {//一局游戏的多线程类
    static int homeNum = 0;
    ArrayList<Player> players;

    public GameThread(ArrayList<Player> players) {
        this.players = players;
        homeNum += 1;
        System.out.println("第" + homeNum + "局游戏开始");
    }

    @Override
    public void run() {
        Gameround game = new Gameround(players);
        game.gameStart();//开始一轮游戏
        for (Player player: players){//游戏结束后,重新开三个玩家交流线程,依靠isInGame的flag跳过选择房间阶段,
            new PlayerMsg(player,player.getRoom()).start();//直接在房间的准备阶段
        }
    }
}
