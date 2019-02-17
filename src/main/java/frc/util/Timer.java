package frc.util;

public class Timer {

    private static long currentTime;

    private long start;

    public void start() {
        this.start = System.currentTimeMillis();
    }

    public long timePassed() {
        return currentTime - start;
    }

    public static void updateTime() {
        currentTime = System.currentTimeMillis();
    }

    public static long getCurrentTime() {
        return currentTime;
    }
    
}