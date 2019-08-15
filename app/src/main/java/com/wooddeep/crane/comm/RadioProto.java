package com.wooddeep.crane.comm;

import com.wooddeep.crane.tookit.Replace;
import com.wooddeep.crane.tookit.StringTool;

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

    public String craneNo = "00";
    public char [] craneNoChars = new char[] {'0', '0'};
    public String masterNo = "00";
    public char [] maseterNoChars = new char[] {'0', '0'};
    public String sourceNo = "00";
    public char [] sourceNoChars = new char[] {'0', '0'};
    public String targetNo = "00";
    public char [] targetNoChars = new char[] {'0', '0'};

    public String sRotate = "100.01";
    public char [] rotateChars = new char[] {'1', '0', '0', '.', '1'};
    public String sRange = "100.01";
    public char [] rangeChars = new char[] {'1', '0', '0', '.', '1'};

    public float rotate = 100.01f;
    public float range = 100.01f;

    /*
    private String modle = "% 1N 2N  0.00N  0.00N  0.00N  0.00N 0N#";
    */
    private String modle = "%AANBBNCCCCCCNDDDDDDN  0.00N  0.00N 0N#";
    private char[] modleChars = new char[modle.length()];
    private char[] sourceTag = new char[]{'A', 'A'};
    private char[] targetTag = new char[]{'b', 'b'};
    private char[] rotateTag = new char[]{'C', 'C', 'C', 'C', 'C', 'C'};
    private char[] rangeTag = new char[]{'D', 'D', 'D', 'D', 'D', 'D'};

    private Replace sourceRepl = new Replace(sourceTag.length);
    private Replace targetRepl = new Replace(targetTag.length);
    private Replace rotateRepl = new Replace(rotateTag.length);
    private Replace rangeRepl = new Replace(rangeTag.length);

    public boolean isQuery = true;

    public RadioProto() {
        modle.getChars(0, modle.length(), modleChars, 0);
    }

    public int parse(byte[] data) {
        if (data[0] != '%') return -1;

        // "%master#"
        if (data[1] == 'm') {
            return CMD_START_MASTER;
        }

        StringTool.stringModify(this.sourceNo, craneNoChars, (char) data[1], (char) data[2]);
        StringTool.stringModify(this.targetNo, targetNoChars, (char) data[4], (char) data[5]);
        this.craneNo = this.sourceNo;

        StringTool.stringModify(this.sRotate, rotateChars, (char) data[7], (char) data[8], (char) data[9],
            (char) data[10], (char) data[11], (char) data[12]);

        StringTool.stringModify(this.sRange, rangeChars, (char) data[14], (char) data[15], (char) data[16],
            (char) data[17], (char) data[18], (char) data[19]);

        this.rotate = Float.parseFloat(this.sRotate);
        this.range = Float.parseFloat(this.sRange);

        if (targetNo.compareToIgnoreCase("0") == 0) { // 其他其他的回应报文
            System.out.printf("# reply: %s -> %s ", sourceNo, targetNo);
            System.out.printf("slave: %s - rotate: %f, range: %f\n", sourceNo, rotate, range);
            isQuery = false; // 从机的回文
        } else {
            System.out.printf("# request: %s -> %s ", sourceNo, targetNo);
            System.out.printf("master: %s - rotate: %f, range: %f\n", sourceNo, rotate, range);
            this.masterNo = sourceNo;
            isQuery = true;
        }

        return 0;
    }

    // 打包报文 如果是主机 则 轮训 其他所有从机，如果是从机 则回应
    public byte[] packReply() {
        // % 1N 1N  3.75N 21.39N  0.00N  0.00N 5N#
        // % 2N 0N  0.88N 51.51N  0.00N  0.00N 0N#
        //String replay = String.format("%%%02sN%02sN%.2fN%.2fN0.00N0.00N 0N#", sourceNo,  targetNo, rotate, range);
        //System.out.println(replay);
        //sourceRepl.setReplacement();

        String replay = "% 2N 0N  0.88N 51.51N  0.00N  0.00N 0N#";
        return replay.getBytes();
    }

    public byte[] startMaster() {
        return "%master#".getBytes();
    }

    public boolean startMasterCmd(String cmd) {
        return cmd.equals("master");
    }

    public static void test() {
        String query = "% 1N 20N 0.88N 51.51N 0.00N 0.00N 0N#";
        byte[] data = query.getBytes();
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

    public void setSourceNo(int no) {
        StringTool.stringModify(this.sourceNo, sourceNoChars, no);
        this.sourceRepl.setReplacement(this.sourceNo, sourceNoChars);

    }

    public String getTargetNo() {
        return targetNo;
    }

    public void setTargetNo(int no) {
        StringTool.stringModify(this.targetNo, targetNoChars, no);
        this.targetRepl.setReplacement(this.targetNo, targetNoChars);
    }

    public float getRotate() {
        return rotate;
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
        this.rotateRepl.setReplacement(rotate, 2); // 回转小数点2位
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
        this.rangeRepl.setReplacement(range, 2); // 幅度小数点两位
    }

    public boolean isQuery() {
        return isQuery;
    }

    public void setQuery(boolean query) {
        isQuery = query;
    }
}
