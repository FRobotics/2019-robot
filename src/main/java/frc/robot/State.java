package frc.robot;

public class State {

    public enum SandstormMode {
        START, INIT, CONTROLLED;
    }

    public enum TeleopMode {
        START, INIT, CONTROLLED;
    }

    public enum TestMode {
        START, INIT, CONTROLLED, RESET;
    }

    public enum Drive {
        CONTROLLED, TURN;
    }

    public enum BallHatch {
        CONTROLLED, PUNCH_BALL, RETRACT_PUNCHER, RAISE_ARMS, LOWER_ARMS; // 1000 10 425 1000
    }

    public enum Elevator {
        CONTROLLED, STOP, GOTO;
    }
}