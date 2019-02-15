/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.function.Function;

import com.analog.adis16448.frc.ADIS16448_IMU;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.cscore.CameraServerJNI;
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
import frc.robot.state.State;
import frc.robot.state.TeleopState;
import frc.robot.subsystems.BallSystem;
import frc.robot.subsystems.DriveTrainSystem;
import frc.robot.subsystems.ElevatorSystem;
import frc.robot.subsystems.HatchSystem;
import frc.robot.subsystems.base.CANMotor;
import frc.util.PosControl;
import frc.robot.subsystems.base.CANDriveMotorPair;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

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
  private State<TeleopState> teleopState;
  private State<DriveState> driveState;
  private State<BallHatchState> ballHatchState;
  private State<ElevatorState> elevatorState;

  public Robot() {
    this.driveTrain = new DriveTrainSystem();
    this.ballSystem = new BallSystem();
    this.elevator = new ElevatorSystem();
    this.hatchSystem = new HatchSystem();

    this.movementController = new Controller();
    this.actionsController = new Controller();

    this.teleopState = new State<TeleopState>(new TeleopState[] { TeleopState.START, TeleopState.DEFAULT });
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
    SmartDashboard.putNumber("rightMotorSpeed", this.driveTrain.getRightMotorSpeed());
    SmartDashboard.putNumber("leftMotorSpeed", this.driveTrain.getLeftMotorSpeed());
    SmartDashboard.putNumber("angle", this.driveTrain.getAngle());
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
          new DigitalInput(0));
      this.elevator.init(new CANMotor(new TalonSRX(9)), new DoubleSolenoid(0, 5, 4), new DoubleSolenoid(1, 1, 0), new Ultrasonic(0, 3));
      this.hatchSystem.init(new DoubleSolenoid(1, 5, 4));
    }
    this.movementController.init(new Joystick(0));
    this.actionsController.init(new Joystick(1));
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    this.teleopState.start();
  }

  @Override
  public void teleopPeriodic() {
    switch (this.teleopState.getState()) {
    default:
    case RESET:
      this.reset();
      if (movementController.buttonDown(Button.START)) {
        this.teleopState.setStates(new TeleopState[] { TeleopState.START, TeleopState.DEFAULT });
      }
      break;
    case START:
      if (!Constants.PRACTICE_ROBOT) {
        this.elevator.lowerArms();
        this.ballSystem.lowerArms();
      }
      break;
    case DEFAULT:
      // drive
      switch (this.driveState.getState()) {
      case CONTROLLED:
        double[] motorOutputs = Util.smoothDrive(movementController.buttonDown(Button.RIGHT_BUMPER),
            -movementController.getAxis(Axis.LEFT_Y), movementController.getAxis(Axis.RIGHT_X));
        double leftMotorOutput = motorOutputs[0] * 10;
        double rightMotorOutput = motorOutputs[1] * 10;
        SmartDashboard.putNumber("leftMotorOuput", leftMotorOutput);
        SmartDashboard.putNumber("rightMotorOuput", rightMotorOutput);
        driveTrain.setLeftMotorSpeed(leftMotorOutput);
        driveTrain.setRightMotorSpeed(rightMotorOutput);
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
        double leftYAxis = actionsController.getAxis(Axis.RIGHT_Y);
        if(actionsController.buttonPressed(Button.A)) {
          elevatorState.setStates(new ElevatorState[]{ElevatorState.STOP, ElevatorState.GOTO});
          elevatorState.getState().setPosControl(new PosControl(0, 0.1, 0.5, t -> t, 1));
        }
        if(actionsController.buttonPressed(Button.B)) {
          elevatorState.setStates(new ElevatorState[]{ElevatorState.STOP, ElevatorState.GOTO});
          elevatorState.getState().setPosControl(new PosControl(19, 0.1, 0.5, t -> t, 1));
        }
        if(actionsController.buttonPressed(Button.X)) {
          elevatorState.setStates(new ElevatorState[]{ElevatorState.STOP, ElevatorState.GOTO});
          elevatorState.getState().setPosControl(new PosControl(0, 0.1, 0.5, t -> t, 1));
        }
        if(actionsController.buttonPressed(Button.Y)) {
          elevatorState.setStates(new ElevatorState[]{ElevatorState.STOP, ElevatorState.GOTO});
          elevatorState.getState().setPosControl(new PosControl(0, 0.1, 0.5, t -> t, 1));
        }
        if(actionsController.getAxis(Axis.D_PAD_X) > 0.2) {
          elevatorState.setStates(new ElevatorState[]{ElevatorState.STOP, ElevatorState.GOTO});
          elevatorState.getState().setPosControl(new PosControl(0, 0.1, 0.5, t -> t, 1));
        }
        if(actionsController.getAxis(Axis.D_PAD_X) < -0.2) {
          elevatorState.setStates(new ElevatorState[]{ElevatorState.STOP, ElevatorState.GOTO});
          elevatorState.getState().setPosControl(new PosControl(75, 0.1, 0.5, t -> t, 1));
        }
        if(actionsController.getAxis(Axis.D_PAD_Y) > 0.2) {
          elevatorState.setStates(new ElevatorState[]{ElevatorState.STOP, ElevatorState.GOTO});
          elevatorState.getState().setPosControl(new PosControl(0, 0.1, 0.5, t -> t, 1));
        }
        if(actionsController.getAxis(Axis.D_PAD_Y) < -0.2) {
          elevatorState.setStates(new ElevatorState[]{ElevatorState.STOP, ElevatorState.GOTO});
          elevatorState.getState().setPosControl(new PosControl(47, 0.1, 0.5, t -> t, 1));
        }
        if (leftYAxis > 0.2 || leftYAxis < -0.2) {
          elevator.move(leftYAxis);
          elevatorState.setState(ElevatorState.CONTROLLED);
          elevatorState.clearQueue();
        } else {
          switch (elevatorState.getState()) {
          case GOTO:
            PosControl posControl = elevatorState.getState().getPosControl();
            double speed = posControl.getSpeed(elevator.getHeight());
            elevator.move(speed);
            if(posControl.onTarget()) {
              elevatorState.setState(ElevatorState.CONTROLLED);
            }
            break;
          case STOP:
            elevator.stop();
            break;
          case CONTROLLED:
            break;
          }
        }
        // ball and hatch
        if (!movementController.buttonDown(Button.X)) {
          ballSystem.stopBallMotor();
        }
        switch (ballHatchState.getState()) {
        case NONE:
          // pick up hatch
          if (movementController.buttonDown(Button.A)) {
            hatchSystem.pushOutward();
          }
          // release hatch
          if (movementController.buttonDown(Button.B)) {
            hatchSystem.comeTogether();
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
      // reset
      if (movementController.buttonDown(Button.BACK)) {
        this.teleopState.setState(TeleopState.RESET);
      }
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
  }

  @Override
  public void testPeriodic() {
  }

  @Override
  public void disabledInit() {
    this.driveTrain.reset();
  }

}
