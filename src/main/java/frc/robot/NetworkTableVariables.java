package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class NetworkTableVariables {
    public static BenFabian robot;
    private static Thread thread = new Thread(new NTVariableThread());

    private static NetworkTable robotTable;
    private static NetworkTable visionTable;
    // drive: input
    public static NetworkTableEntry leftMotorSpeed;
    public static NetworkTableEntry rightMotorSpeed;
    public static NetworkTableEntry leftMotorOutputPercent;
    public static NetworkTableEntry rightMotorOutputPercent;
    public static NetworkTableEntry angle;
    // drive: output
    public static NetworkTableEntry leftMotorTarget;
    public static NetworkTableEntry rightMotorTarget;
    public static NetworkTableEntry leftMotorOutput;
    public static NetworkTableEntry rightMotorOutput;
    public static NetworkTableEntry driveTarget;
    public static NetworkTableEntry leftMotorDistance;
    public static NetworkTableEntry rightMotorDistance;
    // drive: vision
    public static NetworkTableEntry onTarget;
    public static NetworkTableEntry targetAngle;
    // not on practice
    public static NetworkTableEntry ballDetected;
    public static NetworkTableEntry elevatorHeight;
    public static NetworkTableEntry elevatorTarget;
    public static NetworkTableEntry elevatorOutput;

    public static void init() {
        robotTable = NetworkTableInstance.getDefault().getTable("robot");
        visionTable = NetworkTableInstance.getDefault().getTable("vision");

        leftMotorSpeed = robotTable.getEntry("leftMotorSpeed");
        rightMotorSpeed = robotTable.getEntry("rightMotorSpeed");
        leftMotorOutputPercent = robotTable.getEntry("leftMotorOutputPercent");
        rightMotorOutputPercent = robotTable.getEntry("rightMotorOutputPercent");
        angle = robotTable.getEntry("angle");

        leftMotorTarget = robotTable.getEntry("leftMotorTarget");
        rightMotorTarget = robotTable.getEntry("rightMotorTarget");
        leftMotorOutput = robotTable.getEntry("leftMotorOutput");
        rightMotorOutput = robotTable.getEntry("rightMotorOutput");
        driveTarget = robotTable.getEntry("driveTarget");
        leftMotorDistance = robotTable.getEntry("leftMotorDistance");
        rightMotorDistance = robotTable.getEntry("rightMotorDistance");

        onTarget = visionTable.getEntry("TargetFound");
        targetAngle = visionTable.getEntry("TargetAngle");

        ballDetected = robotTable.getEntry("ballDetected");
        elevatorHeight = robotTable.getEntry("elevatorHeight");
        elevatorTarget = robotTable.getEntry("elevatorTarget");
        elevatorOutput = robotTable.getEntry("elevatorOutput");
    }

    private static class NTVariableThread implements Runnable {
        @Override
        public void run() {
            NetworkTableVariables.init();
            while (robot.isRunning() && !thread.isInterrupted()) {
                robot.updateDashboard();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    System.out.println("Network Table thread interrupted:");
                    e.printStackTrace();
                }
            }
        }
    }

    public static void start(BenFabian robot) {
        NetworkTableVariables.robot = robot;
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }
}