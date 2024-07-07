package common;
import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID=1L;
    private String Content[] = new String[4];


    public Message() {

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

}
