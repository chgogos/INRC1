package model;

import java.util.ArrayList;
import java.util.Calendar;

import model.InrcProblem;
public class WeekWorkList
{
    public ArrayList<WeekWork> wwAll = new ArrayList<WeekWork>();
    public WeekWorkList(InrcProblem problem)
    {
    	// find the starting day and length of weekend 
    	Calendar cal = Calendar.getInstance();
		cal.setTime(problem.getStartDate());
    	
		// sd is the first day of scheduling period 1 -> Sun
		int sd = cal.get(Calendar.DAY_OF_WEEK);

        // find all
        for (int i = 0; i < 128; i++)
        {
            WeekWork ww = new WeekWork(i);
            wwAll.add(ww);
        }
    }

}
