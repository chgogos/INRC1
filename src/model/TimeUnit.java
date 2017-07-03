/** George Goulas
 *  Computer Systems Laboratory, 2010
 */
package model;

import java.util.Calendar;
import java.util.Date;

import utils.InrcDateFormat;

/**
 * @author goulas
 *
 */
public class TimeUnit implements Comparable<TimeUnit> {

    public ShiftType shiftType;
    public Date date;

    public TimeUnit() {
    }

    public TimeUnit(ShiftType aShiftType, Date aDate) {
        this.shiftType = aShiftType;
        Calendar cal = Calendar.getInstance();
        cal.setTime(aDate);
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(shiftType.startTime
                .substring(0, 2)));
        cal.set(Calendar.MINUTE, Integer.parseInt(shiftType.startTime
                .substring(3, 5)));
        date = cal.getTime();
    }

    public String toString() {
        return String.format("%s - %s - %d min", InrcDateFormat.dateFormat.format(date), shiftType.getId(),
                shiftType.getDuration());
    }

    @Override
    public int compareTo(TimeUnit atu) {
        return date.compareTo(atu.date);
    }

}
