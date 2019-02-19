package frc.util;

public class Timer4150 {

    private long start;
    private long currentTime;

    private long timePassed;

    public Timer4150() {
        this.start = -1;
        this.currentTime = -1;
        this.timePassed = -1;
    }

    public void start() {
        this.start = currentTime;
    }

    public long timePassed() {
        return timePassed;
    }

    public void update(long now) {
        this.currentTime = System.currentTimeMillis();
        this.timePassed = currentTime - start;
    }

    public long getCurrentTime() {
        return currentTime;
    }
    
}