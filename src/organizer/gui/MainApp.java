package organizer.gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import organizer.logic.*;

public class MainApp extends Application {

    private Stage primaryStage;
    private AnchorPane rootLayout;
    
    private ObservableList<TaskItem> taskData = FXCollections.observableArrayList();
    
    private Logic logic = new Logic();
    private List<Task> tasks;
    
    private MainAppController controller;
    
    public MainApp() throws IOException {
        tasks = logic.loadStorage();
        fillTaskList();
    }
    
    private void fillTaskList() {
        taskData.clear();
        tasks.forEach(task -> taskData.add(new TaskItem(task)));
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Task Organizer");
        
        initRootLayout();
        
        showAllTasks();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    private void initRootLayout() {
        try {
            final FXMLLoader loader = new FXMLLoader();
            final URL url = MainApp.class.getResource("MainApp.fxml");
            loader.setLocation(url);
            rootLayout = (AnchorPane) loader.load();
            
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showAllTasks() {
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public ObservableList<TaskItem> getTaskData() {
        return taskData;
    }
    
    public void performCommand(String commandString) {
        try {
            tasks = logic.executeCommand(commandString);
            fillTaskList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
