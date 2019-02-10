package frc.util;

public class RateLimiter {

    private double rate;

    public RateLimiter(double rate) {
        this(rate, 0);
    }

    public RateLimiter(double rate, double startValue) {
        this.rate = rate;
    }

    /**
     * Get a value based on a target and the last value using the rate
     * 
     * @param targetValue - the target value to try to reach
     * @return the value limited by the rate
     */
    public double get(double targetValue, double currentValue) {
        if (rate == -1) {
            return targetValue;
        } else {
            double diff = targetValue - currentValue;
            if (diff > rate) {
                return currentValue + rate;
            } else if (diff < -rate) {
                return currentValue - rate;
            } else {
                return targetValue;
            }
        }
    }

    public double getRate() {
        return this.rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

}