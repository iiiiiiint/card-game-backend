package common;

public interface MessageType {
    String MESSAGE_LOGIN_SUCCESS="1"; //登录成功
    String MESSAGE_LOGIN_FAIL="2"; //登录失败
    String MESSAGE_LOGIN_REQUEST = "3";//登陆请求
    String MESSAGE_COMM_MES="4";//普通信息包
    String MESSAGE_GET_ONLINE_FRIEND="5"; //得到在线用户列表
    String MESSAGE_RET_ONLINE_FRIEND="6";//返回在线用户列表
    String MESSAGE_CLIENT_EXIT="7"; //客户请求退出
    String MESSAGE_CLIENT_NO_EXIST="8"; //发送目标不存在
    String MESSAGE_CLIENT_OFFLINE="9"; //发送目标不存在
    String MESSAGE_SIGNUP_SUCCESS="10";//注册成功
    String MESSAGE_SIGNUP_FAIL="11";//注册失败
    String MESSAGE_SIGNUP_REQUEST="12";//注册请求
}
