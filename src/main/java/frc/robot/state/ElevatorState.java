package frc.robot.state;

import frc.util.PosControl;

public enum ElevatorState implements StateBase {
    CONTROLLED(), STOP(1000), GOTO();

    private long time;
    private PosControl posControl;

    private ElevatorState() {
        this(-1);
    }

    private ElevatorState(long time) {
        this.time = time;
    }

    @Override
    public long getTime() {
        return time;
    }

    public PosControl getPosControl() {
        return this.posControl;
    }

    public void setPosControl(PosControl posControl) {
        this.posControl = posControl;
    }

}