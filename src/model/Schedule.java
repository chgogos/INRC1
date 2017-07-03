/**
 *
 */
package model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 *
 * @author goulas, cgogos, alefrag
 *
 */
public class Schedule implements ScheduleInterface {
	// PA15052011 Refactor start

	protected InrcProblem problem;
	protected int D; // number of days in planning period;
	protected int T; // number of timeunits
	protected int E; // number of employees
	protected int S; // number of ShiftTypes
	protected int W; // number of weeks
	/** for each timeunit, its employees */
	protected int[][] assignments;

	// Replicated information for compatibility reasons
	/** output - solution - codes of week work */
	protected int[][] sol;

	// PA15052011 Refactor end

	// public Schedule(InrcProblem problem)
	public Schedule(InrcProblem problem) {
		setProblem(problem);
	}

	public Schedule(ScheduleInterface other) {
		setProblem(other.getProblem());
		for (int t = 0; t < problem.timeunits.size(); t++) {
			for (int e = 0; e < problem.employees.size(); e++) {
				assignments[t][e] = other.getAssignment(e, t);
			}
		}
	}

	public void schedule(Employee employee, int timeunit) {
		schedule(employee.getIndex(), timeunit);
	}

	public void schedule(int employee_id, int timeunit) {
		assignments[timeunit][employee_id] = 1;
	}

	public void schedule(int employee_id, int weekNo, int patternCode) {
		update_schedule(employee_id, weekNo, sol[employee_id][weekNo], patternCode);
	}

	public void unschedule(Employee employee, int timeunit) {
		unschedule(employee.getIndex(), timeunit);
	}

	public void unschedule(int employee_id, int timeunit) {
		assignments[timeunit][employee_id] = 0;
	}

	public void setProblem(InrcProblem problem) {
		this.problem = problem;
		D = problem.demands.DAYS;
		E = problem.employees.size();
		S = problem.shiftTypes.size();
		T = D * S;
		W = problem.getWeekNumber();
		assignments = new int[T][E];

		sol = new int[E][W];

		//PA this is a hack. Is it ok???
		wwl = new WeekWorkList(problem);
	}

	public InrcProblem getProblem() {
		return problem;
	}

	public int getAssignment(int employee_id, int timeunit) {
		return assignments[timeunit][employee_id];
	}

	public int[][] getAssignments() {
		return assignments;
	}

	public String toStringForEmployee(Employee e) {
		StringBuffer sb = new StringBuffer();
		sb.append(getStringHeaderLine());
		sb.append(getEmployeeLine(e));
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append(getStringHeaderLine());

		for (Employee e : problem.employees) {
			sb.append(getEmployeeLine(e));
		}
		return sb.toString();
	}

	private String getStringHeaderLine() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%20s", "Day       "));
		for (int i = 0; i < problem.timeunits.size(); i++) {
			if (i % problem.shiftTypes.size() == 0) {
				Calendar cal = new GregorianCalendar();
				cal.setTime(problem.timeunits.get(i).date);
				sb.append(String.format("|%2s", cal.getDisplayName(
						Calendar.DAY_OF_WEEK, Calendar.SHORT,
						Locale.getDefault()).substring(0, 1)));
			} else {
				sb.append("  ");
			}
		}
		sb.append("|\n");

		sb.append(String.format("%20s", "Day       "));
		for (int i = 0; i < problem.timeunits.size(); i++) {
			int day;
			if (i % problem.shiftTypes.size() == 0) {
				day = i / problem.shiftTypes.size();
				sb.append(String.format("|%2s", day));
			} else {
				sb.append("  ");
			}
		}
		sb.append("|\n");

		sb.append(String.format("%20s", "ShiftType "));
		for (int i = 0; i < problem.timeunits.size(); i++) {
			if (i % problem.shiftTypes.size() == 0) {
				sb.append("|");
			}
			sb.append(String.format("%2s",
					problem.timeunits.get(i).shiftType.id));
		}
		sb.append("|\n");

		sb.append("==================");
		for (int i = 0; i < problem.timeunits.size(); i++) {
			sb.append("==");
			if (i % problem.shiftTypes.size() == 0) {
				sb.append("|");
			}
		}
		sb.append("==|\n");
		return sb.toString();
	}

	private String getEmployeeLine(Employee e) {
		int employee_id = e.getIndex();

		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%20s", "Employee " + e.id));
		for (int i = 0; i < problem.timeunits.size(); i++) {
			if (i % problem.shiftTypes.size() == 0) {
				sb.append("|");
			}
			if (assignments[i][employee_id] == 1) {
				sb.append(String.format("%2s",
						problem.timeunits.get(i).shiftType.id));
			} else
				sb.append("  ");
		}
		sb.append("|\n");
		return sb.toString();
	}

	@Override
	public ScheduleInterface cloneSchedule() {
		throw new UnsupportedOperationException("Not implemented yet");
		// return null;
	}

	@Override
	public int getWeekPattern(int employee_id, int weekNo){
		return sol[employee_id][weekNo];
	}
	
	@Override
	public int[] getEmployeeWeekPattern(int employeeId) {
		return sol[employeeId];
	}

	@Override
	public int[][] getWeeklyPatternSol(){
		return sol;
	}
	
	@Override
	/**
	 * A utility method to get the assignment to a shift
	 * It returns 0 if the employee is resting or a number in
	 * {1..|shifts|} if it is working on the specific shift that day
	 */
	public int getDayAssignment(int employeeId, int dayNo) {
		if (dayNo<0 || dayNo>=problem.getTotalDaysReal())
			return 0;
		
		int tuStart = dayNo * problem.shiftTypes.size();
		int tuEnd = (dayNo +1)*problem.shiftTypes.size();
		for(int t=tuStart;t<tuEnd;t++)
			if(assignments[t][employeeId]==1)
				return t-tuStart+1;
		return 0;
	}
	
	/**
	 * update of person p at week cur_week with cd work pattern
	 */
	private static final int week_d_num =7;
	private WeekWorkList wwl;

	@Deprecated
	private void update_schedule(int employee_id, int weekNo, int cd) {
		sol[employee_id][weekNo]=cd;
		int end = (weekNo+1) * week_d_num;
		for (int d = weekNo * week_d_num; d < end; d++) {
			// stop
			if (d >= problem.getTotalDaysReal())
				break;
			// week work has day d to work
			if (wwl.wwAll.get(cd).work[d % week_d_num] == 1) {
				schedule(employee_id, problem.shiftTypes.size() * d + 0);
			} else {
				unschedule(employee_id, problem.shiftTypes.size() * d + 0);
			}
		}
	}

	/**
	 * update of person p at week cur_week with cd work pattern
	 */
	private void update_schedule(int p, int cur_week, int old_cd, int new_cd) {
		sol[p][cur_week]=new_cd;
		int[] old_w = wwl.wwAll.get(old_cd).work;
		int[] new_w = wwl.wwAll.get(new_cd).work;
		int tuStart = cur_week * week_d_num*problem.shiftTypes.size();
		int tuEnd = (cur_week +1)* week_d_num*problem.shiftTypes.size();
		if(tuEnd > T)
			tuEnd = T;

		for (int d = 0, tu=tuStart; tu < tuEnd; tu+=problem.shiftTypes.size(), d++) {
			// week work has day d to work
			if (old_w[d] != new_w[d]) {
				if (old_w[d] == 0) {
					schedule(p, tu);
				} else {
					unschedule(p, tu);
				}
			}
		}
	}


}
