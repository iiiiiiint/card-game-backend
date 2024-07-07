import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        Player player = new Player();
        String next = player.MainMenu();
        while (next != WindowType.EXIT) {
            switch (next) {
                case WindowType.LOGIN:
                    next = player.Login();
                    break;
                case WindowType.SIGNUP:
                    next = player.Signup();
                    break;
                case WindowType.MAINMENU:
                    next = player.MainMenu();
                    break;
                case WindowType.HALL:
                    next = player.Hall();
                    break;
                case WindowType.ROOM:
                    next = player.Room();
                default:
                    break;
            }
        }
    }
}