package frc.robot.state;

import frc.util.PosControl;

public enum DriveState implements StateBase {
    CONTROLLED(), TURN();

    private long time;
    private PosControl posControl;

    private DriveState() {
        this(-1);
    }

    private DriveState(long time) {
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