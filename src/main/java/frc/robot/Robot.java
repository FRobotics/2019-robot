/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.analog.adis16448.frc.ADIS16448_IMU;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.input.Axis;
import frc.robot.input.Button;
import frc.robot.input.Controller;
import frc.robot.subsystems.BallSystem;
import frc.robot.subsystems.DriveTrainSystem;
import frc.robot.subsystems.ElevatorSystem;
import frc.robot.subsystems.HatchSystem;
import frc.robot.subsystems.base.CANDriveMotorPair;
import frc.robot.subsystems.base.CANMotor;
import frc.util.CountdownTimer;
import frc.util.PosControl;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  private boolean autoEnabledFirst;

  // subsystems

  private DriveTrainSystem driveTrain;
  private BallSystem ballSystem;
  private ElevatorSystem elevator;
  private HatchSystem hatchSystem;

  // input

  private Controller movementController;
  private Controller actionsController;

  // states

  private long now;

  private State.SandstormMode sandstormState;
  private State.TeleopMode teleopState;
  private State.TestMode testState;

  private State.Drive driveState;
  private State.BallHatch ballHatchState;
  private State.Elevator elevatorState;

  private CountdownTimer modeStateTimer;
  private CountdownTimer ballHatchTimer;
  private CountdownTimer elevatorTimer;

  private long seenBallCount;

  private int[] ntVariableHandles;
  private boolean robotRunning = true;

  public Robot() {
    this.driveTrain = new DriveTrainSystem();
    this.ballSystem = new BallSystem();
    this.elevator = new ElevatorSystem();
    this.hatchSystem = new HatchSystem();

    this.movementController = new Controller();
    this.actionsController = new Controller();

    this.driveState = State.Drive.CONTROLLED;
    this.ballHatchState = State.BallHatch.CONTROLLED;
    this.elevatorState = State.Elevator.CONTROLLED;

    this.seenBallCount = 0;
    this.autoEnabledFirst = false;
    this.modeStateTimer = new CountdownTimer();
    this.ballHatchTimer = new CountdownTimer();
    this.elevatorTimer = new CountdownTimer();
  }

  /**
   * Resets all of the subsystems to their base state
   */
  public void reset() {
    this.driveTrain.reset();
    if (!Constants.PRACTICE_ROBOT) {
      this.ballSystem.reset();
      this.elevator.reset();
      this.hatchSystem.reset();
    }
  }

  private class NTVariableRunnable implements Runnable {
    @Override
    public void run() {
      initDashboard();
      while (robotRunning) {
        updateDashboard();
      }
    }
  }

  private void initDashboard() {
    this.ntVariableHandles = new int[14];
    //drive
    genNTVHandle("leftMotorSpeed");
    genNTVHandle("rightMotorSpeed");
    genNTVHandle("leftMotorOutputPercent");
    genNTVHandle("rightMotorOutputPercent");
    genNTVHandle("angle");

    genNTVHandle("leftMotorTarget");
    genNTVHandle("rightMotorTarget");
    genNTVHandle("leftMotorOutput");
    genNTVHandle("rightMotorOutput");
    genNTVHandle("driveTarget");
    //not on practice
    genNTVHandle("ballDetected");
    genNTVHandle("elevatorHeight");
    genNTVHandle("elevatorTarget");
    genNTVHandle("elevatorOutput");
  }

  private void updateDashboard() {
    this.currentNTHandle = 0;
    setNTDouble(this.driveTrain.getLeftMotorSpeed());
    setNTDouble(this.driveTrain.getRightMotorSpeed());
    setNTDouble(this.driveTrain.getLeftMotorOutputPercent());
    setNTDouble(this.driveTrain.getRightMotorOutputPercent());
    setNTDouble(this.driveTrain.getAngle());

    setNTDouble(this.driveTrain.getLeftMotorTarget());
    setNTDouble(this.driveTrain.getRightMotorTarget());
    setNTDouble(this.driveTrain.getLeftMotorOutput());
    setNTDouble(this.driveTrain.getRightMotorOutput());
    setNTDouble(driveTarget);
    if(!Constants.PRACTICE_ROBOT) {
      setNTBoolean(this.ballSystem.sensedBall());
      setNTDouble(this.elevator.getHeight());
      setNTDouble(elevatorTarget);
      setNTDouble(elevatorOutput);
    }
  }

  private int currentNTHandle = 0;

  private void genNTVHandle(String key) {
    this.ntVariableHandles[currentNTHandle++] = SmartDashboard.getEntry(key).getHandle();
  }

  private void setNTDouble(double value) {
    NetworkTablesJNI.setDouble(this.ntVariableHandles[currentNTHandle++], 0, value, false);
  }

  private void setNTBoolean(boolean value) {
    NetworkTablesJNI.setBoolean(this.ntVariableHandles[currentNTHandle++], 0, value, false);
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    this.driveTrain.init(new CANDriveMotorPair(new TalonSRX(14), new TalonSRX(13)),
        new CANDriveMotorPair(new TalonSRX(10), new TalonSRX(12)), new ADIS16448_IMU());
    this.driveTrain.setRateLimit(1);
    if (!Constants.PRACTICE_ROBOT) {
      this.ballSystem.init(new CANMotor(new VictorSPX(15)), new DoubleSolenoid(1, 3, 2), new DoubleSolenoid(1, 7, 6),
          new DigitalInput(2));
      this.elevator.init(new CANMotor(new TalonSRX(9)), new DoubleSolenoid(0, 5, 4), new DoubleSolenoid(1, 1, 0),
          new Counter(3));
      this.hatchSystem.init(new DoubleSolenoid(1, 5, 4));
    }
    this.movementController.init(new Joystick(0));
    this.actionsController.init(new Joystick(1));
    Thread ntv = new Thread(new NTVariableRunnable());
    ntv.setPriority(Thread.MIN_PRIORITY);
    ntv.start();
  }

  @Override
  public void autonomousInit() {
    this.autoEnabledFirst = true;
    this.sandstormState = State.SandstormMode.START;
  }

  @Override
  public void autonomousPeriodic() {
    switch (this.sandstormState) {
    default:
    case START:
      this.modeStateTimer.start(1000);
      this.sandstormState = State.SandstormMode.INIT;
      break;
    case INIT:
      this.generalInit();
      this.modeStateTimer.update(now);
      if (this.modeStateTimer.isFinished()) {
        this.sandstormState = State.SandstormMode.CONTROLLED;
      }
      break;
    case CONTROLLED:
      this.generalPeriodic();
      break;
    }
  }

  @Override
  public void teleopInit() {
    if (this.autoEnabledFirst) {
      this.teleopState = State.TeleopMode.CONTROLLED;
    } else {
      this.teleopState = State.TeleopMode.START;
    }
  }

  @Override
  public void teleopPeriodic() {
    switch (this.teleopState) {
    default:
    case START:
      this.modeStateTimer.start(1000);
      this.teleopState = State.TeleopMode.INIT;
      break;
    case INIT:
      this.generalInit();
      this.modeStateTimer.update(now);
      if (this.modeStateTimer.isFinished()) {
        this.teleopState = State.TeleopMode.CONTROLLED;
      }
      break;
    case CONTROLLED:
      this.generalPeriodic();
      break;
    }
  }

  @Override
  public void testInit() {
    this.testState = State.TestMode.START;
  }

  @Override
  public void testPeriodic() {
    switch (this.testState) {
    default:
    case START:
      this.modeStateTimer.start(1000);
      this.testState = State.TestMode.INIT;
      break;
    case INIT:
      this.generalInit();
      this.modeStateTimer.update(now);
      if (this.modeStateTimer.isFinished()) {
        this.testState = State.TestMode.CONTROLLED;
      }
    case CONTROLLED:
      this.generalPeriodic();
      if (movementController.buttonDown(Button.BACK)) {
        this.testState = State.TestMode.RESET;
      }
      break;
    case RESET:
      this.reset();
      if (movementController.buttonDown(Button.START)) {
        this.testState = State.TestMode.START;
      }
      break;
    }
  }

  @Override
  public void disabledInit() {
    this.autoEnabledFirst = false;
    this.driveTrain.reset();
  }

  public void generalInit() {
    if (!Constants.PRACTICE_ROBOT) {
      this.elevator.lowerArms();
      this.ballSystem.lowerArms();
      this.hatchSystem.pushOutward();
    }
  }

  private PosControl drivePosControl;
  private double driveTarget;
  private PosControl elevatorPosControl;
  private double elevatorTarget;
  private double elevatorOutput;

  public void generalPeriodic() {
    // drive
    double xAxis = movementController.getAxis(Axis.RIGHT_X);
    double yAxis = movementController.getAxis(Axis.LEFT_Y);
    switch (this.driveState) {
    case CONTROLLED:
      this.driveTrain.drive(-yAxis, xAxis);
      if (movementController.buttonPressed(Button.LEFT_BUMPER)) {
        driveTarget = driveTrain.getAngle() + 180;
        drivePosControl = new PosControl(driveTarget, 1, 3, t -> t * 0.1, 3);
        driveState = State.Drive.TURN;
      }
      break;
    case TURN:
      if (drivePosControl.onTarget() || Math.abs(xAxis) > 0.2 || Math.abs(yAxis) > 0.2) {
        this.driveState = State.Drive.CONTROLLED;
      } else {
        double speed = drivePosControl.getSpeed(this.driveTrain.getAngle(), now);
        this.driveTrain.turn(speed);
      }
      break;
    }
    // if it's the real robot
    if (!Constants.PRACTICE_ROBOT) {
      // elevator
      boolean moveElevator = false;
      double elevatorTarget = 0;
      if (actionsController.buttonPressed(Button.A)) {
        moveElevator = true;
        if (actionsController.buttonDown(Button.RIGHT_BUMPER)) {
          elevatorTarget = 43.5;
        } else {
          elevatorTarget = 53;
        }
      }
      if (actionsController.buttonPressed(Button.B)) {
        moveElevator = true;
        if (actionsController.buttonDown(Button.RIGHT_BUMPER)) {
          elevatorTarget = 61.5;
        } else {
          elevatorTarget = 73;
        }
      }
      if (actionsController.buttonPressed(Button.X)) {
        moveElevator = true;
        if (actionsController.buttonDown(Button.RIGHT_BUMPER)) {
          elevatorTarget = 80;
        } else {
          elevatorTarget = 91;
        }
      }
      if (moveElevator) {
        elevatorState = State.Elevator.STOP;
        elevatorTimer.update(now);
        elevatorTimer.start(100);
        this.elevatorTarget = elevatorTarget;
        elevatorPosControl = new PosControl(elevatorTarget, 0.1, 0.8, t -> 0.02 * t, 1);
      }
      double leftYAxis = actionsController.getAxis(Axis.RIGHT_Y);
      if (leftYAxis > 0.2 || leftYAxis < -0.2) {
        if (-leftYAxis > 0 || elevator.getHeight() > 39) {
          elevator.move(-leftYAxis);
        } else {
          elevator.stop();
        }
        elevatorState = State.Elevator.CONTROLLED;
      } else {
        switch (elevatorState) {
        case GOTO:
          double speed = elevatorPosControl.getSpeed(elevator.getHeight(), now);
          elevatorOutput = speed;
          elevator.move(speed);
          if (elevatorPosControl.onTarget()) {
            elevatorState = State.Elevator.CONTROLLED;
          }
          break;
        case STOP:
          elevatorTimer.update(now);
          if (elevatorTimer.isFinished()) {
            this.elevatorState = State.Elevator.GOTO;
          }
          elevator.stop();
          break;
        case CONTROLLED:
          elevator.stop();
          break;
        }
      }
      // ball and hatch
      if (!movementController.buttonDown(Button.X)) {
        ballSystem.stopBallMotor();
      }
      switch (ballHatchState) {
      case CONTROLLED:
        // pick up hatch
        if (movementController.buttonPressed(Button.A)) {
          hatchSystem.pushOutward();
        }
        // release hatch
        if (movementController.buttonPressed(Button.B)) {
          hatchSystem.comeTogether();
        }
        if (ballSystem.sensedBall()) {
          seenBallCount++;
          if (seenBallCount < 3000 / 20) {
            ballSystem.pickupBall();
          }
        } else {
          seenBallCount = 0;
        }
        // pick up ball
        if (movementController.buttonDown(Button.X)) {
          ballSystem.pickupBall();
        }

        // release ball
        if (movementController.buttonDown(Button.Y)) {
          this.ballHatchState = State.BallHatch.RAISE_ARMS;
          this.ballHatchTimer.update(now);
          ballHatchTimer.start(425);
        }
        break;
      case RAISE_ARMS:
        this.ballSystem.raiseArms();
        this.ballHatchTimer.update(now);
        if (this.ballHatchTimer.isFinished()) {
          this.ballHatchState = State.BallHatch.PUNCH_BALL;
          ballHatchTimer.start(100);
        }
        break;
      case PUNCH_BALL:
        this.ballSystem.punchBall();
        this.ballHatchTimer.update(now);
        if (this.ballHatchTimer.isFinished()) {
          this.ballHatchState = State.BallHatch.RETRACT_PUNCHER;
          ballHatchTimer.start(500);
        }
        break;
      case RETRACT_PUNCHER:
        this.ballSystem.retractPuncher();
        this.ballHatchTimer.update(now);
        if (this.ballHatchTimer.isFinished()) {
          this.ballHatchState = State.BallHatch.LOWER_ARMS;
          ballHatchTimer.start(1000);
        }
        break;
      case LOWER_ARMS:
        this.ballSystem.lowerArms();
        this.ballHatchTimer.update(now);
        if (this.ballHatchTimer.isFinished()) {
          this.ballHatchState = State.BallHatch.CONTROLLED;
        }
        break;
      }
    }
  }

  @Override
  public void robotPeriodic() {
    now = System.currentTimeMillis();
  }

  @Override
  protected void finalize() {
    super.finalize();
    robotRunning = false;
  }
}
