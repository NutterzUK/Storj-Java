package sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.UniformInterfaceException;
import javafx.application.Platform;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.TaskProgressView;
import storj.io.client.DefaultStorjClient;
import storj.io.client.StorjClient;
import storj.io.client.StorjConfiguration;
import storj.io.restclient.model.Bucket;
import storj.io.restclient.model.BucketEntry;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Controller implements Initializable{

    @FXML
    ListView<Bucket> bucketView;

    @FXML
    TableView<BucketEntryTableView> tableView;

    @FXML
    TableColumn<BucketEntryTableView, String> fileName;

    @FXML
    TableColumn<BucketEntryTableView, String> size;

    @FXML
    TableColumn<BucketEntryTableView, String> type;

    @FXML
    TableColumn<BucketEntryTableView, String> id;

    @FXML
    TaskProgressView<Task<Void>> progressView;

    private ObservableList<Bucket> bucketList;

    private ObservableList<BucketEntryTableView> tableValues;

    private Preferences preferences;

    private StorjClient client;

    private final ExecutorService exec = Executors.newFixedThreadPool(1, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t ;
    });

    public void initialize(URL location, ResourceBundle resources) {

        loadConfigurationFromFile();

        bucketView.setCellFactory(new BucketListCellFactory());
        bucketList = bucketView.getItems();

        fileName.setCellValueFactory(
                new PropertyValueFactory<BucketEntryTableView,String>("fileName")
        );

        size.setCellValueFactory(
                new PropertyValueFactory<BucketEntryTableView,String>("size")
        );

        type.setCellValueFactory(
                new PropertyValueFactory<BucketEntryTableView,String>("type")
        );

        id.setCellValueFactory(
                new PropertyValueFactory<BucketEntryTableView,String>("id")
        );

        tableValues = tableView.getItems();

        bucketView.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if(newValue != null) {
                        runTask(updateFilesTask(newValue.getId()), "Update files list.");
                    }});

        progressView.setGraphicFactory(task -> new ImageView("images/storj-small.jpeg"));

    }

    @FXML
    private void handleOpenSite(){
        Dialog<Site> dialog = new ChoiceDialog<Site>(preferences.getSites().get(0), preferences.getSites());
        dialog.setTitle("Open account");
        dialog.setHeaderText("Select the account to switch to.");

        Optional<Site> result = dialog.showAndWait();

        if (result.isPresent()) {
            String pw = promptForPassword();
            if(pw != null) {
                updateStorjConfiguration(result.get(), pw);
            }
        }
    }

    @FXML
    private void handleDeleteSite(){
        Dialog<Site> dialog = new ChoiceDialog<Site>(preferences.getSites().get(0), preferences.getSites());
        dialog.setTitle("Delete account");
        dialog.setHeaderText("Select the account to delete.");
        Optional<Site> result = dialog.showAndWait();
        if (result.isPresent()) {
            preferences.getSites().remove(result.get());
            writePreferences();
        }
    }

     private boolean checkAccountOpen(){
        if(client == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("First select an account.");
            String s = "You should first select an account. Click Accounts -> New\n or Accounts -> Open.";
            alert.setContentText(s);
            alert.showAndWait();
            return false;
        }
        return true;
    }

    @FXML
    private boolean handleAbout(){
        if(client == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Storj browser");
            String s = "This is a very early version of the Storj Browser.\n\n Version: 0.0.1-Alpha";
            alert.setContentText(s);
            alert.showAndWait();
            return false;
        }
        return true;
    }


    private void firstSelectPrompt(String whatToSelect){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("First select the " + whatToSelect + ".");
        String s = "First select the " + whatToSelect + ".";
        alert.setContentText(s);
        alert.showAndWait();
    }

    @FXML
    private void handleAddNewSite(){
        Dialog<Site> dialog = new Dialog<>();
        dialog.setTitle("Add new account");
        dialog.setHeaderText("Add a new account. \nNote, Storj Browser does not store your password.");
        dialog.setResizable(true);

        Label label0 = new Label("Account name (Anything): ");
        Label label1 = new Label("Bridge Host: ");
        Label label2 = new Label("Username (Email): ");
        Label label3 = new Label("Password: ");

        TextField text0 = new TextField();
        TextField text1 = new TextField();
        TextField text2 = new TextField();
        TextField text3 = new PasswordField();

        text1.setText("https://api.storj.io");
        GridPane grid = new GridPane();

        grid.add(label0, 1, 1);
        grid.add(text0, 2, 1);

        grid.add(label1, 1, 2);
        grid.add(text1, 2, 2);
        grid.add(label2, 1, 3);
        grid.add(text2, 2, 3);
        grid.add(label3, 1, 4);
        grid.add(text3, 2, 4);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        dialog.setResultConverter((button) -> {
            if (button == buttonTypeOk) {
                return new Site(text1.getText(), text2.getText(), text3.getText(), text0.getText());
            }
            return null;
        });

        Optional<Site> result = dialog.showAndWait();

        if (result.isPresent()) {
            preferences.getSites().add(result.get());
            writePreferences();
            updateStorjConfiguration(result.get(), text3.getText());
            runTask(updateBucketsTask(), "Update bucket list.");
        }
    }

    private void updateStorjConfiguration(Site site, String password) {
        StorjConfiguration configuration = new StorjConfiguration(password, site.getUsername(), password);
        client = new DefaultStorjClient(configuration);
        runTask(updateBucketsTask(), "Update bucket list.");
    }

    private void loadConfigurationFromFile(){
        preferences = loadPreferences();
    }

    private String promptForPassword() {
        PasswordInputDialog dialog = new PasswordInputDialog("Password");

        dialog.setTitle("Enter password");
        dialog.setHeaderText("This program does not store your password. \nPlease enter it here to access your storj buckets.");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }

    private void writePreferences(){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String settingFilePath = System.getProperty("user.home") + File.separator + ".storjBrowser" + File.separator + "settings.json";
        try {
            Path pathToFile = new File(settingFilePath).toPath();

            if(!Files.exists(pathToFile)){
                Files.createDirectories(pathToFile.getParent());
            }

            Files.write(pathToFile, gson.toJson(preferences).getBytes(),
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Preferences loadPreferences() {
        Gson gson = new Gson();

        String settingFilePath = System.getProperty("user.home") + File.separator + ".storjBrowser" + File.separator + "settings.json";
        File file = new File(settingFilePath);
        Path pathToFile = file.toPath();
        if(file.exists()){
            try {
                preferences = gson.fromJson(new String(Files.readAllBytes(pathToFile)), Preferences.class);
            } catch (IOException e) {
                e.printStackTrace();
            };
        }else{
            preferences = new Preferences();
            handleAddNewSite();
            writePreferences();
        }
        return preferences;
    }

    @FXML
    private void handleFileDownload(ActionEvent event){
        if(checkAccountOpen()) {
            if (bucketView.getSelectionModel().getSelectedItem() == null || tableView.getSelectionModel().getSelectedItem() == null) {
                firstSelectPrompt("file");
            }else {
                String bucketId = bucketView.getSelectionModel().getSelectedItem().getId();
                Stage stage = (Stage) bucketView.getScene().getWindow();
                final FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save File");
                fileChooser.setInitialFileName(tableView.getSelectionModel().getSelectedItem().getFileName());
                File file = fileChooser.showSaveDialog(stage);
                if (file != null) {
                    if (tableView.getSelectionModel().getSelectedItem() == null) {
                        firstSelectPrompt("file");
                    } else {
                        runTask(downloadFileTask(file, bucketId, tableView.getSelectionModel().getSelectedItem().getId()), "Download file.");
                    }
                }
            }
        }
    }

    private void runTask(Task<Void> task, String name){
        task.setOnFailed(e -> {
          showException(e.getSource().getException());
        });
        Platform.runLater(()-> {
            exec.submit(task);
            progressView.getTasks().add(task);
        });
    }

    @FXML
    private void handleFileUpload(ActionEvent event){
        if(checkAccountOpen()) {
            if(bucketView.getSelectionModel().getSelectedItem() == null){
                firstSelectPrompt("bucket");
            }else {
                String bucketId = bucketView.getSelectionModel().getSelectedItem().getId();
                Stage stage = (Stage) bucketView.getScene().getWindow();
                final FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    runTask(uploadFileTask(file, bucketId), "Upload file.");
                }
            }
        }
    }


    private Task<Void> downloadFileTask(File file, String bucketId, String fileId){
        return new Task<Void>() {

            {this.updateTitle("Downloading to " + file.getName());}

            @Override
            public Void call() throws Exception {
                client.downloadFile(bucketId, fileId, file);
                runTask(updateFilesTask(bucketId), "Update file list.");
                return null;
            }
        };
    }


    private Task<Void> uploadFileTask(File file, String bucketId){
        return new Task<Void>() {
            @Override
            public Void call() throws Exception {
                {this.updateTitle("Uploading file " + file.getName());}
                client.uploadFile(file, bucketId);
                runTask(updateFilesTask(bucketId), "Update file list.");
                return null;
            }
        };
    }

    public void createList() {
        runTask(updateBucketsTask(), "Update bucket list.");
    }

    private Task<Void> updateFilesTask(String bucketId){
        return new Task<Void>() {
            @Override
            public Void call() throws Exception {
                {this.updateTitle("Updating list of files");}

                List<BucketEntry> bucketEntries = client.listFiles(bucketId);
                Platform.runLater(()-> {
                    tableValues.clear();
                    List<BucketEntryTableView> views = bucketEntries.stream().map(bucketEntry -> new BucketEntryTableView(bucketEntry)).collect(Collectors.toList());
                    tableValues.addAll(views);
                });
                return null;
            }
        };
    }

    private Task<Void> updateBucketsTask() {
        return new Task<Void>() {
            @Override
            public Void call() throws Exception {
                {this.updateTitle("Refreshing bucket list.");}

                final List<Bucket> buckets = client.listBuckets();
                Platform.runLater(()->{
                    bucketList.clear();
                    bucketList.addAll(buckets);
                });
                return null;
            }
        };
    }

    private Task<Void> deleteBucketsTask(String bucketId) {
        return new Task<Void>() {
            @Override
            public Void call() throws Exception {
                {this.updateTitle("Deleting bucket " + bucketId + ".");}
                client.deleteBucket(bucketId);
                Platform.runLater(()-> {
                    tableValues.clear();
                    bucketList.clear();
                    runTask(updateBucketsTask(), "Update Bucket list.");
                });
                return null;
            }
        };
    }

    @FXML
    private void handleDeleteBucket(){
        if(checkAccountOpen()) {
            if(bucketView.getSelectionModel().getSelectedItem() == null ) {
                firstSelectPrompt("bucket");
            } else {
                String bucketId = bucketView.getSelectionModel().getSelectedItem().getId();
                runTask(deleteBucketsTask(bucketId), "Delete bucket " + bucketId);
            }
        }
    }

    @FXML
    private void handleCreateBucket(){
        if(checkAccountOpen()) {

            TextInputDialog dialog = new TextInputDialog("Bucket Name");
            dialog.setTitle("Create bucket");
            dialog.setHeaderText("Enter the name of the bucket you would like to create:");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                runTask(createBucketsTask(result.get()), "Create bucket " + result.get());
            }
        }
    }

    private Task<Void> createBucketsTask(String bucketName) {
        return new Task<Void>() {
            @Override
            public Void call() throws Exception {
                {this.updateTitle("Creating bucket " + bucketName + ".");}
                client.createBucket(bucketName);
                tableValues.clear();
                bucketList.clear();
                runTask(updateBucketsTask(), "Update bucket list.");
                return null;
            }
        };
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if(checkAccountOpen()) {
            if (bucketView.getSelectionModel().getSelectedItem() == null || tableView.getSelectionModel().getSelectedItem() == null) {
                firstSelectPrompt("file");
            }else{
                runTask(deleteFileTask(tableView.getSelectionModel().getSelectedItem().getId()), "Deleting file " + tableView.getSelectionModel().getSelectedItem().getFileName());
            }
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        if(checkAccountOpen()) {
            if (bucketView.getSelectionModel().getSelectedItem() == null) {
                firstSelectPrompt("bucket");
            } else {
                String bucketId = bucketView.getSelectionModel().getSelectedItem().getId();
                runTask(updateFilesTask(bucketId), "Update files list.");
            }
        }
    }


    private Task<Void> deleteFileTask(String fileId) {
        return new Task<Void>() {
            @Override
            public Void call() throws Exception {
                {this.updateTitle("Deleting file " + fileId + ".");}
                String bucketId = bucketView.getSelectionModel().getSelectedItem().getId();
                client.deleteFile(bucketId, fileId);
                runTask(updateFilesTask(bucketId), "Update files list.");
                return null;
            }
        };
    }

    private void showException(Throwable throwable) {
        String genericError = "Please feel to report it as an issue on github with the stack trace below.";
        String errorToShow = genericError;
        String restResponse = null;

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        if(throwable instanceof UniformInterfaceException){
            UniformInterfaceException restException = (UniformInterfaceException) throwable;
            int status = restException.getResponse().getStatus();
            String reason = restException.getResponse().getStatusInfo().getReasonPhrase();
            errorToShow = "The bridge returned a status of " + status + " with reason: \"" + reason +
                    "\". An exception trace is below, and if you believe this is an error, please report it on GitHub.";
            restResponse = restException.getResponse().toString();
        }

        alert.setHeaderText("Something went wrong.");
        alert.setContentText(errorToShow);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String exceptionText = sw.toString();

        if(restResponse != null){
            exceptionText += "\n\nResponse from Server:\n\n" + restResponse;
        }

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }
}


