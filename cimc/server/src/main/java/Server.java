import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import thread.PlayerThread;


public class Server{
    
    public static void main(String[] args) throws IOException{
        @SuppressWarnings("resource")
        ServerSocket serverSocket =  new ServerSocket(8888);
        Socket client;
        System.out.println("Server start...");
        
        
        while(true){
            System.out.println("Waiting for connect...");
            client = serverSocket.accept();
            System.out.println("One client has been connected...");
            new PlayerThread(client).start();
        }
    }
}