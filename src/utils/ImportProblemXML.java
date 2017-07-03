package utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import model.Contract;
import model.Cover;
import model.DateSpecificCover;
import model.DayOfWeekCover;
import model.DayOffRequest;
import model.Employee;
import model.InrcProblem;
import model.Pattern;
import model.PatternEntry;
import model.ShiftOffRequest;
import model.ShiftType;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ImportProblemXML extends DefaultHandler {

	InrcProblem problem;
	ShiftType tempShiftType;
	Contract tempContract;
	Employee tempEmployee;
	Pattern tempPattern;
	PatternEntry tempPatternEntry;
	DayOfWeekCover tempDayOfWeekCover;
	DateSpecificCover tempDateSpecificCover;
	DayOffRequest tempDayOffRequest;
	ShiftOffRequest tempShiftOffRequest;
	Cover tempCover;

	static SimpleDateFormat sdf = InrcDateFormat.dateFormat;
	Stack<String> tagsXmlStack;
	String tempVal;
	int emp_id = 0;
	int shift_type_id = 0;

	public ImportProblemXML() {
		problem = new InrcProblem();
		tagsXmlStack = new Stack<String>();
		shift_type_id = 0;
	}

	public void parseFile(String fn) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			sp.parse(fn, this);
			problem.preprocess(fn);
		} catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		} catch (SAXException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public InrcProblem getProblem() {
		return problem;
	}

	// Event Handlers
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		tempVal = "";
		tagsXmlStack.push(qName);
		if (qName.equalsIgnoreCase("SchedulingPeriod")) {
			problem.setId(attributes.getValue("ID"));
		} else if (qName.equalsIgnoreCase("Shift")) {
			if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodShiftTypesShift")) {
				tempShiftType = new ShiftType(attributes.getValue("ID"));
				tempShiftType.setAliasId(shift_type_id);
				shift_type_id++;
			}
		} else if (qName.equalsIgnoreCase("Contract")) {
			tempContract = new Contract(attributes.getValue("ID"));
		} else if (qName.equalsIgnoreCase("SingleAssignmentPerDay")) {
			tempContract.setSingleAssignmentPerDayWeight(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("MaxNumAssignments")) {
			tempContract.hasMaxNumAssignments(attributes.getValue("on"));
			tempContract.setMaxNumAssignmentsWeight(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("MinNumAssignments")) {
			tempContract.hasMinNumAssignments(attributes.getValue("on"));
			tempContract.setMinNumAssignmentsWeight(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("MaxConsecutiveWorkingDays")) {
			tempContract
					.hasMaxConsecutiveWorkingDays(attributes.getValue("on"));
			tempContract.setMaxConsecutiveWorkingDaysWeight(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("MinConsecutiveWorkingDays")) {
			tempContract
					.hasMinConsecutiveWorkingDays(attributes.getValue("on"));
			tempContract.setMinConsecutiveWorkingDaysWeight(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("MaxConsecutiveFreeDays")) {
			tempContract.hasMaxConsecutiveFreeDays(attributes.getValue("on"));
			tempContract.setMaxConsecutiveFreeDaysWeight(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("MinConsecutiveFreeDays")) {
			tempContract.hasMinConsecutiveFreeDays(attributes.getValue("on"));
			tempContract.setMinConsecutiveFreeDaysWeight(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("MaxConsecutiveWorkingWeekends")) {
			tempContract.hasMaxConsecutiveWorkingWeekends(attributes
					.getValue("on"));
			tempContract.setMaxConsecutiveWorkingWeekendsWeight(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("MinConsecutiveWorkingWeekends")) {
			tempContract.hasMinConsecutiveWorkingWeekends(attributes
					.getValue("on"));
			tempContract.setMinConsecutiveWorkingWeekendsWeight(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("MaxWorkingWeekendsInFourWeeks")) {
			tempContract.hasMaxWorkingWeekendsInFourWeeks(attributes
					.getValue("on"));
			tempContract.setMaxWorkingWeekendsInFourWeeksWeight(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("CompleteWeekends")) {
			tempContract.setCompleteWeekendsWeight(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("CompleteWeekends")) {
			tempContract.setCompleteWeekendsWeight(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("IdenticalShiftTypesDuringWeekend")) {
			tempContract.setIdentShiftTypesDuringWeekend(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("NoNightShiftBeforeFreeWeekend")) {
			tempContract.setNoNightShiftBeforeFreeWeekend(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("TwoFreeDaysAfterNightShifts")) {
			tempContract.setTwoFreeDaysAfterNightShifts(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("AlternativeSkillCategory")) {
			tempContract.setAlternativeSkillCategory(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("Employee")) {
			tempEmployee = new Employee(emp_id, attributes.getValue("ID"));
			emp_id++;
		} else if (qName.equalsIgnoreCase("Pattern")) {
			if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodPatternsPattern")) {
				tempPattern = new Pattern(attributes.getValue("ID"), attributes
						.getValue("weight"));
			}
		} else if (qName.equalsIgnoreCase("PatternEntry")) {
			String x = attributes.getValue("index");
			tempPatternEntry = new PatternEntry(x);
		} else if (qName.equalsIgnoreCase("DayOfWeekCover")) {
			tempDayOfWeekCover = new DayOfWeekCover();
		} else if (qName.equalsIgnoreCase("DateSpecificCover")) {
			tempDateSpecificCover = new DateSpecificCover();
			throw new Error("Not used in INRC2010.");
		} else if (qName.equalsIgnoreCase("Cover")) {
			tempCover = new Cover();
		} else if (qName.equalsIgnoreCase("DayOff")) {
			tempDayOffRequest = new DayOffRequest(attributes.getValue("weight"));
		} else if (qName.equalsIgnoreCase("ShiftOff")) {
			tempShiftOffRequest = new ShiftOffRequest(attributes
					.getValue("weight"));
		} else if (qName.equalsIgnoreCase("ShiftOn")) {
			throw new Error(
					"Not used in INRC2010. ShiftOn handling code is missing!");
		} else if (qName.equalsIgnoreCase("DayOn")) {
			throw new Error(
					"Not used in INRC2010. DayOn handling code is missing!");
		}

	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		tempVal = new String(ch, start, length);
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equalsIgnoreCase("StartDate")) {
			try {
				problem.setStartDate(sdf.parse(tempVal));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (qName.equalsIgnoreCase("EndDate")) {
			try {
				problem.setEndDate(sdf.parse(tempVal));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (qName.equalsIgnoreCase("Skill")) {
			if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodSkillsSkill"))
				problem.addSkill(tempVal);
			else if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodShiftTypesShiftSkillsSkill"))
				tempShiftType.addSkill(tempVal);
			else if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodEmployeesEmployeeSkillsSkill"))
				tempEmployee.addSkill(tempVal);
		} else if (qName.equalsIgnoreCase("StartTime")) {
			tempShiftType.setStartTime(tempVal);
		} else if (qName.equalsIgnoreCase("EndTime")) {
			tempShiftType.setEndTime(tempVal);
		} else if (qName.equalsIgnoreCase("Description")) {
			if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodContractsContractDescription"))
				tempContract.setDescription(tempVal);
			else if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodShiftTypesShiftDescription"))
				tempShiftType.setDescription(tempVal);
		} else if (qName.equalsIgnoreCase("Shift")) {
			if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodShiftTypesShift"))
				problem.addShiftType(tempShiftType);
			else if (tagsXmlStackAsString()
					.equalsIgnoreCase(
							"SchedulingPeriodCoverRequirementsDayOfWeekCoverCoverShift")) {
				tempCover.setShiftType(problem.getShiftType(tempVal));
			}
		} else if (qName.equalsIgnoreCase("Contract")) {
			if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodContractsContract"))
				problem.addContract(tempContract);
		} else if (qName.equalsIgnoreCase("SingleAssignmentPerDay")) {
			tempContract.hasSingleAssignmentPerDay(tempVal);
		} else if (qName.equalsIgnoreCase("MaxNumAssignments")) {
			tempContract.setMaxNumAssignments(tempVal);
		} else if (qName.equalsIgnoreCase("MinNumAssignments")) {
			tempContract.setMinNumAssignments(tempVal);
		} else if (qName.equalsIgnoreCase("MaxConsecutiveWorkingDays")) {
			tempContract.setMaxConsecutiveWorkingDays(tempVal);
		} else if (qName.equalsIgnoreCase("MinConsecutiveWorkingDays")) {
			tempContract.setMinConsecutiveWorkingDays(tempVal);
		} else if (qName.equalsIgnoreCase("MaxConsecutiveFreeDays")) {
			tempContract.setMaxConsecutiveFreeDays(tempVal);
		} else if (qName.equalsIgnoreCase("MinConsecutiveFreeDays")) {
			tempContract.setMinConsecutiveFreeDays(tempVal);
		} else if (qName.equalsIgnoreCase("MaxConsecutiveWorkingWeekends")) {
			tempContract.setMaxConsecutiveWorkingWeekends(tempVal);
		} else if (qName.equalsIgnoreCase("MinConsecutiveWorkingWeekends")) {
			tempContract.setMinConsecutiveWorkingWeekends(tempVal);
		} else if (qName.equalsIgnoreCase("MaxWorkingWeekendsInFourWeeks")) {
			tempContract.setMaxWorkingWeekendsInFourWeeks(tempVal);
		} else if (qName.equalsIgnoreCase("WeekendDefinition")) {
			tempContract.setWeekendDefinition(tempVal);
		} else if (qName.equalsIgnoreCase("CompleteWeekends")) {
			tempContract.hasCompleteWeekends(tempVal);
		} else if (qName.equalsIgnoreCase("IdenticalShiftTypesDuringWeekend")) {
			tempContract.hasIdentShiftTypesDuringWeekend(tempVal);
		} else if (qName.equalsIgnoreCase("NoNightShiftBeforeFreeWeekend")) {
			tempContract.hasNoNightShiftBeforeFreeWeekend(tempVal);
		} else if (qName.equalsIgnoreCase("TwoFreeDaysAfterNightShifts")) {
			tempContract.hasTwoFreeDaysAfterNightShifts(tempVal);
		} else if (qName.equalsIgnoreCase("AlternativeSkillCategory")) {
			tempContract.hasAlternativeSkillCategory(tempVal);
		} else if (qName.equalsIgnoreCase("Employee")) {
			problem.addEmployee(tempEmployee);
		} else if (qName.equalsIgnoreCase("name")) {
			tempEmployee.setName(tempVal);
		} else if (qName.equalsIgnoreCase("ContractID")) {
			tempEmployee.setContractId(tempVal);
		} else if (qName.equalsIgnoreCase("ShiftType")) {
			if (tagsXmlStackAsString()
					.equalsIgnoreCase(
							"SchedulingPeriodPatternsPatternPatternEntriesPatternEntryShiftType")) {
				tempPatternEntry.setShiftType(tempVal);
				ShiftType st = problem.getShiftType(tempVal);
				// System.out.println(tempVal + " " + st);
				if (tempVal.equalsIgnoreCase("NONE")
						|| (tempVal.equalsIgnoreCase("ANY")))
					tempPatternEntry.setAliasShiftType(tempVal);
				else
					tempPatternEntry.setAliasShiftType(st.getAliasId());
			}
		} else if (qName.equalsIgnoreCase("Day")) {
			if (tagsXmlStackAsString()
					.equalsIgnoreCase(
							"SchedulingPeriodPatternsPatternPatternEntriesPatternEntryDay")) {
				tempPatternEntry.setDay(tempVal);
			} else if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodCoverRequirementsDayOfWeekCoverDay")) {
				tempDayOfWeekCover.setDay(tempVal);
			}
		} else if (qName.equalsIgnoreCase("PatternEntry")) {
			tempPattern.addPatternEntry(tempPatternEntry);
		} else if (qName.equalsIgnoreCase("Pattern")) {
			if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodPatternsPattern")) {
				problem.addPattern(tempPattern);
			} else if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodContractsContractUnwantedPatternsPattern")) {
				tempContract.addUnwantedPattern(problem.getPattern(tempVal));
			}
		} else if (qName.equalsIgnoreCase("Preferred")) {
			if (tagsXmlStackAsString()
					.equalsIgnoreCase(
							"SchedulingPeriodCoverRequirementsDayofWeekCoverCoverPreferred")) {
				tempCover.setPreferred(Integer.parseInt(tempVal));
			}
		} else if (qName.equals("Cover")) {
			if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodCoverRequirementsDayOfWeekCoverCover")) {
				tempDayOfWeekCover.addCover(tempCover);
			} else if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodCoverRequirementsDateSpecificCoverCover")) {
				tempDateSpecificCover.addCover(tempCover);
			}
		} else if (qName.equals("DayOfWeekCover")) {
			problem.addDayOfWeekCover(tempDayOfWeekCover);
		} else if (qName.equals("DateSpecificCover")) {
			problem.addDateSpecificCover(tempDateSpecificCover);
		} else if (qName.equals("EmployeeID")) {
			if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodDayOffRequestsDayOffEmployeeID")) {
				tempDayOffRequest.setEmployee(problem.getEmployee(tempVal));
			} else if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodShiftOffRequestsShiftOffEmployeeID")) {
				tempShiftOffRequest.setEmployee(problem.getEmployee(tempVal));
			}
		} else if (qName.equals("ShiftTypeID")) {
			tempShiftOffRequest.setShiftType(problem.getShiftType(tempVal));
		} else if (qName.equals("DayOff")) {
			problem.addDayOffRequest(tempDayOffRequest);
		} else if (qName.equals("Date")) {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodDayOffRequestsDayOffDate")) {
				try {
					Date dateOff = (Date) formatter.parse(tempVal);
					tempDayOffRequest.setDateOff(dateOff);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodShiftOffRequestsShiftOffDate")) {
				try {
					Date dateOff = (Date) formatter.parse(tempVal);
					tempShiftOffRequest.setDateOff(dateOff);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (tagsXmlStackAsString().equalsIgnoreCase(
					"SchedulingPeriodCoverRequirementsDateSpecificCoverDate")) {
				try {
					Date date = (Date) formatter.parse(tempVal);
					tempDateSpecificCover.setDate(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} else if (qName.equals("ShiftOff")) {
			problem.addShiftOffRequest(tempShiftOffRequest);
		}
		tagsXmlStack.pop();
	}

	String tagsXmlStackAsString() {
		StringBuffer sb = new StringBuffer();
		for (String tag : tagsXmlStack) {
			sb.append(tag);
		}
		return sb.toString();
	}
}
