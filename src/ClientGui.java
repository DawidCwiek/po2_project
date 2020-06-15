import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ClientGui extends Application {

    private ListView<String> filesList = new ListView<String>();
    private ComboBox dropDownUser = new ComboBox();
    private Label status = new Label();
    private Client client;

    public void updateFileList() {
        ObservableList<String> items = FXCollections.observableArrayList (client.getFilesList());
        Platform.runLater(() -> {
            filesList.setItems(items);
        });
    }

    public void updateStatus(String statusVal) {
        Platform.runLater(() -> {
            this.status.setText(statusVal);
        });
    }


    public  void updateDropDownUser() {
        Platform.runLater(() -> {
            dropDownUser.getItems().setAll(client.getUserList());
        });
    }

    public static void main(String[] args) throws InterruptedException {
        Application.launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parameters params = getParameters();
        List<String> list = params.getRaw();


        // STATUS
        HBox statusBox = new HBox( 5);
        Label statusName = new Label("Status: ");
        updateStatus("connecting");
        statusBox.getChildren().addAll(statusName, status);

        this.client = new Client(list.get(0), list.get(1), 2137, this);

        // LIST
        updateFileList();

        // USER COMBO BOX
        HBox setUserShare = new HBox( 5);
        Label selectUser = new Label("Select user: ");
        updateDropDownUser();
        Button dropDownBtn = new Button("Share File");
        dropDownBtn.setOnAction(event -> {
//            doAction(dropDown.getValue().toString());
            System.out.println(dropDownUser.getValue().toString() + "  " + filesList.getSelectionModel().getSelectedItem() );
//            client.comunication.sendFileAction(filesList.getSelectionModel().getSelectedItem());
            client.comunication.deleteFileAction(filesList.getSelectionModel().getSelectedItem());
        });

        setUserShare.getChildren().addAll(selectUser, dropDownUser, dropDownBtn);

        // MAIN BOX
        VBox mainBox = new VBox(5);
        mainBox.getChildren().addAll(statusBox, filesList, setUserShare);


        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Stage is closing");
            client.comunication.deleteOnlineUserAction();
            System.exit(0);
        });
        
        Scene scene = new Scene(mainBox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
