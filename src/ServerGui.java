import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerGui extends Application {

    private ListView<String> filesList = new ListView();
    private ListView activeUsersList = new ListView();
    private ListView userFile = new ListView();
    private Server server;

    public void refreshUserList() {
        ObservableList<String> items = FXCollections.observableList(server.getOnlineUsers());
        Platform.runLater(() -> {
            this.activeUsersList.setItems(items);
        });
    }

    public void refreshFileList(String user) {
        ObservableList<String> items = FXCollections.observableList(server.getUserFile(user));
        Platform.runLater(() -> {
            this.userFile.setItems(items);
        });
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            this.server = new Server(2137, this);
            this.server.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        HBox mainBox = new HBox(5);

        //LEFT
        VBox leftBox = new VBox(5);

        Label leftLabel = new Label("Users Online:");

        refreshUserList();
        leftBox.getChildren().addAll(leftLabel, this.activeUsersList);


        activeUsersList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                refreshFileList((String)activeUsersList.getSelectionModel().getSelectedItem());
            }
        });


        //RIGHT
        VBox rightBox = new VBox(5);
        Label rightLabel = new Label("User file:");
        rightBox.getChildren().addAll(rightLabel, this.userFile);

        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });

        mainBox.getChildren().addAll(leftBox, rightBox);
        Scene scene = new Scene(mainBox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) throws InterruptedException {
        Application.launch(args);
    }

}
