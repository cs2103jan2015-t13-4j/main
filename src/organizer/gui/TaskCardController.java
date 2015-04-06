package organizer.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

public class TaskCardController extends Region {
	private TaskItem task;
	
	@FXML
	private Label nameLabel;
	@FXML
	private Label startTimeLabel;
	@FXML
	private Label endTimeLabel;
	@FXML
	private Label idLabel;
	
	public TaskCardController() throws IOException {
		this(true);	// default size is small
	}

	public TaskCardController(boolean largeSize) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
        		largeSize ? "TaskCardLarge.fxml" : "TaskCardSmall.fxml"));
        fxmlLoader.setController(this);
        this.getChildren().add(fxmlLoader.load());
	}
	
	@FXML
	private void initialize() {
	}
	
	public void loadTask(TaskItem task) {
		this.task = task;
		nameLabel.textProperty().bind(task.taskNameProperty());
		idLabel.setText(Integer.toString(task.getTaskIndex()));
		startTimeLabel.setText(
				String.format(
						"from %s %s, %d, %d:%d",
						task.getTaskStartDate().getDayOfMonth(),
						task.getTaskStartDate().getMonth().getValue(),
						task.getTaskStartDate().getYear(),
						task.getTaskStartTime().getHour(),
						task.getTaskStartTime().getMinute()));
		endTimeLabel.setText(
				String.format(
						"to %s %s, %d, %d:%d",
						task.getTaskEndDate().getDayOfMonth(),
						task.getTaskEndDate().getMonth().getValue(),
						task.getTaskEndDate().getYear(),
						task.getTaskEndTime().getHour(),
						task.getTaskEndTime().getMinute()));
	}
}
