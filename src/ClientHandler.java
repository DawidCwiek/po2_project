import java.net.Socket;

public class ClientHandler extends Thread{
    private Socket socket;
    private String userName;
    private Comunication comunication;


    public ClientHandler(Socket socket, String userName, String path) {
        this.socket = socket;
        this.userName = userName;
        this.comunication = new Comunication(this.socket, "Server", path + this.userName);
    }

    @Override
    public void run() {
        this.comunication.listen();
    }
}
