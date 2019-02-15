package frc.robot.state;

import java.util.ArrayList;
import java.util.Arrays;

import frc.util.CountdownTimer;

public class State<S extends StateBase> {

    private S state;
    private ArrayList<S> queue;
    private CountdownTimer timer;

    public State(S defaultState) {
        this.state = defaultState;
        this.setTimer(this.state.getTime());
    }

    public State(S[] defaultQueue) {
        this.setQueue(defaultQueue);
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
        this.startNewTimer();
    }

    public void setStates(S[] queue) {
        this.setQueue(queue);
        this.startNewTimer();
    }

    public void clearQueue() {
        this.queue.clear();
    }

    public void periodic() {
        if(this.isFinished()) {
            this.setState(this.queue.remove(0));
        }
    }

    private void startNewTimer() {
        this.timer = state.getTime() < 0 ? null : new CountdownTimer(state.getTime());
        this.startTimer();
    }

    private void setQueue(S[] queue) {
        this.state = queue[0];
        this.queue = new ArrayList<S>(Arrays.asList(queue));
        this.queue.remove(0);
    }

    private void setTimer(long time) {
        this.timer = time < 0 ? null : new CountdownTimer(time);
    }

    private void startTimer() {
        if (this.timer != null)
            this.timer.start();
    }

}