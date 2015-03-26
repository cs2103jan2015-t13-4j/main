package organizer.test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import organizer.logic.Task;
import organizer.storage.Storage;

public class TestStorage {
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private InputStream createInputStreamFromString(final String input) {
		return new ByteArrayInputStream(input.getBytes());
	}

	@Test
	public void testStorageFields() throws IOException {
		final Task targetTask = new Task();
		targetTask.setTaskID(0);
		targetTask.setDueDate(LocalDate.parse("2001-01-01"));
		targetTask.setTaskName("buy milk");
		targetTask.setTaskPriority("low");
		targetTask.setTaskStatus("COMPLETE");
		final String input =
				"id: 0\n"
				+ "name: buy milk\n"
				+ "duedate: 2001-01-01\n"
				+ "status: COMPLETE\n"
				+ "priority: low\n"
				+ "----\n"
				+ "----\n";
		InputStream is = createInputStreamFromString(input);
		Storage storage = new Storage();
		ArrayList<Task> taskList = storage.readFromStream(is);
		/* exactly one task */
		assertEquals(taskList.size(), 1);
		/* correct content */
		final Task oneTask = taskList.get(0);
		assertNotNull(oneTask);
		assertEquals(oneTask.getTaskID(), targetTask.getTaskID());
		assertEquals(oneTask.getTaskName(), targetTask.getTaskName());
		assertEquals(oneTask.getDueDate(), targetTask.getDueDate());
		assertEquals(oneTask.getTaskPriority(), targetTask.getTaskPriority());
		assertEquals(oneTask.getTaskStatus(), targetTask.getTaskStatus());
	}
	
	@Test
	public void testInvalidFields() throws IOException {
		/* invalid fields should be ignored */
		final String input =
				"INVALID\n"
				+ "INVALID\n"
				+ "INVALID\n"
				+ "\n\n"
				+ "----";
		InputStream is = createInputStreamFromString(input);
		Storage storage = new Storage();
		ArrayList<Task> taskList = storage.readFromStream(is);
		
		assertEquals(taskList.size(), 0);
	}
	
	@Test
	public void testThrow() throws IOException {
		final String input = "id: WHAT?!\n----\n";
		InputStream is = createInputStreamFromString(input);
		Storage storage = new Storage();
		exception.expect(NumberFormatException.class);
		ArrayList<Task> taskList = storage.readFromStream(is);
	}

}