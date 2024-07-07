package common;

import java.util.*;

public class Game {
    private ArrayList<Player> Players;//6个玩家

    private ArrayList<String> HandCards;//手牌
    private ArrayList<String> ProfCards;//职业牌
    private ArrayList<String> Camps;//6个阵营

    private ArrayList<String> HandCardsDeck;//手牌堆
    private ArrayList<String> ProfCardsDeck;//职业牌堆

    private String WinnerCamp = null;

    public Game(ArrayList<Player> players) {
        this.Players = players;

        String[] handcardstr = {
                CardInfo.GOBLET, CardInfo.GOBLET, CardInfo.GOBLET, CardInfo.KEY, CardInfo.KEY, CardInfo.KEY,
                CardInfo.THE_COAT_OF_ARMOR, CardInfo.POISON_RING, CardInfo.SEXTANT, CardInfo.MONOCLE,
                CardInfo.PRIVILEGE, CardInfo.COAT, CardInfo.TOME, CardInfo.WHIP, CardInfo.GLOVES,
                CardInfo.CASTING_KNIVES, CardInfo.DAGGER, CardInfo.BROKEN_MIRROR, CardInfo.BLACK_PEARL};//除去神秘手袋的一副牌
        List<String> handcardlist = Arrays.asList(handcardstr);
        this.HandCards = new ArrayList<>(handcardlist);

        String[] profcardstr = {
                ProfInfo.THUG, ProfInfo.GRANDMASTER, ProfInfo.BODY_GUARD, ProfInfo.DOCTOR, ProfInfo.POISON_MIXER,
                ProfInfo.PIREST, ProfInfo.CLAIRVOYANT, ProfInfo.DIPLOMAT, ProfInfo.DUELIST, ProfInfo.HYPNOTIST};//十种职业
        List<String> profcardlist = Arrays.asList(profcardstr);
        this.ProfCards = new ArrayList<>(profcardlist);

        String [] campstr = {"28", "28", "28", "29", "29", "29"};
        List<String> camplist = Arrays.asList(campstr);
        this.Camps = new ArrayList<>(camplist);
    }

    public void GameStart() {

        //设置玩家在游戏内,在游戏结束是以便直接出现在房间里面
        for (Player player : Players){
            player.setInGame(true);
        }
        
        //初始化牌堆并发牌
        GameInit();
        System.out.println("Game Initialized");

        //随机选出第一个出牌者
        Date date = new Date();
        Long seed = Long.parseLong(String.format("%tN", date));
        Random random = new Random(seed);
        int Dealer = random.nextInt(6);
        

        //出牌循环
        while (true) {
            Message this_dealer = new Message(MessageType.MESSAGE_FIRST_DEALER);
            this_dealer.setContent1(String.valueOf(Dealer));
            SendtoAll(this_dealer);                                             //轮到谁了
            System.out.println("It's " + String.valueOf(Dealer) + "'s turn");

            Message mess = new Message();
            mess.setMessageType(MessageType.MESSAGE_YOUR_TURN_1);
            Message r = SendtoOne(Dealer, mess);//提示玩家行动

            boolean Second_ask = true;

            switch (r.getMessageType()) {
                case MessageType.MESSAGE_ATTACK:
                    int attacker = Dealer;
                    int defender = Integer.parseInt(r.getDefender());
                    System.out.println(String.valueOf(attacker) + " chooses to attack " + String.valueOf(defender));
                    Attack(attacker, defender);
                    break;

                case MessageType.MESSAGE_TRADE:
                    int Party1st = Dealer;
                    int Party2nd = Integer.parseInt(r.getTradeTarget());
                    String Cargo1st = r.getTradeObject();
                    System.out.println(String.valueOf(Party1st) + " chooses to trade " + Cargo1st + " with " + String.valueOf(Party2nd));
                    Trade(Party1st, Party2nd, Cargo1st);
                    break;

                case MessageType.MESSAGE_ANNOUNCE_VICTORY:
                    System.out.println(String.valueOf(Dealer) + " chooses to announce victory");
                    AnnonceVictory(Dealer);
                    Second_ask = false;
                    break;

                case MessageType.MESSAGE_ANNOUNCE_SELF_VICTORY:
                    System.out.println(String.valueOf(Dealer) + " chooses to announce solo victory");
                    Message over = new Message(MessageType.MESSAGE_ANNOUNCE_SELF_VICTORY);
                    over.setContent1(Players.get(Dealer).getDeck());
                    SendtoOthers(Dealer, over);
                    Second_ask = false;
                    break;

                case MessageType.MESSAGE_CLAIRVOYANT:
                    Message clair = new Message(MessageType.MESSAGE_CLAIRVOYANT);
                    SendtoOthers(Dealer, clair);
                    clair.setContent1(HandCardsDeck);
                    Message stargaze = SendtoOne(Dealer, clair);
                    String card1 = stargaze.getContent1();
                    String card2 = stargaze.getContent2();//-1代表没有第二张牌
            
                    if(card1.equals("-1")) return;
            
                    HandCardsDeck.remove(card1);
                    if (!card2.equals("-1")) {
                        HandCardsDeck.remove(card2);
                    }
                    Collections.shuffle(HandCardsDeck);
                    if (!card2.equals("-1")) {
                        HandCardsDeck.add(0, card2);
                    }
                    HandCardsDeck.add(0, card1);
                    break; 

                case MessageType.MESSAGE_DIPLOMAT:
                    int target = Integer.parseInt(r.getTradeTarget());
                    String Object = r.getContent2();
                    SendtoOthers(Dealer, r);
                    if (Players.get(target).getDeck().contains(Object) || (Object.equals(CardInfo.SECRET_BAGS) &&
                            (Players.get(target).getDeck().contains(CardInfo.SECRET_BAG_KEY) ||
                            Players.get(target).getDeck().contains(CardInfo.SECRET_BAG_GOBLET)))) {
                        Message succ = new Message(MessageType.MESSAGE_DIPLOMAT);
                        succ.setContent1("sss");
                        Message pcard = SendtoOne(Dealer, succ);
                        String Dip_Cargo = pcard.getContent1();
                        Trade(Dealer, target, Dip_Cargo);
                    } else {
                        Message Effect = new Message(MessageType.MESSAGE_DIPLOMAT_FAILED);
                        SendtoAll(Effect);
                        Effect.setContent1(Players.get(target).getDeck());
                        SendtoOne(Dealer, Effect);
                    }
                    Second_ask = false;
                    break;

                case MessageType.MESSAGE_SKIP_ROUND:
                    Second_ask = false;
                    System.out.println(String.valueOf(Dealer) + " chooses to skip his round");
                    break;

                default:
                    Second_ask = false;
                    System.out.println(r.getMessageType());
                    break;
            }

            if (Second_ask) {
                mess.setMessageType(MessageType.MESSAGE_YOUR_TURN_2);
                r = SendtoOne(Dealer, mess);

                switch (r.getMessageType()) {
                    case MessageType.MESSAGE_ATTACK:
                        int attacker = Dealer;
                        int defender = Integer.parseInt(r.getDefender());
                        System.out.println(String.valueOf(attacker) + " chooses to attack " + String.valueOf(defender));
                        Attack(attacker, defender);
                        break;

                    case MessageType.MESSAGE_TRADE:
                        int Party1st = Dealer;
                        int Party2nd = Integer.parseInt(r.getTradeTarget());
                        String Cargo1st = r.getTradeObject();
                        System.out.println(String.valueOf(Party1st) + " chooses to trade " + Cargo1st + " with " + String.valueOf(Party2nd));
                        Trade(Party1st, Party2nd, Cargo1st);
                        break;

                    case MessageType.MESSAGE_ANNOUNCE_VICTORY:
                        System.out.println(String.valueOf(Dealer) + " chooses to announce victory");
                        AnnonceVictory(Dealer);
                        break;

                    case MessageType.MESSAGE_ANNOUNCE_SELF_VICTORY:
                        System.out.println(String.valueOf(Dealer) + " chooses to announce solo victory");
                        Message over = new Message(MessageType.MESSAGE_ANNOUNCE_SELF_VICTORY);
                        over.setContent1(Players.get(Dealer).getDeck());
                        SendtoOthers(Dealer, over);
                        break;

                    case MessageType.MESSAGE_CLAIRVOYANT:
                        Message clair = new Message(MessageType.MESSAGE_CLAIRVOYANT);
                        SendtoOthers(Dealer, clair);
                        clair.setContent1(HandCardsDeck);
                        Message stargaze = SendtoOne(Dealer, clair);
                        String card1 = stargaze.getContent1();
                        String card2 = stargaze.getContent2();//-1代表没有第二张牌
                
                        if(card1.equals("-1")) return;
                
                        HandCardsDeck.remove(card1);
                        if (!card2.equals("-1")) {
                            HandCardsDeck.remove(card2);
                        }
                        Collections.shuffle(HandCardsDeck);
                        if (!card2.equals("-1")) {
                            HandCardsDeck.add(0, card2);
                        }
                        HandCardsDeck.add(0, card1);
                        break; 

                    case MessageType.MESSAGE_SKIP_ROUND:
                        System.out.println(String.valueOf(Dealer) + " chooses to skip his round");
                        break;

                    default:
                        System.out.println(r.getMessageType());
                        break;
                }
            }

            if (WinnerCamp != null) {
                Message GameOver = new Message(MessageType.MESSAGE_GAMEOVER);
                GameOver.setContent1(WinnerCamp);
                SendtoAll(GameOver);
                break;
            }
            
            Dealer = (Dealer+1) % 6;
        }
    }


    //发牌
    public void GameInit() {

        Collections.shuffle(HandCards);
        Collections.shuffle(ProfCards);
        Collections.shuffle(Camps);//打乱牌组

        List<String> InitHandCards = new ArrayList<>(HandCards.subList(0, 4));
        InitHandCards.add(CardInfo.SECRET_BAG_GOBLET);
        InitHandCards.add(CardInfo.SECRET_BAG_KEY);
        Collections.shuffle(InitHandCards);

        List<String> InitProfCards = new ArrayList<>(ProfCards.subList(0, 6));

        for (int i = 0; i < 6; i++) {
            Players.get(i).InsertCard(InitHandCards.get(i));
            Players.get(i).setProfession(InitProfCards.get(i));
            Players.get(i).setCamp(Camps.get(i));
        }

        this.HandCardsDeck = new ArrayList<>(HandCards.subList(4, 19));
        this.ProfCardsDeck = new ArrayList<>(ProfCards.subList(6, 10));

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 6; i++) {
            Message message = Players.get(i).getInGameState();
            // message.setPlayerSeat(i);
            Message r = SendtoOne(i, message);
            System.out.println(r.getContent1());
            System.out.println(Players.get(i).getname() + " gets his init game state");
        }
    }


    public void Attack(int Attacker, int Defender) {
        int [] ScoreTable = new int[6]; // 1-攻击 0-防守 2-跳过
        for(int i=0;i<6;i++){
            ScoreTable[i] = 2;
        }
        ScoreTable[Attacker] = 1;
        ScoreTable[Defender] = 0;
        Message mess = new Message(MessageType.MESSAGE_ATTACK);
        mess.setDefender(Defender);
        mess.setAttacker(Attacker);
        SendtoOthers(Attacker, mess);

        int AttackPoint = 1;
        int DefendPoint = 1;
        int Supporter = (Attacker) % 6;
        int winner=7;
        int loser=7;
        boolean need_supprt_section = true;
        boolean need_tool_section = true;
        boolean in_duel = false;
        boolean poison_valid = false;
        boolean duelist_valid = false;
        int Poison_mixer = -1;
        int duelist = -1;

        for(int i = 0;i < 6; i++){
            if(Players.get(i).getProfession().equals("22") && i != Attacker && i != Defender){
                poison_valid = true;
                Poison_mixer = i;
            }
            if(Players.get(i).getProfession().equals("26") && (i == Attacker || i == Defender)){
                duelist_valid = true;
                duelist = i;
            }
        }


        // 牧师
        if(!in_duel){
            for(int i = 0;i<6; i ++){
                //Message wait = new Message(MessageType.MESSAGE_WAITING_PIREST);
                //SendtoOthers(i, wait);
                Message ask = new Message(MessageType.MESSAGE_ASK_PIREST_OPERATION);
                Message r = SendtoOne(i, ask);
                if(r.getMessageType().equals(MessageType.MESSAGE_USE_PIREST)){
                    r.setContent1(String.valueOf(i));
                    SendtoOthers(i, r);
                    if(Players.get(Attacker).getDeck().size() >= 2){
                        Message give_card = new Message(MessageType.MESSAGE_GIVE_CARD_TO_PIREST);
                        Message given = SendtoOne(Attacker, give_card);
                        String card = given.getContent1();
                        Players.get(Attacker).DeleteCard(card);
                        Players.get(i).InsertCard(card);
                        
                        Message pirest_taken_card = new Message(MessageType.MESSAGE_PIREST_TAKEN_CARD);
                        pirest_taken_card.setContent1("hidden");
                        SendtoOthers(i, pirest_taken_card);
                        pirest_taken_card.setContent1(card);
                        SendtoOne(i, pirest_taken_card); //需要回复。
                        if(Players.get(i).getDeck().size() > 5){
                            Message exceed = new Message(MessageType.MESSAGE_HANDCARD_EXCEED_MAX);
                            exceed.setContent1(i);
                            SendtoOthers(i, exceed);
                            Message givecard = SendtoOne(i, exceed);
                            String receiver = givecard.getContent1();
                            String card_idx = givecard.getContent2();
                            Players.get(i).DeleteCard(card_idx);
                            Players.get(Integer.parseInt(receiver)).InsertCard(card_idx);
                            Message tell_receiver = new Message(MessageType.MESSAGE_GIVE_CARD);
                            tell_receiver.setContent2(receiver);
                            tell_receiver.setContent1("hidden");
                            SendtoOthers(Integer.parseInt(receiver), tell_receiver);
                            tell_receiver.setContent1(card_idx);
                            SendtoOne(Integer.parseInt(receiver), tell_receiver); // 需要回答。
                        }
                    }
                    return ; // 战斗立刻结束
                }
            }
        }
        

        //支援阶段
        int support_ok_people = 0;
        while (support_ok_people < 4 && need_supprt_section) {
            //选下一个支援者
            Supporter = (Supporter + 1) % 6;
            if(Supporter == Attacker || Supporter == Defender) continue;

            Message ask = new Message(MessageType.MESSAGE_ASK_SUPPORTER_OPERATION);

            while (true){
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(poison_valid){
                    Message ask_poison = new Message(MessageType.MESSAGE_ASK_POISONMIXER_OPERATION);
                    Message poison_reply = SendtoOne(Poison_mixer, ask_poison);
                    if(poison_reply.getMessageType().equals(MessageType.MESSAGE_USE_POISONMIXER)){
                        String winner_ = poison_reply.getContent1();
                        String loser_ = poison_reply.getContent2();
                        poison_reply.setContent1(String.valueOf(Poison_mixer));
                        poison_reply.setContent2(winner_);
                        poison_reply.setContent3(loser_);
                        SendtoOthers(Poison_mixer, poison_reply);
                        winner = Integer.parseInt(winner_);
                        loser = Integer.parseInt(loser_);
                        need_supprt_section = false;
                        need_tool_section = false;
                        break;
                    }
                }
                if(duelist_valid){
                    Message ask_duelist = new Message(MessageType.MESSAGE_ASK_DUELIST_OPERATION);
                    Message duelist_reply = SendtoOne(duelist, ask_duelist);
                    if(duelist_reply.getMessageType().equals(MessageType.MESSAGE_USE_DUELIST)){
                        duelist_reply.setContent1(String.valueOf(duelist));
                        SendtoOthers(duelist,duelist_reply);      //告诉别人决斗家发动技能了
                        AttackPoint = 1;
                        DefendPoint = 1;
                        if(Supporter == Attacker){
                            AttackPoint ++;
                        }else{
                            DefendPoint ++;
                        }
                        for(int p = 0;p < 6; p++){
                            if(p != Attacker && p != Defender){
                                ScoreTable[p] = 2;
                            }
                        }
                        need_supprt_section = false;
                        in_duel = true;
                        break;
                    }
                }
                Message reply = SendtoOne(Supporter, ask);


                if (reply.getMessageType().equals(MessageType.MESSAGE_SUPPORT_DEFEND)) {
                    DefendPoint += 1;
                    ScoreTable[Supporter] = 0;
                    Message suppmess = new Message(MessageType.MESSAGE_SUPPORT_DEFEND);
                    suppmess.setSupporter(Supporter);
                    suppmess.setAttackerPoint(AttackPoint);
                    suppmess.setDefenderPoint(DefendPoint);
                    SendtoOthers(Supporter, suppmess);
                    support_ok_people++;
                    break;

                } else if (reply.getMessageType().equals(MessageType.MESSAGE_SUPPORT_ATTACK)) {
                    ScoreTable[Supporter] = 1;
                    AttackPoint += 1;
                    Message suppmess = new Message(MessageType.MESSAGE_SUPPORT_ATTACK);
                    suppmess.setSupporter(Supporter);
                    suppmess.setAttackerPoint(AttackPoint);
                    suppmess.setDefenderPoint(DefendPoint);
                    SendtoOthers(Supporter, suppmess);
                    support_ok_people++;
                    break;

                } else if (reply.getMessageType().equals(MessageType.MESSAGE_SUPPORT_SKIP)) {
                    Message suppmess = new Message(MessageType.MESSAGE_SUPPORT_SKIP);
                    suppmess.setSupporter(Supporter);
                    suppmess.setAttackerPoint(AttackPoint);
                    suppmess.setDefenderPoint(DefendPoint);
                    SendtoOthers(Supporter, suppmess);
                    support_ok_people++;
                    break;
                } else if (reply.getMessageType().equals(MessageType.MESSAGE_NO_OPERATION) ) {
                    continue;
                }
            }
        }

        // 催眠师

        int sleep_man = 7;
        if(!in_duel){
            if(need_tool_section){
                Message wait = new Message(MessageType.MESSAGE_WAITING_HYPNOTIST);
                SendtoOthers(Attacker, wait);
                Message ask = new Message(MessageType.MESSAGE_ASK_HYPNOTIST_OPERATION);
                Message r = SendtoOne(Attacker, ask);
                if(r.getMessageType().equals(MessageType.MESSAGE_USE_HYPNOTIST)){
                    String pass = r.getContent1();
                    sleep_man = Integer.parseInt(pass);
                    if(ScoreTable[sleep_man]==1){
                        AttackPoint --;
                    }else if(ScoreTable[sleep_man]==0){
                        DefendPoint --;
                    }
                    r.setContent2(String.valueOf(Attacker));
                    SendtoOthers(Attacker, r);
                }
            }else{
                Message wait = new Message(MessageType.MESSAGE_WAITING_HYPNOTIST);
                SendtoAll(wait);
            }
        }
        
        // 使用道具
        int ok_people_num = 0;
        int who_use_poison_ring = 0;               //1说明攻击者使用，-1说明防守者使用
        int [] dont_ask_me = new int[6];
        for(int i = 0;i < 6;i++){
            dont_ask_me[i] = 0;
        }
        while(ok_people_num < 6 && need_tool_section){
            if(in_duel){
                for(int j = 0;j < 6;j ++){
                    if(j != Attacker && j != Defender){
                        dont_ask_me[j] = 1;
                    }
                }
            }
            if(dont_ask_me[Supporter] == 1)
            {
                Supporter = (Supporter + 1)%6;
                continue;
            } 
            if(Supporter == sleep_man){
                dont_ask_me[sleep_man] = 1;
                Supporter = (Supporter + 1) % 6;
                continue;
            }
            if(in_duel && Supporter != Attacker && Supporter != Defender){
                Supporter = (Supporter + 1) % 6;
                continue;
            }
            Message ask = new Message(MessageType.MESSAGE_ASK_TOOL_OPERATION);
            
            while(true){
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message reply = SendtoOne(Supporter, ask);
                boolean plusDefend = reply.getMessageType().equals(MessageType.MESSAGE_USE_WHIP)||reply.getMessageType().equals(MessageType.MESSAGE_USE_GLOVES)
                                    ||reply.getMessageType().equals(MessageType.MESSAGE_USE_GRANDMASTER);
                boolean plusAttack = reply.getMessageType().equals(MessageType.MESSAGE_USE_DAGGER) || reply.getMessageType().equals(MessageType.MESSAGE_USE_CASTING_KNIVES) 
                                    || reply.getMessageType().equals(MessageType.MESSAGE_USE_THUG);

                if(reply.getMessageType().equals(MessageType.MESSAGE_USE_WHIP)){
                    Message c = new Message(MessageType.MESSAGE_USE_WHIP);
                    c.setContent1(String.valueOf(Supporter));
                    SendtoOthers(Supporter, c);
                }else if(reply.getMessageType().equals(MessageType.MESSAGE_USE_GLOVES)){
                    Message c = new Message(MessageType.MESSAGE_USE_GLOVES);
                    c.setContent1(String.valueOf(Supporter));
                    SendtoOthers(Supporter, c);
                }else if(reply.getMessageType().equals(MessageType.MESSAGE_USE_GRANDMASTER)){
                    Message c = new Message(MessageType.MESSAGE_USE_GRANDMASTER);
                    c.setContent1(String.valueOf(Supporter));
                    SendtoOthers(Supporter, c);
                }else if(reply.getMessageType().equals(MessageType.MESSAGE_USE_DAGGER)){
                    Message c = new Message(MessageType.MESSAGE_USE_DAGGER);
                    c.setContent1(String.valueOf(Supporter));
                    SendtoOthers(Supporter, c);
                }else if(reply.getMessageType().equals(MessageType.MESSAGE_USE_CASTING_KNIVES)){
                    Message c = new Message(MessageType.MESSAGE_USE_CASTING_KNIVES);
                    c.setContent1(String.valueOf(Supporter));
                    SendtoOthers(Supporter, c);
                }else if(reply.getMessageType().equals(MessageType.MESSAGE_USE_THUG)){
                    Message c = new Message(MessageType.MESSAGE_USE_THUG);
                    c.setContent1(String.valueOf(Supporter));
                    SendtoOthers(Supporter, c);
                }else if(reply.getMessageType().equals(MessageType.MESSAGE_USE_POISON_RING)){
                    Message c = new Message(MessageType.MESSAGE_USE_POISON_RING);
                    c.setContent1(String.valueOf(Supporter));
                    SendtoOthers(Supporter,c);
                    if(Supporter == Attacker) who_use_poison_ring = 1;
                    if(Supporter == Defender) who_use_poison_ring = -1;
                    break;
                }
                
                if(plusDefend){
                    DefendPoint += 1;
                    //reply.setContent1(String.valueOf(Supporter));
                    //SendtoOthers(Supporter, reply);
                    break;
                }else if(plusAttack){
                    AttackPoint += 1;
                    //reply.setContent1(String.valueOf(Supporter));
                    //SendtoOthers(Supporter, reply);
                    break;
                }else if(reply.getMessageType().equals(MessageType.MESSAGE_USE_BODGUARD)){
                    if(ScoreTable[Supporter]==0){
                        reply.setContent2("0");
                        DefendPoint ++;
                    }else if(ScoreTable[Supporter]==1){
                        reply.setContent2("1");
                        AttackPoint ++;
                    }else{
                        System.out.println("保镖未支援");
                    }

                    reply.setContent1(String.valueOf(Supporter));
                    SendtoOthers(Supporter, reply);
                    break;
                }else if(reply.getMessageType().equals(MessageType.MESSAGE_NO_OPERATION)){
                    break;
                }else if(reply.getMessageType().equals(MessageType.MESSAGE_WILL_NOT_OPERATE)){
                    dont_ask_me[Supporter] = 1;
                    ok_people_num ++;
                    reply.setContent1(String.valueOf(Supporter));
                    SendtoOthers(Supporter, reply);
                    break;
                }else if(reply.getMessageType().equals(MessageType.MESSAGE_USE_POISONMIXER)&&Supporter != Attacker&&Supporter!=Defender){
                    String winner_ = reply.getContent1();
                    String loser_ = reply.getContent2();
                    reply.setContent1(String.valueOf(Supporter));
                    reply.setContent2(winner_);
                    reply.setContent3(loser_);
                    SendtoOthers(Supporter, reply);
                    winner = Integer.parseInt(winner_);
                    loser = Integer.parseInt(loser_);
                    need_tool_section = false;
                    break;
                }else if(reply.getMessageType().equals(MessageType.MESSAGE_USE_DUELIST)&&(Supporter == Attacker || Supporter == Defender)){
                    AttackPoint = 1;
                    DefendPoint = 1;
                    if(Supporter == Attacker){
                        AttackPoint ++;
                    }else{
                        DefendPoint ++;
                    }
                    for(int p = 0;p < 6; p++){
                        if(p != Attacker && p != Defender){
                            ScoreTable[p] = 2;
                        }
                    }
                    reply.setContent1(String.valueOf(Supporter));
                    SendtoOthers(Supporter,reply);      //告诉别人决斗家发动技能了
                    in_duel = true;
                    break;
                }
            }
            Supporter = (Supporter + 1) % 6;
            boolean is_break = true;
            for(int i = 0;i < 6;i++){
                if(dont_ask_me[i] == 0){
                    is_break = false;
                }
            }
            if(is_break) break;
        }
        // 医生
        
        for(int i = 0;i < 6;i ++){
            Message ask = new Message(MessageType.MESSAGE_ASK_DOCTOR_OPERATION);
            Message r = SendtoOne(i, ask);
            if(r.getMessageType().equals(MessageType.MESSAGE_USE_DOCTOR)){
                r.setContent1(String.valueOf(i));
                SendtoOthers(i, r);
                return ; // 战斗立刻结束
            }
        }
        // 开始判定赢家
        if(winner == 7 && loser == 7){
            winner = AttackPoint > DefendPoint ? Attacker : AttackPoint < DefendPoint ? Defender : -1 ;
            loser = AttackPoint < DefendPoint ? Attacker : AttackPoint > DefendPoint ? Defender : -1 ;    
        }
        if(winner == -1 && who_use_poison_ring == 1) 
        {
            winner = Attacker;
            loser = Defender;
        }else if(winner == -1 && who_use_poison_ring == -1)
        {
            winner = Defender;
            loser = Attacker;
        }
        if(winner == -1){
            Message judge = new Message(MessageType.MESSAGE_BATTLE_TIED);
            judge.setWinner(Attacker);
            judge.setLoser(Defender);
            SendtoAll(judge);
            pickCard(Attacker);
            if(Players.get(Attacker).getDeck().size() > 5){
                Message exceed = new Message(MessageType.MESSAGE_HANDCARD_EXCEED_MAX);
                exceed.setContent1(Attacker);
                SendtoOthers(Attacker, exceed);
                Message r = SendtoOne(Attacker, exceed);
                String receiver = r.getContent1();
                String card_idx = r.getContent2();
                Players.get(Attacker).DeleteCard(card_idx);
                Players.get(Integer.parseInt(receiver)).InsertCard(card_idx);
                Message tell_receiver = new Message(MessageType.MESSAGE_GIVE_CARD);
                tell_receiver.setContent2(receiver);
                tell_receiver.setContent1("hidden");
                SendtoOthers(Integer.parseInt(receiver), tell_receiver);
                tell_receiver.setContent1(card_idx);
                SendtoOne(Integer.parseInt(receiver), tell_receiver); // 需要回答。
            }
        }else{
            Message judge = new Message(MessageType.MESSAGE_BATTLE_VICTORY);
            judge.setWinner(winner);
            judge.setLoser(loser);
            SendtoAll(judge);
            Message choice = new Message(MessageType.MESSAGE_CHOOSE_OPERATION);
            Message r = SendtoOne(winner, choice);
            if(r.getMessageType().equals(MessageType.MESSAGE_CHECK_CAMPS)){
                String camps = Players.get(loser).getCamp();
                String profession = Players.get(loser).getProfession();
                Message send_camp = new Message(MessageType.MESSAGE_CHECK_CAMPS);
                send_camp.setCampProfession(camps, profession);
                SendtoOne(winner, send_camp); // 这里需要客户端返回一个已收到的消息。
                Message claim = new Message(MessageType.MESSAGE_CHECK_CAMPS);
                claim.setContent1("notyou");
                SendtoOthers(winner, claim);
            }else if(r.getMessageType().equals(MessageType.MESSAGE_CHECK_HANDCARDS)){
                Message send_handcards = new Message(MessageType.MESSAGE_CHECK_HANDCARDS);
                String handcards = send_handcards.ArraytoString(Players.get(loser).getDeck());
                send_handcards.setCheckHandcard(handcards);
                Message reply = SendtoOne(winner, send_handcards);
                String card_idx = reply.getContent1();
                Players.get(winner).InsertCard(card_idx);
                Players.get(loser).DeleteCard(card_idx);
                Message tell_loser = new Message(MessageType.MESSAGE_WINNER_TAKEN_CARD);
                tell_loser.setContent1(card_idx);
                SendtoOne(loser, tell_loser); // 需要回答。
                Message claim = new Message(MessageType.MESSAGE_CHECK_HANDCARDS);
                claim.setContent1("notyou");
                SendtoOthers(winner, claim);
                

                if(Players.get(loser).getDeck().size() == 0){
                    Message no_handcard = new Message(MessageType.MESSAGE_NO_HANDCARD);
                    no_handcard.setContent1(String.valueOf(loser));
                    Message winner_reply = SendtoOne(winner, no_handcard);
                    String card = winner_reply.getContent1();
                    Players.get(winner).DeleteCard(card);
                    Players.get(loser).InsertCard(card);
                    Message give = new Message(MessageType.MESSAGE_GIVE_CARD);
                    give.setContent1(String.valueOf(winner)); // 推出端
                    give.setContent2("hidden");               // 什么牌
                    give.setContent3(String.valueOf(loser));  // 接收端
                    SendtoOthers(loser, give);
                    give.setContent2(card);
                    SendtoOne(loser, give);                     // 需要回答。
                }


                if(Players.get(winner).getDeck().size() > 5){
                    Message exceed = new Message(MessageType.MESSAGE_HANDCARD_EXCEED_MAX);
                    exceed.setContent1(winner);
                    SendtoOthers(winner, exceed);
                    Message givecard = SendtoOne(winner, exceed);
                    String receiver = givecard.getContent1();
                    String card_idx_ = givecard.getContent2();
                    Players.get(winner).DeleteCard(card_idx_);
                    Players.get(Integer.parseInt(receiver)).InsertCard(card_idx_);
                    Message tell_receiver = new Message(MessageType.MESSAGE_GIVE_CARD);
                    tell_receiver.setContent2(receiver);
                    tell_receiver.setContent1("hidden");
                    tell_receiver.setContent3("aaaaa");
                    SendtoOthers(Integer.parseInt(receiver), tell_receiver);
                    tell_receiver.setContent1(card_idx_);
                    SendtoOne(Integer.parseInt(receiver), tell_receiver); // 需要回答。
                }

                
            }
        }
    }

    public void pickCard(int player){
        if(HandCardsDeck.size() > 0){
            String card = HandCardsDeck.remove(0);
            Players.get(player).InsertCard(card);
            Message pickCard = new Message(MessageType.MESSAGE_PICK_ONE_CARD);
            pickCard.setHandCard(card);
            SendtoOne(player, pickCard);
            pickCard.setContent1("notyou");
            pickCard.setContent2(String.valueOf(player));
            SendtoOthers(player,pickCard);                         //告诉别人他抽牌了
            if (HandCardsDeck.size() == 0) {
                for (Player p : Players) {
                    ArrayList<String> deck = p.getDeck(); // 获取玩家的牌堆
                    for (int i = 0; i < deck.size(); i++) {
                        String checkcard = deck.get(i);
                        if (checkcard.equals(CardInfo.SECRET_BAG_GOBLET)) {
                            deck.set(i, CardInfo.GOBLET); // 将 SECRET_BAG_GOBLET 替换为 GOBLET
                        }
                        if (checkcard.equals(CardInfo.SECRET_BAG_KEY)) {
                            deck.set(i, CardInfo.KEY); // 将 SECRET_BAG_KEY 替换为 KEY
                        }
                    }
                }
            }
        }else{
            Message HandCardsEmpty = new Message(MessageType.MESSAGE_HANDCARDS_EMPTY);
            SendtoOne(player, HandCardsEmpty);
        }
    }

    public void Trade(int Party1st, int Party2nd, String Cargo1st) {
        Message mess = new Message(MessageType.MESSAGE_TRADE);
        mess.setContent1(String.valueOf(Party1st));//交易者
        mess.setContent2(String.valueOf(Party2nd));//被交易者
        mess.setContent3("hidden");
        SendtoOthers(Party1st, Party2nd, mess);
        mess.setContent3(Cargo1st);//交易的牌

        Message r = SendtoOne(Party2nd, mess);
        SendtoOthers(Party2nd, r);

        if (r.getMessageType().equals(MessageType.MESSAGE_TRADE_REJECT)) {
            return;
        }

        String Cargo2nd = r.getContent1();//交易的牌

        Players.get(Party2nd).DeleteCard(Cargo2nd);
        Players.get(Party1st).DeleteCard(Cargo1st);
        Players.get(Party2nd).InsertCard(Cargo1st);
        Players.get(Party1st).InsertCard(Cargo2nd);

        Message Effect = new Message();
        
        if (Cargo1st.equals(CardInfo.BROKEN_MIRROR) || Cargo2nd.equals(CardInfo.BROKEN_MIRROR))
        {
            Effect.setMessageType(MessageType.MESSAGE_TRADE_EFFECTLESS);
            SendtoAll(Effect);
            SendtoAll(Effect);
            Message Done = new Message(MessageType.MESSAGE_TRADE_DONE);
            SendtoAll(Done);
            return;
        }

        switch (Cargo1st) {
            case CardInfo.COAT:
                Effect.setMessageType(MessageType.MESSAGE_COAT);
                Effect.setContent1("notyou");
                SendtoAll(Effect);
                Effect.setContent1(ProfCardsDeck);
                Message ret = SendtoOne(Party1st, Effect);
                String ProfChosen = ret.getContent1();
                ProfCardsDeck.remove(ProfChosen);
                ProfCardsDeck.add(Players.get(Party1st).getProfession());
                Players.get(Party1st).setProfession(ProfChosen);
                break;

            case CardInfo.SECRET_BAG_GOBLET:
                Effect.setMessageType(MessageType.MESSAGE_SECRET_BAG);
                SendtoAll(Effect);
                pickCard(Party1st);

                if(Players.get(Party1st).getDeck().size() > 5){
                    Message exceed = new Message(MessageType.MESSAGE_HANDCARD_EXCEED_MAX);
                    exceed.setContent1(Party1st);
                    SendtoOthers(Party1st, exceed);
                    Message givecard = SendtoOne(Party1st, exceed);
                    String receiver = givecard.getContent1();
                    String card_idx = givecard.getContent2();
                    Players.get(Party1st).DeleteCard(card_idx);
                    Players.get(Integer.parseInt(receiver)).InsertCard(card_idx);
                    Message tell_receiver = new Message(MessageType.MESSAGE_GIVE_CARD);
                    tell_receiver.setContent2(receiver);
                    tell_receiver.setContent1("hidden");
                    tell_receiver.setContent3("aaaaa");
                    SendtoOthers(Integer.parseInt(receiver), tell_receiver);
                    tell_receiver.setContent1(card_idx);
                    SendtoOne(Integer.parseInt(receiver), tell_receiver); // 需要回答。
                }
                break;

            case CardInfo.SECRET_BAG_KEY:
                Effect.setMessageType(MessageType.MESSAGE_SECRET_BAG);
                SendtoAll(Effect);
                pickCard(Party1st);

                if(Players.get(Party1st).getDeck().size() > 5){
                    Message exceed = new Message(MessageType.MESSAGE_HANDCARD_EXCEED_MAX);
                    exceed.setContent1(Party1st);
                    SendtoOthers(Party1st, exceed);
                    Message givecard = SendtoOne(Party1st, exceed);
                    String receiver = givecard.getContent1();
                    String card_idx = givecard.getContent2();
                    Players.get(Party1st).DeleteCard(card_idx);
                    Players.get(Integer.parseInt(receiver)).InsertCard(card_idx);
                    Message tell_receiver = new Message(MessageType.MESSAGE_GIVE_CARD);
                    tell_receiver.setContent2(receiver);
                    tell_receiver.setContent1("hidden");
                    tell_receiver.setContent3("aaaaa");
                    SendtoOthers(Integer.parseInt(receiver), tell_receiver);
                    tell_receiver.setContent1(card_idx);
                    SendtoOne(Integer.parseInt(receiver), tell_receiver); // 需要回答。
                }
                break;

            case CardInfo.TOME:
                Effect.setMessageType(MessageType.MESSAGE_TOME);
                Effect.setContent1("notyou");
                SendtoAll(Effect);
                String Prof1st = Players.get(Party1st).getProfession();
                String Prof2nd = Players.get(Party2nd).getProfession();
                Players.get(Party2nd).setProfession(Prof1st);
                Players.get(Party1st).setProfession(Prof2nd);
                Effect.setContent1(Prof2nd);
                SendtoOne(Party1st, Effect);
                Effect.setContent1(Prof1st);
                SendtoOne(Party2nd, Effect);
                break;

            case CardInfo.SEXTANT:
                Effect.setMessageType(MessageType.MESSAGE_SEXTANT);
                SendtoAll(Effect);
                Effect.setMessageType(MessageType.MESSAGE_SEXTANT_GIVE);
                Effect.setContent1("isyou");
                Message direc = SendtoOne(Party1st, Effect);//方向，0为逆时针，1为顺时针
                Effect.setContent1(direc.getContent1());
                ArrayList<String> sextant = new ArrayList<>();
                
                
                for (int i = 0; i < 6; i++) {
                    Players.get(i).sendMsg(Effect);
                    Message give = Players.get(i).receiveMsg();
                    String card = give.getContent1();
                    sextant.add(card);
                    Players.get(i).DeleteCard(card);
                }
                for (int i = 0; i < 6; i++) {
                    Effect.setMessageType(MessageType.MESSAGE_SEXTANT_RECIEVE);
                    if(direc.getContent1().equals("0")){
                        Effect.setContent1(sextant.get((i+5)%6));
                        Players.get(i).InsertCard(sextant.get((i+5)%6));
                    } else {
                        Effect.setContent1(sextant.get((i+1)%6));
                        Players.get(i).InsertCard(sextant.get((i+1)%6));
                    }
                    SendtoOne(i, Effect);
                }
                break;
                
            case CardInfo.MONOCLE:
                Effect.setMessageType(MessageType.MESSAGE_MONOCLE);
                Effect.setContent1("notyou");
                SendtoAll( Effect);
                Effect.setContent1(Players.get(Party2nd).getCamp());
                SendtoOne(Party1st, Effect);
                break;

            case CardInfo.PRIVILEGE:
                Effect.setMessageType(MessageType.MESSAGE_PRIVILEGE);
                Effect.setContent1("notyou");
                SendtoAll( Effect);
                
                Effect.setContent1(Players.get(Party2nd).getDeck());
                SendtoOne(Party1st, Effect);
                break;
        
            default:
                Effect.setMessageType(MessageType.MESSAGE_TRADE_EFFECTLESS);
                SendtoAll(Effect);
                break;
        }

        switch (Cargo2nd) {
            case CardInfo.COAT:
                Effect.setMessageType(MessageType.MESSAGE_COAT);
                Effect.setContent1("notyou");
                SendtoAll( Effect);
                
                Effect.setContent1(ProfCardsDeck);
                Message ret = SendtoOne(Party2nd, Effect);
                String ProfChosen = ret.getContent1();
                ProfCardsDeck.remove(ProfChosen);
                ProfCardsDeck.add(Players.get(Party2nd).getProfession());
                Players.get(Party2nd).setProfession(ProfChosen);
                break;

            case CardInfo.SECRET_BAG_GOBLET:
                Effect.setMessageType(MessageType.MESSAGE_SECRET_BAG);
                SendtoAll( Effect);
                
                pickCard(Party2nd);

                if(Players.get(Party2nd).getDeck().size() > 5){
                    Message exceed = new Message(MessageType.MESSAGE_HANDCARD_EXCEED_MAX);
                    exceed.setContent1(Party2nd);
                    SendtoOthers(Party2nd, exceed);
                    Message givecard = SendtoOne(Party2nd, exceed);
                    String receiver = givecard.getContent1();
                    String card_idx = givecard.getContent2();
                    Players.get(Party2nd).DeleteCard(card_idx);
                    Players.get(Integer.parseInt(receiver)).InsertCard(card_idx);
                    Message tell_receiver = new Message(MessageType.MESSAGE_GIVE_CARD);
                    tell_receiver.setContent2(receiver);
                    tell_receiver.setContent1("hidden");
                    tell_receiver.setContent3("aaaaa");
                    SendtoOthers(Integer.parseInt(receiver), tell_receiver);
                    tell_receiver.setContent1(card_idx);
                    SendtoOne(Integer.parseInt(receiver), tell_receiver); // 需要回答。
                }
                break;

            case CardInfo.SECRET_BAG_KEY:
                Effect.setMessageType(MessageType.MESSAGE_SECRET_BAG);
                SendtoAll( Effect);
                
                pickCard(Party2nd);

                if(Players.get(Party2nd).getDeck().size() > 5){
                    Message exceed = new Message(MessageType.MESSAGE_HANDCARD_EXCEED_MAX);
                    exceed.setContent1(Party2nd);
                    SendtoOthers(Party2nd, exceed);
                    Message givecard = SendtoOne(Party2nd, exceed);
                    String receiver = givecard.getContent1();
                    String card_idx = givecard.getContent2();
                    Players.get(Party2nd).DeleteCard(card_idx);
                    Players.get(Integer.parseInt(receiver)).InsertCard(card_idx);
                    Message tell_receiver = new Message(MessageType.MESSAGE_GIVE_CARD);
                    tell_receiver.setContent2(receiver);
                    tell_receiver.setContent1("hidden");
                    tell_receiver.setContent3("aaaaa");
                    SendtoOthers(Integer.parseInt(receiver), tell_receiver);
                    tell_receiver.setContent1(card_idx);
                    SendtoOne(Integer.parseInt(receiver), tell_receiver); // 需要回答。
                }
                break;

            case CardInfo.TOME:
                Effect.setMessageType(MessageType.MESSAGE_TOME);
                Effect.setContent1("notyou");

                SendtoAll( Effect);
                
                String Prof1st = Players.get(Party1st).getProfession();
                String Prof2nd = Players.get(Party2nd).getProfession();
                Players.get(Party2nd).setProfession(Prof1st);
                Players.get(Party1st).setProfession(Prof2nd);
                Effect.setContent1(Prof2nd);
                SendtoOne(Party1st, Effect);
                Effect.setContent1(Prof1st);
                SendtoOne(Party2nd, Effect);
                break;

            case CardInfo.SEXTANT:
            Effect.setMessageType(MessageType.MESSAGE_SEXTANT);
                SendtoAll(Effect);
                Effect.setMessageType(MessageType.MESSAGE_SEXTANT_GIVE);
                Effect.setContent1("isyou");

                Message direc = SendtoOne(Party2nd, Effect);//方向，0为逆时针，1为顺时针
                Effect.setContent1(direc.getContent1());
                ArrayList<String> sextant = new ArrayList<>();
                
                
                for (int i = 0; i < 6; i++) {
                    Players.get(i).sendMsg(Effect);

                    Message give = Players.get(i).receiveMsg();
                    String card = give.getContent1();
                    sextant.add(card);
                    Players.get(i).DeleteCard(card);
                }
                for (int i = 0; i < 6; i++) {
                    Effect.setMessageType(MessageType.MESSAGE_SEXTANT_RECIEVE);
                    if(direc.getContent1().equals("0")){
                        Effect.setContent1(sextant.get((i+5)%6));
                        Players.get(i).InsertCard(sextant.get((i+5)%6));
                    } else {
                        Effect.setContent1(sextant.get((i+1)%6));
                        Players.get(i).InsertCard(sextant.get((i+1)%6));
                    }
                    SendtoOne(i, Effect);
                }
                break;
                
            case CardInfo.MONOCLE:
                Effect.setMessageType(MessageType.MESSAGE_MONOCLE);
                Effect.setContent1("notyou");

                SendtoAll( Effect);
                Effect.setContent1(Players.get(Party1st).getCamp());
                SendtoOne(Party2nd, Effect);
                break;

            case CardInfo.PRIVILEGE:
                Effect.setMessageType(MessageType.MESSAGE_PRIVILEGE);
                Effect.setContent1("notyou");

                SendtoAll( Effect);
                
                Effect.setContent1(Players.get(Party1st).getDeck());
                SendtoOne(Party2nd, Effect);
                break;
        
            default:
            Effect.setMessageType(MessageType.MESSAGE_TRADE_EFFECTLESS);
            SendtoAll(Effect);
            break;
        }

        Message Done = new Message(MessageType.MESSAGE_TRADE_DONE);
        SendtoAll(Done);
        
    }

    public void AnnonceVictory(int Annoncer) {
        Message Situation = new Message(MessageType.MESSAGE_ANNOUNCE_VICTORY);
        Situation.setContent1(String.valueOf(Annoncer));
        String acamp = Players.get(Annoncer).getCamp();
        Situation.setContent2(acamp);
        Situation.setContent3(Players.get(Annoncer).getDeck());
        SendtoOthers(Annoncer, Situation);

        String ocamp = acamp.equals("28") ? "29" : "28";

        int sum = 0;
        sum += Players.get(Annoncer).CountTreasure();
        if (sum >= 3) {
            WinnerCamp = acamp;
            return;
        }

        Message PointTeammate = new Message(MessageType.MESSAGE_POINT_TEAMMATE);
        Message Teammate = SendtoOne(Annoncer, PointTeammate);
        int teammate1 = Integer.parseInt(Teammate.getContent1());

        Situation.setContent1(String.valueOf(teammate1));
        String tcamp = Players.get(teammate1).getCamp();
        Situation.setContent2(tcamp);
        Situation.setContent3(Players.get(teammate1).getDeck());
        SendtoAll(Situation);

        if (!tcamp.equals(acamp)) {
            WinnerCamp = ocamp;
            return;
        }
        int delta = Players.get(teammate1).CountTreasure();
        if (delta == 0) {
            WinnerCamp = ocamp;
            return;
        }
        sum += delta;
        if (sum >= 3) {
            WinnerCamp = acamp;
            return;
        }

        Teammate = SendtoOne(Annoncer, PointTeammate);
        int teammate2 = Integer.parseInt(Teammate.getContent1());
        Situation.setContent1(String.valueOf(teammate2));
        tcamp = Players.get(teammate2).getCamp();
        Situation.setContent2(tcamp);
        Situation.setContent3(Players.get(teammate2).getDeck());
        SendtoAll(Situation);

        if (!tcamp.equals(acamp)) {
            WinnerCamp = ocamp;
            return;
        }
        delta = Players.get(teammate1).CountTreasure();
        if (delta == 0) {
            WinnerCamp = ocamp;
            return;
        }
        sum += delta;
        if (sum >= 3) {
            WinnerCamp = acamp;
            return;
        } else {
            WinnerCamp = ocamp;
            return;
        }
    }


    //给所有人发消息
    public void SendtoAll(Message message) {
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 6; i++) {
            Players.get(i).sendMsg(message);
        }
        for (int i = 0; i < 6; i++) {
            Players.get(i).receiveMsg();
            
        }
    }

    //给除了i的其他人发送消息
    public void SendtoOthers(int i, Message message) {
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int j = 0; j < 6; j++) {
            if (j != i) {
                Players.get(j).sendMsg(message);
            }
        }
        for (int j = 0; j < 6; j++) {
            if (j != i) {
                Players.get(j).receiveMsg();
            }
        }
        
    }
    public void SendtoOthers(int i, int j, Message message) {
         try {
             Thread.sleep(100);
         } catch (Exception e) {
             e.printStackTrace();
         }
        for (int k = 0; k < 6; k++) {
            if (k != i && k != j) {
                Players.get(k).sendMsg(message);
            }
        }
        for (int k = 0; k < 6; k++) {
            if (k != i && k != j) {
                Players.get(k).receiveMsg();
            }
        }
    }
    //给i发信息
    public Message SendtoOne(int i, Message mess) {
         try {
             Thread.sleep(100);
         } catch (Exception e) {
             e.printStackTrace();
         }
        Players.get(i).sendMsg(mess);
        Message m1 = Players.get(i).receiveMsg();
        m1 = Players.get(i).receiveMsg();
        return m1;
    }
    
}
