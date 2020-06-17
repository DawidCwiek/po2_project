import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {

    private String path;
    private String name;
    private Integer port;
    private Socket socket;
    private Watcher watcher;
    public List<String> usersList = new ArrayList<>();
    public ClientGui parent;
    private PrintWriter output;
    public Comunication comunication;


    public Client(String userName, String path, Integer port, ClientGui parent) {
        this.name = userName;
        this.path = path;
        this.port = port;
        this.parent = parent;
        this.watcher = new Watcher(path, this);
        connect();
    }

    public void connect() {
        try {
            InetAddress ip = InetAddress.getByName("localhost");
            this.socket = new Socket(ip, this.port);
            output = new PrintWriter(socket.getOutputStream(), true);
            output.println(this.name);
            this.comunication = new Comunication(this.socket, this.name, this.path, this);
            this.comunication.listen();
            this.parent.statusClass.startStatus("online");
            this.parent.updateStatus();
            this.comunication.getFilesInfoAction();
            this.watcher.start();
        } catch (IOException e) {
            this.parent.statusClass.endStatus("online");
            this.parent.updateStatus();
            e.printStackTrace();
        }
    }

    public void synchronizationFiles(JSONArray files) {
        try {
            this.watcher.pause();

            this.parent.statusClass.startStatus("synchronization");
            this.parent.updateStatus();

            File folder = new File(this.path);
            List<File> listOfFiles = new ArrayList<>(Arrays.asList(folder.listFiles()));

            List<String> currentRemote = new ArrayList<>();
            for (Object remote : files) {
                JSONObject remoteFile = (JSONObject) remote;
                currentRemote.add((String) remoteFile.get("name"));
            }

            List<String> currentLocal = new ArrayList<>();
            for (File local : listOfFiles) {
                currentLocal.add(local.getName());
            }

            for (Object remote : files) {
                JSONObject remoteFile = (JSONObject) remote;
                for (File local : listOfFiles) {
                    if (remoteFile.get("name").equals(local.getName())) {
                        if(!remoteFile.get("hash").equals(CheckFile.getCheckSum(local.getAbsolutePath()))) {
                            if((long)remoteFile.get("createTime") > local.lastModified()) {
                                this.comunication.getModificationFile(local.getName());
                            } else {
                                this.comunication.modificationFileAction(local.getName());
                            }
                        }
                        currentLocal.remove(local.getName());
                        currentRemote.remove(remoteFile.get("name"));
                    }
                }
            }

            for(String file : currentLocal) {
                this.comunication.sendFileAction(file);
            }
            for(String file : currentRemote) {
                this.comunication.getFileAction(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.parent.statusClass.endStatus("synchronization");
        this.parent.updateStatus();
        this.watcher.go();
    }

    public List<String> getFilesList() {
        List<String> filesList = new ArrayList<>();

        File folder = new File(this.path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                filesList.add(listOfFiles[i].getName());
            }
        }
        return filesList;
    }

    public List<String> getUserList() {
        return this.usersList;
    }
}

