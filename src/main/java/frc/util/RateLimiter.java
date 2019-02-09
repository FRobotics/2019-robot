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
     * Get a value based on a target and the last value using the rate
     * 
     * @param targetValue - the target value to try to reach
     * @return the value limited by the rate
     */
    public double get(double targetValue) {
        double returnValue;
        if (rate == -1) {
            returnValue = targetValue;
        } else {
            double diff = targetValue - lastValue;
            if (diff > rate) {
                returnValue = lastValue + rate;
            } else if (diff < -rate) {
                returnValue = lastValue - rate;
            } else {
                returnValue = targetValue;
            }
        }
        this.lastValue = returnValue;
        return returnValue;
    }

    /**
     * Sets the last value back to 0
     */
    public void reset() {
        this.reset(0);
    }

    /**
     * Sets the last value to startValue
     * 
     * @param startValue - the value to reset to
     */
    public void reset(double startValue) {
        this.lastValue = startValue;
    }

    public double getRate() {
        return this.rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

}