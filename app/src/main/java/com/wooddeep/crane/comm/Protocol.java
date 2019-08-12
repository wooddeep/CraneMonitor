package com.wooddeep.crane.comm;

public class Protocol {
    //    幅度   高度  重量   风速                          校验码？
    // AA 00 02 00 02 00 03 00 02 00 04 00 04 00 00 80 00 F8 57 55
    private byte start = (byte) 0xAA;

    private int amplitude = 0x0002 & 0xFFFF; // 幅度

    private int height = 0x0002 & 0xFFFF; // 高度

    private int weight = 0x0003 & 0xFFFF; // 幅度

    private int windSpeed = 0x0002 & 0xFFFF; // 幅度

    private byte[] reserved = new byte[10];

    private byte end = (byte) 0x55;

    public int getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(int amplitude) {
        this.amplitude = amplitude;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Protocol() {

    }

    public int parse(byte[] data) {
        if (data.length < 20) {
            return -1;
        }

        if (data[0] != this.start) {
            return -2;
        }

        if (data[19] != this.end) {
            return -3;
        }

        this.amplitude = (data[1] & 0xFF) << 8 | (data[2] & 0xFF);
        this.height = ((data[3] & 0xFF) << 8 | (data[4] & 0xFF));
        this.weight = ((data[5] & 0xFF) << 8 | (data[6] & 0xFF));
        this.windSpeed = ((data[7] & 0xFF) << 8 | (data[8] & 0xFF));

        return 0;
    }

    private byte[] out = new byte[20];

    public byte[] pack() {

        out[0] = start;

        out[1] = (byte) (((this.amplitude & 0xFF00) >> 8) & 0xFF);
        out[2] = (byte) (((this.amplitude & 0x00FF) >> 0) & 0xFF);

        out[3] = (byte) (((this.height & 0xFF00) >> 8) & 0xFF);
        out[4] = (byte) (((this.height & 0x00FF) >> 0) & 0xFF);

        out[5] = (byte) (((this.weight & 0xFF00) >> 8) & 0xFF);
        out[6] = (byte) (((this.weight & 0x00FF) >> 0) & 0xFF);

        out[7] = (byte) (((this.windSpeed & 0xFF00) >> 8) & 0xFF);
        out[8] = (byte) (((this.windSpeed & 0x00FF) >> 0) & 0xFF);

        out[19] = end;
        return out;
    }
}
