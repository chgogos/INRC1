package model;

import java.util.Date;
import java.util.List;

public class DateSpecificCover {
    Date date; // e.g. 31/01/2010

    List<Cover> covers;

    public Date getDate() {
        return date;
    }

    public void setDate(Date aDate) {
        this.date = aDate;
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
        return String.format("%tF \n %s", date, sb.toString());
    }
}
