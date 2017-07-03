package model;

public class PatternEntry {
	int index;
	String shiftType;
	String aliasShiftType;
	String day;

	public PatternEntry(String id) {
		index = Integer.parseInt(id);
	}

	public String getAliasShiftType() {
		return aliasShiftType;
	}

	public void setAliasShiftType(String aliasShiftType) {
		this.aliasShiftType = aliasShiftType;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getShiftType() {
		return shiftType;
	}

	public void setShiftType(String shiftType) {
		this.shiftType = shiftType;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String toString() {
		// return String.format("(%d|%s(%s)|%s)", index, shiftType,
		// aliasShiftType, day);
		return String.format("(%d|%s|%s)", index, shiftType, day);
	}

}
