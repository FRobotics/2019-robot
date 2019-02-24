package frc.util;

import java.util.function.Function;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PosControl {

    private double target;
    private double minSpeed;
    private double maxSpeed;
    private Function<Double, Double> rateFunc;
    private double deadband;

    private double halfWay;

    private double lastPos;
    private double lastSpeed;
    private long lastTime;

    private int onTargetCount;

    public static void main(String[]args) {
        PosControl test = new PosControl(100, 2, 5, x -> 0.5*x, 5);
        double pos = 0;
        for(int i = 1; i < 50 && !test.onTarget(); i++) {
            long time = i;
            pos += test.getSpeed(pos, time);
            System.out.println(time + ": " + pos);
        }
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
    public PosControl(double target, double minSpeed, double maxSpeed, Function<Double, Double> rateFunc,
            double deadband) {
        this.target = target;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.rateFunc = rateFunc;
        this.deadband = deadband;

        this.halfWay = (target - deadband)/2;

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
        double rawDistanceToTarget = target - currentPos;
        double distanceToTarget = Math.abs(rawDistanceToTarget);

        // if within the deadband
        if (distanceToTarget < deadband) {
            onTargetCount++;
            return 0;
        }

        double distance = rawDistanceToTarget;
        if(distance > halfWay) {
            distance = (target - deadband) - distance;
        }

        SmartDashboard.putNumber("distance", distance);

        // use the rate function with the current time and restrict it between the min
        // and max speed
        double speed = rateFunc.apply(distance);
        if(speed < 0) {
            speed = -Math.min(Math.max(-speed, maxSpeed - minSpeed) + minSpeed, minSpeed);
        } else {
            speed = Math.max(Math.min(speed, maxSpeed - minSpeed) + minSpeed, minSpeed);
        }

        // tweak the speed to account for distrubances in the position
        double timeDiff = currentTime - lastTime;
        double speedDiff = (currentPos - lastPos) / timeDiff - lastSpeed / timeDiff;
        speed += speedDiff;

        lastPos = currentPos;
        lastSpeed = speed;
        lastTime = currentTime;

        // inverse the output if it's past the target
        return currentPos - target < 0 ? speed : -speed;
    }

    /**
     * @return Whether the distance travelled is on the target 3 times in a row or
     *         more
     */
    public boolean onTarget() {
        return onTargetCount > 3;
    }
}
