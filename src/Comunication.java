import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class Comunication {

    private Socket socket;
    private PrintWriter output;
    private BufferedReader  input;
    private String name;
    private String folderPath;
    private Server server;
    private Client client;
    public List<String> usersList = new ArrayList<>();


    public Comunication(Socket socket, String name, String folderPath, Server server) {
        this.socket = socket;
        this.name = name;
        this.folderPath = folderPath;

        if (Files.notExists(Paths.get(folderPath))) {
            new File(folderPath).mkdir();
        }
        this.server = server;

        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Comunication(Socket socket, String name, String folderPath, Client client) {
        this.socket = socket;
        this.name = name;
        this.folderPath = folderPath;
        this.client = client;

        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        new Thread(() -> {
            while (true) {
                try {
                    String message = this.input.readLine();
                    if(message == null) break;
                    System.out.println(message);
                    actionManager(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }

    public void send(String message) {
       this.output.println(message);
    }

    public void getFilesInfoAction() {
        JSONObject jo = new JSONObject();
        jo.put("name", this.name);
        jo.put("action", "GET_FILES_INFO");
        send(jo.toString());
    }

    public void getFilesInfo() {
        File folder = new File(this.folderPath);
        File[] listOfFiles = folder.listFiles();

        JSONObject jo = new JSONObject();
        jo.put("name", this.name);
        jo.put("action", "SEND_FILES_INFO");
        JSONArray joArray = new JSONArray();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                JSONObject file = new JSONObject();
                file.put("name", listOfFiles[i].getName());
                file.put("createTime", listOfFiles[i].lastModified());
                file.put("hash", CheckFile.getCheckSum(listOfFiles[i].getAbsolutePath()));
                joArray.add(file);
            }
        }
        jo.put("files", joArray);
        send(jo.toJSONString());
    }

    public void readFilesInfo(String message) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jo = (JSONObject) parser.parse(message);
            this.client.synchronizationFiles((JSONArray)jo.get("files"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void deleteFile(String filename) {
        try {
            File file = new File(this.folderPath + "/" + filename);
            file.delete();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void deleteFileAction(String filename) {
        try {
            JSONObject jo = new JSONObject();
            jo.put("name", this.name);
            jo.put("action", "DELETE_FILE");
            jo.put("fileName", filename);
            send(jo.toString());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getFileAction(String filename) {

        JSONObject jo = new JSONObject();
        jo.put("name", this.name);
        jo.put("action", "GET_FILE");
        jo.put("fileName", filename);

        send(jo.toString());

    }

    public void sendFileAction(String filename) {
        try {
            Path filePath = Paths.get(folderPath + "/" + filename);

            byte[] array = Files.readAllBytes(filePath);
            JSONObject jo = new JSONObject();
            jo.put("name", this.name);
            jo.put("action", "SEND_FILE");
            jo.put("fileName", filename);
            jo.put("file", Base64.getEncoder().encodeToString(array));

            send(jo.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void modificationFileAction(String filename) {
        try {
            Path filePath = Paths.get(folderPath + "/" + filename);

            byte[] array = Files.readAllBytes(filePath);
            JSONObject jo = new JSONObject();
            jo.put("name", this.name);
            jo.put("action", "MODIFICATION_FILE");
            jo.put("fileName", filename);
            jo.put("file", Base64.getEncoder().encodeToString(array));

            send(jo.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getModificationFile(String filename) {
        JSONObject jo = new JSONObject();
        jo.put("name", this.name);
        jo.put("action", "GET_MODIFICATION_FILE");
        jo.put("fileName", filename);

        send(jo.toString());
    }

    private void shareSave(String message) {
        try {
            if(this.client != null) {
                this.client.parent.statusClass.startStatus("downloadFile");
                this.client.parent.updateStatus();
            }
            JSONParser parser = new JSONParser();
            JSONObject jo = (JSONObject) parser.parse(message);
            File file = new File(this.server.path + jo.get("name") + "/" + jo.get("fileName"));
            String byteString = (String)jo.get("file");
            byte[] bytes = Base64.getDecoder().decode(byteString);
            OutputStream os = new FileOutputStream(file);
            os.write(bytes);
            os.close();

            if(this.server != null) {
                this.server.syncUsers((String)jo.get("name"), this.socket);
            }

            if(this.client != null) {
                this.client.parent.statusClass.endStatus("downloadFile");
                this.client.parent.updateStatus();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile(String message) {
        try {
            if(this.client != null) {
                this.client.parent.statusClass.startStatus("downloadFile");
                this.client.parent.updateStatus();
            }
            JSONParser parser = new JSONParser();
            JSONObject jo = (JSONObject) parser.parse(message);
            File file = new File(this.folderPath + "/" + jo.get("fileName"));
            String byteString = (String)jo.get("file");
            byte[] bytes = Base64.getDecoder().decode(byteString);
            OutputStream os = new FileOutputStream(file);
            os.write(bytes);
            os.close();

            if(this.server != null) {
                this.server.syncUsers((String)jo.get("name"), this.socket);
            }

            if(this.client != null) {
                this.client.parent.statusClass.endStatus("downloadFile");
                this.client.parent.updateStatus();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteOnlineUserAction() {
        JSONObject jo = new JSONObject();
        jo.put("name", this.name);
        jo.put("action", "DELETE_ONLINE_USER");
        send(jo.toString());
    }


    public void sendUserListAction() {
        JSONObject jo = new JSONObject();
        jo.put("name", this.name);
        jo.put("action", "UPDATE_USER_LIST");
        jo.put("userList", this.server.getOnlineUsers());
        send(jo.toString());
    }

    private void updateUserList(String message) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jo = (JSONObject) parser.parse(message);
            this.usersList = (List<String>) jo.get("userList");
            if(this.client != null) {
                this.client.usersList = this.usersList;
                this.client.parent.updateDropDownUser();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void shareFileAction(String filename, String userName) {
        try {
            Path filePath = Paths.get(folderPath + "/" + filename);

            byte[] array = Files.readAllBytes(filePath);
            JSONObject jo = new JSONObject();
            jo.put("name", userName);
            jo.put("action", "SHARE_FILE");
            jo.put("fileName", filename);
            jo.put("file", Base64.getEncoder().encodeToString(array));

            send(jo.toString());
            System.out.println(jo.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void actionManager(String message) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jo = (JSONObject) parser.parse(message);
            String action = (String)jo.get("action");

            switch(action) {
                case "SEND_FILE":
                    saveFile(message);
                    break;
                case "GET_FILE":
                    sendFileAction((String)jo.get("fileName"));
                    break;
                case "DELETE_FILE":
                    deleteFile((String)jo.get("fileName"));
                    break;
                case "MODIFICATION_FILE":
                    deleteFile((String)jo.get("fileName"));
                    saveFile(message);
                    break;
                case "GET_MODIFICATION_FILE":
                    modificationFileAction((String)jo.get("fileName"));
                    break;
                case "DELETE_ONLINE_USER":
                    this.server.deleteOnlineUser(this.socket);
                    break;
                case "UPDATE_USER_LIST":
                    updateUserList(message);
                    break;
                case "GET_FILES_INFO":
                    getFilesInfo();
                    break;
                case "SEND_FILES_INFO":
                    readFilesInfo(message);
                    break;
                case "SHARE_FILE":
                    shareSave(message);
                    break;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
