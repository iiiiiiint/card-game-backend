package thread;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;

import common.Message;
import common.MessageType;
import common.Player;
import common.Room;
import common.User;

public class PlayerThread extends Thread {

    private static ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<String, User>() {{
        put("a", new User("a", "1"));
        put("b", new User("b", "1"));
        put("c", new User("c", "1"));
        put("d", new User("d", "1"));
        put("e", new User("e", "1"));
        put("f", new User("f", "1"));
    }};
    private static ConcurrentHashMap<String, Room> roomMap = new ConcurrentHashMap<>();

    Player player;
    Room room;
    User user;
    Socket client;

    public PlayerThread(Socket client) {
        this.client = client;
        this.user = null;
        this.room = null;
    }
    public PlayerThread(Player player,Room room){
        this.player = player;
        this.room = room;
        this.user = null;
    }

    /**

     * @title: run

     * @description: 玩家与服务器交流的多线程
     *

     */
    @Override
    public void run() {
        try{
            this.player = new Player(client);
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
        try {
            while(this.user == null) {
                Message inputMessage = new Message();
                try {
                    inputMessage = this.player.receiveMsg();
                } catch (Exception e) {
                    System.out.println(e);
                    e.printStackTrace();
                }
                Message outMessage = new Message();

                switch(inputMessage.getMessageType()){
                
                    case MessageType.MESSAGE_SIGNUP_REQUEST:
                        
                        String UserID = inputMessage.getUserName();
                        String PassWd = inputMessage.getPassWord();

                        System.out.println("Sign up request! UserID: " + UserID + ", PassWord: " + PassWd);

                        if(isUser(UserID)){
                            outMessage.setMessageType(MessageType.MESSAGE_SIGNUP_FAIL);
                            outMessage.setReason("This user already exist!");
                            System.out.println("Reject! This user already exist!");
                        }else{
                            userMap.put(UserID, new User(UserID, PassWd));
                            outMessage.setMessageType(MessageType.MESSAGE_SIGNUP_SUCCESS);
                        }
                        this.player.sendMsg(outMessage);
                        break;
                    
                    case MessageType.MESSAGE_LOGIN_REQUEST:
                        
                        UserID = inputMessage.getUserName();
                        PassWd = inputMessage.getPassWord();
    
                        System.out.println("Sign in request! UserID: " + UserID + ", PassWord: " + PassWd);
    
                        if(!isUser(UserID)){
                            outMessage.setMessageType(MessageType.MESSAGE_LOGIN_FAIL);
                            outMessage.setReason("The user does not exist!");
                            System.out.println("Reject! The user does not exist!");
                            this.player.sendMsg(outMessage);
                        }else{
                            User temp = userMap.get(UserID);
                            if(!temp.getPasswd().equals(PassWd)){
                                outMessage.setMessageType(MessageType.MESSAGE_LOGIN_FAIL);
                                outMessage.setReason("Password wrong!");
                                System.out.println("Reject! Password wrong!");
                                this.player.sendMsg(outMessage);
                            }else{
                                outMessage.setMessageType(MessageType.MESSAGE_LOGIN_SUCCESS);
                                this.player.sendMsg(outMessage);
                                this.user = temp;
                                this.player.setname(temp.getUserId());
                                System.out.println("User: " + UserID + " Sign in successfully.");
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

            do {
                while(this.room == null){
                    Message inMessage = player.receiveMsg();
                    System.out.println("received create or select!");
                    // Message inMessage = new Message();
                    if(inMessage.getMessageType().equals(MessageType.MESSAGE_CREATE_ROOM)){
                        System.out.println("User: "+ player.getname() +" is creating room");
                        this.room = CreateRoom(player);
                        Message outMessage = this.room.getRoomMessage();
                        player.sendMsg(outMessage);
                        System.out.println("User: "+player.getname()+" created a new room!");
                    }else if(inMessage.getMessageType().equals(MessageType.MESSAGE_SELECT_ROOM)){
                        System.out.println("User: " + player.getname() + " is selecting room...");
                        String RoomID = inMessage.getSelectRoomID();
                        Message outMessage = new Message();
                        if(!existRoom(RoomID)){
                            outMessage.setMessageType(MessageType.MESSAGE_SELECT_ROOM_FAIL);
                            outMessage.setReason("Invalid RoomID!");
                            System.out.println("Invalid RoomID!");
                        }else{
                            this.room = SelectRoom(RoomID);
                            if(this.room == null){
                                outMessage.setMessageType(MessageType.MESSAGE_SELECT_ROOM_FAIL);
                                outMessage.setReason("The room is full!");
                                System.out.println("The room is full!");
                            }else{
                                outMessage = this.room.getRoomMessage();
                                System.out.println("Join by selecting room successfully!");
                            }
                        }
                        player.sendMsg(outMessage);
                    }
                }

            } while (!gameReady());//六个人都准备后break
            this.room.decrease();
            // this.player.sendMsg(null);
            synchronized (this.room) {
                if (this.room.n == 0) {
                    this.room.n = 6;
                    new GameThread(this.room.getPlayers()).start();//开始游戏
                    System.out.println(this.player.getname() + "创建了一个游戏进程");
                }
            }
            System.out.println(this.user.getUserId() + "'s player thread finished");
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }


    public Room CreateRoom(Player player){
        Room ret = new Room();
        Date date = new Date();
        Long seed = Long.parseLong(String.format("%tN", date));
        Random rand = new Random(seed);
        int temp = rand.nextInt(8999) + 1000;
        String s = String.valueOf(temp);
        while(existRoom(s)){
            temp = rand.nextInt(8999) + 1000;
            s = String.valueOf(temp);
        }
        ret.SetRoomNumber(s);
        roomMap.put(s, ret);
        System.out.println("Room "+ s +" is created.");
        ret.addPlayer(player);
        player.setRoom(ret);
        return ret;
    }

    //选择房间
    public Room SelectRoom(String RoomID) throws IOException {
        Room room_;
        while (true){
            room_ = roomMap.get(RoomID);
            int PlayerNum = room_.getNum();
            if(PlayerNum == 6){
                return null;
            }else{
                room_.addPlayer(player);
                player.setRoom(room_);
                return room_;
            }
        }
    }
    public boolean existRoom(String RoomID){
        Room room_ = roomMap.get(RoomID);
        if(room_ == null) return false;
        return true;
    }

    public boolean gameReady(){
        if(this.room == null) return false;
        Message playerMessage;//放玩家消息
        this.room.setStart(false);//重置房间开始信号
        player.setInGame(false);//游戏退出后,设为不在游戏内
        while(true){//每隔0.01秒服务器向玩家询问准备状态,若3人都准备了,则房间信号置true
            if (this.room.isStart()) {//房间开始信号
                Message outMessage = new Message();
                outMessage.setMessageType(MessageType.MESSAGE_GAME_START_SOON);
                try{
                    player.sendMsg(outMessage);
                    System.out.println("Game Start Soon");
                }catch(Exception e){
                    System.out.println(e);
                    System.out.println("即将开始游戏信号发送错误...");
                }

                // this.room.getReadyPlayer().clear();//开始游戏后重置准备玩家
                return true;//返回true
            }
            else{
                Message outMessage = this.room.getRoomMessage();
                
                try{
                    player.sendMsg(outMessage);
                }catch(Exception e){
                    System.out.println(e);
                    System.out.println("实时共享房间信息发送错误...");
                    interrupt();
                }
            }
            
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            playerMessage = player.receiveMsg();
            if (playerMessage.becomeNoReady()){
                this.room.removeReady(player);//设置房间的准备玩家的list
            }
            if (playerMessage.becomeReady()) {
                this.room.addReady(player);
            }
            if (playerMessage.getMessageType().equals(MessageType.MESSAGE_PLAYER_QUIT_ROOM)) {
                this.room.removePlayer(player);//房间移除玩家
                player.setRoom(null);//玩家移除房间
                System.out.println("quit");
                return false;//重新进入大厅阶段,选择房间
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


