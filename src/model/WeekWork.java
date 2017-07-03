package model;

import model.Contract;

//---
// describe the work and rest of 1 week
//-------------------------------------
public class WeekWork
{
    public int code;
    // cost of ww
    public int cost;

    // more cost for soft constraints
    int costSC = 1; //100;

    // 1 -> work
    // 0 -> rest
    public int[] work = new int[7];

    // number of
    // w1 -> 010
    // w2 -> 0110 ...
    // w7 -> 1111111
    int w1, w2, w3, w4, w5, w7;

    // w1_r -> .....01
    // w2_r -> ....011 ...
    // w6_r -> 0111111
    int w1_r, w2_r, w3_r, w4_r, w5_r, w6_r;
    int w1_l, w2_l, w3_l, w4_l, w5_l, w6_l;
    int w_r, w_l;

    // number of
    // r1 -> 101
    // r2 -> 1001 ...
    // r7 -> 0000000
    int r1, r2, r3, r4, r5, r7;
    int r1_r, r2_r, r3_r, r4_r, r5_r, r6_r;
    int r1_l, r2_l, r3_l, r4_l, r5_l, r6_l;
    int r_r, r_l;

    // total work, total rest
    int n0;
	int n1;

    // string representation of work
    public String wwStr;
    public String wwStr01;
    // work representation
    String w_str = ". ";
    // rest representation
    String r_str = "R ";

    // is week work legal ?
    public boolean legal;

    // working week end
    int w_wend;
    // resting week end
    int r_wend;
    // complete week end
    int c_wend;
    
    public WeekWork(int c)
    {
		init(c);
    }
    
	public WeekWork(int c, int wend_day, int wend_length)
	{
		init(c);
		calcWend(wend_day, wend_length);
	}

	void init(int c)
	{
        code = c;

        // fill work
        // transforming at 7 bits binary value
        work[6] = c % 2;
        work[5] = c / 2 % 2;
        work[4] = c / 4 % 2;
        work[3] = c / 8 % 2;
        work[2] = c / 16 % 2;
        work[1] = c / 32 % 2;
        work[0] = c / 64 % 2;

        // total work, total rest
        setN1(workSum(0, 6));
        n0 = 7 - getN1();

        // calculate w1, w2, ...
        calcW();

        // calculate r1, r2, ...
        calcR();

        wwStr = WeekWorkStr();
        wwStr01 = WeekWorkStr01();

        cost = 0; //n1;
        legal = true;
    }

	void calcWend(int wend_day, int wend_length)
	{
		if (wend_day + wend_length > 6)
			return;
		
		// working
		if (workSum(wend_day, wend_day + wend_length - 1) > 0)
			w_wend = 1;
		// complete working
		if (workSum(wend_day, wend_day + wend_length - 1) == wend_length)
			c_wend = 1;
		// resting
		if (workSum(wend_day, wend_day + wend_length - 1) == 0)
			r_wend = 1;
	}
	
    // calculate w1, w2, ...
    void calcW()
    {
        int i;

        for (i = 1; i <= 5; i++)
            if (work[i-1] == 0 && work[i] == 1 && work[i + 1] == 0)
                w1++;
        for (i = 1; i <= 4; i++)
            if (work[i-1] == 0 && workSum(i, i + 1) == 2 && work[i + 2] == 0)
                w2++;
        for (i = 1; i <= 3; i++)
            if (work[i-1] == 0 && workSum(i, i + 2) == 3 && work[i + 3] == 0)
                w3++;
        for (i = 1; i <= 2; i++)
            if (work[i-1] == 0 && workSum(i, i + 3) == 4 && work[i + 4] == 0)
                w4++;
        if (work[0] == 0 && workSum(1, 5) == 5 && work[6] == 0)
            w5++;
        if (getN1() == 7)
        {
            w7++; w_r = 7; w_l = 7;
        }

        if (work[5] == 0 && workSum(6, 6) == 1)
                { w1_r++; w_r = 1; }
        if (work[4] == 0 && workSum(5, 6) == 2)
                { w2_r++; w_r = 2; }
        if (work[3] == 0 && workSum(4, 6) == 3)
                { w3_r++; w_r = 3; }
        if (work[2] == 0 && workSum(3, 6) == 4)
                { w4_r++; w_r = 4; }
        if (work[1] == 0 && workSum(2, 6) == 5)
                { w5_r++; w_r = 5; }
        if (work[0] == 0 && workSum(1, 6) == 6)
                { w6_r++; w_r = 6; }

        if (work[1] == 0 && workSum(0, 0) == 1)
                { w1_l++; w_l = 1; }
        if (work[2] == 0 && workSum(0, 1) == 2)
                { w2_l++; w_l = 2; }
        if (work[3] == 0 && workSum(0, 2) == 3)
                { w3_l++; w_l = 3; }
        if (work[4] == 0 && workSum(0, 3) == 4)
                { w4_l++; w_l = 4; }
        if (work[5] == 0 && workSum(0, 4) == 5)
                { w5_l++; w_l = 5; }
        if (work[6] == 0 && workSum(0, 5) == 6)
                { w6_l++; w_l = 6; }
    }

    // calculate r1, r2, ...
    void calcR()
    {
        int i;

        for (i = 1; i <= 5; i++)
            if (work[i-1] == 1 && work[i] == 0 && work[i + 1] == 1)
                r1++;
        for (i = 1; i <= 4; i++)
            if (work[i-1] == 1 && workSum(i, i + 1) == 0 && work[i + 2] == 1)
                r2++;
        for (i = 1; i <= 3; i++)
            if (work[i-1] == 1 && workSum(i, i + 2) == 0 && work[i + 3] == 1)
                r3++;
        for (i = 1; i <= 2; i++)
            if (work[i-1] == 1 && workSum(i, i + 3) == 0 && work[i + 4] == 1)
                r4++;
        if (work[0] == 1 && workSum(1, 5) == 0 && work[6] == 1)
            r5++;
        if (n0 == 7)
        {
            r7++; r_r = 7; r_l = 7;
        }

        if (work[5] == 1 && workSum(6, 6) == 0)
            { r1_r++; r_r = 1; }
        if (work[4] == 1 && workSum(5, 6) == 0)
            { r2_r++; r_r = 2; }
        if (work[3] == 1 && workSum(4, 6) == 0)
            { r3_r++; r_r = 3; }
        if (work[2] == 1 && workSum(3, 6) == 0)
            { r4_r++; r_r = 4; }
        if (work[1] == 1 && workSum(2, 6) == 0)
            { r5_r++; r_r = 5; }
        if (work[0] == 1 && workSum(1, 6) == 0)
            { r6_r++; r_r = 6; }

        if (work[1] == 1 && workSum(0, 0) == 0)
            { r1_l++; r_l = 1; }
        if (work[2] == 1 && workSum(0, 1) == 0)
            { r2_l++; r_l = 2; }
        if (work[3] == 1 && workSum(0, 2) == 0)
            { r3_l++; r_l = 3; }
        if (work[4] == 1 && workSum(0, 3) == 0)
            { r4_l++; r_l = 4; }
        if (work[5] == 1 && workSum(0, 4) == 0)
            { r5_l++; r_l = 5; }
        if (work[6] == 1 && workSum(0, 5) == 0)
            { r6_l++; r_l = 6; }
    }

    // work from i1 to i2
    int workSum(int i1, int i2)
    {
        int n = 0;
        for (int i = i1; i <= i2; i++)
            n += work[i];
        return n;
    }

    /**
     * string representation of work
     */
    String WeekWorkStr()
    {
        String s = "";
        for (int i = 0; i < 7; i++)
            if (work[i] == 1) s += w_str;
            else s += r_str;
        return s;
    }
    String WeekWorkStr01()
    {
        String s = "";
        for (int i = 0; i < 7; i++)
            if (work[i] == 1) s += "1";
            else s += "0";
        return s;
    }

    /**
     * check soft constraints
     * update cost
     * update legal
     */
    public void check(Contract con)
    {
    	//---
        // min - max consecutive work
    	//---------------------------
        int min_cw = 0;
        if (con.minConsecutiveWorkingDaysB) min_cw = con.minConsecutiveWorkingDays;
        int max_cw = 15;
        if (con.maxConsecutiveWorkingDaysB) max_cw = con.maxConsecutiveWorkingDays;

        if (con.maxNumAssignmentsB && max_cw > con.maxNumAssignments)
            max_cw = con.maxNumAssignments;

        int n = 0;
        if (min_cw > 1 && this.w1 > 0) n += (min_cw-1)*w1;
        if (min_cw > 2 && this.w2 > 0) n += (min_cw-2)*w2;
        if (min_cw > 3 && this.w3 > 0) n += (min_cw-3)*w3;
		if (min_cw > 4 && this.w4 > 0) n += (min_cw-4)*w4;
        if (min_cw > 5 && this.w5 > 0) n += (min_cw-5)*w5;
        if (n > 0)
        {
            cost += n * costSC * con.minConsecutiveWorkingDaysW; legal = false;
        }
        
        n = 0;
        if (max_cw < 1 && this.w1 > 0) n += (1-max_cw)*w1;
        if (max_cw < 2 && this.w2 > 0) n += (2-max_cw)*w2;
        if (max_cw < 3 && this.w3 > 0) n += (3-max_cw)*w3;
        if (max_cw < 4 && this.w4 > 0) n += 4-max_cw;
        if (max_cw < 5 && this.w5 > 0) n += 5-max_cw;

        if (max_cw < 1 && w1_r + w1_l > 0) n += (1-max_cw)*(w1_r+w1_l);
        if (max_cw < 2 && w2_r + w2_l > 0) n += (2-max_cw)*(w2_r+w2_l);
        if (max_cw < 3 && w3_r + w3_l > 0) n += (3-max_cw)*(w3_r+w3_l);
        if (max_cw < 4 && w4_r + w4_l > 0) n += 4-max_cw;
        if (max_cw < 5 && w5_r + w5_l > 0) n += 5-max_cw;
        
        if (max_cw < 6 && w6_r + w6_l > 0) n += 6-max_cw;
        if (max_cw < 7 && w7 > 0) n += 7-max_cw;
        if (n > 0)
        {
            cost += n * costSC * con.maxConsecutiveWorkingDaysW; legal = false;
        }

        //---
        // min - max consecutive rest
        //---------------------------
        int min_cr = 0;
        if (con.minConsecutiveFreeDaysB) min_cr = con.minConsecutiveFreeDays;
        int max_cr = 15;
        if (con.maxConsecutiveFreeDaysB) max_cr = con.maxConsecutiveFreeDays;

        n = 0;
        if (min_cr > 1 && this.r1 > 0) n += (min_cr-1)*r1;
        if (min_cr > 2 && this.r2 > 0) n += (min_cr-2)*r2;
        if (min_cr > 3 && this.r3 > 0) n += (min_cr-3)*r3;
        if (min_cr > 4 && this.r4 > 0) n += (min_cr-4)*r4;
        if (min_cr > 5 && this.r5 > 0) n += (min_cr-5)*r5;
        
        if (n >0)
        {
            cost += n * costSC * con.minConsecutiveFreeDaysW; legal = false;
        }
        
        n = 0;
        if (max_cr < 1 && this.r1 > 0) n += (1-max_cr)*r1;
        if (max_cr < 2 && this.r2 > 0) n += (2-max_cr)*r2;
        if (max_cr < 3 && this.r3 > 0) n += (3-max_cr)*r3;
        if (max_cr < 4 && this.r4 > 0) n += 4-max_cr;
        if (max_cr < 5 && this.r5 > 0) n += 5-max_cr;

        if (max_cr < 1 && r1_r + r1_l > 0) n += (1-max_cr)*(r1_r+r1_l);
        if (max_cr < 2 && r2_r + r2_l > 0) n += (2-max_cr)*(r2_r+r2_l);
        if (max_cr < 3 && r3_r + r3_l > 0) n += (3-max_cr)*(r3_r+r3_l);
        if (max_cr < 4 && r4_r + r4_l > 0) n += (4-max_cr)*(r4_r+r4_l);
        if (max_cr < 5 && r5_r + r5_l > 0) n += 5-max_cr;
        
        if (max_cr < 6 && r6_r + r6_l > 0) n += 6-max_cr;
        if (max_cr < 7 && r7 > 0) n += 7-max_cr;
        
        if (n > 0)
        {
        	cost += n * costSC * con.maxConsecutiveFreeDaysW; legal = false;
        }

        // complete working
        if (con.completeWeekendsB && c_wend == 1)
        {
        	cost += costSC * con.completeWeekendsW; legal = false;
        }
    }

    /**
     * check soft constraints
     * return connection cost
     */
    public int cost_con(WeekWork ww, Contract con)
    {
        int c = 0;

        //---
        // min - max consecutive work
        //---------------------------
        int min_cw = 0;
        if (con.minConsecutiveWorkingDaysB) min_cw = con.minConsecutiveWorkingDays;
        int max_cw = 15;
        if (con.maxConsecutiveWorkingDaysB) max_cw = con.maxConsecutiveWorkingDays;

        if (con.maxNumAssignmentsB && max_cw > con.maxNumAssignments)
            max_cw = con.maxNumAssignments;

        // 1. work RRR.... - ..RRRRR work
        if (w_r > 0 &&  ww.w_l > 0)
       	{
        	if (w_r + ww.w_l < min_cw)
        	{
                c += ( min_cw - w_r - ww.w_l) * costSC * con.minConsecutiveWorkingDaysW;
        	}
        	if (w_r + ww.w_l > max_cw)
        	{
        		int n1 = 0;
        		if (w_r > max_cw) n1 = w_r - max_cw;
        		int n2 = 0;
        		if (ww.w_l > max_cw) n2 = ww.w_l - max_cw;
        		
                c += ( w_r + ww.w_l - n1 - n2 - max_cw) * costSC * con.maxConsecutiveWorkingDaysW;
        	}
        }

        //---
        // min - max consecutive rest
        //---------------------------
        int min_cr = 0;
        if (con.minConsecutiveFreeDaysB) min_cr = con.minConsecutiveFreeDays;
        int max_cr = 15;
        if (con.maxConsecutiveFreeDaysB) max_cr = con.maxConsecutiveFreeDays;

        // 2. rest ....RRR - RRRRR rest
        if (r_r > 0 &&  ww.r_l > 0)
       	{
        	if (r_r + ww.r_l < min_cr)
        	{
                c += ( min_cr - r_r - ww.r_l) * costSC * con.minConsecutiveFreeDaysW;
        	}
        	if (r_r + ww.r_l > max_cr)
        	{
        		int n1 = 0;
        		if (r_r > max_cr) n1 = r_r - max_cr;
        		int n2 = 0;
        		if (ww.r_l > max_cr) n2 = ww.r_l - max_cr;
        		
                c += ( r_r + ww.r_l - n1 - n2 - max_cr) * costSC * con.maxConsecutiveFreeDaysW;
        	}
        }

        // 3. work RRR.... - RRRRR rest
        if (w_r > 0 &&  ww.w_l == 0)
       	{
        	if (w_r < min_cw)
        	{
                c += ( min_cw - w_r) * costSC * con.minConsecutiveWorkingDaysW;
        	}
        	if (ww.r_l < min_cr)
        	{
                c += ( min_cr - ww.r_l) * costSC * con.maxConsecutiveFreeDaysW;
        	}
        }
        
        // 4. rest ....RRR - ...RRRRR work
        if (w_r == 0 &&  ww.w_l > 0)
       	{
        	if (r_r < min_cr)
        	{
                c += ( min_cr - r_r) * costSC * con.minConsecutiveFreeDaysW;
        	}
        	if (ww.w_l < min_cw)
        	{
                c += ( min_cw - ww.w_l) * costSC * con.maxConsecutiveWorkingDaysW;
        	}
        }

        // work of 2 ww
        //if (this.n1 + ww.n1 > con.maxNumAssignments)
        //{
            //c += costSC;
        //}
        return c;
    }
    
    /**
     * check soft constraints
     * for legal reason
     */
    public boolean check_con(WeekWork ww, Contract con)
    {
    	//---
        // min - max consecutive work
    	//---------------------------
        int min_cw = 0;
        if (con.minConsecutiveWorkingDaysB) min_cw = con.minConsecutiveWorkingDays;
        int max_cw = 15;
        if (con.maxConsecutiveWorkingDaysB) max_cw = con.maxConsecutiveWorkingDays;

        if (con.maxNumAssignmentsB && max_cw > con.maxNumAssignments)
            max_cw = con.maxNumAssignments;

        if (w_r + ww.w_l > 0 && w_r + ww.w_l < min_cw)
        {
            return false;
        }
        if (w_r + ww.w_l > 0 && w_r + ww.w_l > max_cw)
        {
            return false;
        }

        //---
        // min - max consecutive rest
        //---------------------------
        int min_cr = 0;
        if (con.minConsecutiveFreeDaysB) min_cr = con.minConsecutiveFreeDays;
        int max_cr = 15;
        if (con.maxConsecutiveFreeDaysB) max_cr = con.maxConsecutiveFreeDays;

        if (r_r + ww.r_l > 0 && r_r + ww.r_l < min_cr)
        {
            return  false;
        }
        if (r_r + ww.r_l > 0 && r_r + ww.r_l > max_cr)
        {
            return  false;
        }

        // work of 2 ww
        if (con.maxNumAssignmentsB && this.getN1() + ww.getN1() > con.maxNumAssignments)
        {
            return false;
        }

        return true;
    }

	public void setN1(int n1) {
		this.n1 = n1;
	}

	public int getN1() {
		return n1;
	}
    
}
