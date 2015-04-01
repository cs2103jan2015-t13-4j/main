package organizer.storage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import organizer.logic.Task;

/**
* File Format
* id: ...
* name: ...
* date: ...
* description: ...
* ----
*/

public class Storage {

	public static final String defaultFileName = "storage.txt";

	private static final String nameFieldIdentifier = "name: ";
	private static final String dueDateFieldIdentifier = "duedate: ";
	private static final String startTimeFieldIdentifier = "start: ";
	private static final String endTimeFieldIdentifier = "end: ";
	private static final String statusFieldIdentifier = "status: ";
	private static final String taskIdFieldIdentifier = "id: ";
	private static final String priorityFieldIdentifier = "priority: ";

	private static final String endIdentifier = "----";
	
	/**
	 * readFile
	 * @return ArrayList<Task> a list of tasks
	 * @throws IOException
	 */
	public ArrayList<Task> readFile() throws IOException {
		return readFile(defaultFileName);
	}
	
	public ArrayList<Task> readFromStream(InputStream in) throws IOException {
		ArrayList<Task> taskList = new ArrayList<Task>();
		try (Scanner sc = new Scanner(in)) {
			Task task = new Task();
			boolean begin = false;
			while(sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.startsWith(nameFieldIdentifier)) {
					begin = true;
					task.setTaskName(line.substring(nameFieldIdentifier.length()));
				} else if (line.startsWith(dueDateFieldIdentifier)) {
					begin = true;
					task.setDueDate(LocalDate.parse(line.substring(dueDateFieldIdentifier.length())));
				} else if(line.startsWith(startTimeFieldIdentifier)) {
					begin = true;
					task.setStartTime(LocalTime.parse(line.substring(startTimeFieldIdentifier.length())));
				} else if(line.startsWith(endTimeFieldIdentifier)) {
					begin = true;
					task.setEndTime(LocalTime.parse(line.substring(endTimeFieldIdentifier.length())));
				}else if (line.startsWith(statusFieldIdentifier)) {
					begin = true;
					task.setTaskStatus(line.substring(statusFieldIdentifier.length()));
				} else if (line.equals(endIdentifier)) {
					if (begin) {
						taskList.add(task);
						task = new Task();
						begin = false;
					}
				} else if (line.startsWith(taskIdFieldIdentifier)) {
					begin = true;
					task.setTaskID(Integer.parseInt(line.substring(taskIdFieldIdentifier.length())));
				} else if (line.startsWith(priorityFieldIdentifier)) {
					begin = true;
					task.setTaskPriority(line.substring(priorityFieldIdentifier.length()));
				}
			}
			// clean up
			if (begin) {
				taskList.add(task);
			}
			return taskList;
		}
	}

	public ArrayList<Task> readFile(String fileName) throws IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		return readFromStream(new FileInputStream(file));
	}
	
	public void writeFile(List<Task> taskList) throws IOException {
		writeFile(taskList, defaultFileName);
	}
	
	/**
	 * writeFile writes a list to the file
	 * @param taskList
	 * @param fileName
	 * @throws IOException
	 */
	public void writeFile(List<Task> taskList, String fileName) throws IOException {
		try (PrintWriter pw = new PrintWriter(new File(fileName))) {
			Iterator<Task> taskIterator = taskList.iterator();
			while (taskIterator.hasNext()) {
				// name
				final Task task = taskIterator.next();
				pw.print(taskIdFieldIdentifier);
				pw.println(task.getTaskID());
				pw.print(nameFieldIdentifier);
				pw.println(task.getTaskName());
				if (task.getDueDate() != null) {
					pw.print(dueDateFieldIdentifier);
					pw.println(task.getDueDate().toString());
				}
				if (task.getStartTime() != null) {
					pw.print(startTimeFieldIdentifier);
					pw.println(task.getStartTime());
				}
				
				if (task.getEndTime() != null) {
					pw.print(endTimeFieldIdentifier);
					pw.println(task.getEndTime());
				}
				pw.print(statusFieldIdentifier);
				pw.println(task.getTaskStatus());
				if (task.getTaskPriority() != null) {
					pw.print(priorityFieldIdentifier);
					pw.println(task.getTaskPriority());
				}
				
				pw.println(endIdentifier);
			}
		} catch (IOException e) {
			throw e;
		}
	}
	

}