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
    String MESSAGE_CREATE_ROOM="13";
    String MESSAGE_SELECT_ROOM="14";
    String MESSAGE_ROOM_INFO="15";
    String MESSAGE_SELECT_ROOM_FAIL = "16";
    String MESSAGE_GAME_START_SOON = "17";
    String MESSAGE_PLAYER_READY = "18";
    String MESSAGE_PLAYER_CANCEL_READY = "19";
    String MESSAGE_PLAYER_QUIT_ROOM = "20";
    String MESSAGE_CONNECTED_SUCCESSFULLY = "21";


    //游戏内信息类型
    String MESSAGE_YOUR_GAME_STATE = "22";//玩家状态（手牌、职业、阵营）
    String MESSAGE_YOUR_TURN_1 = "23";//到你的回合了
    String MESSAGE_YOUR_TURN_2 = "23a";
    String MESSAGE_TRADE = "24";
    String MESSAGE_ATTACK = "25";
    String MESSAGE_ANNOUNCE_VICTORY = "26";
    String MESSAGE_ANNOUNCE_SELF_VICTORY = "26s";
    String MESSAGE_SKIP_ROUND = "27";

    String MESSAGE_ASK_FOR_OPERATION = "28";//询问是否有操作
    String MESSAGE_NO_OPERATION = "29";//没有采取操作

    String MESSAGE_SUPPORT_ATTACK = "31";
    String MESSAGE_SUPPORT_DEFEND = "32";
    String MESSAGE_SUPPORT_SKIP = "33";
    String MESSAGE_BATTLE_TIED = "34";
    String MESSAGE_BATTLE_VICTORY = "35";
    String MESSAGE_PICK_ONE_CARD = "36";
    String MESSAGE_HANDCARDS_EMPTY = "37";
    String MESSAGE_CHOOSE_OPERATION = "38";
    String MESSAGE_CHECK_CAMPS = "39";
    String MESSAGE_CHECK_HANDCARDS = "40";

    String MESSAGE_TRADE_ACCEPT = "41";//交易达成
    String MESSAGE_TRADE_REJECT = "42";//交易拒绝
    String MESSAGE_TRADE_DONE = "43"; //交易完成

    String MESSAGE_USE_WHIP = "44";
    String MESSAGE_USE_CASTING_KNIVES = "45";
    String MESSAGE_USE_DAGGER = "46";
    String MESSAGE_USE_GLOVES = "47";
    String MESSAGE_USE_THUG = "48";
    String MESSAGE_USE_GRANDMASTER = "49";
    String MESSAGE_USE_BODGUARD = "50";
    String MESSAGE_WILL_NOT_OPERATE = "57";
    String MESSAGE_USE_POISONMIXER = "58"; //调毒师，发给服务器的信息中content1为winner，content2为loser
    String MESSAGE_USE_PIREST = "59";
    String MESSAGE_WAITING_PIREST = "60";

    String MESSAGE_COAT = "51";
    String MESSAGE_MONOCLE = "52";
    String MESSAGE_SEXTANT_GIVE = "53a";
    String MESSAGE_SEXTANT_RECIEVE = "53b";
    String MESSAGE_SEXTANT = "53c";
    String MESSAGE_TOME = "54";
    String MESSAGE_SECRET_BAG = "55";
    String MESSAGE_PRIVILEGE = "56";
    String MESSAGE_TRADE_EFFECTLESS = "te";
    /*57, 58,59,61被用过了，从61接着写 */

    String MESSAGE_GIVE_CARD = "61";
    String MESSAGE_WAITING_DOCTOR = "62";
    String MESSAGE_USE_DOCTOR = "63";
    String MESSAGE_PIREST_TAKEN_CARD = "64";
    String MESSAGE_FIRST_DEALER = "65";
    String MESSAGE_ASK_PIREST_OPERATION = "66";
    String MESSAGE_ASK_SUPPORTER_OPERATION = "67";
    String MESSAGE_ASK_HYPNOTIST_OPERATION = "68";
    String MESSAGE_ASK_TOOL_OPERATION = "69";

    //宣告胜利
    String MESSAGE_POINT_TEAMMATE = "70";
    String MESSAGE_GAMEOVER = "71";

    String MESSAGE_DIPLOMAT = "72";
    String MESSAGE_DIPLOMAT_SUCCEES = "72s";
    String MESSAGE_DIPLOMAT_FAILED = "72f";
    String MESSAGE_CLAIRVOYANT = "73";


    String MESSAGE_WAITING_HYPNOTIST = "74";
    String MESSAGE_USE_HYPNOTIST = "75";
    String MESSAGE_WAITING_DUELIST = "76";
    String MESSAGE_USE_DUELIST = "77";

    String MESSAGE_ASK_DOCTOR_OPERATION = "78";
    String MESSAGE_ASK_POISON_RING = "79";
    String MESSAGE_USE_POISON_RING = "80";
    String MESSAGE_NOT_USE_POISON_RING = "81";
    String MESSAGE_WINNER_TAKEN_CARD = "82";
    String MESSAGE_HANDCARD_EXCEED_MAX = "83";
    String MESSAGE_GIVE_CARD_TO_PIREST = "84";
    String MESSAGE_ASK_POISONMIXER_OPERATION = "85";
    String MESSAGE_ASK_DUELIST_OPERATION = "86";
    String MESSAGE_NO_HANDCARD = "87";

    String MESSAGE_CLAIR_SKILL = "88";
}
