package model;

public class Cover {
    ShiftType shiftType; // e.g. E
    int preferred; // e.g. 2

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shift) {
        this.shiftType = shift;
    }

    public int getPreferred() {
        return preferred;
    }

    public void setPreferred(int preferred) {
        this.preferred = preferred;
    }

    public String toString() {
        return String.format("(%s|%d)", shiftType.id, preferred);
    }
}
