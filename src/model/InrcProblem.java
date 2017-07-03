package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 * @author cgogos, alefrag
 *
 *         The class represents the problem instance
 *
 *         PA 15052011 I have started moving some common structures that are
 *         produced after preprocessing of problem input data to the problem
 *         class
 *
 */
public class InrcProblem {

	public String datasetFileName;
	String id;
	String SchedulingPeriodId;
	Date startDate;
	Date endDate;
	public List<String> skills;
	public List<Employee> employees;
	public List<ShiftType> shiftTypes;
	public List<ShiftType> nightShiftTypes;
	public List<Contract> contracts;
	public List<Pattern> patterns;
	public List<TimeUnit> timeunits;
	public List<DayOfWeekCover> dayOfWeekCovers;
	public List<DateSpecificCover> dateSpecificCovers;
	public List<DayOffRequest> dayOffRequests;
	public List<ShiftOffRequest> shiftOffRequests;
	public Demands demands;

	// PA15052011 Caching structures generated by preprocessing
	// can combine p1 with p2 ?
	boolean[][] canCombineEmployee;
	/** days of week */
	int d_num = 7;
	/** number of weeks */
	private int w_num;
	/** total number of days */
	int tot_days;

	/** actual total number of days */
	int tot_days_real;

	// days to get Friday from StartDay
	int days_to_friday;

	public InrcProblem() {
		skills = new ArrayList<String>();
		employees = new ArrayList<Employee>();
		shiftTypes = new ArrayList<ShiftType>();
		nightShiftTypes = new ArrayList<ShiftType>();
		contracts = new ArrayList<Contract>();
		patterns = new ArrayList<Pattern>();
		timeunits = new ArrayList<TimeUnit>();
		dayOfWeekCovers = new ArrayList<DayOfWeekCover>();
		dateSpecificCovers = new ArrayList<DateSpecificCover>();
		dayOffRequests = new ArrayList<DayOffRequest>();
		shiftOffRequests = new ArrayList<ShiftOffRequest>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSchedulingPeriodId() {
		return SchedulingPeriodId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setSchedulingPeriodId(String schedulingPeriodId) {
		SchedulingPeriodId = schedulingPeriodId;
	}

	public void setStartDate(Date aDate) {
		startDate = aDate;
	}

	public void setEndDate(Date aDate) {
		endDate = aDate;
	}

	public void addSkill(String aSkill) {
		skills.add(aSkill);
		Collections.sort(skills);
	}

	public void addShiftType(ShiftType shiftType) {
		shiftTypes.add(shiftType);
		if (shiftType.isNightShift()) {
			nightShiftTypes.add(shiftType);
		}
	}

	public void addContract(Contract c) {
		contracts.add(c);
	}

	public void addEmployee(Employee e) {
		employees.add(e);
	}

	public void addPattern(Pattern p) {
		patterns.add(p);
	}

	public void addDayOfWeekCover(DayOfWeekCover co) {
		dayOfWeekCovers.add(co);
	}

	public void addDateSpecificCover(DateSpecificCover co) {
		dateSpecificCovers.add(co);
	}

	public void addDayOffRequest(DayOffRequest dor) {
		dayOffRequests.add(dor);
	}

	public void addShiftOffRequest(ShiftOffRequest sor) {
		shiftOffRequests.add(sor);
	}

	public void generateTimeUnits() {
		Collections.sort(shiftTypes);
		for (Date dt : getPlanningPeriodDates()) {
			for (ShiftType st : shiftTypes) {
				TimeUnit tu = new TimeUnit(st, dt);
				timeunits.add(tu);
			}
		}
		Collections.sort(timeunits);
	}

	public List<Date> getPlanningPeriodDates() {
		List<Date> ppDates = new ArrayList<Date>();
		GregorianCalendar aDateC = new GregorianCalendar();
		aDateC.setTime(startDate);
		GregorianCalendar endDateC = new GregorianCalendar();
		endDateC.setTime(endDate);
		while (aDateC.before(endDateC)) {
			ppDates.add(aDateC.getTime());
			aDateC.add(Calendar.DAY_OF_MONTH, 1);
		}
		ppDates.add(endDateC.getTime());
		return ppDates;
	}

	public void associateContractsWithEmployees() {
		for (Employee emp : employees) {
			for (Contract contract : contracts) {
				if (contract.id.equalsIgnoreCase(emp.contractId)) {
					emp.setContract(contract);
					contract.employeesOnContract.add(emp);
				}
			}
		}
	}

	public List<TimeUnit> getTimeUnits() {
		return timeunits;
	}

	public Pattern getPattern(String id) {
		for (Pattern p : patterns) {
			if (p.getId().equalsIgnoreCase(id)) {
				return p;
			}
		}
		return null;
	}

	public void generateDemands() {
		demands = new Demands(shiftTypes, getPlanningPeriodDates());
		for (DayOfWeekCover dwc : dayOfWeekCovers) {
			demands.parseDayOfWeekCover(dwc);
		}
		demands.populatePlanningPeriodDemands();
	}

	public Employee getEmployee(String id) {
		for (Employee emp : employees) {
			if (emp.id.equalsIgnoreCase(id)) {
				return emp;
			}
		}
		return null;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public ShiftType getShiftType(String id) {
		for (ShiftType st : shiftTypes) {
			if (st.id.equalsIgnoreCase(id)) {
				return st;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(this.toStringCondensed());

		sb.append("***********Skills***********\n");
		for (String skill : skills) {
			sb.append(skill + "\n");
		}
		sb.append("\n");
		sb.append("********Shift Types*********\n");
		for (ShiftType st : shiftTypes) {
			sb.append(st + "\n");
		}

		sb.append("\n");
		sb.append("********Night Shift Types*********\n");
		for (ShiftType st : nightShiftTypes) {
			sb.append(st + "\n");
		}

		sb.append("\n");
		sb.append("********Contracts*********\n");
		for (Contract c : contracts) {
			sb.append(c + "\n");
		}

		sb.append("\n");
		sb.append("********Employees*********" + "\n");
		for (Employee emp : employees) {
			sb.append(emp + "\n");
		}

		sb.append("\n");
		sb.append("********Time Units*********\n");
		for (TimeUnit tu : timeunits) {
			sb.append(tu + "\n");
		}

		sb.append("\n");
		sb.append("********Patterns*********\n");

		for (Pattern p : patterns) {
			sb.append(p + "\n");
		}

		sb.append("\n");
		sb.append("********DayOfWeekCovers*********\n");
		for (DayOfWeekCover dowc : dayOfWeekCovers) {
			sb.append(dowc + "\n");
		}

		sb.append("\n");
		sb.append("********DateSpecificCovers - unfinished *********\n");
		for (DateSpecificCover dsc : dateSpecificCovers) {
			sb.append(dsc + "\n");
		}

		sb.append("\n");
		sb.append("********DayOffRequests*********\n");
		for (DayOffRequest dor : dayOffRequests) {
			sb.append(dor + "\n");
		}

		sb.append("\n");
		sb.append("********ShiftOffRequests*********\n");
		for (ShiftOffRequest sor : shiftOffRequests) {
			sb.append(sor + "\n");
		}

		sb.append("\n");
		sb.append("********Week Demands*********\n");
		sb.append(demands.getWeekDemandsAsString());

		sb.append("\n");
		sb.append("********Planning Period Demands per ShiftType*********\n");
		sb.append(demands.getPlanningPeriodDemandsPerShiftTypeAsString());

		sb.append("\n");
		sb.append("********Planning Period Demands per Skill*********\n");
		sb.append(demands.getPlanningPeriodDemandsPerSkillAsString());

		return sb.toString();
	}

	public String toStringCondensed() {
		StringBuffer sb = new StringBuffer();
		sb
				.append("#####################################################################");
		sb.append("\nPROBLEM = " + id + "\n");
		sb.append(String.format("Start date=%tF\t End date=%tF\n", startDate,
				endDate));
		sb.append(String.format("Skills            =%d\t", skills.size()));
		sb.append(String.format("ShiftTypes        =%d\t", shiftTypes.size()));
		sb.append(String.format("Contracts         =%d\n\n", contracts.size()));
		for (Contract contract : contracts) {
			sb.append(String.format("%s\n", contract.toString()));
		}
		sb.append(String.format("Employees         =%d\t", employees.size()));
		sb.append(String.format("Patterns          =%d\t", patterns.size()));
		sb.append(String.format("DayOfWeekCovers   =%d\n", dayOfWeekCovers
				.size()));
		sb.append(String.format("DateSpecificCovers=%d\t", dateSpecificCovers
				.size()));
		sb.append(String.format("DayOffRequests    =%d\t", dayOffRequests
				.size()));
		sb.append(String.format("ShiftOffRequests  =%d\n", shiftOffRequests
				.size()));
		for (ShiftType st : shiftTypes) {
			sb.append(String.format("ShiftType     =%s --> %s\n", st.getId(),
					st.getRequiredSkills()));
		}
		sb
				.append("#####################################################################");
		sb.append("\n");
		return sb.toString();
	}

	// PA 15052011 Start

	public void preprocess(String fn) {
		datasetFileName = fn;
		generateTimeUnits();
		associateContractsWithEmployees();
		generateDemands();
		generateCanCombineEmployee();
		generateProblemDimensions();
	}

	public boolean canCombineEmployee(int employee1_id, int employee2_id) {
		return canCombineEmployee[employee1_id][employee2_id];
	}

	private void generateCanCombineEmployee() {
		// find connections
		canCombineEmployee = new boolean[getEmployees().size()][getEmployees()
				.size()];
		for (int p1 = 0; p1 < getEmployees().size(); p1++) {
			canCombineEmployee[p1][p1] = true;
			for (int p2 = p1 + 1; p2 < getEmployees().size(); p2++) {
				canCombineEmployee[p1][p2] = employees.get(p1).canSubstitute(
						employees.get(p2));
				canCombineEmployee[p2][p1] = canCombineEmployee[p1][p2];
			}
		}
	}

	private void generateProblemDimensions() {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(getStartDate());

		days_to_friday = Calendar.FRIDAY - calendar1.get(Calendar.DAY_OF_WEEK);

		long milliseconds1 = calendar1.getTimeInMillis();

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(getEndDate());
		long milliseconds2 = calendar2.getTimeInMillis();

		long diff = milliseconds2 - milliseconds1;
		// long diffSeconds = diff / 1000;
		// long diffMinutes = diff / (60 * 1000);
		// long diffHours = diff / (60 * 60 * 1000);
		long diffDays = diff / (24 * 60 * 60 * 1000);

		// how many weeks
		w_num = ((int) diffDays + 1) / 7;

		// 1 more week if needed
		if (((int) diffDays + 1) % 7 > 0)
			w_num++;

		// total days
		tot_days = d_num * getWeekNumber();

		// actual total days
		tot_days_real = (int) diffDays + 1;
	}

	public int getEmployeesNumber() {
		return employees.size();
	}

	public int getWeekNumber() {
		return w_num;
	}

	public int getSkillsNumber() {
		return skills.size();
	}

	public int getTotalDays() {
		return tot_days;
	}

	public int getTotalDaysReal() {
		return tot_days_real;
	}

	public int getDaysToFriday() {
		return days_to_friday;
	}

	public int getShiftTypesNumber() {
		return shiftTypes.size();
	}

	public int getContractId(int emp_id) {
		return Integer.parseInt(employees.get(emp_id).getContract().id);
	}

	public boolean hasSkill(int emp_id, int skill_id) {
		String sk = demands.skills.get(skill_id);

		for (String rs : employees.get(emp_id).skills) {
			if (rs.equals(sk))
				return true;
		}
		return false;
	}

	/**
	 * person i have shift type s ?
	 */
	public boolean can_do_shift(int i, int s) {
		// from s index find shift types
		ShiftType sk = shiftTypes.get(s);

		// try to find it in person skills
		for (String rs : employees.get(i).skills) {
			for (String rs1 : sk.getRequiredSkills()) {
				if (rs.equals(rs1))
					return true;
			}
		}
		return false;
	}

	/**
	 * find total number of asked days off for person p
	 */
	public int num_asked_day_off(int p) {
		int n = 0;
		for (DayOffRequest dor : dayOffRequests) {
			if (employees.get(p).id == dor.getEmployee().id)
				n++;
		}
		return n;
	}

	// PA 15052011 End
}