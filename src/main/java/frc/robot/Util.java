package frc.robot;

public class Util {
    public static double[] smoothDrive(boolean slowMode, double yAxis, double xAxis) {
        double fb = smooth(yAxis, 0.2, 2); // inverted so up is forward (positive = forward)
        double lr = smooth(-xAxis, 0.2, 3); // inverted so left turns left & right turns right

        // start out with just forward and backward
        double left, right;
        left = right = fb;

        // apply turning v2
        if (Math.abs(lr) > 0.2) {
            if (fb > 0.0) {
                if (lr > 0.0) {
                    left = fb - lr;
                    right = Math.max(fb, lr);
                } else {
                    left = Math.max(fb, -lr);
                    right = fb + lr;
                }
            } else {
                if (lr > 0.0) {
                    left = -Math.max(-fb, lr);
                    right = fb + lr;
                } else {
                    left = fb - lr;
                    right = -Math.max(-fb, -lr);
                }
            }
            left *= 0.75;
            right *= 0.75;
        }

        // apply slow mode
        if (slowMode) {
            right *= 0.5;
            left *= 0.5;
        }

        return new double[] { left, right };
    }

    public static double smooth(double value, double deadband, double power) {
        if (value > deadband) {
            double newValue = (value - deadband) / (1 - deadband);
            return Math.pow(newValue, power);
        }
        if (value < -deadband) {
            double newValue = (value + deadband) / (1 - deadband);
            return -Math.abs(Math.pow(newValue, power));
        }
        return 0;
    }
}