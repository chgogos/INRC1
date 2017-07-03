package model;

public class WorkAssignment {
    public int emp_id;
    public int time_unit_id;
    public int day_of_year;
    public int shift_type_id;

    public WorkAssignment(int emp_id, int time_unit_id, int day_of_year, int shift_type_id){
        this.emp_id = emp_id;
        this.time_unit_id=time_unit_id;
        this.day_of_year=day_of_year;
        this.shift_type_id = shift_type_id;
    }

}
