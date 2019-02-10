package frc.robot.state;

import frc.util.CountdownTimer;

public class State<S extends StateBase> {

    private S state;
    private CountdownTimer timer;

    public State(S defaultState) {
        this.state = defaultState;
        this.setTimer(this.state.getTime());
    }

    public void start() {
        this.startTimer();
    }

    public S getState() {
        return this.state;
    }

    public boolean isFinished() {
        return this.timer == null ? false : this.timer.isFinished();
    }

    public void setState(S state) {
        this.state = state;
        this.timer = state.getTime() < 0 ? null : new CountdownTimer(state.getTime());
        this.startTimer();
    }

    private void setTimer(long time) {
        this.timer = time < 0 ? null : new CountdownTimer(time);
    }

    private void startTimer() {
        if (this.timer != null)
            this.timer.start();
    }

}