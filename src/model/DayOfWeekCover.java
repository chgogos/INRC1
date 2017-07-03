package model;

import java.util.ArrayList;
import java.util.List;

public class DayOfWeekCover {
    String day; // e.g. Monday
    List<Cover> covers;

    public DayOfWeekCover() {
        covers = new ArrayList<Cover>();
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<Cover> getCovers() {
        return covers;
    }

    public void setCovers(List<Cover> covers) {
        this.covers = covers;
    }

    public void addCover(Cover cover) {
        covers.add(cover);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Cover cover : covers) {
            sb.append(cover.toString());
            sb.append("\t");
        }
        return String.format("%s \n %s", day, sb.toString());
    }
}
