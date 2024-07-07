package client.Menu;

import common.Utility;
import common.Actions;

public class MainMenu {

    private String key = ""; //接收用户键盘输入
    
    public String Show() {
        System.out.println("============欢迎登录网络通信系统============");
        Actions.PrintBeginActions();
        key = Utility.readString();
        return key;
    }

}
