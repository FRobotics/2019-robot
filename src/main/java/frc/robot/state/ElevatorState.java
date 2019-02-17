package frc.robot.state;

public enum ElevatorState implements StateBase {
    CONTROLLED(), STOP(1000), GOTO();

    private long time;

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

}