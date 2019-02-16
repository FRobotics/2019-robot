package frc.robot.state;

public enum SandstormState implements StateBase {
    DEFAULT(), START(1000);

    private long time;

    private SandstormState() {
        this(-1);
    }

    private SandstormState(long time) {
        this.time = time;
    }

    @Override
    public long getTime() {
        return time;
    }

}