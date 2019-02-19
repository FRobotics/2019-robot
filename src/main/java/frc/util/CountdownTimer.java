package frc.util;

public class CountdownTimer extends Timer4150 {

    private long target;

    public CountdownTimer() {
        this.target = 0;
    }

    public CountdownTimer(long target) {
        this.target = target;
    }

    public void start(long target) {
        this.target = target;
        this.start();
    }

    public boolean isFinished() {
        return this.timePassed() > target;
    }
}