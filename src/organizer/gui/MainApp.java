package organizer.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import organizer.logic.*;
import organizer.parser.*;

//@author A0113627L
public class MainApp extends Application {
	private static final Logger LOGGER_MainApp = Logger.getLogger(MainApp.class.getName());
	private static final String RESOURCE_MAINAPP_FXML = "MainApp.fxml";
	private static final String RESOURCE_APP_TITLE = "MnemoniCal";

    private Stage primaryStage;
    private AnchorPane rootLayout;
    
    private List<TaskItem> taskData = new ArrayList<>();
    private String currentCommandStatus = "";
    
    private CommandParser commandParser = new CommandParser();
    private List<Task> tasks;
    
    private MainAppController controller;
    
    private double xOffset;
    private double yOffset;

    public MainApp() throws IOException {
        tasks = commandParser.loadStorage();
        assert tasks != null;
        LOGGER_MainApp.info(String.format("INIT: load %d tasks", tasks.size()));
        fillTaskList();
    }
    
    private void fillTaskList() {
        taskData.clear();
        int counter = 0;
        for (Task task : tasks) {
        	taskData.add(new TaskItem(task, ++counter));
        }
    }
    
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle(RESOURCE_APP_TITLE);
        primaryStage.setResizable(false);
        
        initRootLayout();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    private void attachDraggableEvent() {
    	rootLayout.setOnMousePressed(event -> {
    		xOffset = event.getSceneX();
    		yOffset = event.getSceneY();
    	});
    	rootLayout.setOnMouseDragged(event -> {
    		primaryStage.setX(event.getScreenX() - xOffset);
    		primaryStage.setY(event.getScreenY() - yOffset);
    	});
    }
    
    private void initRootLayout() throws IOException {
        try {
            final FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(RESOURCE_MAINAPP_FXML));
            rootLayout = (AnchorPane) loader.load();
            assert rootLayout != null;
            attachDraggableEvent();
            
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
        	LOGGER_MainApp.throwing(FXMLLoader.class.getName(), "load", e);
        	throw e;
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public List<TaskItem> getTaskData() {
        return taskData;
    }
    
    public String getCurrentCommandStatus() {
    	return currentCommandStatus;
    }
    
    public ResultSet performCommand(String commandString) throws IOException {
        try {
        	final ResultSet returnResult = commandParser.executeCommand(commandString);
            tasks = returnResult.getReturnList();
            assert tasks != null;
            currentCommandStatus = returnResult.getOpStatus();
            fillTaskList();
            return returnResult;
        } catch (IOException e) {
        	LOGGER_MainApp.throwing(CommandParser.class.getName(), "executeCommand", e);
        	throw e;
        }
    }
}
