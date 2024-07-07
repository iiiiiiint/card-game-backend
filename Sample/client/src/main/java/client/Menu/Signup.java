package client.Menu;

import common.Utility;
import client.service.UserClientService;

public class Signup {

    private UserClientService userClientService = new UserClientService();
    
    public String SignUp() {
        System.out.print("请输入用户号：");
        String userId = Utility.readString();
        System.out.print("请输入密 码：");
        String pwd = Utility.readString();
        //去服务端看看用户是否合法
        
        if (userClientService.signupUser(userId, pwd)) {
            System.out.println("============恭喜您，注册成功！============");
            return MenuType.BEGIN;
        } else {
            System.out.println("============注册失败！============");
            return MenuType.SIGNUP;
        }
    }
}
