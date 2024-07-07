package common;

public class Actions {
    private static String[] ActionsList = {"Exit", "Log in", "Sign up", "List online users", "Send to all", "Private chat"};
    private static int[] BeginActionsList = {0, 1, 2};
    private static int[] HallActionsList = {0, 3, 4, 5};

    public static void PrintBeginActions() {
        for(int i = 0; i < BeginActionsList.length; i++){
            System.out.println(BeginActionsList[i] + "  " + ActionsList[BeginActionsList[i]]);
        }
    }

    public static void PrintHallActions() {
        for(int i = 0; i < HallActionsList.length; i++){
            System.out.println(HallActionsList[i] + "  " + ActionsList[HallActionsList[i]]);
        }
    }
}
