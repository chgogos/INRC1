package eval;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import model.Contract;
import model.Employee;
import model.InrcProblem;
import model.Pattern;
import model.Schedule;
import model.ScheduleInterface;
import model.WorkAssignment;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

public class CostEvaluator implements Evaluator {
	// private static final EtmMonitor etmMonitor = EtmManager.getEtmMonitor();

	public ScheduleHelper scheduleHelper;
	InrcProblem problem;

	private int singleAssignmentPerDayCost; // ok
	private int maxNumAssignmentsCost; // ok
	private int minNumAssignmentsCost; // ok
	private int maxConsecutiveWorkingDaysCost; // ok
	private int minConsecutiveWorkingDaysCost; // ok
	private int maxConsecutiveFreeDaysCost; // ok
	private int minConsecutiveFreeDaysCost; // ok
	private int maxConsecutiveWorkingWeekendsCost; // ok
	private int minConsecutiveWorkingWeekendsCost; // ok
	private int maxWorkingWeekendsinFourWeeksCost; // ok (not tested)
	private int incompleteWorkingWeekendsCost; // ok
	private int identicalShiftTypesDuringWeekendCost; // ok
	private int twoFreeDaysAfterNightShiftCost;
	private int alternativeSkillCost; // ok
	private int unwantedPatternsCost; // ok
	private int dayOffRequestsCost; // ok
	private int shiftOffRequestsCost; // ok

	public CostEvaluator(ScheduleHelper sh) {
		this.scheduleHelper = sh;
		problem = scheduleHelper.getProblem();
		for (Contract contract : problem.contracts) {
			DatesHandler dh = new DatesHandler(problem, contract);
			contract.dh = dh;
		}
	}

	public InrcProblem getProblem() {
		return problem;
	}

	/**
	 * gets cached cost from the ScheduleHelper object.
	 */
	public int getCost() {
		return scheduleHelper.getCachedCost();
		// return singleAssignmentPerDayCost + maxNumAssignmentsCost
		// + minNumAssignmentsCost + maxConsecutiveWorkingDaysCost
		// + minConsecutiveWorkingDaysCost + maxConsecutiveFreeDaysCost
		// + minConsecutiveFreeDaysCost
		// + maxConsecutiveWorkingWeekendsCost
		// + minConsecutiveWorkingWeekendsCost
		// + maxWorkingWeekendsinFourWeeksCost
		// + incompleteWorkingWeekendsCost
		// + identicalShiftTypesDuringWeekendCost + alternativeSkillCost
		// + twoFreeDaysAfterNightShiftCost + unwantedPatternsCost
		// + dayOffRequestsCost + shiftOffRequestsCost;
	}

	public String costAsString() {
		singleAssignmentPerDayCost = getSingleAssignmentPerDayCost();
		maxNumAssignmentsCost = getMaxNumAssignmentsCost();
		minNumAssignmentsCost = getMinNumAssignmentsCost();
		maxConsecutiveWorkingDaysCost = getMaxConsecutiveWorkingDaysCost();
		minConsecutiveWorkingDaysCost = getMinConsecutiveWorkingDaysCost();
		maxConsecutiveFreeDaysCost = getMaxConsecutiveFreeDaysCost();
		minConsecutiveFreeDaysCost = getMinConsecutiveFreeDaysCost();
		maxConsecutiveWorkingWeekendsCost = getMaxConsecutiveWorkingWeekendsCost();
		minConsecutiveWorkingWeekendsCost = getMinConsecutiveWorkingWeekendsCost();
		maxWorkingWeekendsinFourWeeksCost = getMaxWorkingWeekendsInFourWeeksCost();
		incompleteWorkingWeekendsCost = getIncompleteWorkingWeekendsCost();
		identicalShiftTypesDuringWeekendCost = getIdenticalShiftTypeDuringWeekendCost();
		twoFreeDaysAfterNightShiftCost = getTwoFreeDaysAfterNightShiftCost();
		alternativeSkillCost = getAlternativeSkillCost();
		unwantedPatternsCost = getUnwantedPatternsCost();
		dayOffRequestsCost = getDayOffRequestsCost();
		shiftOffRequestsCost = getShiftOffRequestsCost();
		return String.format("SingleAssignmentPerDayCost=%d\n"
				+ "MaxNumAssignments=%d\n" + "MinNumAssignments=%d\n"
				+ "MaxConsecutiveWorkingDays=%d\n"
				+ "MinConsecutiveWorkingDays=%d\n"
				+ "MaxConsecutiveFreeDays=%d\n" + "MinConsecutiveFreeDays=%d\n"
				+ "MaxConsecutiveWorkingWeekends=%d\n"
				+ "MinConsecutiveWorkingWeekends=%d\n"
				+ "MaxWorkingWeekendsinFourWeeks=%d\n"
				+ "IncompleteWorkingWeekends=%d\n"
				+ "IdenticalShiftTypesDuringWeekend=%d\n"
				+ "TwoFreeDaysAfterNightShift=%d\n"
				+ "AlternativeSkillCategory=%d\n" + "UnwantedPatterns=%d\n"
				+ "DayOff=%d\n" + "ShiftOff=%d\n", singleAssignmentPerDayCost,
				maxNumAssignmentsCost, minNumAssignmentsCost,
				maxConsecutiveWorkingDaysCost, minConsecutiveWorkingDaysCost,
				maxConsecutiveFreeDaysCost, minConsecutiveFreeDaysCost,
				maxConsecutiveWorkingWeekendsCost,
				minConsecutiveWorkingWeekendsCost,
				maxWorkingWeekendsinFourWeeksCost,
				incompleteWorkingWeekendsCost,
				identicalShiftTypesDuringWeekendCost,
				twoFreeDaysAfterNightShiftCost, alternativeSkillCost,
				unwantedPatternsCost, dayOffRequestsCost, shiftOffRequestsCost);
	}

	public String costDayWorkAsString() {
		singleAssignmentPerDayCost = getSingleAssignmentPerDayCost();
		maxNumAssignmentsCost = getMaxNumAssignmentsCost();
		minNumAssignmentsCost = getMinNumAssignmentsCost();
		maxConsecutiveWorkingDaysCost = getMaxConsecutiveWorkingDaysCost();
		minConsecutiveWorkingDaysCost = getMinConsecutiveWorkingDaysCost();
		maxConsecutiveFreeDaysCost = getMaxConsecutiveFreeDaysCost();
		minConsecutiveFreeDaysCost = getMinConsecutiveFreeDaysCost();
		maxConsecutiveWorkingWeekendsCost = getMaxConsecutiveWorkingWeekendsCost();
		minConsecutiveWorkingWeekendsCost = getMinConsecutiveWorkingWeekendsCost();
		maxWorkingWeekendsinFourWeeksCost = getMaxWorkingWeekendsInFourWeeksCost();
		incompleteWorkingWeekendsCost = getIncompleteWorkingWeekendsCost();
		dayOffRequestsCost = getDayOffRequestsCost();
		return String.format("SingleAssignmentPerDayCost=%d\n"
				+ "MaxNumAssignments=%d\n" + "MinNumAssignments=%d\n"
				+ "MaxConsecutiveWorkingDays=%d\n"
				+ "MinConsecutiveWorkingDays=%d\n"
				+ "MaxConsecutiveFreeDays=%d\n" + "MinConsecutiveFreeDays=%d\n"
				+ "MaxConsecutiveWorkingWeekends=%d\n"
				+ "MinConsecutiveWorkingWeekends=%d\n"
				+ "MaxWorkingWeekendsinFourWeeks=%d\n"
				+ "IncompleteWorkingWeekends=%d\n" + "DayOff=%d\n",
				singleAssignmentPerDayCost, maxNumAssignmentsCost,
				minNumAssignmentsCost, maxConsecutiveWorkingDaysCost,
				minConsecutiveWorkingDaysCost, maxConsecutiveFreeDaysCost,
				minConsecutiveFreeDaysCost, maxConsecutiveWorkingWeekendsCost,
				minConsecutiveWorkingWeekendsCost,
				maxWorkingWeekendsinFourWeeksCost,
				incompleteWorkingWeekendsCost,
				identicalShiftTypesDuringWeekendCost,
				twoFreeDaysAfterNightShiftCost, alternativeSkillCost,
				unwantedPatternsCost, dayOffRequestsCost);
	}

	// start: SingleAssignmentPerDayCost ##########################
	public int getSingleAssignmentPerDayCost() {
		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			if (!emp.getContract().singleAssignmentPerDayB)
				continue;
			cost = cost + getSingleAssignmentPerDayCostFor(emp);
		}
		return cost;
	}

	// end: SingleAssignmentPerDayCost ##########################

	public int getSingleAssignmentPerDayCostFor(Employee emp) {
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getSingleAssignmentPerDayCostFor");
		List<Integer> wd = scheduleHelper.getWorkDayOfYearNumbers(emp);
		int c = 0;
		for (int i = 1; i < wd.size(); i++) {
			if (wd.get(i) == wd.get(i - 1))
				c++;
		}
		// point.collect();
		return c * emp.getContract().singleAssignmentPerDayW;
	}

	// start: NumAssignmentsCost Max/Min ##########################
	public int getMaxNumAssignmentsCost() {
		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			cost += getMaxNumAssignmentsCostFor(emp);
		}
		return cost;
	}

	public int getMaxNumAssignmentsCostFor(Employee emp) {
		if (!emp.getContract().maxNumAssignmentsB)
			return 0;
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getMaxNumAssignmentsCostFor");
		int cost = 0;
		int c = getNumAssignments(emp);
		if (c > emp.getContract().maxNumAssignments) {
			cost = (c - emp.getContract().maxNumAssignments)
					* emp.getContract().maxNumAssignmentsW;
		}
		// point.collect();
		return cost;
	}

	// speed up method
	public int getMaxNumAssignmentsCostFor(Employee emp, int numAssignments) {
		if (!emp.getContract().maxNumAssignmentsB)
			return 0;
		int cost = 0;
		int c = numAssignments;
		if (c > emp.getContract().maxNumAssignments) {
			cost = (c - emp.getContract().maxNumAssignments)
					* emp.getContract().maxNumAssignmentsW;
		}
		return cost;
	}

	public int getMinNumAssignmentsCost() {
		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			cost += getMinNumAssignmentsCostFor(emp);
		}
		return cost;
	}

	public int getMinNumAssignmentsCostFor(Employee emp) {
		if (!emp.getContract().minNumAssignmentsB)
			return 0;
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getMinNumAssignmentsCostFor");
		int cost = 0;
		int c = getNumAssignments(emp);
		if (c < emp.getContract().minNumAssignments) {
			cost = (emp.getContract().minNumAssignments - c)
					* emp.getContract().minNumAssignmentsW;
		}
		// point.collect();
		return cost;
	}

	// speed up method
	public int getMinNumAssignmentsCostFor(Employee emp, int numAssignments) {
		if (!emp.getContract().minNumAssignmentsB)
			return 0;
		int cost = 0;
		int c = numAssignments;
		if (c < emp.getContract().minNumAssignments) {
			cost = (emp.getContract().minNumAssignments - c)
					* emp.getContract().minNumAssignmentsW;
		}
		return cost;
	}

	private int getNumAssignments(Employee emp) {
		return scheduleHelper.getNumAssignments(emp.getIndex());
	}

	// End: NumAssignmentsCost Max/Min ##########################

	// Start: ConsecutiveWorkingDaysCost###################################
	public int getMaxConsecutiveWorkingDaysCost() {
		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			cost = cost + getMaxConsecutiveWorkingDaysCostFor(emp);
		}
		return cost;
	}

	public int getMaxConsecutiveWorkingDaysCostFor(Employee emp) {
		if (!emp.getContract().maxConsecutiveWorkingDaysB)
			return 0;
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getMaxConsecutiveWorkingDaysCostFor");
		List<Integer> cwd = getConsecutiveWorkDays(emp);
		int cost = 0;
		for (Integer c : cwd) {
			if (c > emp.getContract().maxConsecutiveWorkingDays) {
				cost += (c - emp.getContract().maxConsecutiveWorkingDays)
						* emp.getContract().maxConsecutiveWorkingDaysW;
			}
		}
		// point.collect();
		return cost;
	}

	// speed up
	private int getMaxConsecutiveWorkingDaysCostFor(Employee emp,
			List<Integer> cwd) {
		if (!emp.getContract().maxConsecutiveWorkingDaysB)
			return 0;
		int cost = 0;
		for (Integer c : cwd) {
			if (c > emp.getContract().maxConsecutiveWorkingDays) {
				cost += (c - emp.getContract().maxConsecutiveWorkingDays)
						* emp.getContract().maxConsecutiveWorkingDaysW;
			}
		}
		return cost;
	}

	public int getMinConsecutiveWorkingDaysCost() {
		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			cost = cost + getMinConsecutiveWorkingDaysCostFor(emp);
		}
		return cost;
	}

	public int getMinConsecutiveWorkingDaysCostFor(Employee emp) {
		if (!emp.getContract().minConsecutiveWorkingDaysB)
			return 0;
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getMinConsecutiveWorkingDaysCostFor");
		List<Integer> cwd = getConsecutiveWorkDays(emp);
		int cost = 0;
		for (Integer c : cwd) {
			if (c < emp.getContract().minConsecutiveWorkingDays) {
				cost += (emp.getContract().minConsecutiveWorkingDays - c)
						* emp.getContract().minConsecutiveWorkingDaysW;
			}
		}
		// point.collect();
		return cost;
	}

	// speed up
	private int getMinConsecutiveWorkingDaysCostFor(Employee emp,
			List<Integer> cwd) {
		if (!emp.getContract().minConsecutiveWorkingDaysB)
			return 0;
		int cost = 0;
		for (Integer c : cwd) {
			if (c < emp.getContract().minConsecutiveWorkingDays) {
				cost += (emp.getContract().minConsecutiveWorkingDays - c)
						* emp.getContract().minConsecutiveWorkingDaysW;
			}
		}
		return cost;
	}

	private List<Integer> getConsecutiveWorkDays(Employee emp) {
		return scheduleHelper.getConsecutiveWorkDays(emp);
	}

	// End: ConsecutiveWorkingDaysCost Max/Min############################

	// Start: ConsecutiveFreeDaysCost Max/Min#############################
	public int getMaxConsecutiveFreeDaysCost() {
		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			cost = cost + getMaxConsecutiveFreeDaysCostFor(emp);
		}
		return cost;
	}

	public int getMaxConsecutiveFreeDaysCostFor(Employee emp) {
		if (!emp.getContract().maxConsecutiveFreeDaysB)
			return 0;
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getMaxConsecutiveFreeDaysCostFor");
		List<Integer> cfd = getConsecutiveFreeDays(emp);
		int cost = 0;
		for (Integer c : cfd) {
			if (c > emp.getContract().maxConsecutiveFreeDays) {
				cost += (c - emp.getContract().maxConsecutiveFreeDays)
						* emp.getContract().maxConsecutiveFreeDaysW;
			}
		}
		// point.collect();
		return cost;
	}

	// speed up
	public int getMaxConsecutiveFreeDaysCostFor(Employee emp, List<Integer> cfd) {
		if (!emp.getContract().maxConsecutiveFreeDaysB)
			return 0;
		int cost = 0;
		for (Integer c : cfd) {
			if (c > emp.getContract().maxConsecutiveFreeDays) {
				cost += (c - emp.getContract().maxConsecutiveFreeDays)
						* emp.getContract().maxConsecutiveFreeDaysW;
			}
		}
		return cost;
	}

	public int getMinConsecutiveFreeDaysCost() {
		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			cost = cost + getMinConsecutiveFreeDaysCostFor(emp);
		}
		return cost;
	}

	public int getMinConsecutiveFreeDaysCostFor(Employee emp) {
		if (!emp.getContract().minConsecutiveFreeDaysB)
			return 0;
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getMinConsecutiveFreeDaysCostFor");
		List<Integer> cwd = getConsecutiveFreeDays(emp);
		int cost = 0;
		for (Integer c : cwd) {
			if (c < emp.getContract().minConsecutiveFreeDays) {
				cost += (emp.getContract().minConsecutiveFreeDays - c)
						* emp.getContract().minConsecutiveFreeDaysW;
			}
		}
		// point.collect();
		return cost;
	}

	// speed up
	private int getMinConsecutiveFreeDaysCostFor(Employee emp, List<Integer> cfd) {
		if (!emp.getContract().minConsecutiveFreeDaysB)
			return 0;
		int cost = 0;
		for (Integer c : cfd) {
			if (c < emp.getContract().minConsecutiveFreeDays) {
				cost += (emp.getContract().minConsecutiveFreeDays - c)
						* emp.getContract().minConsecutiveFreeDaysW;
			}
		}
		return cost;
	}

	private List<Integer> getConsecutiveFreeDays(Employee emp) {
		return scheduleHelper.getConsecutiveFreeDays(emp);
	}

	// End: ConsecutiveFreeDaysCost Max/Min#############################

	// Start: ConsecutiveWorkingWeekendsCost Max/Min##############
	public int getMaxConsecutiveWorkingWeekendsCost() {
		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			cost += getMaxConsecutiveWorkingWeekendsCostFor(emp);
		}
		return cost;
	}

	public int getMaxConsecutiveWorkingWeekendsCostFor(Employee emp) {
		if (!emp.getContract().maxConsecutiveWorkingWeekendsB)
			return 0;
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getMaxConsecutiveWorkingWeekendsCost");
		int cost = 0;
		for (int c : getConsecutiveWorkingWeekendsCostFor(emp)) {
			if (c > emp.getContract().maxConsecutiveWorkingWeekends) {
				cost += (c - emp.getContract().maxConsecutiveWorkingWeekends)
						* emp.getContract().maxConsecutiveWorkingWeekendsW;
			}
		}
		// point.collect();
		return cost;
	}

	// speed up
	public int getMaxConsecutiveWorkingWeekendsCostFor(Employee emp,
			List<Integer> cww) {
		if (!emp.getContract().maxConsecutiveWorkingWeekendsB)
			return 0;
		int cost = 0;
		for (int c : cww) {
			if (c > emp.getContract().maxConsecutiveWorkingWeekends) {
				cost += (c - emp.getContract().maxConsecutiveWorkingWeekends)
						* emp.getContract().maxConsecutiveWorkingWeekendsW;
			}
		}
		return cost;
	}

	public int getMinConsecutiveWorkingWeekendsCost() {
		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			cost += getMinConsecutiveWorkingWeekendsCostFor(emp);
		}
		return cost;
	}

	public int getMinConsecutiveWorkingWeekendsCostFor(Employee emp) {
		if (!emp.getContract().minConsecutiveWorkingWeekendsB)
			return 0;
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getMinConsecutiveWorkingWeekendsCostFor");
		int cost = 0;
		for (int c : getConsecutiveWorkingWeekendsCostFor(emp)) {
			if (c < emp.getContract().minConsecutiveWorkingWeekends) {
				cost += (emp.getContract().minConsecutiveWorkingWeekends - c)
						* emp.getContract().minConsecutiveWorkingWeekendsW;
			}
		}
		// point.collect();
		return cost;
	}

	public int getMinConsecutiveWorkingWeekendsCostFor(Employee emp,
			List<Integer> cww) {
		if (!emp.getContract().minConsecutiveWorkingWeekendsB)
			return 0;
		int cost = 0;
		for (int c : cww) {
			if (c < emp.getContract().minConsecutiveWorkingWeekends) {
				cost += (emp.getContract().minConsecutiveWorkingWeekends - c)
						* emp.getContract().minConsecutiveWorkingWeekendsW;
			}
		}
		return cost;
	}

	public List<Integer> getConsecutiveWorkingWeekendsCostFor(Employee emp) {
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getConsecutiveWorkingWeekendsCostFor");
		List<Integer> consecutiveWeekends = new ArrayList<Integer>();
		List<Integer> dates = scheduleHelper
				.getDistinctWorkDayOfYearNumbers(emp);
		int WS = emp.getContract().getWeekendDaysList().size();
		int[] X = new int[WS];
		int k = 0;
		for (ArrayList<Integer> d : emp.getContract().getWeekendDaysList()) {
			for (int i = 0; i < dates.size(); i++) {
				if (d.contains(dates.get(i)))
					X[k] = 1;
			}
			k++;
		}
		for (int i = 1; i < WS; i++) {
			if ((X[i] > 0) && (X[i - 1] > 0)) {
				X[i] = X[i - 1] + 1;
				X[i - 1] = 0;
			}
			if (X[i - 1] > 0)
				consecutiveWeekends.add(X[i - 1]);
		}
		if (X[WS - 1] > 0)
			consecutiveWeekends.add(X[WS - 1]);

		// point.collect();
		return consecutiveWeekends;
	}

	// speed up
	private List<Integer> getConsecutiveWorkingWeekendsCostFor(Employee emp,
			List<Integer> dwdoy) {
		List<Integer> consecutiveWeekends = new ArrayList<Integer>();
		List<Integer> dates = dwdoy;
		int WS = emp.getContract().getWeekendDaysList().size();
		int[] X = new int[WS];
		int k = 0;
		for (ArrayList<Integer> d : emp.getContract().getWeekendDaysList()) {
			for (int i = 0; i < dates.size(); i++) {
				if (d.contains(dates.get(i)))
					X[k] = 1;
			}
			k++;
		}
		for (int i = 1; i < WS; i++) {
			if ((X[i] > 0) && (X[i - 1] > 0)) {
				X[i] = X[i - 1] + 1;
				X[i - 1] = 0;
			}
			if (X[i - 1] > 0)
				consecutiveWeekends.add(X[i - 1]);
		}
		if (X[WS - 1] > 0)
			consecutiveWeekends.add(X[WS - 1]);
		return consecutiveWeekends;
	}

	// End: ConsecutiveWorkingWeekendsCost Max/Min##############

	// Start: IncompleteWorkingWeekendsCost ####################
	public int getIncompleteWorkingWeekendsCost() {
		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			cost = cost + getIncompleteWorkingWeekendsCostFor(emp);
		}
		return cost;
	}

	public int getIncompleteWorkingWeekendsCostFor(Employee emp) {
		if (!emp.getContract().completeWeekendsB)
			return 0;
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getIncompleteWorkingWeekendsCostFor");
		int c = 0;
		List<Integer> workingDays = scheduleHelper
				.getDistinctWorkDayOfYearNumbers(emp);
		List<ArrayList<Integer>> weekends = emp.getContract()
				.getWeekendDaysList();
		for (ArrayList<Integer> weekend : weekends) {
			if (isIncompleteWeekend(weekend, workingDays))
				c++;
		}
		// point.collect();
		return c * emp.getContract().completeWeekendsW;
	}

	public int getIncompleteWorkingWeekendsCostFor(Employee emp,
			List<Integer> dwdoy) {
		if (!emp.getContract().completeWeekendsB)
			return 0;
		int c = 0;
		List<ArrayList<Integer>> weekends = emp.getContract()
				.getWeekendDaysList();
		for (ArrayList<Integer> weekend : weekends) {
			if (isIncompleteWeekend(weekend, dwdoy))
				c++;
		}
		return c * emp.getContract().completeWeekendsW;
	}

	private boolean isIncompleteWeekend(ArrayList<Integer> daysOfWeek,
			List<Integer> workingDays) {
		int c = 0;
		for (Integer x : daysOfWeek) {
			if (workingDays.contains(x))
				c++;
		}
		return ((c != 0) && (c < daysOfWeek.size()));
	}

	// End: IncompleteWorkingWeekendsCost ####################################

	// START: IdenticalShiftTypeDuringWeekend##################################
	public int getIdenticalShiftTypeDuringWeekendCost() {
		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			cost += getIdenticalShiftTypeDuringWeekendCostFor(emp);
		}
		return cost;
	}

	public int getIdenticalShiftTypeDuringWeekendCostFor(Employee emp) {
		if (!emp.getContract().identShiftTypesDuringWeekendB)
			return 0;
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getIdenticalShiftTypeDuringWeekendCostFor");
		int cost = 0;
		for (List<Integer> days : emp.getContract().dh.daysPerWeenendOfPlanningPeriod) {
			List<String> aList = new ArrayList<String>();
			for (Integer da : days) {
				aList.addAll(scheduleHelper.getShiftTypesForEmployee(emp
						.getIndex(), da));
			}

			if (aList.size() > 0) {
				Set<String> aSet = new HashSet<String>(aList);
				if (!((aSet.size() == 1) && (aList.size() == emp.getContract().dh
						.getWeekendSize())))
					cost += emp.getContract().identShiftTypesDuringWeekendW
							* aSet.size();
			}
		}
		// point.collect();
		return cost;
	}

	// END: IdenticalShiftTypeDuringWeekend################################

	// START: MaxWorkingWeekendsInFourWeeksCost################################
	public int getMaxWorkingWeekendsInFourWeeksCost() {
		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			cost += getMaxWorkingWeekendsInFourWeeksCostFor(emp);
		}
		return cost;
	}

	public int getMaxWorkingWeekendsInFourWeeksCostFor(Employee emp) {
		if (!emp.getContract().maxWorkingWeekendsInFourWeeksB)
			return 0;
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getMaxWorkingWeekendsInFourWeeksCostFor");
		int cost = 0;
		int c = getWorkingWeekends(emp);
		if (c > emp.getContract().maxWorkingWeekendsInFourWeeks) {
			cost = (c - emp.getContract().maxWorkingWeekendsInFourWeeks)
					* emp.getContract().maxWorkingWeekendsInFourWeeksW;
		}
		// point.collect();
		return cost;
	}

	private int getWorkingWeekends(Employee emp) {
		List<Integer> dates = getWorkDayInWeekendOfYearNumbers(emp);
		int[] X = new int[emp.getContract().getWeekendDaysList().size()];
		int k = 0;
		for (ArrayList<Integer> d : emp.getContract().getWeekendDaysList()) {
			for (int i = 0; i < dates.size(); i++) {
				if (d.contains(dates.get(i)))
					X[k] = 1;
			}
			k++;
		}
		int c = 0;
		for (int i = 0; i < X.length; i++) {
			if (X[i] == 1)
				c++;
		}
		return c;
	}

	// END: MaxWorkingWeekendsInFourWeeksCost################################

	// START: twoFreeDaysAfterNightShiftCost#################################
	public int getTwoFreeDaysAfterNightShiftCost() {
		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			cost += getTwoFreeDaysAfterNightShiftCostFor(emp);
		}
		return cost;
	}

	public int getTwoFreeDaysAfterNightShiftCostFor(Employee emp) {
		if (!emp.getContract().twoFreeDaysAfterNightShiftsB)
			return 0;
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getTwoFreeDaysAfterNightShiftCostFor");
		int c = 0;
		for (Integer dow : scheduleHelper.getDaysOfYearWorkingOnNightShift(emp
				.getIndex())) {
			List<WorkAssignment> wa1 = scheduleHelper
					.getWorkAssignmentsForEmployeeAndDateOfYear(emp.getIndex(),
							dow + 1);
			List<WorkAssignment> wa2 = scheduleHelper
					.getWorkAssignmentsForEmployeeAndDateOfYear(emp.getIndex(),
							dow + 2);
			if ((wa1.isEmpty()) && (wa2.isEmpty()))
				c++;
		}
		// point.collect();
		return c * emp.getContract().twoFreeDaysAfterNightShiftsW;
	}

	// END: twoFreeDaysAfterNightShiftCost#################################

	// START: alternativeSkillCost ########################################
	public int getAlternativeSkillCost() {
		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			cost += getAlternativeSkillCostFor(emp);
		}
		return cost;
	}

	public int getAlternativeSkillCostFor(Employee emp) {
		if (!emp.getContract().alternativeSkillCategoryB)
			return 0;
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getAlternativeSkillCategoryCostFor");
		int c = 0;
		for (Integer tu_id : scheduleHelper.getTimeUnitsWithWorkFor(emp
				.getIndex())) {
			int k = scheduleHelper.skillMatching(emp.getIndex(), tu_id);
			c = c + k;
		}
		// point.collect();
		return c * emp.getContract().alternativeSkillCategoryW;
	}

	// END: alternativeSkillCost ########################################

	public int getUnwantedPatternsCost() {

		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			cost += getUnwantedPatternsCostFor(emp);
		}
		return cost;
	}

	public int getUnwantedPatternsCostFor(Employee emp) {
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getUnwantedPatternsCostFor");
		int cost = 0;
		for (Pattern pat : emp.getContract().unwantedPatterns) {
			// System.out.println(pat.toString());
			cost += pat.getWeight()
					* getUnwantedPatternNumberFor(emp, pat.searchString());
		}
		// point.collect();
		return cost;
	}

	public int getUnwantedShiftTypePatternsCostFor(Employee emp) {
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getUnwantedShiftTypePatternsCostFor");
		int cost = 0;
		for (Pattern pat : emp.getContract().unwantedShiftTypePatterns) {
			// System.out.println(pat.toString());
			cost += pat.getWeight()
					* getUnwantedPatternNumberFor(emp, pat.searchString());
		}
		// point.collect();
		return cost;
	}

	public int getUnwantedAnyNoneExclusivePatternsCostFor(Employee emp) {
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getUnwantedAnyNoneExclusivePatternsCostFor");
		int cost = 0;
		for (Pattern pat : emp.getContract().unwantedAnyNoneExclusivePatterns) {
			// System.out.println(pat.toString());
			cost += pat.getWeight()
					* getUnwantedPatternNumberFor(emp, pat.searchString());
		}
		// point.collect();
		return cost;
	}

	public int getUnwantedPatternNumberFor(Employee emp,
			String regexSearchString) {
		if (regexSearchString.length() == 0)
			return 0;
		int c = 0;
		StringBuilder text = scheduleHelper.scheduleAsStringFor(emp);
		List<String> dn = getDayNamesIn(regexSearchString);
		if (!dn.isEmpty()) {
			List<String> ss = scheduleHelper.getSubSchedules(text, dn);
			for (String text2 : ss) {
				String t[] = regexSearchString.split(";");
				c += getSimpleUnwantedPatternNumberFor(text2, t[0]);
			}
		} else {
			c = getSimpleUnwantedPatternNumberFor(text.toString(),
					regexSearchString);
		}
		return c;
	}

	private List<String> getDayNamesIn(String regexSearchString) {
		String temp[] = regexSearchString.split(";");
		List<String> dn = new ArrayList<String>();
		for (int i = 1; i < temp.length; i++) {
			dn.add(temp[i]);
		}
		return dn;
	}

	public int getSimpleUnwantedPatternNumberFor(String text,
			String regexSearchString) {
		if (regexSearchString.length() == 0)
			return 0;
		int c = 0;
		// System.out.println("The schedule will be searched for "
		// + regexSearchString);
		java.util.regex.Pattern pattern = java.util.regex.Pattern
				.compile(regexSearchString);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			// System.out.printf("I found the text \"%s\" starting at "
			// + "index %d and ending at index %d.%n", matcher.group(),
			// matcher.start(), matcher.end());
			c++;
		}
		return c;
	}

	// start: DayOffRequestsCost #################################
	public int getDayOffRequestsCost() {
		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			cost += getDayOffRequestCostFor(emp);
		}
		return cost;
	}

	// speed up (14/6/2010)
	public int getDayOffRequestCostFor(Employee emp) {
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getDayOffRequestCostFor");
		int cost = scheduleHelper.getDayOffRequestCostFor(emp.getIndex());
		// point.collect();
		return cost;
	}

	public int getDayOffRequestCostFor1(Employee emp) {
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getDayOffRequestCostFor");
		int cost = 0;
		List<Integer> doysList = scheduleHelper.getDayOffRequests(emp
				.getIndex());
		for (Integer doy : doysList) {
			List<WorkAssignment> was = scheduleHelper
					.getWorkAssignmentsForEmployeeAndDateOfYear(emp.getIndex(),
							doy);
			for (WorkAssignment wa : was) {
				cost = cost
						+ scheduleHelper.getDayOffRequestWeight(wa.day_of_year,
								wa.emp_id);
			}
		}
		// point.collect();
		return cost;
	}

	// end: DayOffRequestsCost #################################

	// start: ShiftOffRequestsCost #################################
	public int getShiftOffRequestsCost() {
		int cost = 0;
		for (Employee emp : problem.getEmployees()) {
			cost += getShiftOffRequestsCostFor(emp);
		}
		return cost;
	}

	// speed up (14/06/2010)
	public int getShiftOffRequestsCostFor(Employee emp) {
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getShiftOffRequestCostFor");
		int cost = scheduleHelper.getShiftOffRequestCostFor(emp.getIndex());
		// point.collect();
		return cost;
	}

	public int getShiftOffRequestsCostFor1(Employee emp) {
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:getShiftOffRequestCostFor");
		int cost = 0;
		List<Integer> aList = scheduleHelper
				.getTimeUnitsWithShiftOffRequests(emp.getIndex());
		for (Integer time_unit_index : aList) {
			WorkAssignment wa = scheduleHelper
					.getWorkAssignmentForEmployeeAndTimeUnit(emp.getIndex(),
							time_unit_index);
			if (wa != null) {
				cost = cost
						+ scheduleHelper.getShiftOffRequestWeight(
								wa.time_unit_id, wa.emp_id);
			}
		}
		// point.collect();
		return cost;
	}

	// end: ShiftOffRequestsCost #################################

	private String asString(int[] X) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < X.length; i++)
			sb.append(X[i]);
		return sb.toString();
	}

	private List<Integer> getWorkDayInWeekendOfYearNumbers(Employee emp) {
		List<Integer> dates = new ArrayList<Integer>();
		for (int i = 0; i < problem.getTimeUnits().size(); i++) {
			if (scheduleHelper.isEmployeeWorkingInTimeunit(emp.getIndex(), i)) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(problem.timeunits.get(i).date);
				// if ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				// || (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY))
				if (emp.getContract().dh.weekendDayOfWeekIndices.contains(cal
						.get(Calendar.DAY_OF_WEEK)))
					dates.add(cal.get(Calendar.DAY_OF_YEAR));
			}
		}
		return dates;
	}

	public boolean isFeasible() {
		boolean flag = true;
		if (singleAssignmentPerDayCost > 0) {
			System.out.println("Single assignment per day is violated!");
			flag = false;
		} else {
			System.out.println("Single assignment per day is OK!");
		}
		if (!isDemandCovered()) {
			System.out.println("Demand is not covered!");
			flag = false;
		} else {
			System.out.println("Demand is OK!");
		}
		return flag;
	}

	public boolean isDemandCovered() {
		int M = problem.demands.PPR.length;
		int N = problem.shiftTypes.size();
		boolean flag = true;
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				/**
				 * int c = 0; for (Employee emp : scheduleHelper
				 * .getEmployeesWorkingOnTimeunit(i * N + j)) {
				 *
				 * List<String> employeeSkills = emp.skills; List<String>
				 * shiftTypeSkills = problem.shiftTypes.get(j)
				 * .getRequiredSkills(); boolean f = false; for (String s :
				 * shiftTypeSkills) { if (employeeSkills.contains(s)) { //
				 * System.out // .printf( //
				 * "Demand in day %d skill %d is coverred by %s\n", // i, j,
				 * emp.id); f = true; } } if (f) c++; }
				 **/
				int c = scheduleHelper.getEmployeesWorkingOnTimeunit(i * N + j)
						.size();
				if (problem.demands.PPR[i][j] != c) {
					System.out
							.printf(
									"Demand problem should be %d but is %d in (DAY_INDEX:%d, SHIFT_TYPE:%s)\n",
									problem.demands.PPR[i][j], c, i,
									problem.shiftTypes.get(j));
					flag = false;
				}
			}
		}
		return flag;
	}

	public StringBuilder scheduleAsStringFor(Employee emp) {
		return scheduleHelper.scheduleAsStringFor(emp);
	}

	public void evaluate() {
		// EtmPoint point = etmMonitor.createPoint("CostEvaluator:evaluate");
		// CG 16/6/2010
		for (Employee emp : problem.employees) {
			scheduleHelper.setEmployeeCachedDayWorkCost(emp.getIndex(),
					evaluateDayWorkCostFor(emp));
			scheduleHelper.setEmployeeCachedShiftCost(emp.getIndex(),
					evaluateShiftWorkCostFor(emp));
		}
		// CG 16/6/2010
		// point.collect();
	}

	public int evaluateCostFor(Employee emp) {
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:evaluateCostFor");
		int c1 = evaluateDayWorkCostFor(emp);
		int c2 = evaluateShiftWorkCostFor(emp);
		int c = c1 + c2;
		// point.collect();
		return c;
	}

	public int evaluateDayWorkCost() {
		int sum = 0;
		for (Employee emp : problem.employees) {
			sum += evaluateDayWorkCostFor(emp);
		}
		return sum;
	}

	public int evaluateDayWorkCostFor(Employee emp) {
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:evaluateDayWorkCostFor");
		int numberOfAssignments = getNumAssignments(emp);
		int c2 = getMaxNumAssignmentsCostFor(emp, numberOfAssignments);
		int c3 = getMinNumAssignmentsCostFor(emp, numberOfAssignments);

		List<Integer> cwd = getConsecutiveWorkDays(emp);
		int c4 = getMaxConsecutiveWorkingDaysCostFor(emp, cwd);
		int c5 = getMinConsecutiveWorkingDaysCostFor(emp, cwd);

		List<Integer> cfd = getConsecutiveFreeDays(emp);
		int c6 = getMaxConsecutiveFreeDaysCostFor(emp, cfd);
		int c7 = getMinConsecutiveFreeDaysCostFor(emp, cfd);

		List<Integer> dwdoy = scheduleHelper
				.getDistinctWorkDayOfYearNumbers(emp);
		List<Integer> cww = getConsecutiveWorkingWeekendsCostFor(emp, dwdoy);
		int c8 = getMaxConsecutiveWorkingWeekendsCostFor(emp, cww);
		int c9 = getMinConsecutiveWorkingWeekendsCostFor(emp, cww);
		int c10 = getMaxWorkingWeekendsInFourWeeksCostFor(emp);
		int c11 = getIncompleteWorkingWeekendsCostFor(emp, dwdoy);
		int c15a = getUnwantedAnyNoneExclusivePatternsCostFor(emp);
		int c16 = getDayOffRequestCostFor(emp);
		int c = c2 + c3 + c4 + c5 + c6 + c7 + c8 + c9 + c10 + c11 + c15a + c16;
		scheduleHelper.setEmployeeCachedDayWorkCost(emp.getIndex(), c);
		// point.collect();
		return c;
	}

	// called by SolveWeek
	public int evaluateDayWorkCostFor(Employee emp, int numberOfAssignments) {
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:evaluateDayWorkCostFor");

		int c2 = getMaxNumAssignmentsCostFor(emp, numberOfAssignments);
		int c3 = getMinNumAssignmentsCostFor(emp, numberOfAssignments);

		List<Integer> cwd = getConsecutiveWorkDays(emp);
		int c4 = getMaxConsecutiveWorkingDaysCostFor(emp, cwd);
		int c5 = getMinConsecutiveWorkingDaysCostFor(emp, cwd);

		List<Integer> cfd = getConsecutiveFreeDays(emp);
		int c6 = getMaxConsecutiveFreeDaysCostFor(emp, cfd);
		int c7 = getMinConsecutiveFreeDaysCostFor(emp, cfd);

		List<Integer> dwdoy = scheduleHelper
				.getDistinctWorkDayOfYearNumbers(emp);
		List<Integer> cww = getConsecutiveWorkingWeekendsCostFor(emp, dwdoy);
		int c8 = getMaxConsecutiveWorkingWeekendsCostFor(emp, cww);
		int c9 = getMinConsecutiveWorkingWeekendsCostFor(emp, cww);
		int c10 = getMaxWorkingWeekendsInFourWeeksCostFor(emp);
		int c11 = getIncompleteWorkingWeekendsCostFor(emp, dwdoy);
		int c15a = getUnwantedAnyNoneExclusivePatternsCostFor(emp);
		int c16 = scheduleHelper.get_CV_DayOffRequestCostFor(emp.getIndex());
		int c = c2 + c3 + c4 + c5 + c6 + c7 + c8 + c9 + c10 + c11 + c15a + c16;
		scheduleHelper.setEmployeeCachedDayWorkCost(emp.getIndex(), c);
		// point.collect();
		return c;
	}

	public int evaluateShiftWorkCostFor(Employee emp) {
		// EtmPoint point = etmMonitor
		// .createPoint("CostEvaluator:evaluateShiftWorkCostFor");
		int c12 = getIdenticalShiftTypeDuringWeekendCostFor(emp);
		int c14 = getTwoFreeDaysAfterNightShiftCostFor(emp);
		int c15b = getUnwantedShiftTypePatternsCostFor(emp);
		int c17 = getShiftOffRequestsCostFor(emp);
		int c18 = getAlternativeSkillCostFor(emp);
		int c19 = getSingleAssignmentPerDayCostFor(emp);
		int c = c12 + c14 + c15b + c17 + c18 + c19;
		scheduleHelper.setEmployeeCachedShiftCost(emp.getIndex(), c);
		// point.collect();
		return c;
	}

	public ScheduleInterface getSchedule() {
		return (ScheduleInterface) scheduleHelper;
	}

	@Override
	public void setSchedule(ScheduleInterface schedule) {
		scheduleHelper.populateAssignmentsFrom(schedule);
	}

	public void setSchedule(Schedule schedule) {
		scheduleHelper.populateAssignmentsFrom(schedule);
	}

	@Override
	public int evaluateDayWorkCost(int emp_id, int[] wcd) {
		int i, c = 0;
		int[] pwcd = new int[wcd.length];

		// save previous and change to w_cd
		for (i = 0; i < problem.getWeekNumber(); i++) {
			pwcd[i] = scheduleHelper.getWeekPattern(emp_id, i);
			if (pwcd[i] != wcd[i])
				scheduleHelper.schedule(emp_id, i, wcd[i]);
		}
		c = evaluateDayWorkCostFor(problem.employees.get(emp_id));
		// restore previous
		for (i = 0; i < problem.getWeekNumber(); i++) {
			if (pwcd[i] != wcd[i])
				scheduleHelper.schedule(emp_id, i, pwcd[i]);
		}
		return c;
	}

	@Override
	public String analyzeCost() {
		throw new UnsupportedOperationException("Not implemented yet");
	}



}