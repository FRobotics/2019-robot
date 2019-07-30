package frc.robot.subsystems;

public class ClimbSystem {
    private Solenoid4150 frontSolenoid;
    private Solenoid4150 backSolenoid;

    public void init(Solenoid4150 frontSolenoid, Solenoid4150 backSolenoid) {
        this.frontSolenoid = frontSolenoid;
        this.backSolenoid = backSolenoid;
    }

    public void raiseFront() {
        frontSolenoid.set(true);
    }

    public void lowerFront() {
        frontSolenoid.set(false);
    }

    public void raiseBack() {
        backSolenoid.set(true);
    }

    public void lowerBack() {
        backSolenoid.set(false);
    }

    public void reset() {
        this.lowerFront();
        this.lowerBack();
    }
}