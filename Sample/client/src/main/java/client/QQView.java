package client;

import client.Menu.Hall;
import client.Menu.Login;
import client.Menu.MainMenu;
import client.Menu.MenuType;
import common.User;

public class QQView {

    public static User user = new User();
    public static void main(String[] args) {

        String next = new MainMenu().Show();

        while(next != MenuType.EXIT){
            switch (next) {
                case MenuType.LOGIN:
                    next = new Login().Show();
                    break;
                case MenuType.HALL:
                    next = new Hall().Show();
                    break;
            
                default:
                    break;
            }
        }
    }

    public static void SetUserInformation(String UserId, String PassWd){
        user.setPasswd(PassWd);
        user.setUserId(UserId);
    }

    public static String GetUserId(){
        return user.getUserId();
    }
}
