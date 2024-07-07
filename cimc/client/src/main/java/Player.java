import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import com.google.gson.Gson;


public class Player {
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private Socket socket;
    private Scanner scanner = new Scanner(System.in);
    private String PlayerId;
    private String PassWd;
    private Message MessageIn;
    private Message MessageOut = new Message();
    private String RoomPlayerInfo = new String();
    private String RoomReadyInfo = new String();
    private String RoomId;
    private String Next = new String();
    private Gson gson = new Gson();
    private String JsonIn = "";
    private String JsonOut = "";
    private Boolean IsReady = false;
    private Boolean IsModify = false;

    public Player() {
        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 8888);
            InputStream inputStream = this.socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            this.bufferedReader = new BufferedReader(inputStreamReader);
            System.out.println(this.bufferedReader.readLine());
            OutputStream outputStream = this.socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            this.bufferedWriter = new BufferedWriter(outputStreamWriter);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
        }
    }

    public String MainMenu() {
        try {
 
            System.out.println("1 Log in");
            System.out.println("2 Sign up");
            System.out.println("3 Exit");
    
            String Choice = scanner.nextLine();
            // Choice == 1 -> Login window
            // Choice == 2 -> Sign up window
            // Choice == 3 -> Exit
            switch (Choice) {
                case "1":
                    Next = WindowType.LOGIN;
                    break;
                case "2":
                    Next = WindowType.SIGNUP;
                    break;
                case "3":
                    Next = WindowType.EXIT;
                    break;
                default:
                    Next = WindowType.MAINMENU;
                    break;
            }
            return Next;
        } catch (Exception e) {
            // TODO: handle exception
            return Next;
        }

    }

    public String Login() {
        try {
            System.out.println("UserId: ");
            PlayerId = scanner.nextLine();
            System.out.println("PassWd: ");
            PassWd = scanner.nextLine();

            MessageOut.setPlayerId(PlayerId);
            MessageOut.setPassWord(PassWd);
            MessageOut.setMessageType(MessageType.MESSAGE_LOGIN_REQUEST);

            JsonOut = gson.toJson(MessageOut);

            this.bufferedWriter.write(JsonOut);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
            JsonIn = this.bufferedReader.readLine();
            MessageIn = gson.fromJson(JsonIn, Message.class);

            System.out.println(MessageIn.getMessageType());

            if(MessageIn.getMessageType().equals(MessageType.MESSAGE_LOGIN_SUCCESS)){
                System.out.println("Log in successfully!");
                Next = WindowType.HALL;
                // then come to hall()
            }else if(MessageIn.getMessageType().equals(MessageType.MESSAGE_LOGIN_FAIL)){
                System.out.println(MessageIn.getReason());
                Next = WindowType.MAINMENU;
            }else{
                Next = WindowType.MAINMENU;
            }
            return Next;
                // then while
        } catch (Exception e) {
            System.out.println(e);
            // TODO: handle exception
            return Next;
        }
        
    }

    public String Signup() {
        try {
            System.out.println("UserId: ");
            PlayerId = scanner.nextLine();
            System.out.println("PassWd: ");
            PassWd = scanner.nextLine();

            MessageOut.setPlayerId(PlayerId);
            MessageOut.setPassWord(PassWd);
            MessageOut.setMessageType(MessageType.MESSAGE_SIGNUP_REQUEST);
            JsonOut = gson.toJson(MessageOut);

            this.bufferedWriter.write(JsonOut);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();

            JsonIn = this.bufferedReader.readLine();
            MessageIn = gson.fromJson(JsonIn, Message.class);

            if(MessageIn.getMessageType().equals(MessageType.MESSAGE_SIGNUP_SUCCESS)) {
                System.out.println("Sign up successfully!");
                Next = WindowType.MAINMENU;
                // 
            }else if(MessageIn.getMessageType().equals(MessageType.MESSAGE_SIGNUP_FAIL)) {
                System.out.println(MessageIn.getReason());
                Next = WindowType.SIGNUP;
            }
            return Next;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
            return Next;
        }
        
    }

    public String Hall() {
        try {
            System.out.println("Hall: ");
            System.out.println("1 Select Room");
            System.out.println("2 Create Room");
    
            String Choice = scanner.nextLine();

            if(Choice.equals("1")) {
                System.out.println("Room Id: ");
                RoomId = scanner.nextLine();
    
                MessageOut.setRoomId(RoomId);
                MessageOut.setMessageType(MessageType.MESSAGE_SELECT_ROOM);
                
                JsonOut = gson.toJson(MessageOut);
                this.bufferedWriter.write(JsonOut);
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();
                
                JsonIn = this.bufferedReader.readLine();
                MessageIn = gson.fromJson(JsonIn, Message.class);
                if(MessageIn.getMessageType().equals(MessageType.MESSAGE_ROOM_INFO)) {
                    RoomPlayerInfo = MessageIn.getPlayerInfo();
                    RoomReadyInfo = MessageIn.getReadyInfo();
                    IsModify = true;
                    Next = WindowType.ROOM;
                }else if(MessageIn.getMessageType().equals(MessageType.MESSAGE_SELECT_ROOM_FAIL)) {
                    System.out.println(MessageIn.getReason());
                    Next = WindowType.HALL;
                }
    
            }else if(Choice.equals("2")) {
                MessageOut.setMessageType(MessageType.MESSAGE_CREATE_ROOM);

                JsonOut = gson.toJson(MessageOut);
                this.bufferedWriter.write(JsonOut);
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();
                
                JsonIn = this.bufferedReader.readLine();
                MessageIn = gson.fromJson(JsonIn, Message.class);

                if(MessageIn.getMessageType().equals(MessageType.MESSAGE_ROOM_INFO)){
                    RoomId = MessageIn.getRoomId();
                    RoomPlayerInfo = MessageIn.getPlayerInfo();
                    RoomReadyInfo = MessageIn.getReadyInfo();
                    IsModify = true;
                }
                    
                Next = WindowType.ROOM;
            }
            return Next;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
            return Next;
        }

    }

    public String Room() {
        try {
            while(true) {
                JsonIn = this.bufferedReader.readLine();
                if (JsonIn != null) {
                    MessageIn = gson.fromJson(JsonIn, Message.class);
                    if (MessageIn.getMessageType().equals(MessageType.MESSAGE_ROOM_INFO)) {
                        IsModify = true;
                        RoomPlayerInfo = MessageIn.getPlayerInfo();
                        RoomReadyInfo = MessageIn.getReadyInfo();
                        System.out.println(RoomReadyInfo);
                    } else if (MessageIn.getMessageType().equals(MessageType.MESSAGE_GAME_START_SOON)) {
                        Next = WindowType.GAME;
                        break;
                    }
                }
                if (IsModify) {
                    System.out.println("RoomId: " + RoomId);
                    System.out.println("Player Info: " + RoomPlayerInfo);
                    System.out.println("Ready Info: " + RoomReadyInfo);
    
                    if(IsReady) {
                        System.out.println("1 Not Ready");
                    }else{
                        System.out.println("1 Ready");
                    }
                    System.out.println("2 Exit");
                }
    
                String Choice = scanner.nextLine();
    
                if (Choice.equals("1")) {
                    if (!IsReady) {
                        MessageOut.setMessageType(MessageType.MESSAGE_PLAYER_READY);
                        IsReady = true;
                    } else {
                        MessageOut.setMessageType(MessageType.MESSAGE_PLAYER_CANCEL_READY);
                        IsReady = false;
                    }
                    JsonOut = gson.toJson(MessageOut);
                    this.bufferedWriter.write(JsonOut);
                    this.bufferedWriter.newLine();
                    this.bufferedWriter.flush();
                } else if(Choice.equals("2")) {
                    Next = WindowType.HALL;
                    break;
                }
            }
            return Next;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
            return Next;
        }
    }
    public String Game() {
        System.out.println("in Game");
        return Next;
    }
}
