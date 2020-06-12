import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;


public class Comunication {

    private Socket socket;
    private PrintWriter output;
    private BufferedReader  input;
    private String name;
    private String folderPath;


    public Comunication(Socket socket, String name, String folderPath) {
        this.socket = socket;
        this.name = name;
        this.folderPath = folderPath;

        if (Files.notExists(Paths.get(folderPath)) && name == "Server") {
            new File(folderPath).mkdir();
        }

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
                    saveFile(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void send(String message) {
        this.output.println(message);
    }

    private void deleteFile(String filename) {
        try {
            File file = new File(this.folderPath + filename);
            file.delete();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sendFile(String filename) {
        try {
            Path filePath = Paths.get(folderPath + filename);

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

    private void saveFile(String message) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jo = (JSONObject) parser.parse(message);
            File file = new File(this.folderPath + "/" + jo.get("fileName"));
            String byteString = (String)jo.get("file");
            byte[] bytes = Base64.getDecoder().decode(byteString);
            OutputStream os = new FileOutputStream(file);
            os.write(bytes);
            os.close();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
