package eval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Employee;

public class Invoker {

	List<Command> commands;
	Command undoCommand;
	ScheduleHelper scheduleHelper;

	ScheduleHelper restorePointScheduleHelper;

	public Invoker(ScheduleHelper sh) {
		scheduleHelper = sh;
		commands = new ArrayList<Command>();
		clearCommands();
	}

	public void addCommand(Command cmd) {
		commands.add(cmd);
	}

	public void executeLastCommand() {
		Command cmd = commands.get(commands.size() - 1);
		cmd.execute();
		undoCommand = cmd;
	}

	public void undoLastCommand() {
		undoCommand.undo();
		commands.remove(undoCommand);
		if (!commands.isEmpty())
			undoCommand = commands.get(commands.size() - 1);
	}

	public void clearCommands() {
		commands.clear();
	}

	public void undoAllCommands() {
		Collections.reverse(commands);
		for (Command cmd : commands) {
			cmd.undo();
		}
		clearCommands();
	}

	public void removeCurrentCommand() {
		commands.remove(undoCommand);
	}

	public void scheduleEmployeeToTimeunit(int emp_id, int tu) {
		ScheduleEmployeeToTimeunitCommand cmd = new ScheduleEmployeeToTimeunitCommand(
				scheduleHelper, emp_id, tu);
		addCommand(cmd);
		executeLastCommand();
	}

	public void unscheduleEmployeeFromTimeunit(int emp_id, int tu) {
		UnscheduleEmployeeFromTimeunitCommand cmd = new UnscheduleEmployeeFromTimeunitCommand(
				scheduleHelper, emp_id, tu);
		addCommand(cmd);
		executeLastCommand();
	}

	public void updateAllEmployeesCostContribution(CostEvaluator ce) {
		for (Employee emp : ce.getProblem().employees) {
			updateEmployeeCostContribution(ce, emp);
		}
	}

	public void updateEmployeeCostContribution(CostEvaluator ce, Employee emp) {
		scheduleHelper.setEmployeeCachedDayWorkCost(emp.getIndex(), ce
				.evaluateDayWorkCostFor(emp));
		scheduleHelper.setEmployeeCachedShiftCost(emp.getIndex(), ce
				.evaluateShiftWorkCostFor(emp));
	}

	public void createRestorePointForScheduleHelper() {
		restorePointScheduleHelper = scheduleHelper.cloneSchedule();
	}

	public void restoreToScheduleHelper() {
		scheduleHelper = restorePointScheduleHelper;
		clearCommands();
	}

	public ScheduleHelper getScheduleHelper() {
		return scheduleHelper;
	}
}
