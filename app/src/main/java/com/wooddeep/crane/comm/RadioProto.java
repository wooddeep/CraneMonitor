package com.wooddeep.crane.comm;

/**
 * Created by niuto on 2019/8/1.
 */

public class RadioProto {
    // 请求
    // % 1N 2N  0.88N 51.51N  0.00N  0.00N 0N#

    //回应
    // % 2N 0N  0.88N 51.51N  0.00N  0.00N 0N#

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
        if (data[0] != '%' || data[data.length - 1] != '#') return -1;
        String cmd = new String(data);
        String [] cells = cmd.split("\\s+");
        if (cells.length != 8) return -2;
        this.sourceNo = cells[1];
        this.targetNo = cells[2];
        this.craneNo = this.sourceNo;

        this.rotate = Float.parseFloat(cells[3].replace("N", ""));
        this.range  = Float.parseFloat(cells[4].replace("N", ""));

        if (targetNo.compareToIgnoreCase("0N") == 0) { // 其他其他的回应报文
            System.out.printf("# reply: %s -> %s ", sourceNo, targetNo);
            System.out.printf("slave: %s - rotate: %f, range: %f\n", sourceNo, rotate, range);
            isQuery = false; // 从机的回文
        } else {
            System.out.printf("# request: %s -> %s ", sourceNo, targetNo);
            System.out.printf("master: %s - rotate: %f, range: %f\n", sourceNo, rotate, range);
            this.masterNo = sourceNo;
        }

        return 0;
    }

    // 打包报文 如果是主机 则 轮训 其他所有从机，如果是从机 则回应
    public void packReply() {
        String replay = String.format("%% %s %s %fN %fN 0.00N 0.00N 0N#", sourceNo,  targetNo, rotate, range);

        //StringBuffer sb = new StringBuffer();
        //sb.append("% ");
        //sb.append(Str)

        System.out.println(replay);
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
}
