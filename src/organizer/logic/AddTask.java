
package organizer.logic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@author A0098824N
public class AddTask {
	private static final String PATTERN_DEADLINE_DATEONLY = "((19|20\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]))";
	private static final String PATTERN_DEADLINE_DAYASDATE = "monday|tuesday|wednesday|thursday|friday|saturday|sunday|mon|tues|wed|thurs|fri|sat|sun|today|tomorrow";

	private static final String PATTERN_DEADLINE_DATETIME = "((19|20\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]))(\\s)(([01]?[0-9]|2[0-3]):([0-5][0-9]))";
	private static final String PATTERN_DEADLINE_DAYTIME = "(\\w+)(\\s)(([01]?[0-9]|2[0-3]):([0-5][0-9]))";
	
	private static final String KEYWORD_DEADLINE = " by ";
	private static final String TYPE_DEADLINE = "DEADLINE";
	private static final String TYPE_FLOATING = "FLOATING";
	private static final String TYPE_TIMED = "TIMED";
	private static final String TYPE_ERROR = "ERROR";
	private static final String TIME_DEADLINE ="23:59";
	
	private static final String KEYWORD_TIMED_DATE = " on ";
	private static final String KEYWORD_TIMED_TODAY = "today";
	private static final String KEYWORD_TIMED_TMRW = "tomorrow";
	private static final String KEYWORD_NOTIMED_DATE = " from ";
	
	//add {taskname} {on} {date}
	private static final String PATTERN_TIMED_START_DATE = "((19|20\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]))";
	//add {taskname} {on} {day}
	private static final String PATTERN_TIMED_START_DAY = "(\\w+)";
	//add {taskname} {today|tomorrow}
	private static final String PATTERN_TIMED_START_TODAYTMRW = "(today|tomorrow)";
	//add {taskname} {on} {date} {from} {time} {to} {time}
	private static final String PATTERN_TIMED_STARTEND_1DATE = "((19|20\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]))(\\s)(\\bfrom\\b)(\\s)(([01]?[0-9]|2[0-3]):([0-5][0-9]))(\\s)(\\bto\\b)(\\s)(([01]?[0-9]|2[0-3]):([0-5][0-9]))";
	//add {taskname} {on} {mon-sun} {from} {time} {to} {time}
	private static final String PATTERN_TIMED_STARTEND_1DAY = "(\\w+)(\\s)(\\bfrom\\b)(\\s)(([01]?[0-9]|2[0-3]):([0-5][0-9]))(\\s)(\\bto\\b)(\\s)(([01]?[0-9]|2[0-3]):([0-5][0-9]))";	
	//add {taskname} {today|tomorrow} {from} {time} {to} {time}
	private static final String PATTERN_TIMED_STARTEND_TODAYTMRWTIMERANGE = "(today|tomorrow)(\\s)(\\bfrom\\b)(\\s)(([01]?[0-9]|2[0-3]):([0-5][0-9]))(\\s)(\\bto\\b)(\\s)(([01]?[0-9]|2[0-3]):([0-5][0-9]))";
	//add {taskname} {on} {date} {from} {time} {to} {date} {time}
	private static final String PATTERN_TIMED_STARTEND_2DATE = "((19|20\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]))(\\s)(\\bfrom\\b)(\\s)(([01]?[0-9]|2[0-3]):([0-5][0-9]))(\\s)(\\bto\\b)(\\s)((19|20\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]))(\\s)(([01]?[0-9]|2[0-3]):([0-5][0-9]))";
	//add {taskname} {from} {date} {to} {date}
	private static final String PATTERN_NOTIMED_STARTEND_2DATE = "((19|20\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]))(\\s)(\\bto\\b)(\\s)((19|20\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]))";
	//add {taskname} {on} {date} {time}
	private static final String PATTERN_TIMED_START_DATETIME = "((19|20\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]))(\\s)(([01]?[0-9]|2[0-3]):([0-5][0-9]))";
	//add {taskname} {on} {mon-sun} {time}
	private static final String PATTERN_TIMED_START_DAYTIME = "(\\w+)(\\s)(([01]?[0-9]|2[0-3]):([0-5][0-9]))";
	//add {taskname} {today|tomorrow} {time}
	private static final String PATTERN_TIMED_START_TODAYTMRWTIME = "(today|tomorrow)(\\s)(([01]?[0-9]|2[0-3]):([0-5][0-9]))";

	private static final String MESSAGE_SUCCESS = "Add task operation is successful!";
	private static final String MESSAGE_UNSUCCESS = "Add task operation is unsuccessful! End date/time error!";
	
	private DateAndTime dtCheck = new DateAndTime();
	
	ResultSet returnResult = new ResultSet();

	
	public ResultSet execute(String taskInfo, TaskListSet allLists) {
		returnResult = new ResultSet();
		String taskName = null;
		String taskDateTime = null;
		ArrayList<Task> taskList = allLists.getTaskList();
		int taskID = allLists.getTaskList().size();
		Task tempItem = new Task();
		
		if(taskInfo.contains(KEYWORD_DEADLINE)) {
			taskName = taskInfo.substring(0, taskInfo.lastIndexOf(KEYWORD_DEADLINE)).trim();
			taskDateTime = taskInfo.substring(taskInfo.lastIndexOf(KEYWORD_DEADLINE)+KEYWORD_DEADLINE.length()).trim();
			tempItem = addDeadlineTask(taskName, taskDateTime, tempItem);
		} else if(taskInfo.contains(KEYWORD_TIMED_DATE)) {
			taskName = taskInfo.substring(0, taskInfo.lastIndexOf(KEYWORD_TIMED_DATE)).trim();
			taskDateTime = taskInfo.substring(taskInfo.lastIndexOf(KEYWORD_TIMED_DATE)+KEYWORD_TIMED_DATE.length()).trim();
			tempItem = addTimedTask_DATEDAY(taskName, taskDateTime, tempItem);
		} else if(taskInfo.contains(KEYWORD_TIMED_TODAY)) {
			taskName = taskInfo.substring(0, taskInfo.lastIndexOf(KEYWORD_TIMED_TODAY)).trim();
			taskDateTime = taskInfo.substring(taskInfo.lastIndexOf(KEYWORD_TIMED_TODAY)).trim();
			tempItem =  addTimedTask_TODAYTMRW(taskName, taskDateTime, tempItem);
		} else if(taskInfo.contains(KEYWORD_TIMED_TMRW)) {
			taskName = taskInfo.substring(0, taskInfo.lastIndexOf(KEYWORD_TIMED_TMRW)).trim();
			taskDateTime = taskInfo.substring(taskInfo.lastIndexOf(KEYWORD_TIMED_TMRW)).trim();
			tempItem =  addTimedTask_TODAYTMRW(taskName, taskDateTime, tempItem);
		} else if(taskInfo.contains(KEYWORD_NOTIMED_DATE)) {
			taskName = taskInfo.substring(0, taskInfo.lastIndexOf(KEYWORD_NOTIMED_DATE)).trim();
			taskDateTime = taskInfo.substring(taskInfo.lastIndexOf(KEYWORD_NOTIMED_DATE)+KEYWORD_NOTIMED_DATE.length()).trim();
			tempItem = addTimedTask_DATEDAY(taskName, taskDateTime, tempItem);
		} else {
			taskName = taskInfo;
			tempItem = addFloatingTask(taskName, tempItem);
		}
		
		tempItem.setTaskID(taskID);
		if(tempItem.getTaskType().equals(TYPE_ERROR)) {
			returnResult.setOpStatus(MESSAGE_UNSUCCESS);
		} else {
			taskList.add(tempItem);
			returnResult.setOpStatus(MESSAGE_SUCCESS);
		}
		
		allLists.setTaskList(taskList);
		returnResult.setReturnList(taskList);
		
		return returnResult;
	}
	
	public Task addDeadlineTask(String taskName, String taskDateTime, Task deadlineTask) {
		Matcher DEADLINE_DATEONLY;
		Matcher DEADLINE_DATETIME;
		Matcher DEADLINE_DAYTIME;
		
		DEADLINE_DATEONLY = Pattern.compile(PATTERN_DEADLINE_DATEONLY).matcher(taskDateTime);
		DEADLINE_DATETIME = Pattern.compile(PATTERN_DEADLINE_DATETIME).matcher(taskDateTime);
		DEADLINE_DAYTIME = Pattern.compile(PATTERN_DEADLINE_DAYTIME).matcher(taskDateTime);
		
		if(DEADLINE_DATEONLY.matches()) {
			deadlineTask.setTaskEndDate(dtCheck.toValidDate(taskDateTime));
			deadlineTask.setTaskEndTime(LocalTime.parse(TIME_DEADLINE));
			deadlineTask.setTaskType(TYPE_DEADLINE);
		} else if(taskDateTime.matches(PATTERN_DEADLINE_DAYASDATE)) {
			deadlineTask.setTaskEndDate(dtCheck.determineDate(taskDateTime));
			deadlineTask.setTaskEndTime(LocalTime.parse(TIME_DEADLINE));
			deadlineTask.setTaskType(TYPE_DEADLINE);
		} else if(DEADLINE_DATETIME.matches()) {
			deadlineTask.setTaskEndDate(dtCheck.toValidDate(DEADLINE_DATETIME.group(1)));
			deadlineTask.setTaskEndTime(dtCheck.determineHour(DEADLINE_DATETIME.group(6)));
			deadlineTask.setTaskType(TYPE_DEADLINE);
		} else if(DEADLINE_DAYTIME.matches()) {
			deadlineTask.setTaskEndDate(dtCheck.determineDate(DEADLINE_DAYTIME.group(1)));
			deadlineTask.setTaskEndTime(dtCheck.determineHour(DEADLINE_DAYTIME.group(3)));
			deadlineTask.setTaskType(TYPE_DEADLINE);
		} else {
			taskName = taskName.concat(" "+taskDateTime);
			deadlineTask.setTaskType(TYPE_FLOATING);
		}
		
		deadlineTask.setTaskName(taskName);
		return deadlineTask;
		
	}
	
	public Task addFloatingTask(String taskName, Task floatTask) {
		floatTask.setTaskName(taskName);
		floatTask.setTaskType(TYPE_FLOATING);
		return floatTask;
	}
	
	public Task addTimedTask_DATEDAY(String taskName, String taskDateTime, Task timedTask) {
		
		Matcher TIMED_STARTEND_2DATE;
		Matcher TIMED_STARTEND_1DATE;
		Matcher TIMED_STARTEND_1DAY;
		Matcher TIMED_START_DATETIME;
		Matcher TIMED_START_DAYTIME;
		Matcher TIMED_START_DATE;
		Matcher TIMED_START_DAY;
		Matcher NOTIMED_STARTEND_2DATE;
		
		TIMED_STARTEND_2DATE = Pattern.compile(PATTERN_TIMED_STARTEND_2DATE).matcher(taskDateTime);
		TIMED_STARTEND_1DATE = Pattern.compile(PATTERN_TIMED_STARTEND_1DATE).matcher(taskDateTime);
		TIMED_STARTEND_1DAY = Pattern.compile(PATTERN_TIMED_STARTEND_1DAY).matcher(taskDateTime);
		TIMED_START_DATETIME = Pattern.compile(PATTERN_TIMED_START_DATETIME).matcher(taskDateTime);
		TIMED_START_DAYTIME = Pattern.compile(PATTERN_TIMED_START_DAYTIME).matcher(taskDateTime);
		TIMED_START_DATE = Pattern.compile(PATTERN_TIMED_START_DATE).matcher(taskDateTime);
		TIMED_START_DAY = Pattern.compile(PATTERN_TIMED_START_DAY).matcher(taskDateTime);
		NOTIMED_STARTEND_2DATE = Pattern.compile(PATTERN_NOTIMED_STARTEND_2DATE).matcher(taskDateTime);
		
		if(TIMED_STARTEND_1DATE.matches()) {
			timedTask.setTaskStartDate(dtCheck.toValidDate(TIMED_STARTEND_1DATE.group(1)));
			timedTask.setTaskEndDate(timedTask.getTaskStartDate());
			LocalTime startTime = dtCheck.determineHour(TIMED_STARTEND_1DATE.group(8));
			LocalTime endTime = dtCheck.determineHour(TIMED_STARTEND_1DATE.group(14));
			
			if(dtCheck.isValidDueDT(null, null, startTime, endTime)) {
				timedTask.setTaskStartTime(startTime);
				timedTask.setTaskEndTime(endTime);
				timedTask.setTaskType(TYPE_TIMED);
			} else {
				timedTask.setTaskType(TYPE_ERROR);
			}

			
		} else if(TIMED_STARTEND_1DAY.matches()) {
			timedTask.setTaskStartDate(dtCheck.determineDate(TIMED_STARTEND_1DAY.group(1)));
			timedTask.setTaskEndDate(timedTask.getTaskStartDate());
			LocalTime startTime = dtCheck.determineHour(TIMED_STARTEND_1DAY.group(5));
			LocalTime endTime = dtCheck.determineHour(TIMED_STARTEND_1DAY.group(11));
			
			if(dtCheck.isValidDueDT(null, null, startTime, endTime)) {
				timedTask.setTaskStartTime(startTime);
				timedTask.setTaskEndTime(endTime);
				timedTask.setTaskType(TYPE_TIMED);
			} else {
				timedTask.setTaskType(TYPE_ERROR);
			}

		} else if(TIMED_STARTEND_2DATE.matches()) {
			LocalDate startDate = dtCheck.toValidDate(TIMED_STARTEND_2DATE.group(1));
			LocalDate endDate = dtCheck.toValidDate(TIMED_STARTEND_2DATE.group(14));
			LocalTime startTime = dtCheck.determineHour(TIMED_STARTEND_2DATE.group(8));
		    LocalTime endTime = dtCheck.determineHour(TIMED_STARTEND_2DATE.group(19));
		    if(dtCheck.isValidDueDT(startDate, endDate, startTime, endTime)) {
		    	timedTask.setTaskStartDate(startDate);
				timedTask.setTaskEndDate(endDate);
				timedTask.setTaskStartTime(startTime);
				timedTask.setTaskEndTime(endTime);
				timedTask.setTaskType(TYPE_TIMED);
		    } else {
		    	timedTask.setTaskType(TYPE_ERROR);
		    }

		} else if(NOTIMED_STARTEND_2DATE.matches()) {
			LocalDate startDate = dtCheck.toValidDate(NOTIMED_STARTEND_2DATE.group(1));
			LocalDate endDate = dtCheck.toValidDate(NOTIMED_STARTEND_2DATE.group(8));
			if(dtCheck.isValidDueDT(startDate, endDate, null, null)) {
				timedTask.setTaskStartDate(startDate);
				timedTask.setTaskEndDate(endDate);
				timedTask.setTaskType(TYPE_TIMED);
			} else {
				timedTask.setTaskType(TYPE_ERROR);
			}
			
		} else if(TIMED_START_DATETIME.matches()) {
			timedTask.setTaskStartDate(dtCheck.toValidDate(TIMED_START_DATETIME.group(1)));
			timedTask.setTaskStartTime(dtCheck.determineHour(TIMED_START_DATETIME.group(6)));
			timedTask.setTaskType(TYPE_TIMED);
			
		} else if(TIMED_START_DAYTIME.matches()) {
			timedTask.setTaskStartDate(dtCheck.determineDate(TIMED_START_DAYTIME.group(1)));
			timedTask.setTaskStartTime(dtCheck.determineHour(TIMED_START_DAYTIME.group(3)));
			timedTask.setTaskType(TYPE_TIMED);
		} else if(TIMED_START_DATE.matches()) {
			timedTask.setTaskStartDate(dtCheck.toValidDate(TIMED_START_DATE.group(1)));
			timedTask.setTaskType(TYPE_TIMED);
		} else if(TIMED_START_DAY.matches()) {
			timedTask.setTaskStartDate(dtCheck.determineDate(TIMED_START_DAY.group(1)));
			timedTask.setTaskType(TYPE_TIMED);
		} else {
			taskName = taskName.concat(" "+taskDateTime);
			timedTask.setTaskType(TYPE_FLOATING);
		}

		timedTask.setTaskName(taskName);
		return timedTask;
	}
	
	public Task addTimedTask_TODAYTMRW(String taskName, String taskDatetime, Task timedTask) {
		Matcher TIMED_STARTEND_TODAYTMRWTIMERANGE;
		Matcher TIMED_START_TODAYTMRWTIME;
		Matcher TIMED_START_TODAYTMRW;
		
		TIMED_STARTEND_TODAYTMRWTIMERANGE = Pattern.compile(PATTERN_TIMED_STARTEND_TODAYTMRWTIMERANGE).matcher(taskDatetime);
		TIMED_START_TODAYTMRWTIME = Pattern.compile(PATTERN_TIMED_START_TODAYTMRWTIME).matcher(taskDatetime);
		TIMED_START_TODAYTMRW = Pattern.compile(PATTERN_TIMED_START_TODAYTMRW).matcher(taskDatetime);
		
		if(TIMED_STARTEND_TODAYTMRWTIMERANGE.matches()) {
			timedTask.setTaskStartDate(dtCheck.determineDate(TIMED_STARTEND_TODAYTMRWTIMERANGE.group(1)));
			timedTask.setTaskEndDate(timedTask.getTaskStartDate());
			LocalTime startTime = dtCheck.determineHour(TIMED_STARTEND_TODAYTMRWTIMERANGE.group(5));
			LocalTime endTime = dtCheck.determineHour(TIMED_STARTEND_TODAYTMRWTIMERANGE.group(11));

			if(dtCheck.isValidDueDT(null, null, startTime, endTime)) {
				timedTask.setTaskStartTime(startTime);
				timedTask.setTaskEndTime(endTime);
				timedTask.setTaskType(TYPE_TIMED);
			} else {
				timedTask.setTaskType(TYPE_ERROR);
			}

			
		} else if(TIMED_START_TODAYTMRWTIME.matches()) {
			timedTask.setTaskStartDate(dtCheck.determineDate(TIMED_START_TODAYTMRWTIME.group(1)));
			timedTask.setTaskStartTime(dtCheck.determineHour(TIMED_START_TODAYTMRWTIME.group(3)));
			timedTask.setTaskType(TYPE_TIMED);
		} else if(TIMED_START_TODAYTMRW.matches()) {
			timedTask.setTaskStartDate(dtCheck.determineDate(TIMED_START_TODAYTMRW.group(1)));
			timedTask.setTaskType(TYPE_TIMED);
		} else {
			taskName = taskName.concat(" "+ taskDatetime);
			timedTask.setTaskType(TYPE_FLOATING);
		}
		
		timedTask.setTaskName(taskName);
		return timedTask;

	}
}
