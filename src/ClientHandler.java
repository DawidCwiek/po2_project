import java.net.Socket;

public class ClientHandler extends Thread{
    public Socket socket;
    public String userName;
    public Comunication comunication;


    public ClientHandler(Socket socket, String userName, String path, Server server) {
        this.socket = socket;
        this.userName = userName;
        this.comunication = new Comunication(this.socket, "Server", path + this.userName, server);
    }

    @Override
    public void run() {
        this.comunication.listen();
    }
}
