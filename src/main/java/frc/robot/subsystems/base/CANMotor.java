package frc.robot.subsystems.base;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

public class CANMotor implements Motor {

    private BaseMotorController motor;

    public CANMotor(BaseMotorController motor) {
        this.motor = motor;
        this.motor.setNeutralMode(NeutralMode.Brake);
    }

    @Override
    public void setSpeed(double speed) {
        motor.set(ControlMode.Velocity, speed);
    }

    @Override
    public Motor setInverted(boolean inverted) {
        motor.setInverted(inverted);
        return this;
    }

}