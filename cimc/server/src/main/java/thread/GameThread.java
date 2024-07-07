package thread;

import java.util.ArrayList;

import common.Game;
import common.Player;

/**

 * @title: GameThread
 *
 * @description: 一局游戏的多线程,用于不同玩家的游戏
 *

 */
class GameThread extends Thread {//一局游戏的多线程类
    ArrayList<Player> players;

    public GameThread(ArrayList<Player> players) {
        this.players = players;
        System.out.println("游戏开始！");
    }

    @Override
    public void run() {
        Game game = new Game(players);
        game.GameStart();//开始一轮游戏
        for (Player player: players){//游戏结束后,重新开三个玩家交流线程,依靠isInGame的flag跳过选择房间阶段,
            new PlayerThread(player,player.getRoom()).start();//直接在房间的准备阶段
        }
    }
}