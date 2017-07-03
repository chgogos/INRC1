package eval;


public class UnscheduleEmployeeFromTimeunitCommand implements Command {

	ScheduleHelper scheduleHelper;
	int emp_id;
	int timeunit_id;

	public UnscheduleEmployeeFromTimeunitCommand(ScheduleHelper scheduleHelper,
			int emp_id, int timeunit_id) {
		this.scheduleHelper = scheduleHelper;
		this.emp_id = emp_id;
		this.timeunit_id = timeunit_id;
	}

	@Override
	public void execute() {
		scheduleHelper.unschedule(emp_id, timeunit_id);
	}

	@Override
	public void undo() {
		scheduleHelper.schedule(emp_id, timeunit_id);
	}

}
