package common;
import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private String Content[] = new String[4];


    public Message() {

    }

    public Message(String MessageType) {
        this.Content[0] = MessageType;
    }

    public String getMessageType() {
        return Content[0];
    }

    public void setMessageType(String MessageType) {
        this.Content[0] = MessageType;
    }

    public String getUserName() {
        return Content[1];
    }

    public void setUserName(String UserName) {
        this.Content[1] = UserName;
    }

    public String getPassWord() {
        return Content[2];
    }
    public void setPassWord(String PassWord) {
        this.Content[2] = PassWord;
    }
    public void setReason(String Reason){
        this.Content[1] = Reason;
    }
    public void setRoomID(String roomid) {
        this.Content[1] = roomid;
    }

    public String getSelectRoomID(){
        return this.Content[1];
    }

    public void setRoomPlayerInfo(String playerinfo) {
        this.Content[2] = playerinfo;
    }

    public void setRoomReadyInfo(String readyinfo) {
        this.Content[3] = readyinfo;
    }
    public boolean becomeReady(){
        if(this.Content[0].equals(MessageType.MESSAGE_PLAYER_READY)){
            return true;
        }else{
            return false;
        }
    }
    public boolean becomeNoReady(){
        if(this.Content[0].equals(MessageType.MESSAGE_PLAYER_CANCEL_READY)){
            return true;
        }else{
            return false;
        }
    }
/*------------------------------- */



    public void setPlayerId(String UserName) {
        this.Content[1] = UserName;
    }


    public String getReason() {
        return this.Content[1];
    }

    public void setRoomId(String RoomId) {
        this.Content[1] = RoomId;
    }

    public String getRoomId() {
        return this.Content[1];
    }

    public String getPlayerInfo() {
        return this.Content[2];
    }

    public String getReadyInfo() {
        return this.Content[3];
    }


    /*------------游戏内信号-------------*/
    public String getPlayerDeck() {
        return this.Content[1];
    }
    
    public void setPlayerDeck(ArrayList<String> Deck) {
        String deckString = ArraytoString(Deck);
        this.Content[1] = deckString;
    }

    public String getPlayerProf() {
        return this.Content[2];
    }
    
    public void setPlayerProf(String Prof) {
        this.Content[2] = Prof;
    }

    public String getPlayerCamp() {
        return this.Content[3];
    }
    
    public void setPlayerCamp(String Camp) {
        this.Content[3] = Camp;
    }

    public void setPlayerSeat(int i) {
        this.Content[4] = String.valueOf(i);
    }
    public String getDefender(){
        return this.Content[1];
    }
    public String getAttackerDuel(){
        return this.Content[2];
    }
    public void setDefender(int a){
        this.Content[1] = String.valueOf(a);
    }
    public void setAttacker(int a){
        this.Content[2] = String.valueOf(a);
    }
    public void setSupporter(int a){
        this.Content[1] = String.valueOf(a);
    }
    public void setAttackerPoint(int a){
        this.Content[2] = String.valueOf(a);
    }
    public void setDefenderPoint(int a){
        this.Content[3] = String.valueOf(a);
    }

    public String ArraytoString(ArrayList<String> arrayList){
        int n = arrayList.size();
        String ret = "";
        for (int i = 0; i < n; i++) {
            ret += arrayList.get(i);
            if (i != n-1) {
                ret += ",";
            }
        }
        return ret;
    }
    public void setWinner(int a){
        this.Content[1] = String.valueOf(a);
    }
    public void setLoser(int a){
        this.Content[2] = String.valueOf(a);
    }
    public void setHandCard(String a){
        this.Content[1] = a;
    }
    public void setCampProfession(String a, String b){
        this.Content[1] = a;
        this.Content[2] = b;
    }
    public void setCheckHandcard(String a){
        this.Content[1] = a;
    }
    public String getTradeTarget(){
        return this.Content[1];
    }
    public String getTradeObject(){
        return this.Content[2];
    }
    public void setContent1(String a){
        this.Content[1] = a;
    }
    public void setContent1(ArrayList<String> arrayList){
        this.Content[1] = ArraytoString(arrayList);
    }
    public void setContent1(int a){
        this.Content[1] = String.valueOf(a);
    }
    public void setContent2(String a){
        this.Content[2] = a;
    }
    public void setContent2(ArrayList<String> arrayList){
        this.Content[2] = ArraytoString(arrayList);
    }
    public void setContent2(int a){
        this.Content[2] = String.valueOf(a);
    }
    public void setContent3(String a){
        this.Content[3] = a;
    }
    public void setContent3(ArrayList<String> arrayList){
        this.Content[3] = ArraytoString(arrayList);
    }
    public void setContent3(int a){
        this.Content[3] = String.valueOf(a);
    }

    public String getContent1(){
        return this.Content[1];
    }
    public String getContent2(){
        return this.Content[2];
    }
    public String getContent3(){
        return this.Content[3];
    }
}
