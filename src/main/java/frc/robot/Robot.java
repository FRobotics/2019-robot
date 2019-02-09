/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.input.Axis;
import frc.robot.input.Button;
import frc.robot.input.Controller;
import frc.robot.subsystems.BallSystem;
import frc.robot.subsystems.DriveTrainSystem;
import frc.robot.subsystems.ElevatorSystem;
import frc.robot.subsystems.HatchSystem;
import frc.util.CountdownTimer;

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

  // teleop

  private enum TeleopState {
    START, RESET, DEFAULT, DELIVER_BALL
  }

  private TeleopState teleopState;
  private CountdownTimer modeTimer;

  public Robot() {
    this.driveTrain = new DriveTrainSystem();
    this.ballSystem = new BallSystem();
    this.elevator = new ElevatorSystem();
    this.hatchSystem = new HatchSystem();

    this.movementController = new Controller();
    this.actionsController = new Controller();

    this.teleopState = TeleopState.RESET;
  }

  /**
   * Resets all of the subsystems to their base state
   */
  public void reset() {
    this.driveTrain.reset();
    this.ballSystem.reset();
    this.elevator.reset();
    this.hatchSystem.reset();
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    // TODO: put in channel ids
    this.driveTrain.init(new PWMTalonSRX(0), new PWMTalonSRX(0));
    this.ballSystem.init(new PWMTalonSRX(0), new DoubleSolenoid(0, 0), new DoubleSolenoid(0, 0));
    this.elevator.init(new PWMTalonSRX(0), new DoubleSolenoid(0, 0), new DoubleSolenoid(0, 0));
    this.hatchSystem.init(new DoubleSolenoid(0, 0));
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    this.modeTimer = new CountdownTimer(1000);
  }

  @Override
  public void teleopPeriodic() {
    switch (this.teleopState) {
    default:
    case RESET:
      this.reset();
      if (modeTimer.isFinished() && movementController.buttonDown(Button.START)) {
        this.teleopState = TeleopState.START;
      }
      break;
    case START:
      this.elevator.lowerArms();
      if (modeTimer.isFinished()) {
        this.teleopState = TeleopState.DEFAULT;
        this.modeTimer.start();
      }
      break;
    case DEFAULT:
      // drive
      double[] motorOutputs = Util.smoothDrive(movementController.buttonDown(Button.RIGHT_BUMPER),
          movementController.getAxis(Axis.LEFT_Y), movementController.getAxis(Axis.RIGHT_X));
      driveTrain.setLeftMotorSpeed(motorOutputs[0]);
      driveTrain.setRightMotorSpeed(motorOutputs[1]);
      // elevator
      double leftYAxis = actionsController.getAxis(Axis.LEFT_Y);
      if (leftYAxis > 0.2) {
        elevator.moveUp(leftYAxis);
      } else if (leftYAxis < -0.2) {
        elevator.moveDown(-leftYAxis); // this function seems kinda pointless
      } else {
        elevator.stop();
      }
      // ball
      double rightYAxis = actionsController.getAxis(Axis.RIGHT_Y);
      if (rightYAxis > 0.2) {
        ballSystem.armsDown();
        ballSystem.releaseBall();
      } else if (rightYAxis < -0.2) {
        ballSystem.armsDown();
        ballSystem.pickupBall();
      } else {
        ballSystem.raiseArms();
        ballSystem.stopBallMotor();
      }
      if (actionsController.buttonDown(Button.A)) {
        ballSystem.punchBall();
      } else {
        ballSystem.retractPuncher();
      }
      // hatch
      if (actionsController.buttonDown(Button.LEFT_BUMPER)) {
        hatchSystem.pushOutward();
      } else if (actionsController.buttonDown(Button.RIGHT_BUMPER)) {
        hatchSystem.comeTogether();
      }
      // reset
      if (movementController.buttonDown(Button.BACK)) {
        this.teleopState = TeleopState.RESET;
        this.modeTimer.start();
      }
      break;
    }
  }

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
  }

}
