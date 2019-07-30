package com.wooddeep.crane.simulator;

/**
 * Created by niuto on 2019/7/30.
 */

public class SimulatorFlags {

    private  boolean start = true;

    private  boolean stop = false;

    private  boolean runing = false;

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
        if (start == true) {
            setStop(false);
            setRuning(false);
        }
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
        if (stop == true) {
            setStart(false);
            setRuning(false);
        }
    }

    public boolean isRuning() {
        return runing;
    }

    public void setRuning(boolean runing) {
        this.runing = runing;
        if (runing == true) {
            setStop(false);
            setStart(false);
        }
    }
}
