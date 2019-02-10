package frc.robot.state;

public enum DriveState implements StateBase {
    CONTROLLED(), TURN_180();

    private long time;

    private DriveState() {
        this.time = -1;
    }

    private DriveState(long time) {
        this.time = time;
    }

    @Override
    public long getTime() {
        return time;
    }

}