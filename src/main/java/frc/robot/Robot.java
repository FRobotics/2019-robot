/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.subsystems.DriveTrain;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  private DriveTrain driveTrain;

  public Robot() {
    this.driveTrain = new DriveTrain();
  }

  /**
   * Resets all of the subsystems to their base state
   */
  public void reset() {
    this.driveTrain.reset();
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    this.driveTrain.init(new PWMTalonSRX(0), new PWMTalonSRX(0));
  }

  @Override
  public void autonomousInit() {
    reset();
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    reset();
  }

  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void testInit() {
    reset();
  }

  @Override
  public void testPeriodic() {
  }

}
