/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

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
import frc.robot.subsystems.base.CANMotor;
import frc.robot.subsystems.base.CANDriveMotorPair;
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
    START, RESET, DEFAULT
  }

  private TeleopState teleopState;
  private CountdownTimer stateTimer;

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
    /*
     * this.ballSystem.reset(); this.elevator.reset(); this.hatchSystem.reset();
     */
  }

  public void updateDashboard() {
    SmartDashboard.putNumber("rightMotorSpeed", this.driveTrain.getRightMotorSpeed());
    SmartDashboard.putNumber("leftMotorSpeed", this.driveTrain.getLeftMotorSpeed());
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    // TODO: put in correct channel ids
    this.driveTrain.init(new CANDriveMotorPair(new TalonSRX(14), new TalonSRX(13)),
        new CANDriveMotorPair(new TalonSRX(10), new TalonSRX(12)));
    this.driveTrain.setRateLimit(0.1);
    // TODO: one of these isn't a TalonSRX
    /*
     * this.ballSystem.init(new CANMotor(new TalonSRX(1)), new DoubleSolenoid(0, 1),
     * new DoubleSolenoid(2, 3)); this.elevator.init(new CANMotor(new TalonSRX(2)),
     * new DoubleSolenoid(4, 5), new DoubleSolenoid(6, 7));
     * this.hatchSystem.init(new DoubleSolenoid(0, 1));
     */
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
    this.stateTimer = new CountdownTimer(1000);
  }

  @Override
  public void teleopPeriodic() {
    switch (this.teleopState) {
    default:
    case RESET:
      this.reset();
      if (stateTimer.isFinished() && movementController.buttonDown(Button.START)) {
        this.teleopState = TeleopState.START;
      }
      break;
    case START:
      // this.elevator.lowerArms();
      if (stateTimer.isFinished()) {
        this.teleopState = TeleopState.DEFAULT;
        this.stateTimer.start();
      }
      break;
    case DEFAULT:
      // drive
      double[] motorOutputs = Util.smoothDrive(movementController.buttonDown(Button.RIGHT_BUMPER),
          -movementController.getAxis(Axis.LEFT_Y), movementController.getAxis(Axis.RIGHT_X));
      double leftMotorOutput = motorOutputs[0] * 10;
      double rightMotorOutput = motorOutputs[1] * 10;
      SmartDashboard.putNumber("leftMotorOuput", leftMotorOutput);
      SmartDashboard.putNumber("rightMotorOuput", rightMotorOutput);
      driveTrain.setLeftMotorSpeed(leftMotorOutput);
      driveTrain.setRightMotorSpeed(rightMotorOutput);
      // elevator
      /*
       * double leftYAxis = actionsController.getAxis(Axis.LEFT_Y); if (leftYAxis >
       * 0.2) { elevator.moveUp(leftYAxis); } else if (leftYAxis < -0.2) {
       * elevator.moveDown(-leftYAxis); // this function seems kinda pointless } else
       * { elevator.stop(); } // ball double rightYAxis =
       * actionsController.getAxis(Axis.RIGHT_Y); if (rightYAxis > 0.2) {
       * ballSystem.armsDown(); ballSystem.releaseBall(); } else if (rightYAxis <
       * -0.2) { ballSystem.armsDown(); ballSystem.pickupBall(); } else {
       * ballSystem.raiseArms(); ballSystem.stopBallMotor(); } if
       * (actionsController.buttonDown(Button.A)) { ballSystem.punchBall(); } else {
       * ballSystem.retractPuncher(); } // hatch if
       * (actionsController.buttonDown(Button.LEFT_BUMPER)) {
       * hatchSystem.pushOutward(); } else if
       * (actionsController.buttonDown(Button.RIGHT_BUMPER)) {
       * hatchSystem.comeTogether(); } // reset if
       * (movementController.buttonDown(Button.BACK)) { this.teleopState =
       * TeleopState.RESET; this.stateTimer.start(); } break;
       */
    }
    updateDashboard();
  }

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
  }

}
