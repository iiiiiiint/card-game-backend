package client.Menu;

import client.QQView;
import common.Actions;
import common.Utility;
import client.service.UserClientService;

public class Hall {
    private String key = ""; //接收用户键盘输入
    private UserClientService userClientService = new UserClientService();

    public String Show(){
        System.out.println("============【" + QQView.GetUserId() + "】网络通信系统二级菜单============");
        Actions.PrintHallActions();
        key = Utility.readString();
        String name; //发送给谁
        String contents; //消息内容
        switch (key) {
            case MenuType.LIST_ONLINE_USERS:
                userClientService.onlineFriendList();
                break;
            case MenuType.SEND_TO_ALL:
                System.out.print("群发内容：" );
                contents = Utility.readString();
                userClientService.sendAll(contents);
                break;
            case MenuType.PRIVATE_CHAT:
                System.out.print("发送给：");
                name = Utility.readString();
                System.out.print("内容：" );
                contents = Utility.readString();
                userClientService.Send(name,contents);
                break;
            case MenuType.EXIT:
                userClientService.closedComm();
                System.out.println("客户端退出...");
                break;
        }
        try {
            Thread.sleep(5); //为了输出好看些
            return MenuType.HALL;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return MenuType.HALL;
        }
    }
}
