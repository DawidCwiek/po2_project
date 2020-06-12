import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    private ServerSocket serverSocket;
    private List<String> onlineUsers = new ArrayList<>();
    private String path = "/Users/dawidcwiek/Desktop/serwer_pliki/";

    public Server(Integer port) {
        try {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            Socket socket = null;

            try {
                socket = serverSocket.accept();
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String userName = input.readLine();
                System.out.println("A new client is connected : " + socket.getPort());

                Thread t = new ClientHandler(socket, userName, path);
                t.start();

            }
            catch (IOException e){
                try {
                    serverSocket.close();
                }catch (IOException ec) {
                    ec.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(2137);
            server.run();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

