package frc.util;

public class Timer {

    private long start;

    public void start() {
        this.start = System.currentTimeMillis();
    }

    public long timePassed() {
        return System.currentTimeMillis() - start;
    }

}