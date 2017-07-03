package eval;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;

import model.DayOffRequest;
import model.Employee;
import model.InrcProblem;
import model.Schedule;
import model.ScheduleInterface;
import model.ShiftOffRequest;
import model.WorkAssignment;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

public class ScheduleHelper extends Schedule {

	// private static final EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
	static java.util.regex.Pattern pattern1 = java.util.regex.Pattern
			.compile("[-]+");
	static java.util.regex.Pattern pattern2 = java.util.regex.Pattern
			.compile("[^-]+");

	private String[] planningPeriodDayNames;
	private int[] planningPeriodDaysOfYear;
	// dc is a Dictionary with key the day number of year and value the day
	// index in the planning period
	private Dictionary<Integer, Integer> dc = new Hashtable<Integer, Integer>();
	private int[] nightTU;
	private int[][] dayOffRequests;
	private int[][] shiftOffRequests;


	//PA15052011 Refactor start
	//private InrcProblem problem;
	// T X E array assignments[i][j]=1 if for timeunit i employee j is assigned,
	// assignments[i][j]=0 otherwise
	//private int[][] assignments;
//	private int D; // number of days in planning period;
//	private int T; // number of timeunits
//	private int E; // number of employees
//	private int S; // number of ShiftTypes
	//PA15052011 Refactor end

	private StringBuilder[] employeeSchedules;
	private List<Integer>[] consecutiveFreeDays;
	private List<Integer>[] consecutiveWorkDays;
	private List<Integer>[] timeUnitsShiftOffRequestsList;
	private List<Integer>[] dayOffRequestsList;
	private int[] employeeCachedDayWorkCost;
	private int[] employeeCachedShiftCost;
	private int[][] skillMatchingCost;

	@SuppressWarnings("unchecked")
	public ScheduleHelper(InrcProblem problem) {

		//PA15052011 Refactor start
		super(problem);
//		D = problem.demands.DAYS;
//		E = problem.employees.size();
//		S = problem.shiftTypes.size();
//		T = D * S;
//		assignments = new int[T][E];
		//PA15052011 Refactor end

		nightTU = new int[T];
		dayOffRequests = new int[D][E];
		shiftOffRequests = new int[T][E];
		for (int i = 0; i < T; i++) {
			nightTU[i] = (problem.shiftTypes.get(i % S).isNightShift() ? 1 : 0);
		}
		planningPeriodDaysOfYear = new int[problem.demands.DAYS];
		generateDayOfYears();
		for (DayOffRequest dor : problem.dayOffRequests) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dor.getDateOff());
			int doy = cal.get(Calendar.DAY_OF_YEAR);
			int i = dc.get(doy);
			int j = dor.getEmployee().getIndex();
			dayOffRequests[i][j] = dor.getWeight();
		}
		for (ShiftOffRequest sor : problem.shiftOffRequests) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(sor.getDateOff());
			int doy = cal.get(Calendar.DAY_OF_YEAR);
			int i = dc.get(doy);
			int k = problem.shiftTypes.indexOf(sor.getShiftType());
			int j = sor.getEmployee().getIndex();
			shiftOffRequests[i * S + k][j] = sor.getWeight();
		}
		planningPeriodDayNames = new String[D];
		Calendar cal = Calendar.getInstance();
		cal.setTime(problem.getStartDate());
		for (int i = 0; i < D; i++) {
			planningPeriodDayNames[i] = DatesHandler.EN_DAY_NAMES[cal
					.get(Calendar.DAY_OF_WEEK) - 1];
			cal.add(Calendar.DAY_OF_YEAR, 1);
		}

		employeeSchedules = new StringBuilder[E];
		consecutiveFreeDays = new List[E];
		consecutiveWorkDays = new List[E];
		timeUnitsShiftOffRequestsList = new List[E];
		dayOffRequestsList = new List[E];
		for (int emp_id = 0; emp_id < E; emp_id++) {
			updateScheduleAsStringFor(emp_id);
			consecutiveFreeDays[emp_id] = new ArrayList<Integer>();
			consecutiveWorkDays[emp_id] = new ArrayList<Integer>();
			timeUnitsShiftOffRequestsList[emp_id] = computeTimeUnitsWithShiftOffRequests(emp_id);
			dayOffRequestsList[emp_id] = computeDayOffRequests(emp_id);
		}

		employeeCachedDayWorkCost = new int[E];
		employeeCachedShiftCost = new int[E];

		skillMatchingCost = new int[E][S];
		for (int i = 0; i < E; i++)
			for (int j = 0; j < S; j++) {
				List<String> employeeSkills = problem.employees.get(i).skills;
				List<String> shiftTypeSkills = problem.shiftTypes.get(j).requiredSkills;
				int c = 0;
				for (String requiredSkill : shiftTypeSkills) {
					if (!employeeSkills.contains(requiredSkill)) {
						c++;
					}
				}
				skillMatchingCost[i][j] = c;
			}
	}

	private void generateDayOfYears() {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(problem.getStartDate());
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(problem.getEndDate());
		int k = 0;
		while (!cal1.after(cal2)) {
			int x = cal1.get(Calendar.DAY_OF_YEAR);
			planningPeriodDaysOfYear[k] = x;
			dc.put(x, k);
			k++;
			cal1.add(Calendar.DAY_OF_YEAR, 1);
		}
	}

	public void populateAssignmentsFrom(ScheduleInterface schedule) {
		for (int time_unit_id = 0; time_unit_id < T; time_unit_id++) {
			for (int emp_id = 0; emp_id < E; emp_id++) {
				if (schedule.getAssignment(emp_id, time_unit_id) == 1) {
					assignments[time_unit_id][emp_id] = 1;
				} else {
					assignments[time_unit_id][emp_id] = 0;
				}
			}
		}
		for (int emp_id = 0; emp_id < E; emp_id++) {
			updateScheduleAsStringFor(emp_id);
		}
	}

	public int getCachedCost() {
		int sum = 0;
		for (int i = 0; i < E; i++) {
			sum += employeeCachedDayWorkCost[i];
			sum += employeeCachedShiftCost[i];
		}
		return sum;
	}

	public void setEmployeeCachedDayWorkCost(int emp_id, int value) {
		employeeCachedDayWorkCost[emp_id] = value;
	}

	public int getEmployeeCachedDayWorkCost(int emp_id) {
		return employeeCachedDayWorkCost[emp_id];
	}

	public void setEmployeeCachedShiftCost(int emp_id, int value) {
		employeeCachedShiftCost[emp_id] = value;
	}

	public int getEmployeeCachedShiftCost(int emp_id) {
		return employeeCachedShiftCost[emp_id];
	}

	public int getEmployeeCachedCost(int emp_id) {
		return employeeCachedShiftCost[emp_id]
				+ employeeCachedDayWorkCost[emp_id];
	}

	public int getDayOfYear(int timeUnitIndex) {
		return planningPeriodDaysOfYear[timeUnitIndex / S];
	}

	public String getShiftType(int timeUnitIndex) {
		return problem.shiftTypes.get(timeUnitIndex % S).id;
	}

	public ArrayList<String> getShiftTypesForEmployee(int emp_id, int dayOfYear) {
		// EtmPoint point = etmMonitor
		// .createPoint("ScheduleHelper:getShiftTypesForEmployee");
		ArrayList<String> shiftTypes = new ArrayList<String>();
		int i = dc.get(dayOfYear);
		for (int j = 0; j < S; j++) {
			if (assignments[i * S + j][emp_id] == 1) {
				shiftTypes.add(problem.shiftTypes.get(j).getId());
			}
		}
		// point.collect();
		return shiftTypes;
	}

	public List<Integer> getDaysOfYearWorkingOnNightShift(int emp_id) {
		List<Integer> dow = new ArrayList<Integer>();
		for (int i = 0; i < T; i++) {
			if ((assignments[i][emp_id] == 1) && (nightTU[i] == 1))
				dow.add(planningPeriodDaysOfYear[i]);
		}
		return dow;
	}

	public List<WorkAssignment> getWorkAssignmentsForEmployeeAndDateOfYear(
			int emp_id, int doy) {
		List<WorkAssignment> was = new ArrayList<WorkAssignment>();
		int k = dc.get(doy);
		for (int shift_type_id = 0; shift_type_id < S; shift_type_id++) {
			int time_unit_id = k * S + shift_type_id;
			if (time_unit_id >= T)
				continue;
			if (assignments[time_unit_id][emp_id] == 1) {
				WorkAssignment wa = new WorkAssignment(emp_id, time_unit_id,
						doy, shift_type_id);
				was.add(wa);
			}
		}
		return was;
	}

	public WorkAssignment getWorkAssignmentForEmployeeAndTimeUnit(int emp_id,
			int time_unit_index) {
		if (assignments[time_unit_index][emp_id] == 1) {
			int doy = planningPeriodDaysOfYear[time_unit_index / S];
			int shift_type_id = time_unit_index % S;
			return new WorkAssignment(emp_id, time_unit_index, doy,
					shift_type_id);
		} else
			return null;
	}

	private List<Integer> computeDayOffRequests(int emp_id) {
		List<Integer> doys = new ArrayList<Integer>();
		for (int i = 0; i < D; i++) {
			if (dayOffRequests[i][emp_id] > 0) {
				doys.add(planningPeriodDaysOfYear[i]);
			}
		}
		return doys;
	}

	public List<Integer> getDayOffRequests(int emp_id) {
		return dayOffRequestsList[emp_id];
	}

	public int getDayOffRequestWeight(int dayOfYear, int emp_id) {
		return dayOffRequests[dc.get(dayOfYear)][emp_id];
	}

	public List<Integer> getTimeUnitsWithShiftOffRequests(int emp_id) {
		return timeUnitsShiftOffRequestsList[emp_id];
	}

	private List<Integer> computeTimeUnitsWithShiftOffRequests(int emp_id) {
		List<Integer> aList = new ArrayList<Integer>();
		for (int i = 0; i < T; i++) {
			if (shiftOffRequests[i][emp_id] > 0) {
				aList.add(i);
			}
		}
		return aList;
	}

	public int getShiftOffRequestWeight(int timeUnitId, int emp_id) {
		return shiftOffRequests[timeUnitId][emp_id];
	}

	public StringBuilder scheduleAsStringFor(Employee emp) {
		return employeeSchedules[emp.getIndex()];
	}

	private void updateScheduleAsStringFor(int emp_id) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < D; i++) {
			boolean f = false;
			// if two assignments per day exist for the same employee only the
			// first one is taken under consideration
			for (int j = 0; j < S && !f; j++) {
				if (assignments[i * S + j][emp_id] == 1) {
					sb.append(problem.shiftTypes.get(j).getAliasId());
					f = true;
				}
			}
			if (!f) {
				sb.append("-");
			}
		}
		employeeSchedules[emp_id] = sb;
	}

	public List<String> getSubSchedules(StringBuilder text, List<String> dn) {
		List<String> ss = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		boolean f = false;
		for (int i = 0; i < D; i++) {
			if (planningPeriodDayNames[i].equalsIgnoreCase(dn.get(0))) {
				sb = new StringBuilder();
				f = false;
			}
			if (dn.contains(planningPeriodDayNames[i])) {
				sb.append(text.charAt(i));
			}
			if (planningPeriodDayNames[i].equalsIgnoreCase(dn
					.get(dn.size() - 1))) {
				// takes under consideration only sequences of days having at
				// least one working shift
				if (employeeWorking(sb.toString())) {
					ss.add(sb.toString());
				}
				f = true;
			}
		}
		if (f == false) {
			// takes under consideration only sequences of days having at
			// least one working shift
			if (employeeWorking(sb.toString()))
				ss.add(sb.toString());
		}
		return ss;
	}

	private boolean employeeWorking(String string) {
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) != '-') {
				return true;
			}
		}
		return false;
	}

	private void assignWorkToScheduleAsStringFor(int emp_id, int timeunit) {
		char c = problem.shiftTypes.get(timeunit % S).getAliasId().charAt(0);
		employeeSchedules[emp_id].setCharAt(timeunit / S, c);
	}

	private void unassignWorkFromScheduleAsStringFor(int emp_id, int timeunit) {
		employeeSchedules[emp_id].setCharAt(timeunit / S, '-');
	}

	public List<Integer> getConsecutiveFreeDays(Employee emp) {
		updateConsecutiveFreeDays(emp);
		return consecutiveFreeDays[emp.getIndex()];
	}

	private void updateConsecutiveFreeDays(Employee emp) {
		List<Integer> cfds = consecutiveFreeDays[emp.getIndex()];
		cfds.clear();
		StringBuilder text = employeeSchedules[emp.getIndex()];
		Matcher matcher = pattern1.matcher(text);
		while (matcher.find()) {
			cfds.add(matcher.group().length());
		}
	}

	public List<Integer> getConsecutiveWorkDays(Employee emp) {
		updateConsecutiveWorkDays(emp);
		return consecutiveWorkDays[emp.getIndex()];
	}

	private void updateConsecutiveWorkDays(Employee emp) {
		List<Integer> cwds = consecutiveWorkDays[emp.getIndex()];
		cwds.clear();
		StringBuilder text = employeeSchedules[emp.getIndex()];
		Matcher matcher = pattern2.matcher(text);
		while (matcher.find()) {
			cwds.add(matcher.group().length());
		}
	}

	public List<Integer> getWorkDayOfYearNumbers(Employee emp) {
		List<Integer> dates = new ArrayList<Integer>();
		int emp_id = emp.getIndex();
		for (int i = 0; i < T; i++) {
			if (assignments[i][emp_id] == 1) {
				dates.add(planningPeriodDaysOfYear[i / S]);
			}
		}
		return dates;
	}

	public List<Integer> getDistinctWorkDayOfYearNumbers(Employee emp) {
		// EtmPoint point = etmMonitor
		// .createPoint("ScheduleHelper:getDistinctWorkDayOfYearNumbers");
		List<Integer> ds2 = new ArrayList<Integer>();
		StringBuilder sb = employeeSchedules[emp.getIndex()];
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) != '-') {
				ds2.add(planningPeriodDaysOfYear[i]);
			}
		}
		// point.collect();
		return ds2;
	}

	public void displayCostContributionPerEmployee() {
		int sum = 0;
		for (Employee emp : problem.employees) {
			System.out.printf("Employee %s (%d + %d)=%d\n", emp.id,
					getEmployeeCachedDayWorkCost(emp.getIndex()),
					getEmployeeCachedShiftCost(emp.getIndex()),
					getEmployeeCachedCost(emp.getIndex()));
			sum += getEmployeeCachedCost(emp.getIndex());
		}
		System.out.printf("Total cost = %d\n", sum);
	}

	public List<Integer> findTUsEmployee2WorksEmployee1Rests(int e1, int tu1,
			int e2) {
		List<Integer> tus = new ArrayList<Integer>();
		int d1 = tu1 / S;
		if (isEmployeeWorkingInDay(e2, d1))
			return tus;
		for (int i = 0; i < D; i++) {
			int work_e1 = -1;
			int work_e2 = -1;
			for (int j = 0; j < S; j++) {
				if (assignments[i * S + j][e1] == 1)
					work_e1 = i * S + j;
				if (assignments[i * S + j][e2] == 1)
					work_e2 = i * S + j;
			}
			if ((work_e1 == -1) && (work_e2 != -1)) {
				int tu2 = work_e2;
				if (hasRequiredSkills(e1, tu2) && hasRequiredSkills(e2, tu1))
					tus.add(work_e2);
			}
		}
		return tus;
	}

	private boolean hasRequiredSkills(int emp_id, int tu_id) {
		List<String> skills_tu_id = problem.shiftTypes.get(tu_id % S).requiredSkills;
		List<String> employee_skills = problem.employees.get(emp_id).skills;
		for (String skill : skills_tu_id) {
			if (!employee_skills.contains(skill))
				return false;
		}
		return true;
	}

	public boolean isEmployeeWorkingInDay(int emp_id, int day_id) {
		boolean f = false;
		for (int j = 0; j < S; j++) {
			if (assignments[day_id * S + j][emp_id] == 1)
				return true;
		}
		return f;
	}

	public boolean isEmployeeWorkingInTimeunit(int emp_id, int tu_id) {
		return (assignments[tu_id][emp_id] == 1);
	}

	public int getNumAssignments(int emp_id) {
		int c = 0;
		for (int i = 0; i < T; i++)
			if (assignments[i][emp_id] == 1)
				c++;
		return c;
	}

	public List<Employee> getEmployeesWorkingOnTimeunit(int tu_id) {
		List<Employee> employees = new ArrayList<Employee>();
		for (int j = 0; j < E; j++) {
			if (assignments[tu_id][j] == 1) {
				employees.add(problem.employees.get(j));
			}
		}
		return employees;
	}

	public Schedule getAsGoulasSchedule() {
		Schedule schedule = new Schedule(problem);
		for (int i = 0; i < T; i++) {
			for (int j = 0; j < E; j++) {
				if (assignments[i][j] == 1) {
					schedule.schedule(problem.employees.get(j), i);
				}
			}
		}
		return schedule;
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
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%20s", "Employee " + e.id));
		for (int i = 0; i < problem.timeunits.size(); i++) {
			if (i % problem.shiftTypes.size() == 0) {
				sb.append("|");
			}
			if (isEmployeeWorkingInTimeunit(e.getIndex(), i)) {
				sb.append(String.format("%2s",
						problem.timeunits.get(i).shiftType.id));
			} else
				sb.append("  ");
		}
		sb.append("|\n");
		return sb.toString();
	}

	@Override
	public ScheduleHelper cloneSchedule() {
		ScheduleHelper clonedSchedule;
		clonedSchedule = new ScheduleHelper(problem);
		for (Employee emp : problem.employees) {
			int i = emp.getIndex();
			clonedSchedule.employeeSchedules[i] = new StringBuilder(
					employeeSchedules[i].toString());
			clonedSchedule.consecutiveFreeDays[i] = new ArrayList<Integer>();
			fillUp(clonedSchedule.consecutiveFreeDays[i],
					consecutiveFreeDays[i]);
			clonedSchedule.consecutiveWorkDays[i] = new ArrayList<Integer>();
			fillUp(clonedSchedule.consecutiveWorkDays[i],
					consecutiveWorkDays[i]);
			clonedSchedule.timeUnitsShiftOffRequestsList[i] = new ArrayList<Integer>();
			fillUp(clonedSchedule.timeUnitsShiftOffRequestsList[i],
					timeUnitsShiftOffRequestsList[i]);
			clonedSchedule.dayOffRequestsList[i] = new ArrayList<Integer>();
			fillUp(clonedSchedule.dayOffRequestsList[i], dayOffRequestsList[i]);
		}
		for (int i = 0; i < E; i++) {
			clonedSchedule.employeeCachedDayWorkCost[i] = employeeCachedDayWorkCost[i];
			clonedSchedule.employeeCachedShiftCost[i] = employeeCachedShiftCost[i];
		}
		for (int i = 0; i < T; i++) {
			for (int j = 0; j < E; j++) {
				clonedSchedule.assignments[i][j] = assignments[i][j];
			}
		}
		for(int i=0;i<E;i++)
			for(int j=0;j<W;j++){
				clonedSchedule.sol[i][j]=sol[i][j];
			}
		return clonedSchedule;
	}

	private void fillUp(List<Integer> toList, List<Integer> fromList) {
		for (Integer x : fromList) {
			toList.add(x);
		}
	}

	public List<Integer> getTimeUnitsWithWorkFor(int emp_id) {
		List<Integer> tuww = new ArrayList<Integer>();
		for (int i = 0; i < T; i++) {
			if (assignments[i][emp_id] == 1) {
				tuww.add(i);
			}
		}
		return tuww;
	}

	public int skillMatching(int emp_id, int tu_id) {
		return skillMatchingCost[emp_id][tu_id % S];
	}

	/**
	 * @return the employeeCachedDayWorkCost
	 */
	public final int[] getEmployeeCachedDayWorkCost() {
		return employeeCachedDayWorkCost;
	}

	/**
	 * @return the employeeCachedShiftCost
	 */
	public final int[] getEmployeeCachedShiftCost() {
		return employeeCachedShiftCost;
	}

	// speed up (14-6-2010)
	public int getDayOffRequestCostFor(int emp_id) {
		int cost = 0;
		for (Integer doy : dayOffRequestsList[emp_id]) {
			int k = dc.get(doy);
			for (int shift_type_id = 0; shift_type_id < S; shift_type_id++) {
				int time_unit_id = k * S + shift_type_id;
				if (assignments[time_unit_id][emp_id] == 1) {
					cost += dayOffRequests[k][emp_id];
				}
			}
		}
		return cost;
	}

	// speed up (15-6-2010)
	// Based on solveweek: An assignment always occurs on the first shift
	public int get_CV_DayOffRequestCostFor(int emp_id) {
		int cost = 0;
		for (Integer doy : dayOffRequestsList[emp_id]) {
			int k = dc.get(doy);
			if (assignments[k * S][emp_id] == 1) {
				cost += dayOffRequests[k][emp_id];
			}
		}
		return cost;
	}

	// speed up (14-6-2010)
	public int getShiftOffRequestCostFor(int emp_id) {
		int cost = 0;
		for (Integer time_unit_id : timeUnitsShiftOffRequestsList[emp_id]) {
			if (assignments[time_unit_id][emp_id] == 1) {
				cost += shiftOffRequests[time_unit_id][emp_id];
			}
		}
		return cost;
	}

	public void loadWorkDayAssignment(int[][] sol) {
		for (int employee_id = 0; employee_id < sol.length; employee_id++)
			for (int weekNo = 0; weekNo < sol[employee_id].length; weekNo++) {
				schedule(employee_id, weekNo, sol[employee_id][weekNo]);
			}
	}

	// ##############################################################
	// INTERFACE
//	@Override
//	public void schedule(int emp_id, int weekNo, int patternCode) {
//		sol[emp_id][weekNo] = patternCode;
//		String b = String.format("%7s", Integer.toBinaryString(patternCode))
//				.replace(" ", "0");
//		for (int i = 0; i < 7; i++) {
//			char c = b.charAt(i);
//			int timeunit = weekNo * 7 * S + i *S;
//			if (c == '1') {
//				schedule(emp_id, timeunit);
//			} else {
//				unschedule(emp_id, timeunit);
//			}
//		}
//	}

	@Override
	public void schedule(int emp_id, int timeunit) {
		super.schedule(emp_id, timeunit);
		assignWorkToScheduleAsStringFor(emp_id, timeunit);
	}

	@Override
	public void unschedule(int emp_id, int timeunit) {
		super.unschedule(emp_id,timeunit);
		unassignWorkFromScheduleAsStringFor(emp_id, timeunit);
	}

	// ##############################################################
	// NOT USED
	// ##############################################################

	@Deprecated
	public List<Integer> getDistinctWorkDayOfYearNumbers1(Employee emp) {
		List<Integer> ds2 = getWorkDayOfYearNumbers(emp);
		removeDuplicateWithOrder(ds2);
		return ds2;
	}

	@Deprecated
	private void removeDuplicateWithOrder(List<Integer> arList) {
		Set<Integer> set = new HashSet<Integer>();
		List<Integer> newList = new ArrayList<Integer>();
		for (Integer x : arList) {
			if (set.add(x))
				newList.add(x);
		}
		arList.clear();
		arList.addAll(newList);
	}

	@Deprecated
	public List<Integer> getListOfFirstDaysOfFreeWeekends(Employee emp) {
		List<Integer> listOfFirstDaysOfFreeWeekends = new ArrayList<Integer>();
		for (List<Integer> weekendDays : emp.getContract().dh
				.getDaysPerWeenendOfPlanningPeriods()) {
			boolean flag = true;
			inner: for (int weekendDay : weekendDays) {
				if (!getWorkAssignmentsForEmployeeAndDateOfYear(emp.getIndex(),
						weekendDay).isEmpty()) {
					flag = false;
					break inner;
				}
			}
			if (flag)
				listOfFirstDaysOfFreeWeekends.add(weekendDays.get(0));
		}
		return listOfFirstDaysOfFreeWeekends;
	}

	@Deprecated
	public List<Integer> getDaysOfYearWorkingForEmployee(int emp_id) {
		List<Integer> dyw = new ArrayList<Integer>();
		for (int i = 0; i < T; i++) {
			if (assignments[i][emp_id] == 1) {
				dyw.add(planningPeriodDaysOfYear[i / S]);
			}
		}
		return dyw;
	}
}
