import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    private ServerSocket serverSocket;
    private List<ClientHandler> onlineUsers = new ArrayList<>();
    public String path = "/Users/dawidcwiek/Desktop/serwer_pliki/";
    private ServerGui parent;

    public Server(Integer port, ServerGui parent) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.parent = parent;
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void deleteOnlineUser(Socket socket) {
        for(ClientHandler client : this.onlineUsers) {
           if(client.socket == socket) {
               onlineUsers.remove(client);
               break;
           }
        }
        refreshUsers();
        System.out.println(onlineUsers.toString());
        this.parent.refreshUserList();
    }

    public List<String> getOnlineUsers() {
        List<String> users = new ArrayList<>();
        for(ClientHandler client : this.onlineUsers) {
            if(users.indexOf(client.userName) == -1)
                users.add(client.userName);
        }
        return users;
    }

    private void refreshUsers() {
        for(ClientHandler client : this.onlineUsers) {
           client.comunication.sendUserListAction();
        }
    }

    public List<String> getUserFile(String user) {
        List<String> filesList = new ArrayList<>();

        File folder = new File(this.path + user);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                filesList.add(listOfFiles[i].getName());
            }
        }
        return filesList;
    }

    public void syncUsers(String name, Socket socket) {
        for(ClientHandler client : this.onlineUsers) {
            if(client.userName.equals(name) && !client.socket.equals(socket)) {
                System.out.println(name);
                client.comunication.getFilesInfo();
            }
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
                ClientHandler clientHandler = new ClientHandler(socket, userName, path, this);
                onlineUsers.add(clientHandler);
                Thread t = clientHandler;
                t.start();
                refreshUsers();
                System.out.println(onlineUsers.toString());
                this.parent.refreshUserList();
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

//    public static void main(String[] args) {
//        try {
//            Server server = new Server(2137, null);
//            server.run();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}

