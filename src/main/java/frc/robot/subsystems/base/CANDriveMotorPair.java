package frc.robot.subsystems.base;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

import frc.robot.Constants;

public class CANDriveMotorPair implements EncoderMotor {

    private BaseMotorController trevor;
    private BaseMotorController ahmad;
    
    public CANDriveMotorPair(BaseMotorController master, BaseMotorController follower) {
        this.trevor = master;
        this.ahmad = follower;

        master.setNeutralMode(NeutralMode.Brake);
        follower.setNeutralMode(NeutralMode.Brake);
        
        master.setSensorPhase(false);
        follower.setSensorPhase(false);

        int slotIdx = Constants.Drive.PID_LOOP_INDEX;
        int timeoutMS = Constants.Drive.TIMEOUT_MS;

        master.config_kF(slotIdx, Constants.Drive.F, timeoutMS);
        master.config_kP(slotIdx, Constants.Drive.P, timeoutMS);
        master.config_kI(slotIdx, Constants.Drive.I, timeoutMS);
        master.config_kD(slotIdx, Constants.Drive.D, timeoutMS);
        master.config_IntegralZone(slotIdx, Constants.Drive.INTEGRAL_ZONE, timeoutMS);

        master.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, slotIdx, timeoutMS);
        follower.follow(master);
    }

    @Override
    public void setSpeed(double speed) {
        trevor.set(ControlMode.Velocity, speed * Constants.Drive.OUTPUT_MULTIPLIER);
    }

    @Override
    public double getSpeed() {
        return trevor.getSelectedSensorVelocity() * Constants.Drive.INPUT_MULTIPLIER;
    }

    @Override
    public EncoderMotor setInverted(boolean inverted) {
        trevor.setInverted(inverted);
        ahmad.setInverted(inverted);
        return this;
    }

    @Override
    public EncoderMotor invert() {
        this.setInverted(!trevor.getInverted());
        return this;
    }

}