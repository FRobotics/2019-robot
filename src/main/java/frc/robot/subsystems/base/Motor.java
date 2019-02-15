package frc.robot.subsystems.base;

public interface Motor {
    void setSpeed(double speed);
    void setVoltage(double percent);
    Motor setInverted(boolean inverted);
    Motor invert();
}