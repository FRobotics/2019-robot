package frc.robot.subsystems.base;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class CANTalonSRXMotor implements Motor {
    
    private TalonSRX motor;

    public CANTalonSRXMotor(int deviceNumber) {
        motor = new TalonSRX(deviceNumber);
    }

    @Override
    public void setSpeed(double speed) {
        motor.set(ControlMode.Velocity, speed);
    }

}