package client.Menu;

import common.Utility;
import client.service.UserClientService;
import client.QQView;

public class Login {
    private UserClientService userClientService = new UserClientService();

    public String Show() {
        System.out.print("请输入用户号：");
        String UserId = Utility.readString();
        System.out.print("请输入密 码：");
        String PassWd = Utility.readString();
        //去服务端看看用户是否合法
        if (userClientService.checkUser(UserId, PassWd)) {
            QQView.SetUserInformation(UserId, PassWd);
            System.out.println("============欢迎【" + UserId + "】登录网络通信系统============");
            return MenuType.HALL;
        } else {
            System.out.println("登录失败！");
            return MenuType.BEGIN;
        }
    }
}
