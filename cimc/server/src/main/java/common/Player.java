package common;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;

public class Player {
    private ArrayList<String> Deck;//玩家的牌
    private String Profession;//玩家的职业
    private String Camp;//玩家阵营
    private BufferedWriter bufferedWriter;//玩家的输出流
    private BufferedReader bufferedReader;//玩家的输入流
    private boolean isInGame;//玩家是否在游戏标识
    private Room room;//玩家所在的房间
    private Socket socket;
    private String name;

    private Gson gson = new Gson();

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

    public Player(Socket socket){
        try {
            this.socket = socket;
            OutputStream outputStream = this.socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            this.bufferedWriter = new BufferedWriter(outputStreamWriter);
            Message temp_Message = new Message();
            temp_Message.setMessageType(MessageType.MESSAGE_CONNECTED_SUCCESSFULLY);
            this.sendMsg(temp_Message);
            InputStream inputStream = this.socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            this.bufferedReader = new BufferedReader(inputStreamReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.Deck = new ArrayList<>();
        this.isInGame = false;
    }

    public void sendMsg(Message s){
        try {
            String mess = gson.toJson(s);
            System.out.println("Send a message to "+ name +" : "+ mess);
            this.bufferedWriter.write(mess);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("Send Message Fail.");
        }
        
    }

    public Message receiveMsg(){
        try {
            Message mess = gson.fromJson(this.bufferedReader.readLine(), Message.class);
            System.out.println("From " + name +" receive message type : " + mess.getMessageType());

            return mess;
        } catch (Exception e) {
            System.out.println("玩家离开");
        }
        return null;
    }

    public String getname() {
        return this.name;
    }

    public void setname(String _name) {
        this.name = _name;
    }

    public ArrayList<String> getDeck() {
        return Deck;
    }

    public void InsertCard(String card) {
        Deck.add(card);
    }

    public void DeleteCard(String card) {
        Deck.remove(card);
    }

    public String getProfession() {
        return Profession;
    }

    public void setProfession(String profession) {
        Profession = profession;
    }

    public String getCamp() {
        return Camp;
    }

    public void setCamp(String camp) {
        Camp = camp;
    }

    public Message getInGameState() {
        Message mess = new Message();
        mess.setMessageType(MessageType.MESSAGE_YOUR_GAME_STATE);
        mess.setPlayerDeck(Deck);
        mess.setPlayerProf(Profession);
        mess.setPlayerCamp(Camp);
        return mess;
    }

    public int CountTreasure() {
        int ret = 0;
        for (String card: Deck) {
            if (card.equals(CardInfo.GOBLET) && this.Camp.equals("28")) {
                ret += 1;
            }
            if (card.equals(CardInfo.KEY) && this.Camp.equals("29")) {
                ret += 1;
            }
        }
        return ret;
    }

}
