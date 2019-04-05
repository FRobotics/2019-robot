package frc.robot.subsystems.base;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

import frc.robot.Constants;

public class CANDriveMotorPair implements EncoderMotor {

    private BaseMotorController trevor;
    private BaseMotorController ahmad;
    
    public CANDriveMotorPair(BaseMotorController trevor, BaseMotorController ahmad) {
        this.trevor = trevor;
        this.ahmad = ahmad;

        trevor.setNeutralMode(NeutralMode.Brake);
        ahmad.setNeutralMode(NeutralMode.Brake);
        
        trevor.setSensorPhase(false);
        ahmad.setSensorPhase(false);

        int slotIdx = Constants.Drive.PID_LOOP_INDEX;
        int timeoutMS = Constants.Drive.TIMEOUT_MS;

        trevor.config_kF(slotIdx, Constants.Drive.F, timeoutMS);
        trevor.config_kP(slotIdx, Constants.Drive.P, timeoutMS);
        trevor.config_kI(slotIdx, Constants.Drive.I, timeoutMS);
        trevor.config_kD(slotIdx, Constants.Drive.D, timeoutMS);
        trevor.config_IntegralZone(slotIdx, Constants.Drive.INTEGRAL_ZONE, timeoutMS);

        trevor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, slotIdx, timeoutMS);
        ahmad.follow(trevor);
    }

    @Override
    public void setSpeed(double speed) {
        trevor.set(ControlMode.Velocity, speed * Constants.Drive.OUTPUT_MULTIPLIER);
    }

    @Override
    public void setPercentOutput(double percent) {
        trevor.set(ControlMode.PercentOutput, percent);
    }

    @Override
    public double getSpeed() {
        return trevor.getSelectedSensorVelocity() * Constants.Drive.INPUT_MULTIPLIER;
    }

    @Override
    public double getOutputPercent() {
        return trevor.getMotorOutputPercent();
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

    @Override
    public double getDistance() {
        return this.trevor.getSelectedSensorPosition() * Constants.Drive.DISTANCE_MULTIPLIER;
    }

}