package frc.robot.subsystems.base;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

public class CANMotorPair implements Motor {

    private BaseMotorController master;
    private BaseMotorController follower;
    
    public CANMotorPair(BaseMotorController master, BaseMotorController follower) {
        this.master = master;
        this.follower = follower;

        master.setNeutralMode(NeutralMode.Brake);
        follower.setNeutralMode(NeutralMode.Brake);
        
        master.setSensorPhase(false);
        follower.setSensorPhase(false);

        master.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 30);
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