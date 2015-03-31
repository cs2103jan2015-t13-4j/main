package organizer.logic;

import java.util.ArrayList;

public class CompleteTask {
	private static final String MESSAGE_INVALID_TASK = "Selected task does not exists!";
	private static final String MESSAGE_SUCCESS = "%1$s task(s) operation is successful!\n\n";
	
	public ResultSet execute(String taskInfo, TaskListSet allLists, Validation validOp) {
		int lineNum = Integer.parseInt(taskInfo.trim());
		ResultSet returnResult = new ResultSet();
		ArrayList<Task> tempList = allLists.getTaskList();
		
		if(validOp.isValidTask(lineNum, allLists)) {
			int taskID = validOp.checkForTaskID(lineNum, allLists);
			tempList.get(taskID).setTaskStatus("COMPLETE");
			returnResult.setOpStatus(String.format(MESSAGE_SUCCESS, "Complete"));
		} else {
			returnResult.setOpStatus(MESSAGE_INVALID_TASK);
		}
		
		allLists.setTaskList(tempList);
		returnResult.setReturnList(allLists.getTaskList());
		return returnResult;
	}
}
