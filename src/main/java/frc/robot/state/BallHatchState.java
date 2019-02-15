package frc.robot.state;

public enum BallHatchState implements StateBase {
    NONE(), PUNCH_BALL(1000), RAISE_ARMS(425), LOWER_ARMS(1000);

    private long time;

    private BallHatchState(long time) {
        this.time = time;
    }

    private BallHatchState() {
        this(-1);
    }

    @Override
    public long getTime() {
        return time;
    }

}