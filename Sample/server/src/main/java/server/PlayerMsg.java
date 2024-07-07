package server;

import java.net.Socket;

import common.Player;
import common.Room;

public class PlayerMsg extends Thread{

    Player player;//一个玩家
    Room room;//进入的房间

    public PlayerMsg(Socket client) {
        this.player = new Player(client);
    }
    public PlayerMsg(Player player,Room room){
        this.player = player;
        this.room = room;
    }

    @Override
    public void run(){
        try{

        }catch{
            
        }
    }
    
}
