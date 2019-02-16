package frc.robot.input;

// enum
public enum Axis {
    LEFT_X(0), LEFT_Y(1), TRIGGER_LEFT(2), TRIGGER_RIGHT(3), RIGHT_X(4), RIGHT_Y(5)/*, D_PAD_X(6), D_PAD_Y(7)*/;

    private int ID;

    private Axis(int ID) {
        this.ID = ID;
    }

    public int getId() {
        return ID;
    }

}
