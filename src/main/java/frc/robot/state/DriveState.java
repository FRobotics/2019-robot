package frc.robot.state;

public enum DriveState implements StateBase {
    CONTROLLED(), TURN();

    private long time;

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

}