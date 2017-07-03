package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * A shift type repesents a time frame for which anurse with a certain skill is
 * required (e.g. between 08h30 and 16h30 a head nurse need to be present).
 *
 */
public class ShiftType implements Comparable<ShiftType> {
    private static final String[] aliases = { "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L", "M", "N", "O" }; //15 shifttypes

    private String aliasId;

    public String id;
    public String description;
    public String startTime;
    public String endTime;
    public List<String> requiredSkills;

    public String getAliasId() {
        return aliasId;
    }

    public void setAliasId(int k) {
        aliasId = aliases[k];
    }

    public ShiftType(String id) {
        this.setId(id);
        requiredSkills = new ArrayList<String>();
    }

    int getNumberOfRequiredSkills() {
        return requiredSkills.size();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String toString() {
        return String
                .format(
                        "ID=%2s ALIAS=%s Start Time=%s End Time=%s Description=%10s \t Required Skills=%s",
                        id,aliasId, startTime, endTime, description, requiredSkills);
    }

    public void addSkill(String tempVal) {
        requiredSkills.add(tempVal);
    }

    public int getDuration() {
        Calendar cal1 = Calendar.getInstance();
        int hourOfDay = Integer.parseInt(startTime.substring(0, 2));
        int minute = Integer.parseInt(startTime.substring(3, 5));
        cal1.set(2000, 1, 1, hourOfDay, minute, 0);
        Calendar cal2 = Calendar.getInstance();
        hourOfDay = Integer.parseInt(endTime.substring(0, 2));
        minute = Integer.parseInt(endTime.substring(3, 5));
        cal2.set(2000, 1, 1, hourOfDay, minute, 0);
        if (cal1.after(cal2)) {
            cal2.add(Calendar.DAY_OF_MONTH, 1);
        }
        long diff = cal2.getTimeInMillis() - cal1.getTimeInMillis();
        long diffHours = diff / (60 * 1000);
        return (int) diffHours;
    }

    /**
     *
     * A night shift is a shift type that includes midnight
     */
    public boolean isNightShift() {
        Calendar cal1 = Calendar.getInstance();
        int hourOfDay = Integer.parseInt(startTime.substring(0, 2));
        int minute = Integer.parseInt(startTime.substring(3, 5));
        cal1.set(2000, 1, 1, hourOfDay, minute, 0);
        Calendar cal2 = Calendar.getInstance();
        hourOfDay = Integer.parseInt(endTime.substring(0, 2));
        minute = Integer.parseInt(endTime.substring(3, 5));
        cal2.set(2000, 1, 1, hourOfDay, minute, 0);
        if (cal1.after(cal2))
            return true;
        else
            return false;
    }

    @Override
    public int compareTo(ShiftType ast) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime
                .substring(0, 2)));
        cal.set(Calendar.MINUTE, Integer.parseInt(startTime.substring(3, 5)));
        Date date = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ast.startTime.substring(
                0, 2)));
        cal.set(Calendar.MINUTE, Integer
                .parseInt(ast.startTime.substring(3, 5)));
        Date bDate = cal.getTime();
        return date.compareTo(bDate);
    }

}
