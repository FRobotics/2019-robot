package frc.util;

public class AltPosControl {

	private final double target;
	private final double minSpeed;
	private final double maxSpeed;
	private final double rate;
	private final double deadband;
	private final boolean startMax;
	private final double threshold;
	private int onTargetCount;
	private static final int ON_TARGET_GOAL = 3;
	private double speed;
	private double slope;

	public AltPosControl(double target, double minSpeed, double maxSpeed, double rate, double deadband, boolean startMax, double threshold) {
		this.target = target;
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
		this.rate = rate;
		this.deadband = deadband;
		this.startMax = startMax;
		this.threshold = threshold;
		this.onTargetCount = 0;
		this.speed = 0;
		this.slope = ((maxSpeed - minSpeed)/(threshold - deadband));
	}
	
	public boolean onTarget() {
		System.out.println("TEST: " + rate);
		return onTargetCount >= ON_TARGET_GOAL;
	}

	public double getSpeed(double traveled) {
		double error = target - traveled;
		double absError = Math.abs(error);
		if (absError < deadband) {
			speed = 0;
			if(onTargetCount < ON_TARGET_GOAL) onTargetCount++;
		} else if (absError < threshold) {
			speed = (absError - deadband) * slope + minSpeed;
		} else {
			if(startMax) speed = maxSpeed;
			else speed = Math.max(Math.min(speed + rate, maxSpeed), minSpeed);
		}
		return error >= 0 ? speed : -speed;
	}

	public double getTarget() {
		return target;
	}

	public double getMinSpeed() {
		return minSpeed;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public double getRate() {
		return rate;
	}

	public double getDeadband() {
		return deadband;
	}
}