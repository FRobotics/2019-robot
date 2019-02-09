package frc.util;

public class CountdownTimer extends Timer {

    private long target;

    public CountdownTimer(long target) {
        this.target = target;
    }

    public boolean isFinished() {
        return this.timePassed() > target;
    }
}