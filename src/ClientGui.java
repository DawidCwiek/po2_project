import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ClientGui extends Application {

    private ListView<String> filesList = new ListView<String>();
    private ComboBox dropDownUser = new ComboBox();
    private Label status = new Label();
    private Client client;
    public Status statusClass = new Status();

    public void updateFileList() {
        ObservableList<String> items = FXCollections.observableArrayList (client.getFilesList());
        Platform.runLater(() -> {
            filesList.setItems(items);
        });
    }

    public void updateStatus() {
        Platform.runLater(() -> {
            this.status.setText(this.statusClass.status);
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
        updateStatus();
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
            if(filesList.getSelectionModel().getSelectedItem() == null || dropDownUser.getValue() == null)
                new Alert(Alert.AlertType.ERROR, "You must select a user and file!").showAndWait();
            else
                client.comunication.shareFileAction(filesList.getSelectionModel().getSelectedItem(), dropDownUser.getValue().toString());

        });

        setUserShare.getChildren().addAll(selectUser, dropDownUser, dropDownBtn);

        // MAIN BOX
        VBox mainBox = new VBox(5);
        mainBox.getChildren().addAll(statusBox, filesList, setUserShare);


        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Stage is closing");
            if(this.client.comunication != null) this.client.comunication.deleteOnlineUserAction();
            System.exit(0);
        });
        
        Scene scene = new Scene(mainBox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
