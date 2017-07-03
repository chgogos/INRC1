package model;

import java.util.Date;

import utils.InrcDateFormat;

public class DayOffRequest {

    int weight;
    Employee employee;
    Date dateOff;

    public DayOffRequest(String weight) {
        this.weight = Integer.parseInt(weight);
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Date getDateOff() {
        return dateOff;
    }

    public void setDateOff(Date dateOff) {
        this.dateOff = dateOff;
    }

    public String toString() {
        return String.format("WEIGHT=%2d | EMPLOYEE=%10s DATE=%s", weight,
                employee.name, InrcDateFormat.dateFormat.format(dateOff));
    }
}
