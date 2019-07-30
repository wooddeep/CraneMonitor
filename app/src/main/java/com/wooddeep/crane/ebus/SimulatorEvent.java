package com.wooddeep.crane.ebus;

public class SimulatorEvent {

    private boolean start;
    private boolean stop;
    private boolean running;

    private int stopValue;

    public SimulatorEvent() {
    }

    public SimulatorEvent(boolean start, boolean stop, boolean running, int sv) {
        this.start = start;
        this.stop = stop;
        this.running = running;
        this.stopValue = sv;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getStopValue() {
        return stopValue;
    }

    public void setStopValue(int stopValue) {
        this.stopValue = stopValue;
    }
}
