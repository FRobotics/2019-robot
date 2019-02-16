/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
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
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.input.Axis;
import frc.robot.input.Button;
import frc.robot.input.Controller;
import frc.robot.state.BallHatchState;
import frc.robot.state.DriveState;
import frc.robot.state.ElevatorState;
import frc.robot.state.SandstormState;
import frc.robot.state.State;
import frc.robot.state.TeleopState;
import frc.robot.state.TestState;
import frc.robot.subsystems.BallSystem;
import frc.robot.subsystems.DriveTrainSystem;
import frc.robot.subsystems.ElevatorSystem;
import frc.robot.subsystems.HatchSystem;
import frc.robot.subsystems.base.CANDriveMotorPair;
import frc.robot.subsystems.base.CANMotor;
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
  // in the future just store an array of #s or enums as a "path" to travel, makes
  // things a lot easier
  private State<SandstormState> sandstormState;
  private State<TeleopState> teleopState;
  private State<TestState> testState;

  private State<DriveState> driveState;
  private State<BallHatchState> ballHatchState;
  private State<ElevatorState> elevatorState;

  private long seenBallCount;

  public Robot() {
    this.driveTrain = new DriveTrainSystem();
    this.ballSystem = new BallSystem();
    this.elevator = new ElevatorSystem();
    this.hatchSystem = new HatchSystem();

    this.movementController = new Controller();
    this.actionsController = new Controller();

    this.driveState = new State<DriveState>(DriveState.CONTROLLED);
    this.ballHatchState = new State<BallHatchState>(BallHatchState.NONE);
    this.elevatorState = new State<ElevatorState>(ElevatorState.CONTROLLED);
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
    /*SmartDashboard.putNumber("leftMotorSpeed", this.driveTrain.getLeftMotorSpeed());
    SmartDashboard.putNumber("rightMotorSpeed", this.driveTrain.getRightMotorSpeed());
    SmartDashboard.putNumber("leftMotorOutputPercent", this.driveTrain.getLeftMotorOutputPercent());
    SmartDashboard.putNumber("rightMotorOutputPercent", this.driveTrain.getRightMotorOutputPercent());
    SmartDashboard.putNumber("angle", this.driveTrain.getAngle());
    SmartDashboard.putBoolean("ballDetected", this.ballSystem.sensedBall());
    SmartDashboard.putNumber("elevatorHeight", this.elevator.getHeight());*/
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
    this.seenBallCount = 0;
    this.autoEnabledFirst = false;
  }

  @Override
  public void autonomousInit() {
    this.autoEnabledFirst = true;
    this.sandstormState = new State<SandstormState>(new SandstormState[] { SandstormState.START, SandstormState.DEFAULT });
    this.sandstormState.start();
  }

  @Override
  public void autonomousPeriodic() {
    switch (this.sandstormState.getState()) {
      default:
      case START:
        this.generalStart();
        break;
      case DEFAULT:
        this.generalPeriodic();
        break;
      }
      this.sandstormState.periodic();
      this.driveState.periodic();
      this.ballHatchState.periodic();
      this.elevatorState.periodic();
      updateDashboard();
  }

  @Override
  public void teleopInit() {
    if(this.autoEnabledFirst) {
      this.teleopState = new State<TeleopState>(TeleopState.DEFAULT);
    } else {
      this.teleopState = new State<TeleopState>(new TeleopState[] { TeleopState.START, TeleopState.DEFAULT });
    }
    this.teleopState.start();
  }

  @Override 
  public void teleopPeriodic() {
    
    switch (this.teleopState.getState()) {
    default:
    case START:
      this.generalStart();
      break;
    case DEFAULT:
      this.generalPeriodic();
      break;
    }
    this.teleopState.periodic();
    this.driveState.periodic();
    this.ballHatchState.periodic();
    this.elevatorState.periodic();
    updateDashboard();
  }

  @Override
  public void testInit() {
    this.testState = new State<TestState>(new TestState[] { TestState.START, TestState.DEFAULT });
    this.testState.start();
  }

  @Override
  public void testPeriodic() {
    switch (this.testState.getState()) {
    default:
    case RESET:
      this.reset();
      if (movementController.buttonDown(Button.START)) {
        this.testState.setStates(new TestState[] { TestState.START, TestState.DEFAULT });
      }
      break;
    case START:
      this.generalStart();
      break;
    case DEFAULT:
      this.generalPeriodic();
      // reset
      if (movementController.buttonDown(Button.BACK)) {
        this.testState.setState(TestState.RESET);
      }
      break;
    }
    this.testState.periodic();
    this.driveState.periodic();
    this.ballHatchState.periodic();
    this.elevatorState.periodic();
    updateDashboard();
  }

  @Override
  public void disabledInit() {
    this.autoEnabledFirst = false;
    this.driveTrain.reset();
  }

  @Override
  public void disabledPeriodic() {
    this.updateDashboard();
  }

  public void generalStart() {
    if (!Constants.PRACTICE_ROBOT) {
      this.elevator.lowerArms();
      this.ballSystem.lowerArms();
      this.hatchSystem.pushOutward();
    }
  }

  public void generalPeriodic() {
    // drive
    switch (this.driveState.getState()) {
    case CONTROLLED:
      double[] motorTargets = Util.smoothDrive(movementController.buttonDown(Button.RIGHT_BUMPER),
          -movementController.getAxis(Axis.LEFT_Y), movementController.getAxis(Axis.RIGHT_X));
      double leftMotorTarget = motorTargets[0] * 10;
      double rightMotorTarget = motorTargets[1] * 10;
      SmartDashboard.putNumber("leftMotorTarget", leftMotorTarget);
      SmartDashboard.putNumber("rightMotorTarget", rightMotorTarget);
      double leftMotorOutput = driveTrain.setLeftMotorSpeed(leftMotorTarget);
      double rightMotorOutput = driveTrain.setRightMotorSpeed(rightMotorTarget);
      SmartDashboard.putNumber("leftMotorOutput", leftMotorOutput);
      SmartDashboard.putNumber("rightMotorOutput", rightMotorOutput);
      break;
    case TURN:
      PosControl posControl = this.driveState.getState().getPosControl();
      if (posControl.onTarget()) {
        this.driveState.setState(DriveState.CONTROLLED);
      } else {
        double speed = posControl.getSpeed(this.driveTrain.getAngle());
        this.driveTrain.turn(speed);
      }
      break;
    }
    // if it's the real robot
    if (!Constants.PRACTICE_ROBOT) {
      // elevator
      // I really gotta move these functions into their respective classes jeez
      if (actionsController.buttonPressed(Button.A)) {
        elevatorState.setStates(new ElevatorState[] { ElevatorState.STOP, ElevatorState.GOTO });
        elevatorState.getState().setPosControl(new PosControl(0, 0.1, 0.5, t -> t, 1));
      }
      if (actionsController.buttonPressed(Button.B)) {
        elevatorState.setStates(new ElevatorState[] { ElevatorState.STOP, ElevatorState.GOTO });
        elevatorState.getState().setPosControl(new PosControl(19, 0.1, 0.5, t -> t, 1));
      }
      if (actionsController.buttonPressed(Button.X)) {
        elevatorState.setStates(new ElevatorState[] { ElevatorState.STOP, ElevatorState.GOTO });
        elevatorState.getState().setPosControl(new PosControl(0, 0.1, 0.5, t -> t, 1));
      }
      if (actionsController.buttonPressed(Button.Y)) {
        elevatorState.setStates(new ElevatorState[] { ElevatorState.STOP, ElevatorState.GOTO });
        elevatorState.getState().setPosControl(new PosControl(0, 0.1, 0.5, t -> t, 1));
      }
      /*if (actionsController.getAxis(Axis.D_PAD_X) > 0.2) {
        elevatorState.setStates(new ElevatorState[] { ElevatorState.STOP, ElevatorState.GOTO });
        elevatorState.getState().setPosControl(new PosControl(0, 0.1, 0.5, t -> t, 1));
      }
      if (actionsController.getAxis(Axis.D_PAD_X) < -0.2) {
        elevatorState.setStates(new ElevatorState[] { ElevatorState.STOP, ElevatorState.GOTO });
        elevatorState.getState().setPosControl(new PosControl(75, 0.1, 0.5, t -> t, 1));
      }
      if (actionsController.getAxis(Axis.D_PAD_Y) > 0.2) {
        elevatorState.setStates(new ElevatorState[] { ElevatorState.STOP, ElevatorState.GOTO });
        elevatorState.getState().setPosControl(new PosControl(0, 0.1, 0.5, t -> t, 1));
      }
      if (actionsController.getAxis(Axis.D_PAD_Y) < -0.2) {
        elevatorState.setStates(new ElevatorState[] { ElevatorState.STOP, ElevatorState.GOTO });
        elevatorState.getState().setPosControl(new PosControl(47, 0.1, 0.5, t -> t, 1));
      }*/
      double leftYAxis = actionsController.getAxis(Axis.RIGHT_Y);
      if (leftYAxis > 0.2 || leftYAxis < -0.2) {
        elevator.move(-leftYAxis);
        elevatorState.setState(ElevatorState.CONTROLLED);
        elevatorState.clearQueue();
      } else {
        switch (elevatorState.getState()) {
        case GOTO:
          PosControl posControl = elevatorState.getState().getPosControl();
          if(posControl == null) {
            elevatorState.setState(ElevatorState.CONTROLLED);
            break;
          }
          double speed = posControl.getSpeed(elevator.getHeight());
          if(speed < 0) {
            speed /= 2;
          }
          elevator.move(-speed);
          if (posControl.onTarget()) {
            elevatorState.setState(ElevatorState.CONTROLLED);
          }
          break;
        case STOP:
        case CONTROLLED:
          elevator.stop();
          break;
        }
      }
      // ball and hatch
      if (!movementController.buttonDown(Button.X)) {
        ballSystem.stopBallMotor();
      }
      switch (ballHatchState.getState()) {
      case NONE:
        ballSystem.retractPuncher();
        // pick up hatch
        if (movementController.buttonDown(Button.A)) {
          hatchSystem.pushOutward();
        }
        // release hatch
        if (movementController.buttonDown(Button.B)) {
          hatchSystem.comeTogether();
        }
        if (ballSystem.sensedBall()) {
          seenBallCount++;
          if (seenBallCount < 3000 / 20)
            ballSystem.pickupBall();
        } else {
          seenBallCount = 0;
        }
        // pick up ball
        if (movementController.buttonDown(Button.X)) {
          ballSystem.pickupBall();
        }

        // release ball
        if (movementController.buttonDown(Button.Y)) {
          this.ballHatchState.setStates(new BallHatchState[] { BallHatchState.RAISE_ARMS, BallHatchState.PUNCH_BALL,
              BallHatchState.LOWER_ARMS, BallHatchState.NONE });
        }
        break;
      case RAISE_ARMS:
        this.ballSystem.raiseArms();
        break;
      case LOWER_ARMS:
        this.ballSystem.lowerArms();
        break;
      case PUNCH_BALL:
        this.ballSystem.punchBall();
        break;
      }
    }
  }

}
