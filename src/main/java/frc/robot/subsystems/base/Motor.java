package frc.robot.subsystems.base;

public interface Motor {
    void setSpeed(double speed);
    Motor setInverted(boolean inverted);
    Motor invert();
}