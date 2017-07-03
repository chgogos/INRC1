package model;

import java.util.ArrayList;
import java.util.List;

import eval.DatesHandler;


public class Contract {
	public String id;
	public String description;

	public boolean singleAssignmentPerDayB;
	public int singleAssignmentPerDayW;

	public boolean maxNumAssignmentsB;
	public int maxNumAssignments;
	public int maxNumAssignmentsW;

	public boolean minNumAssignmentsB;
	public int minNumAssignments;
	public int minNumAssignmentsW;

	public boolean maxConsecutiveWorkingDaysB;
	public int maxConsecutiveWorkingDays;
	public int maxConsecutiveWorkingDaysW;

	public boolean minConsecutiveWorkingDaysB;
	public int minConsecutiveWorkingDays;
	public int minConsecutiveWorkingDaysW;

	public boolean maxConsecutiveFreeDaysB;
	public int maxConsecutiveFreeDays;
	public int maxConsecutiveFreeDaysW;

	public boolean minConsecutiveFreeDaysB;
	public int minConsecutiveFreeDays;
	public int minConsecutiveFreeDaysW;

	public boolean maxConsecutiveWorkingWeekendsB;
	public int maxConsecutiveWorkingWeekends;
	public int maxConsecutiveWorkingWeekendsW;

	public boolean minConsecutiveWorkingWeekendsB;
	public int minConsecutiveWorkingWeekends;
	public int minConsecutiveWorkingWeekendsW;

	public boolean maxWorkingWeekendsInFourWeeksB;
	public int maxWorkingWeekendsInFourWeeks;
	public int maxWorkingWeekendsInFourWeeksW;

	public String weekendDefinition;

	public boolean completeWeekendsB;
	public int completeWeekendsW;

	public boolean identShiftTypesDuringWeekendB;
	public int identShiftTypesDuringWeekendW;

	public boolean noNightShiftBeforeFreeWeekendB;
	public int noNightShiftBeforeFreeWeekendW;

	public boolean twoFreeDaysAfterNightShiftsB;
	public int twoFreeDaysAfterNightShiftsW;

	public boolean alternativeSkillCategoryB;
	public int alternativeSkillCategoryW;

	public int numberOfUnwantedPatterns;

	public List<Pattern> unwantedPatterns;
	public List<Pattern> unwantedShiftTypePatterns;
	public List<Pattern> unwantedAnyNoneExclusivePatterns;

	public DatesHandler dh;
	
	//PA18052011
	public List<Employee> employeesOnContract;

	public List<ArrayList<Integer>> getWeekendDaysList() {
		return dh.getDaysPerWeenendOfPlanningPeriods();
	}

	public Contract(String id) {
		this.id = id;
		unwantedPatterns = new ArrayList<Pattern>();
		unwantedShiftTypePatterns = new ArrayList<Pattern>();
		unwantedAnyNoneExclusivePatterns = new ArrayList<Pattern>();
		
		//PA18052011
		employeesOnContract = new ArrayList<Employee>();
	}

	public String toString() {
		StringBuffer unwpat = new StringBuffer();
		for (Pattern p : unwantedPatterns) {
			unwpat.append(p.toString() + "\n");
		}
		return String
				.format(
						"ID=%2s %s SingleAssignmentPerDay[%s|%d]  "
								+ "\nMaxNumAssignments[%s|%d|%d] MinNumAssignments[%s|%d|%d]  "
								+ "\nMaxConsecutiveWorkingDays[%s|%d|%d] MinConsecutiveWorkingDays[%s|%d|%d] "
								+ "\nMaxConsecutiveFreeDays [%s|%d|%d] MinConsecutiveFreeDays [%s|%d|%d] "
								+ "\nMaxConsecutiveWorkingWeekends [%s|%d|%d] MinConsecutiveWorkingWeekends [%s|%d|%d] "
								+ "\nMaxWorkingWeekendsInFourWeeks [%s|%d|%d]"
								+ "\nWeekendDefinition [%s] CompleteWeekends [%s|%d]"
								+ "\nIdent.ShiftTypesDuringWeekend [%s|%d] NoNightShiftBeforeFreeWeekend [%s|%d]"
								+ "\ntwoFreeDaysAfterNightShifts [%s|%d] AlternativeSkillCategory [%s|%d]"
								+ "\nUnwantedPatterns \n%s" + "\n\n", id,
						description,
						singleAssignmentPerDayB ? "true" : "false",
						singleAssignmentPerDayW, maxNumAssignmentsB ? "true"
								: "false", maxNumAssignments,
						maxNumAssignmentsW, minNumAssignmentsB ? "true"
								: "false", minNumAssignments,
						minNumAssignmentsW, maxConsecutiveWorkingDaysB ? "true"
								: "false", maxConsecutiveWorkingDays,
						maxConsecutiveWorkingDaysW,
						minConsecutiveWorkingDaysB ? "true" : "false",
						minConsecutiveWorkingDays, minConsecutiveWorkingDaysW,
						maxConsecutiveFreeDaysB ? "true" : "false",
						maxConsecutiveFreeDays, maxConsecutiveFreeDaysW,
						minConsecutiveFreeDaysB ? "true" : "false",
						minConsecutiveFreeDays, minConsecutiveFreeDaysW,
						maxConsecutiveWorkingWeekendsB ? "true" : "false",
						maxConsecutiveWorkingWeekends,
						maxConsecutiveWorkingWeekendsW,
						minConsecutiveWorkingWeekendsB ? "true" : "false",
						minConsecutiveWorkingWeekends,
						minConsecutiveWorkingWeekendsW,
						maxWorkingWeekendsInFourWeeksB ? "true" : "false",
						maxWorkingWeekendsInFourWeeks,
						maxWorkingWeekendsInFourWeeksW, weekendDefinition,
						completeWeekendsB ? "true" : "false",
						completeWeekendsW,
						identShiftTypesDuringWeekendB ? "true" : "false",
						identShiftTypesDuringWeekendW,
						noNightShiftBeforeFreeWeekendB ? "true" : "false",
						noNightShiftBeforeFreeWeekendW,
						twoFreeDaysAfterNightShiftsB ? "true" : "false",
						twoFreeDaysAfterNightShiftsW,
						alternativeSkillCategoryB ? "true" : "false",
						alternativeSkillCategoryW, unwpat.toString());
	}

	public void setDescription(String des) {
		this.description = des;
	}

	public void hasSingleAssignmentPerDay(String tempVal) {
		singleAssignmentPerDayB = tempVal.equalsIgnoreCase("true");
	}

	public void setSingleAssignmentPerDayWeight(String weight) {
		singleAssignmentPerDayW = Integer.parseInt(weight);
	}

	public void hasMaxNumAssignments(String tempVal) {
		maxNumAssignmentsB = tempVal.equalsIgnoreCase("1");
	}

	public void setMaxNumAssignments(String tempVal) {
		maxNumAssignments = Integer.parseInt(tempVal);
	}

	public void setMaxNumAssignmentsWeight(String weight) {
		maxNumAssignmentsW = Integer.parseInt(weight);
	}

	public void hasMinNumAssignments(String tempVal) {
		minNumAssignmentsB = tempVal.equalsIgnoreCase("1");
	}

	public void setMinNumAssignments(String tempVal) {
		minNumAssignments = Integer.parseInt(tempVal);
	}

	public void setMinNumAssignmentsWeight(String weight) {
		minNumAssignmentsW = Integer.parseInt(weight);
	}

	public void hasMaxConsecutiveWorkingDays(String tempVal) {
		maxConsecutiveWorkingDaysB = tempVal.equalsIgnoreCase("1");
	}

	public void setMaxConsecutiveWorkingDays(String tempVal) {
		maxConsecutiveWorkingDays = Integer.parseInt(tempVal);
	}

	public void setMaxConsecutiveWorkingDaysWeight(String weight) {
		maxConsecutiveWorkingDaysW = Integer.parseInt(weight);
	}

	public void hasMinConsecutiveWorkingDays(String tempVal) {
		minConsecutiveWorkingDaysB = tempVal.equalsIgnoreCase("1");
	}

	public void setMinConsecutiveWorkingDays(String tempVal) {
		minConsecutiveWorkingDays = Integer.parseInt(tempVal);
	}

	public void setMinConsecutiveWorkingDaysWeight(String weight) {
		minConsecutiveWorkingDaysW = Integer.parseInt(weight);
	}

	public void hasMaxConsecutiveFreeDays(String tempVal) {
		maxConsecutiveFreeDaysB = tempVal.equalsIgnoreCase("1");
	}

	public void setMaxConsecutiveFreeDays(String tempVal) {
		maxConsecutiveFreeDays = Integer.parseInt(tempVal);
	}

	public void setMaxConsecutiveFreeDaysWeight(String weight) {
		maxConsecutiveFreeDaysW = Integer.parseInt(weight);
	}

	public void setMinConsecutiveFreeDaysWeight(String weight) {
		minConsecutiveFreeDaysW = Integer.parseInt(weight);
	}

	public void hasMinConsecutiveFreeDays(String tempVal) {
		minConsecutiveFreeDaysB = tempVal.equalsIgnoreCase("1");
	}

	public void setMinConsecutiveFreeDays(String tempVal) {
		minConsecutiveFreeDays = Integer.parseInt(tempVal);
	}

	public void hasMaxConsecutiveWorkingWeekends(String tempVal) {
		maxConsecutiveWorkingWeekendsB = tempVal.equalsIgnoreCase("1");
	}

	public void setMaxConsecutiveWorkingWeekends(String tempVal) {
		maxConsecutiveWorkingWeekends = Integer.parseInt(tempVal);
	}

	public void setMaxConsecutiveWorkingWeekendsWeight(String weight) {
		maxConsecutiveWorkingWeekendsW = Integer.parseInt(weight);
	}

	public void hasMinConsecutiveWorkingWeekends(String tempVal) {
		minConsecutiveWorkingWeekendsB = tempVal.equalsIgnoreCase("1");
	}

	public void setMinConsecutiveWorkingWeekends(String tempVal) {
		minConsecutiveWorkingWeekends = Integer.parseInt(tempVal);
	}

	public void setMinConsecutiveWorkingWeekendsWeight(String weight) {
		minConsecutiveWorkingWeekendsW = Integer.parseInt(weight);
	}

	public void hasMaxWorkingWeekendsInFourWeeks(String tempVal) {
		maxWorkingWeekendsInFourWeeksB = tempVal.equalsIgnoreCase("1");
	}

	public void setMaxWorkingWeekendsInFourWeeks(String tempVal) {
		maxWorkingWeekendsInFourWeeks = Integer.parseInt(tempVal);
	}

	public void setMaxWorkingWeekendsInFourWeeksWeight(String weight) {
		maxWorkingWeekendsInFourWeeksW = Integer.parseInt(weight);
	}

	public void setWeekendDefinition(String wd) {
		this.weekendDefinition = wd;
	}

	public void hasCompleteWeekends(String tempVal) {
		completeWeekendsB = tempVal.equalsIgnoreCase("true");
	}

	public void setCompleteWeekendsWeight(String weight) {
		completeWeekendsW = Integer.parseInt(weight);
	}

	public void hasIdentShiftTypesDuringWeekend(String tempVal) {
		identShiftTypesDuringWeekendB = tempVal.equalsIgnoreCase("true");
	}

	public void setIdentShiftTypesDuringWeekend(String weight) {
		identShiftTypesDuringWeekendW = Integer.parseInt(weight);
	}

	public void hasNoNightShiftBeforeFreeWeekend(String tempVal) {
		noNightShiftBeforeFreeWeekendB = tempVal.equalsIgnoreCase("true");
	}

	public void setNoNightShiftBeforeFreeWeekend(String weight) {
		noNightShiftBeforeFreeWeekendW = Integer.parseInt(weight);
	}

	public void hasTwoFreeDaysAfterNightShifts(String tempVal) {
		twoFreeDaysAfterNightShiftsB = tempVal.equalsIgnoreCase("true");
	}

	public void setTwoFreeDaysAfterNightShifts(String weight) {
		twoFreeDaysAfterNightShiftsW = Integer.parseInt(weight);
	}

	public void hasAlternativeSkillCategory(String tempVal) {
		alternativeSkillCategoryB = tempVal.equalsIgnoreCase("true");
	}

	public void setAlternativeSkillCategory(String weight) {
		alternativeSkillCategoryW = Integer.parseInt(weight);
	}

	public void addUnwantedPattern(Pattern p) {
		unwantedPatterns.add(p);
		if (p.shiftTypePattern) {
			unwantedShiftTypePatterns.add(p);
		} else {
			unwantedAnyNoneExclusivePatterns.add(p);
		}
	}

	// getters
	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public int getNumberOfUnwantedPatterns() {
		return unwantedPatterns.size();
	}

	public List<Pattern> getUnwantedPatterns() {
		return unwantedPatterns;
	}
}
