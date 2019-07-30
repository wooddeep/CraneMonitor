package com.wooddeep.crane.ebus;

import com.wooddeep.crane.persist.entity.Calibration;

/**
 * Created by niuto on 2019/7/30.
 */

public class CalibrationEvent {
    public Calibration calibration;
    public CalibrationEvent(Calibration calibration) {
        this.calibration = calibration;
    }
}
