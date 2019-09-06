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

    public String permitNo = "00";
    public char [] permitNoChars = new char[] {'0', '0'};

    public String sRotate = "100.01";
    public char [] rotateChars = new char[] {'1', '0', '0', '.', '1', '0'};
    public String sRange = "100.01";
    public char [] rangeChars = new char[] {'1', '0', '0', '.', '1', '0'};

    public float rotate = 100.01f;
    public float range = 100.01f;

    /*
    private String modle = "% 1N 2N  0.00N  0.00N  0.00N  0.00N 0N#";
    */
    private String modle = "%AANBBNCCCCCCNDDDDDDN  0.00N  0.00NEEN#";
    public char[] modleChars = new char[modle.length()];
    public byte[] modleBytes = new byte[modle.length()];
    private char[] sourceTag = new char[]{'A', 'A'};
    private char[] targetTag = new char[]{'B', 'B'};
    private char[] permitTag = new char[]{'E', 'E'};
    private char[] rotateTag = new char[]{'C', 'C', 'C', 'C', 'C', 'C'};
    private char[] rangeTag = new char[]{'D', 'D', 'D', 'D', 'D', 'D'};

    private Replace sourceRepl = new Replace(sourceTag.length);
    private Replace targetRepl = new Replace(targetTag.length);
    private Replace rotateRepl = new Replace(rotateTag.length);
    private Replace rangeRepl = new Replace(rangeTag.length);
    private Replace permitRepl = new Replace(permitTag.length);

    public boolean isQuery = true;

    public RadioProto() {
        sourceRepl.setTemplate(sourceTag);
        targetRepl.setTemplate(targetTag);
        rotateRepl.setTemplate(rotateTag);
        permitRepl.setTemplate(permitTag);
        rangeRepl.setTemplate(rangeTag);
        modle.getChars(0, modle.length(), modleChars, 0);
    }

    public int parse(byte[] data) {

        //System.out.println(new String(data));
        if (data[0] != '%') return -1;

        // "%master#"
        if (data[1] == 'm') {
            return CMD_START_MASTER;
        }

        StringTool.stringModify(this.sourceNo, sourceNoChars, (char) data[1], (char) data[2]);
        StringTool.stringModify(this.targetNo, targetNoChars, (char) data[4], (char) data[5]);
        StringTool.stringModify(this.permitNo, permitNoChars, (char) data[36], (char) data[37]);
        StringTool.stringModify(this.sRotate, rotateChars, (char) data[7], (char) data[8], (char) data[9],
            (char) data[10], (char) data[11], (char) data[12]);
        StringTool.stringModify(this.sRange, rangeChars, (char) data[14], (char) data[15], (char) data[16],
            (char) data[17], (char) data[18], (char) data[19]);

        if (targetNoChars[0] == ' ' &&  targetNoChars[1] == '0') { // 回应报文
            System.out.printf("# reply: "); // + "->" +" %s ", sourceNo, targetNo);
            StringTool.showCharArray(sourceNoChars);
            System.out.printf(" -> ");
            StringTool.showCharArray(targetNoChars);
            System.out.printf(" slave: ");// + "%s" + " - rotate: %f, range: %f\n", sourceNo, rotate, range);
            StringTool.showCharArray(sourceNoChars);
            System.out.printf(" - rotate: ");
            StringTool.showCharArray(rotateChars);
            rotate = Float.parseFloat(new String(rotateChars)); // TODO 替换成自己计算值
            System.out.printf(" , range: ");
            StringTool.showCharArray(rangeChars);
            range = Float.parseFloat(new String(rangeChars)); // TODO 替换成自己计算值
            System.out.println("");
            isQuery = false; // 从机的回文
        } else {
            System.out.printf("# request: "); // + "->" +" %s ", sourceNo, targetNo);
            StringTool.showCharArray(sourceNoChars);
            System.out.print(" -> ");
            StringTool.showCharArray(targetNoChars);
            System.out.printf(" master: ");// + "%s" + " - rotate: %f, range: %f\n", sourceNo, rotate, range);
            StringTool.showCharArray(sourceNoChars);
            System.out.printf(" - rotate: ");
            StringTool.showCharArray(rotateChars);
            System.out.printf(" , range: ");
            StringTool.showCharArray(rangeChars);
            System.out.println("");

            this.masterNo = sourceNo;
            isQuery = true;
        }

        return 0;
    }

    // 打包报文 如果是主机 则 轮训 其他所有从机，如果是从机 则回应
    public byte[] packReply() {
        for (int i = 0; i < modleChars.length; i++) {
            modleBytes[i] = (byte)modleChars[i];
        }
        return modleBytes;
    }

    public byte[] startMaster() {
        return "%master#".getBytes();
    }

    public boolean startMasterCmd(String cmd) {
        return cmd.equals("master");
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

    public int getSourceNoInt() {
        int no = 0;

        for (int i = 0; i < this.sourceNoChars.length; i++) {
            if (this.sourceNoChars[i] == ' ') continue;
            no = no * 10 + (int) (this.sourceNoChars[i] - '0');
        }
        return no;
    }

    public void setSourceNo(int no) {
        //StringTool.stringModify(this.sourceNo, sourceNoChars, no);
        this.sourceRepl.setReplacement(no);
        StringTool.stringModify(modleChars, this.sourceRepl);
    }

    public String getTargetNo() {
        return targetNo;
    }

    public void setTargetNo(int no) {
        //StringTool.stringModify(this.targetNo, targetNoChars, no);
        this.targetRepl.setReplacement(no);
        StringTool.stringModify(modleChars, this.targetRepl);
    }

    public void setPermitNo(int no) {
        //StringTool.stringModify(this.targetNo, targetNoChars, no);
        this.permitRepl.setReplacement(no);
        StringTool.stringModify(modleChars, this.permitRepl);
    }

    public float getRotate() {
        return rotate;
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
        this.rotateRepl.setReplacement(rotate, 3, 2); // 回转小数点2位
        StringTool.stringModify(modleChars, this.rotateRepl);
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
        this.rangeRepl.setReplacement(range, 3, 2); // 幅度小数点两位
        StringTool.stringModify(modleChars, this.rangeRepl);
    }

    public boolean isQuery() {
        return isQuery;
    }

    public void setQuery(boolean query) {
        isQuery = query;
    }
}
