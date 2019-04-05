package frc.robot.subsystems.base;

public interface EncoderMotor extends Motor {
    public double getSpeed();
    public double getOutputPercent();
    public double getDistance();
    @Override
    EncoderMotor setInverted(boolean inverted);
    @Override
    EncoderMotor invert();
}