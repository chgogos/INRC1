package model;

import java.util.Date;

import utils.InrcDateFormat;

public class ShiftOffRequest {
    int weight;
    Employee employee;
    ShiftType shiftType;
    Date dateOff;

    public ShiftOffRequest(String weight) {
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

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }

    public Date getDateOff() {
        return dateOff;
    }

    public void setDateOff(Date dateOff) {
        this.dateOff = dateOff;
    }

    public String toString() {
        return String.format(
                "WEIGHT=%d | EMPLOYEE=%s | SHIFTTYPE=%s | DATE=%s", weight,
                employee.name, shiftType.getId(), InrcDateFormat.dateFormat
                        .format(dateOff));
    }
}
