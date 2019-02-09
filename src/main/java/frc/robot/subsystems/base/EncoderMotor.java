package frc.robot.subsystems.base;

public interface EncoderMotor extends Motor {
    public double getSpeed();
    @Override
    EncoderMotor setInverted(boolean inverted);
    @Override
    EncoderMotor invert();
}