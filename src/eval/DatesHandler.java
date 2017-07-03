package eval;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import model.Contract;
import model.InrcProblem;

public class DatesHandler {
	public static final String[] EN_DAY_NAMES = { "Sunday", "Monday",
			"Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
	public final static int[] DNE = { Calendar.SUNDAY, Calendar.MONDAY,
			Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
			Calendar.FRIDAY, Calendar.SATURDAY };

//	public static final String[] EN_DAY_NAMES = { "Monday",
//			"Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
//	public final static int[] DNE = { Calendar.MONDAY,
//			Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
//			Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY };

	private InrcProblem problem;
	private List<String> weekendDayNames = new ArrayList<String>();

	public List<Integer> weekendDayOfWeekIndices = new ArrayList<Integer>();

	List<ArrayList<Integer>> daysPerWeenendOfPlanningPeriod = new ArrayList<ArrayList<Integer>>();

	public DatesHandler(InrcProblem problem, Contract contract) {
		this.problem = problem;
		String wd = contract.weekendDefinition;
		for (int i = 0; i < EN_DAY_NAMES.length; i++) {
			if (wd.indexOf(EN_DAY_NAMES[i]) != -1) {
				weekendDayNames.add(EN_DAY_NAMES[i]);
				weekendDayOfWeekIndices.add(DNE[i]);
			}
		}
		generateWeekendList();
		// System.out.println(weekendPlanningPeriodDays);
	}

	private void generateWeekendList() {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(problem.getStartDate());
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(problem.getEndDate());
		boolean b = false;
		ArrayList<Integer> w = new ArrayList<Integer>();
		while (!cal1.after(cal2)) {
			int x = cal1.get(Calendar.DAY_OF_WEEK);

			if (isWeekendDay(x)) {
				if (b == false) {
					w = new ArrayList<Integer>();
					daysPerWeenendOfPlanningPeriod.add(w);
					w.add(cal1.get(Calendar.DAY_OF_YEAR));
					b = true;
				} else {
					w.add(cal1.get(Calendar.DAY_OF_YEAR));
				}
			} else
				b = false;
			cal1.add(Calendar.DAY_OF_YEAR, 1);
		}
	}

	private boolean isWeekendDay(int x) {
		return weekendDayOfWeekIndices.contains(x);
	}

	public List<ArrayList<Integer>> getDaysPerWeenendOfPlanningPeriods() {
		return daysPerWeenendOfPlanningPeriod;
	}

	public int getWeekendSize() {
		return weekendDayNames.size();
	}
}
