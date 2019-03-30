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

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.input.Axis;
import frc.robot.input.Button;
import frc.robot.input.Controller;
import frc.robot.subsystems.BallSystem;
import frc.robot.subsystems.DriveTrainSystem;
import frc.robot.subsystems.ElevatorSystem;
import frc.robot.subsystems.HatchSystem;
import frc.robot.subsystems.Solenoid4150;
import frc.robot.subsystems.base.CANDriveMotorPair;
import frc.robot.subsystems.base.CANMotor;
import frc.util.AltPosControl;
import frc.util.CountdownTimer;

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

  //private long seenBallCount;

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

    //this.seenBallCount = 0;
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

  public void updateDashboard() {
    // drive: input
    NetworkTableVariables.leftMotorSpeed.setDouble(this.driveTrain.getLeftMotorSpeed());
    NetworkTableVariables.rightMotorSpeed.setDouble(this.driveTrain.getRightMotorSpeed());
    NetworkTableVariables.leftMotorOutputPercent.setDouble(this.driveTrain.getLeftMotorOutputPercent());
    NetworkTableVariables.rightMotorOutputPercent.setDouble(this.driveTrain.getRightMotorOutputPercent());
    NetworkTableVariables.angle.setDouble(this.driveTrain.getAngle());
    // drive: output
    NetworkTableVariables.leftMotorTarget.setDouble(this.driveTrain.getLeftMotorTarget());
    NetworkTableVariables.rightMotorTarget.setDouble(this.driveTrain.getRightMotorTarget());
    NetworkTableVariables.leftMotorOutput.setDouble(this.driveTrain.getLeftMotorOutput());
    NetworkTableVariables.rightMotorOutput.setDouble(this.driveTrain.getRightMotorOutput());
    NetworkTableVariables.driveTarget.setDouble(driveTarget);
    // drive: vision
    targetFound = NetworkTableVariables.onTarget.getBoolean(false);
    angleTarget = NetworkTableVariables.targetAngle.getDouble(0);
    // not on practice
    if (!Constants.PRACTICE_ROBOT) {
      NetworkTableVariables.ballDetected.setBoolean(this.ballSystem.sensedBall());
      NetworkTableVariables.elevatorHeight.setDouble(this.elevator.getHeight());
      NetworkTableVariables.elevatorTarget.setDouble(elevatorTarget);
      NetworkTableVariables.elevatorOutput.setDouble(elevatorOutput);
    }
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    this.driveTrain.init(new CANDriveMotorPair(new TalonSRX(14), new TalonSRX(13)),
        new CANDriveMotorPair(new TalonSRX(10), new TalonSRX(12)), new ADIS16448_IMU());
    this.driveTrain.setRateLimit(2);
    if (!Constants.PRACTICE_ROBOT) {
      this.ballSystem.init(new CANMotor(new VictorSPX(15)), new Solenoid4150(new DoubleSolenoid(1, 3, 2)),
          new Solenoid4150(new DoubleSolenoid(1, 7, 6)), new DigitalInput(2));
      this.elevator.init(new CANMotor(new TalonSRX(9)).invert(), new Solenoid4150(new DoubleSolenoid(0, 5, 4)),
          new Solenoid4150(new DoubleSolenoid(1, 1, 0)), new Counter(3));
      this.hatchSystem.init(new Solenoid4150(new DoubleSolenoid(1, 5, 4)));
    }
    this.movementController.init(new Joystick(0));
    this.actionsController.init(new Joystick(1));
    NetworkTableVariables.start();
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
      // if (movementController.buttonDown(Button.BACK)) {
      // this.testState = State.TestMode.RESET;
      // }
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

  private AltPosControl drivePosControl;
  private double driveTarget;
  private AltPosControl elevatorPosControl;
  private double elevatorTarget;
  private double elevatorOutput;
  private double angleTarget;
  private boolean targetFound;

  public void generalPeriodic() {
    // drive
    double xAxis = movementController.getAxis(Axis.RIGHT_X);
    double yAxis = movementController.getAxis(Axis.LEFT_Y);
    if (movementController.buttonDown(Button.BACK)) {
      this.reset();
    }
    if (movementController.buttonDown(Button.START)) {
      this.generalInit();
    }
    switch (this.driveState) {
    case CONTROLLED:
      this.driveTrain.drive(-yAxis, xAxis);
      if (movementController.buttonPressed(Button.LEFT_BUMPER)) {
        driveTarget = this.driveTrain.getAngle() - 90;
        // drivePosControl = new PosControl(driveTarget, driveTrain.getAngle(), 1, 5, t -> t * 0.01, 1);
        drivePosControl = new AltPosControl(driveTarget, 1, 7.5, 0.05, 1, true, 35);
        driveState = State.Drive.TURN;
      } else if (movementController.buttonPressed(Button.RIGHT_BUMPER)) {
        driveTarget = this.driveTrain.getAngle() + 90;
        drivePosControl = new AltPosControl(driveTarget, 1, 7.5, 0.05, 1, true, 35);
        driveState = State.Drive.TURN;
      } else if (Math.abs(movementController.getAxis(Axis.TRIGGER_LEFT)) > 0.2) {
        if (targetFound) {
          driveTarget = angleTarget;
          drivePosControl = new AltPosControl(driveTarget, 1, 6, 0.05, 1, true, 45);
          driveState = State.Drive.TURN;
        }
      }
      break;
    case TURN:
      if (drivePosControl.onTarget() || Math.abs(xAxis) > 0.2 || Math.abs(yAxis) > 0.2) {
        this.driveState = State.Drive.CONTROLLED;
      } else {
        double speed = drivePosControl.getSpeed(this.driveTrain.getAngle());
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
          // low hatch
          elevatorTarget = 43.5;
        } else {
          // low ball
          elevatorTarget = 53;
        }
      }
      if (actionsController.buttonPressed(Button.B)) {
        moveElevator = true;
        if (actionsController.buttonDown(Button.RIGHT_BUMPER)) {
          // medium hatch
          elevatorTarget = 61.5;
        } else {
          // medium ball
          elevatorTarget = 73;
        }
      }
      if (actionsController.buttonPressed(Button.Y)) {
        moveElevator = true;
        if (actionsController.buttonDown(Button.RIGHT_BUMPER)) {
          // high hatch
          elevatorTarget = 80;
        } else {
          // high ball
          elevatorTarget = 91;
        }
      }
      if (actionsController.buttonPressed(Button.X)) {
        moveElevator = true;
        // cargo ship ball
        elevatorTarget = 60;
      }
      if (moveElevator) {
        elevatorState = State.Elevator.STOP;
        elevatorTimer.update(now);
        elevatorTimer.start(100);
        this.elevatorTarget = elevatorTarget + elevator.getHeight();
        // elevatorPosControl = new PosControl(elevatorTarget, elevator.getHeight(),
        // 0.1, 0.8, t -> 0.02 * t, 1);
        elevatorPosControl = new AltPosControl(elevatorTarget, 0.5, 3, 0.1, 0.2, true, 45);
      }
      double leftYAxis = actionsController.getAxis(Axis.RIGHT_Y);
      if (leftYAxis > 0.2 || leftYAxis < -0.2) {
        elevator.move(-leftYAxis);
        elevatorState = State.Elevator.CONTROLLED;
      } else {
        switch (elevatorState) {
        case GOTO:
          double speed = elevatorPosControl.getSpeed(elevator.getHeight());
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
        /*
         * if (ballSystem.sensedBall()) { seenBallCount++; if (seenBallCount < 3000 /
         * 20) { ballSystem.pickupBall(); } } else { seenBallCount = 0; }
         */
        // pick up ball
        if (movementController.buttonDown(Button.X)) {
          hatchSystem.pushOutward();
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

  public boolean isRunning() {
    return this.robotRunning;
  }
}
