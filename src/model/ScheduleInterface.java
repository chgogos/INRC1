package model;


public interface ScheduleInterface {
	public void setProblem(InrcProblem problem);
	public InrcProblem getProblem();
	public void schedule(int employee_id, int timeunit);
	public void unschedule(int employee_id, int timeunit);
	public int getAssignment(int employee_id, int timeunit);
	public int getDayAssignment(int employee_id, int dayNo);
	public ScheduleInterface cloneSchedule();

	//API for weekly solver
	public void schedule(int employee_id, int weekNo, int patternCode);
	public int getWeekPattern(int employee_id, int weekNo);
	public int[] getEmployeeWeekPattern(int employee_id);
	
	//PA We should not return a whole structure directly
	//When this is needed we should be able to either clone
	//the solution or iterate through a method
	@Deprecated
	public int[][] getWeeklyPatternSol();
}
