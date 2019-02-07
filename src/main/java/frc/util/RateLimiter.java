package frc.util;

public class RateLimiter {

    private double lastValue;
    private double rate;

    public RateLimiter(double rate) {
        this(rate, 0);
    }

    public RateLimiter(double rate, double startValue) {
        this.lastValue = startValue;
        this.rate = rate;
    }

    /**
     * get a value based on a target and the last value using the rate
     */
    public double get(double targetValue) {
        if(rate == -1) {
            return targetValue;
        }
        double diff = targetValue - lastValue;
        if(diff > rate) {
            return lastValue + rate;
        }
        if(diff < - rate) {
            return lastValue - rate;
        }
        return targetValue;
    }

    public double getRate() {
        return this.rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

}