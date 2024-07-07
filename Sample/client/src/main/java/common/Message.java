package common;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID=1L;
    private String Sender; //发送者
    private String Getter; //接收者
    private String Content; //内容
    private String SendTime; //发送时间
    private String MessageType; //消息类型
    private String UserName; //用户名
    private String PassWord; //密码

    public Message(String Sender, String MessageType) {
        this.Sender = Sender;
        this.MessageType = MessageType;
    }

    public Message() {

    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String Sender) {
        this.Sender = Sender;
    }

    public String getGetter() {
        return Getter;
    }

    public void setGetter(String Getter) {
        this.Getter = Getter;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public String getSendTime() {
        return SendTime;
    }

    public void setSendTime(String SendTime) {
        this.SendTime = SendTime;
    }

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String MessageType) {
        this.MessageType = MessageType;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setPassWord(String PassWord) {
        this.PassWord = PassWord;
    }
}
