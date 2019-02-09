package frc.robot.subsystems.base;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

import frc.robot.Constants;

public class CANDriveMotorPair implements Motor {

    private BaseMotorController master;
    private BaseMotorController follower;
    
    public CANDriveMotorPair(BaseMotorController master, BaseMotorController follower) {
        this.master = master;
        this.follower = follower;

        master.setNeutralMode(NeutralMode.Brake);
        follower.setNeutralMode(NeutralMode.Brake);
        
        master.setSensorPhase(false);
        follower.setSensorPhase(false);

        int slotIdx = Constants.CANDrive.PID_LOOP_INDEX;
        int timeoutMS = Constants.CANDrive.TIMEOUT_MS;

        master.config_kF(slotIdx, Constants.CANDrive.F, timeoutMS);
        master.config_kP(slotIdx, Constants.CANDrive.P, timeoutMS);
        master.config_kI(slotIdx, Constants.CANDrive.I, timeoutMS);
        master.config_kD(slotIdx, Constants.CANDrive.D, timeoutMS);
        master.config_IntegralZone(slotIdx, Constants.CANDrive.INTEGRAL_ZONE, timeoutMS);

        master.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, slotIdx, timeoutMS);
        follower.follow(master);
    }

    @Override
    public void setSpeed(double speed) {
        master.set(ControlMode.Velocity, speed);
    }

    @Override
    public Motor setInverted(boolean inverted) {
        master.setInverted(inverted);
        follower.setInverted(inverted);
        return this;
    }

}