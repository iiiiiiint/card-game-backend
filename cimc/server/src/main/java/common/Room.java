package common;


import java.util.ArrayList;

public class Room {
    private ArrayList<Player> players = new ArrayList<>();//在房间里的玩家
    private int num;//人数
    private boolean start;//开始游戏信号
    private ArrayList<Player> readyPlayer = new ArrayList<>();//已经准备了的玩家
    public String RoomNumber;
    public int n = 6;
    

    public void decrease(){
        synchronized (this){
            n--;
        }
    }

    public Room() {
        this.num = 0;
        this.start = false;
    }

    /**

     * @title: AssPlayer

     * @description: 添加加入房间的玩家
     *


     */
    public void addPlayer(Player player) {//加入玩家
        players.add(player);
        num++;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public ArrayList<Player> getReadyPlayer() {
        return readyPlayer;
    }

    /**

     * @title: addReady

     * @description: 加入已经准备的玩家
     *
     * @param player Player

     */
    public void addReady(Player player) {//加入准备玩家
        if (!readyPlayer.contains(player)) {
            readyPlayer.add(player);
            System.out.println(player.getname() + " ready");
        }
        if (readyPlayer.size() == 6) {
            System.out.println("6 人准备好了");
            this.start = true;//三人都准备则开始信号置true
        }
    }
    /**

     * @title: removeReady

     * @description: 移除取消准备的玩家
     *
     * @param player Player

     */
    public void removeReady(Player player) {//取消准备玩家
        if (readyPlayer.remove(player))
            System.out.println("unready");
    }

    public int getNum() {
        return num;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**

     * @title: removePlayer

     * @description: 移除退出房间的玩家
     *
     * @param player Player

     */
    public void removePlayer(Player player) {//玩家退出房间
        players.remove(player);//删除房间里的玩家
        readyPlayer.remove(player);//如果退出的是已准备玩家,也删除
        num--;
    }

    public boolean isStart() {
        return start;
    }


    public String GetRoomNumber() {
        return RoomNumber;
    }

    public void SetRoomNumber(String roomnum) {
        this.RoomNumber = roomnum;
    }

    public Message getRoomMessage() {
        Message roommes = new Message();
        roommes.setMessageType(MessageType.MESSAGE_ROOM_INFO);
        roommes.setRoomID(this.RoomNumber);
        String playerinfo = "";
        String readyinfo = "";
        for (int i = 0; i < 6; i++) {
            if (i < players.size()) {
                playerinfo += players.get(i).getname();
                if (readyPlayer.contains(players.get(i))) readyinfo += "1";
                else readyinfo += "0";
            }
            
            if (i != 5) playerinfo += ",";
            if (i != 5) readyinfo += ",";
        }
        roommes.setRoomPlayerInfo(playerinfo);
        roommes.setRoomReadyInfo(readyinfo);
        return roommes;
    }
}
