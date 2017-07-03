package model;

import java.util.ArrayList;
import java.util.List;

public class Pattern {
    String id;
    int weight;
    List<PatternEntry> patternEntries;
    boolean shiftTypePattern=false;

    public Pattern(String id, String weight) {
        patternEntries = new ArrayList<PatternEntry>();
        this.id = id;
        this.weight = Integer.parseInt(weight);
    }

    public void addPatternEntry(PatternEntry pe) {
        patternEntries.add(pe);
        if (!(pe.shiftType.equalsIgnoreCase("None")) && !(pe.shiftType.equalsIgnoreCase("Any")))
        	shiftTypePattern = true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("[ID=%s|WEIGHT=%d] \t", id, weight));
        for (PatternEntry pe : patternEntries) {
            sb.append(pe.toString());
            sb.append("\t");
        }
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<PatternEntry> getPatternEntries() {
        return patternEntries;
    }

    public void setPatternEntries(List<PatternEntry> patternEntries) {
        this.patternEntries = patternEntries;
    }

    public String searchString() {
        StringBuffer sb = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        for (PatternEntry pe : patternEntries) {
            if (pe.getAliasShiftType().equalsIgnoreCase("ANY")) {
                sb.append(".");
            } else if (pe.getAliasShiftType().equalsIgnoreCase("NONE")) {
                sb.append("-");
            } else {
                sb.append(pe.getAliasShiftType());
            }
            if (!pe.day.equalsIgnoreCase("ANY")) {
                sb2.append(";" + pe.day);
            }
        }
        return sb.toString() + sb2.toString();
    }
}
