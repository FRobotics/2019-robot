package frc.robot.subsystems;


public class HatchSystem {

    private Solenoid4150 solenoid;

    public void init(Solenoid4150 solenoid) {
        this.solenoid = solenoid;
    }

    public void comeTogether() {
        this.solenoid.set(true);
    }

    public void pushOutward() {
        this.solenoid.set(false);
    }

    public void reset() {
        this.pushOutward();
    }

}