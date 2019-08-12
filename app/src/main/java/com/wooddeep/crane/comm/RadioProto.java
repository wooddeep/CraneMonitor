package com.wooddeep.crane.comm;

/**
 * Created by niuto on 2019/8/1.
 */

public class RadioProto {
    // 请求
    // % 1N 2N  0.88N 51.51N  0.00N  0.00N 0N#

    // % 1N 1N  3.75N 21.40N  0.00N  0.00N18N#

    //回应
    // % 2N 0N  0.88N 51.51N  0.00N  0.00N 0N#

    public final static int CMD_START_MASTER = 1;

    public String craneNo;
    public String masterNo;
    public String sourceNo;
    public String targetNo;
    public float rotate;
    public float range;

    public boolean isQuery = true;

    public RadioProto() {
    }

    public int parse(byte[] data) {
        //System.out.println(new String(data));
        if (data[0] != '%' /*|| data[data.length - 1] != '#'*/) return -1;
        String cmd = new String(data); // TODO 修改成其他方式

        if (cmd.equals("%master#")) {
            return CMD_START_MASTER;
        }

        cmd = cmd.replace("%", "").replace("#", "");
        String [] cells = cmd.split("N");
        if (cells.length < 7) return -2;
        this.sourceNo = cells[0];
        this.targetNo = cells[1];
        this.craneNo = this.sourceNo;

        this.rotate = Float.parseFloat(cells[2]);
        this.range  = Float.parseFloat(cells[3]);

        if (targetNo.compareToIgnoreCase("0") == 0) { // 其他其他的回应报文
            //System.out.printf("# reply: %s -> %s ", sourceNo, targetNo);
            //System.out.printf("slave: %s - rotate: %f, range: %f\n", sourceNo, rotate, range);
            isQuery = false; // 从机的回文
        } else {
            //System.out.printf("# request: %s -> %s ", sourceNo, targetNo);
            //System.out.printf("master: %s - rotate: %f, range: %f\n", sourceNo, rotate, range);
            this.masterNo = sourceNo;
            isQuery = true;
        }

        return 0;
    }

    // 打包报文 如果是主机 则 轮训 其他所有从机，如果是从机 则回应
    public byte [] packReply() {
        // % 1N 1N  3.75N 21.39N  0.00N  0.00N 5N#
        // % 2N 0N  0.88N 51.51N  0.00N  0.00N 0N#
        //String replay = String.format("%%%02sN%02sN%.2fN%.2fN0.00N0.00N 0N#", sourceNo,  targetNo, rotate, range);
        //System.out.println(replay);
        String replay = "% 2N 0N  0.88N 51.51N  0.00N  0.00N 0N#";
        return replay.getBytes();
    }

    public byte [] startMaster() {
        return "%master#".getBytes();
    }

    public boolean startMasterCmd(String cmd) {
        return cmd.equals("master");
    }

    public static void test() {
        String query = "% 1N 20N 0.88N 51.51N 0.00N 0.00N 0N#";
        byte [] data = query.getBytes();
        RadioProto radioProto = new RadioProto();

        radioProto.parse(data);


        radioProto.sourceNo = "1N";
        radioProto.targetNo = "0N";
        radioProto.rotate = 1.234f;
        radioProto.range = 2.345f;

        radioProto.packReply();
    }

    public String getCraneNo() {
        return craneNo;
    }

    public void setCraneNo(String craneNo) {
        this.craneNo = craneNo;
    }

    public String getMasterNo() {
        return masterNo;
    }

    public void setMasterNo(String masterNo) {
        this.masterNo = masterNo;
    }

    public String getSourceNo() {
        return sourceNo;
    }

    public void setSourceNo(String sourceNo) {
        this.sourceNo = sourceNo;
    }

    public String getTargetNo() {
        return targetNo;
    }

    public void setTargetNo(String targetNo) {
        this.targetNo = targetNo;
    }

    public float getRotate() {
        return rotate;
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public boolean isQuery() {
        return isQuery;
    }

    public void setQuery(boolean query) {
        isQuery = query;
    }
}
