import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private String path;
    private String name;
    private Integer port;
    private Socket socket;
    private Watcher watcher;
    private List<String> usersList = new ArrayList<>();
    public ClientGui parent;
    private PrintWriter output;
    public Comunication comunication;


    public Client(String userName, String path, Integer port, ClientGui parent) {
        this.name = userName;
        this.path = path;
        this.port = port;
        this.parent = parent;
        this.watcher = new Watcher(path, this);
        this.watcher.start();
        connect();
    }

    public void connect() {
        try {
            InetAddress ip = InetAddress.getByName("localhost");
            this.socket = new Socket(ip, this.port);
            output = new PrintWriter(socket.getOutputStream(), true);
            output.println(this.name);
            this.comunication = new Comunication(this.socket, this.name, this.path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getFilesList() {
        List<String> filesList = new ArrayList<>();

        File folder = new File(this.path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                filesList.add(listOfFiles[i].getName());
                System.out.println(listOfFiles[i].getName());
            }
        }
        return filesList;
    }

    public List<String> getUserList() {
        this.usersList.add("Mexican Peso");
        this.usersList.add("Canadian Dollar");
        return this.usersList;
    }
}

