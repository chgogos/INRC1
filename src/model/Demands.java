package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Demands {
	String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
			"Saturday", "Sunday" };
	HashMap<String, Integer> dayNames;
	List<ShiftType> shiftTypes;
	List<Date> planningPeriod;
	public List<String> skills;
	int S; // number of shift types
	public int DAYS; // number of days in the planning period
	int[][] DWC; // Day of Week Cover Preferred
	public int[][] PPR; // Planning Period Requirements
	public int[][] PPSR; // Planning Period Skill Requirements

	public Demands(List<ShiftType> shiftTypes, List<Date> planningPeriod) {
		dayNames = new HashMap<String, Integer>();
		for (int i = 0; i < days.length; i++) {
			dayNames.put(days[i], i);
		}
		// for (int i = 0; i < 7; i++) {
		// // start on Monday
		// dayNames.put(DatesHandler.EN_DAY_NAMES[(i + 6) % 7], i);
		// }

		this.shiftTypes = shiftTypes;
		this.planningPeriod = planningPeriod;
		this.S = shiftTypes.size();
		this.DAYS = planningPeriod.size();
		DWC = new int[7][S];
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < S; j++) {
				DWC[i][j] = 0;
			}
		PPR = new int[DAYS][S];
		for (int i = 0; i < DAYS; i++)
			for (int j = 0; j < S; j++) {
				PPR[i][j] = 0;
			}

		// Assumption: each shiftType has exactly one skill
		Set<String> skillsSet = new HashSet<String>();
		for (ShiftType st : shiftTypes) {
			skillsSet.add(st.getRequiredSkills().get(0));
		}
		skills = new ArrayList<String>(skillsSet);
		Collections.sort(skills);
		PPSR = new int[DAYS][skills.size()];
		for (int i = 0; i < DAYS; i++)
			for (int j = 0; j < skills.size(); j++) {
				PPSR[i][j] = 0;
			}
	}

	void parseDayOfWeekCover(DayOfWeekCover dwc) {
		int i = dayNames.get(dwc.day);
		for (Cover cover : dwc.covers) {
			int j = getIndexOfShiftType(cover.shiftType.id);
			DWC[i][j] = cover.getPreferred();
		}
	}

	void populatePlanningPeriodDemands() {
		int i = 0;
		for (Date dte : planningPeriod) {
			int dow = dte.getDay();
			dow = dow == 0 ? 6 : dow - 1;
			for (int j = 0; j < S; j++) {
				PPR[i][j] = DWC[dow][j];
				PPSR[i][getSkillIndexOf(j)] += DWC[dow][j];
			}
			i++;
		}
	}

	private int getSkillIndexOf(int j) {
		for (int i = 0; i < skills.size(); i++) {
			if (skills.get(i).equalsIgnoreCase(
					shiftTypes.get(j).getRequiredSkills().get(0))) {
				return i;
			}
		}
		throw new Error("This should not happen");
	}

	private int getIndexOfShiftType(String shift) {
		int pos = 0;
		for (ShiftType st : shiftTypes) {
			// TODO: (George) Chris check, ta ID stin XML einai case sesitive,
			// ara de nomizw oti thelei ignorecase.
			if (st.id.equalsIgnoreCase(shift))
				return pos;
			else
				pos++;
		}
		return pos;
	}

	public String getWeekDemandsAsString() {
		StringBuffer sb = new StringBuffer("XXXXXXXXXX");
		for (ShiftType st : shiftTypes) {
			sb.append(String.format("%5s", st.id, st.requiredSkills));
		}
		for (int i = 0; i < 7; i++) {
			sb.append(String.format("\n%9s|", days[i]));
			for (int j = 0; j < S; j++)
				sb.append(String.format("%5d", DWC[i][j]));
		}
		sb.append("\n\n");
		return sb.toString();
	}

	public String getPlanningPeriodDemandsPerShiftTypeAsString() {
		StringBuffer sb = new StringBuffer("XXXXXXXXXXXXXXX");
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy E");
		for (ShiftType st : shiftTypes) {
			sb.append(String.format("%5s", st.id));
		}
		for (int i = 0; i < planningPeriod.size(); i++) {
			sb.append(String
					.format("\n%14s|", df.format(planningPeriod.get(i))));
			for (int j = 0; j < S; j++)
				sb.append(String.format("%5d", PPR[i][j]));
		}
		sb.append("\n\n");
		return sb.toString();
	}

	public String getPlanningPeriodDemandsPerSkillAsString() {
		StringBuffer sb = new StringBuffer("XXXXXXXXXXXXXXX");
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy E");
		for (String sk : skills) {
			sb.append(String.format("%10s", sk));
		}
		for (int i = 0; i < planningPeriod.size(); i++) {
			sb.append(String
					.format("\n%14s|", df.format(planningPeriod.get(i))));
			for (int j = 0; j < skills.size(); j++)
				sb.append(String.format("%10d", PPSR[i][j]));
		}
		sb.append("\n\n");
		return sb.toString();
	}

	public int getEmployeesNoDemand(int time_unit_id) {
		return PPR[time_unit_id / S][time_unit_id % S];
	}
}
