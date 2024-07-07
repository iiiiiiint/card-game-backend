package common;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Player {
    private ArrayList<String> playerDeck;//玩家的牌
    private ObjectOutputStream out;//玩家的输出流
    private ObjectInputStream in;//玩家的输入流
    private boolean isInGame;//玩家是否在游戏标识
    private Room room;//玩家所在的房间
    private String name;

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setInGame(boolean inGame) {
        isInGame = inGame;
    }

    public boolean isInGame() {
        return isInGame;
    }

    public Player(Socket socket, String username){
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.name = username;
        this.isInGame = false;
    }

    /**

     * @title: sendMsg

     * @description: 给玩家发送消息
     *
     * @param  s String

     * @return 无

     */
    public void sendMsg(Message s){
        try {
            this.out.writeObject(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**

     * @title: receiveMsg

     * @description: 接受玩家发送的消息
     *

     * @return String 玩家发送的消息

     */
    public Message receiveMsg(){
        try {
            return (Message)this.in.readObject();
        } catch (Exception e) {
            System.out.println("玩家离开");
        }
        return null;
    }
    public ObjectInputStream getIn(){
        return in;
    }

    public ObjectOutputStream getOut(){
        return out;
    }

    /**

     * @title: outputPlayerDeck

     * @description: 格式化输出玩家卡牌信息
     *

     * @return String 玩家的卡牌信息

     */
    public String outputPlayerDeck(){
        StringBuilder str = new StringBuilder();
        for (String s : playerDeck){
            str.append(s);
        }
        return str.toString();
    }
    public ArrayList<String> getPlayerDeck() {
        return playerDeck;
    }

    public int getQiangdizhu() {
        return qiangdizhu;
    }

    public void setQiangdizhu(int qiangdizhu) {
        this.qiangdizhu = qiangdizhu;
    }

    public void setPlayerDeck(ArrayList<String> playerDeck) {
        this.playerDeck = playerDeck;
    }

    public String getname() {
        return this.name;
    }

}
