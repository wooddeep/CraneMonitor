package com.wooddeep.crane.comm;

/**
 * Created by niuto on 2019/8/30.
 */

@SuppressWarnings("unused")
public class ControlProto {

    public boolean carOut2 = false; // 小车出2
    public boolean carOut1 = false; // 小车出1
    public boolean rotate5 = false;
    public boolean rotate4 = false;
    public boolean rotate3 = false;
    public boolean rotate2 = false;
    public boolean leftRote = false;
    public boolean rightRote = false;

    public boolean reserved0 = false;
    public boolean reserved1 = false;

    public boolean moment3 = false;
    public boolean moment2 = false;
    public boolean moment1 = false;

    public boolean weight1 = false;

    public boolean carBack2 = false;  // 小车回2
    public boolean carBack1 = false;  // 小车回1

    public byte[] control = new byte[]{0x03, 0x07, 0x01, 0x01, (byte) 0xFF, (byte) 0xFF, 0x00};

    public boolean isCarOut2() {
        return carOut2;
    }

    public void setCarOut2(boolean carOut2) {
        this.carOut2 = carOut2;

        byte data = control[4];
        if (carOut2 == false) {
            data = (byte) (data & 0x7F);
        } else {
            data = (byte) (data | 0x80);
        }
        control[4] = data;
    }

    public boolean isCarOut1() {
        return carOut1;
    }

    public void setCarOut1(boolean carOut1) {
        this.carOut1 = carOut1;

        byte data = control[4];
        if (carOut1 == false) {
            data = (byte) (data & 0xBF);
        } else {
            data = (byte) (data | 0x40);
        }
        control[4] = data;

    }

    public boolean isRotate5() {
        return rotate5;
    }

    public void setRotate5(boolean rotate5) {
        this.rotate5 = rotate5;

        byte data = control[4];
        if (rotate5 == false) {
            data = (byte) (data & 0xDF);
        } else {
            data = (byte) (data | 0x20);
        }
        control[4] = data;

    }

    public boolean isRotate4() {
        return rotate4;
    }

    public void setRotate4(boolean rotate4) {
        this.rotate4 = rotate4;

        byte data = control[4];
        if (rotate4 == false) {
            data = (byte) (data & 0xEF);
        } else {
            data = (byte) (data & 0x10);
        }
        control[4] = data;
    }

    public boolean isRotate3() {
        return rotate3;
    }

    public void setRotate3(boolean rotate3) {
        this.rotate3 = rotate3;

        byte data = control[4];
        if (rotate3 == false) {
            data = (byte) (data & 0xF7);
        } else {
            data = (byte) (data | 0x08);
        }
        control[4] = data;
    }

    public boolean isRotate2() {
        return rotate2;
    }

    public void setRotate2(boolean rotate2) {
        this.rotate2 = rotate2;

        byte data = control[4];
        if (rotate2 == false) {
            data = (byte) (data & 0xFB);
        } else {
            data = (byte) (data | 0x04);
        }
        control[4] = data;
    }

    public boolean isLeftRote() {
        return leftRote;
    }

    public void setLeftRote(boolean leftRote) {
        this.leftRote = leftRote;

        byte data = control[4];
        if (leftRote == false) {
            data = (byte) (data & 0xFD);
        } else {
            data = (byte) (data | 0x02);
        }
        control[4] = data;
    }

    public boolean isRightRote() {
        return rightRote;
    }

    public void setRightRote(boolean rightRote) {
        this.rightRote = rightRote;

        byte data = control[4];
        if (rightRote == false) {
            data = (byte) (data & 0xFE);
        } else {
            data = (byte) (data | 0x01);
        }
        control[4] = data;
    }

    public boolean isReserved0() {
        return reserved0;
    }

    public void setReserved0(boolean reserved0) {
        this.reserved0 = reserved0;
    }

    public boolean isReserved1() {
        return reserved1;
    }

    public void setReserved1(boolean reserved1) {
        this.reserved1 = reserved1;
    }

    public boolean isMoment3() {
        return moment3;
    }

    public void setMoment3(boolean moment3) {
        this.moment3 = moment3;

        byte data = control[5];
        if (moment3 == false) {
            data = (byte) (data & 0xDF);
        } else {
            data = (byte) (data | 0x20);
        }
        control[5] = data;
    }

    public boolean isMoment2() {
        return moment2;
    }

    public void setMoment2(boolean moment2) {
        this.moment2 = moment2;

        byte data = control[5];
        if (moment2 == false) {
            data = (byte) (data & 0xEF);
        } else {
            data = (byte) (data | 0x10);
        }
        control[5] = data;
    }

    public boolean isMoment1() {
        return moment1;
    }

    public void setMoment1(boolean moment1) {
        this.moment1 = moment1;

        byte data = control[5];
        if (moment1 == false) {
            data = (byte) (data & 0xF7);
        } else {
            data = (byte) (data | 0x08);
        }
        control[5] = data;
    }

    public boolean isWeight1() {
        return weight1;
    }

    public void setWeight1(boolean weight1) {
        this.weight1 = weight1;

        byte data = control[5];
        if (weight1 == false) {
            data = (byte) (data & 0xFB);
        } else {
            data = (byte) (data | 0x04);
        }
        control[5] = data;
    }

    public boolean isCarBack2() {
        return carBack2;
    }

    public void setCarBack2(boolean carBack2) {
        this.carBack2 = carBack2;

        byte data = control[5];
        if (carBack2 == false) {
            data = (byte) (data & 0xFD);
        } else {
            data = (byte) (data | 0x02);
        }
        control[5] = data;
    }

    public boolean isCarBack1() {
        return carBack1;
    }

    public void setCarBack1(boolean carBack1) {
        this.carBack1 = carBack1;

        byte data = control[5];
        if (carBack1 == false) {
            data = (byte) (data & 0xFE);
        } else {
            data = (byte) (data | 0x01);
        }
        control[5] = data;
    }

    public void clear() {
        control[4] = 0;
        control[5] = 0;
    }

    public byte[] pacakge() {
        return control;
    }
}
