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
    String MESSAGE_CREATE_ROOM="13"; // 创建一个房间
    String MESSAGE_SELECT_ROOM="14"; // 选择一个房间
    String MESSAGE_ROOM_INFO="15"; // 这条信息是房间信息
    String MESSAGE_SELECT_ROOM_FAIL = "16"; // 选择房间失败
    String MESSAGE_GAME_START_SOON = "17"; // 六个人都准备好了， 游戏马上开始
    String MESSAGE_PLAYER_READY = "18"; // 用户准备
    String MESSAGE_PLAYER_CANCEL_READY = "19"; // 用户取消准备
    String MESSAGE_PLAYER_QUIT_ROOM = "20"; // 用户退出
    String MESSAGE_CONNECTED_SUCCESSFULLY = "21"; // 连接成功

        //游戏内信息类型
    String MESSAGE_YOUR_GAME_STATE = "22";//玩家状态（手牌、职业、阵营）
    String MESSAGE_YOUR_TURN = "23";//到你的回合了
    String MESSAGE_TRADE = "24";
    String MESSAGE_ATTACK = "25";
    String MESSAGE_ANNOUNCE_VICTORY = "26";
    String MESSAGE_SKIP_ROUND = "27";
}