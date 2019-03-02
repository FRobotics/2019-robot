package frc.util;

import java.util.function.Function;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PosControl {

    private double target;
    private double maybeTarget;
    private double minSpeed;
    private double maxSpeed;
    private Function<Double, Double> rateFunc;
    private double deadband;
    private double startPos;

    private double halfWay;

    private double lastPos;
    private double lastSpeed;
    private long lastTime;

    private int onTargetCount;

    public static void main(String[] args) {
        /*
         * PosControl test = new PosControl(100, 2, 5, x -> 0.5*x, 5); double pos = 0;
         * for(int i = 1; i < 50 && !test.onTarget(); i++) { long time = i; pos +=
         * test.getSpeed(pos, time); System.out.println(time + ": " + pos); }
         */
    }

    /**
     * 
     * @param target   - The distance you want to reach
     * @param minSpeed - The minimum speed allowed
     * @param maxSpeed - The maximum speed allowed
     * @param rateFunc - A function to calculated the rate based on the time (in
     *                 millis). This should increase as time goes on.
     * @param deadband - The distance before the target you want to set the speed to
     *                 0 to account for inertia and things
     */
    public PosControl(double target, double currentPos, double minSpeed, double maxSpeed,
            Function<Double, Double> rateFunc, double deadband) {
        this.target = target;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.rateFunc = rateFunc;
        this.deadband = deadband;
        this.startPos = currentPos;

        this.maybeTarget = Math.abs(target + (deadband * (target < 0 ? 1 : -1)));
        this.halfWay = maybeTarget / 2;

        this.lastPos = 0;
        this.lastSpeed = 0;
        this.onTargetCount = 0;
    }

    /**
     * Get the calculated speed that should be output
     * 
     * @param currentPos  - the distance you've travelled from the start
     * @param currentTime - the current time in ms
     * @return the speed to output
     */
    public double getSpeed(double currentPos, long currentTime) {
        double rawDistanceToTarget = (target + startPos) - currentPos;
        double distanceToTarget = Math.abs(rawDistanceToTarget);

        // if within the deadband
        if (distanceToTarget < deadband) {
            onTargetCount++;
            return 0;
        }

        double distance = distanceToTarget;
        if (distance > halfWay) {
            distance = maybeTarget - distance;
        }

        SmartDashboard.putNumber("test", distance);

        // use the rate function with the current time and restrict it between the min
        // and max speed
        double speed = setBetween(rateFunc.apply(distance));
        SmartDashboard.putNumber("test2", speed);

        // tweak the speed to account for distrubances in the position
        // double timeDiff = currentTime - lastTime;
        // double speedDiff = (currentPos - lastPos) / timeDiff - lastSpeed;
        // speed += speedDiff;

        // lastPos = currentPos;
        // lastSpeed = speed;
        // lastTime = currentTime;

        // inverse the output if it's past the target
        return currentPos - target < 0 ? speed : -speed;
    }

    public double setBetween(double n) {
        if (n < this.minSpeed) {
            return this.minSpeed;
        }
        if (n > this.maxSpeed) {
            return this.maxSpeed;
        }
        return n;
    }

    /**
     * @return Whether the distance travelled is on the target 3 times in a row or
     *         more
     */
    public boolean onTarget() {
        return onTargetCount > 3;
    }
}
