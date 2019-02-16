package frc.robot.state;

public enum TestState implements StateBase {
    DEFAULT(), START(1000), RESET();

    private long time;

    private TestState() {
        this(-1);
    }

    private TestState(long time) {
        this.time = time;
    }

    @Override
    public long getTime() {
        return time;
    }

}