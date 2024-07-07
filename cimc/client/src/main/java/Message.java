import java.io.Serializable;

public class Message implements Serializable {
    // private static final long serialVersionUID=1L;
    private String Content[] = new String[4];


    public Message() {

    }

    public void setMessageType(String MessageType) {
        this.Content[0] = MessageType;
    }

    public String getMessageType() {
        return this.Content[0];
    }

    public void setPlayerId(String UserName) {
        this.Content[1] = UserName;
    }

    public void setPassWord(String PassWord) {
        this.Content[2] = PassWord;
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
}