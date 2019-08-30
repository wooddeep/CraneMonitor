package com.wooddeep.crane.alarm;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.widget.ImageView;

import com.wooddeep.crane.comm.ControlProto;
import com.wooddeep.crane.ebus.AlarmEvent;
import com.wooddeep.crane.element.BaseElem;
import com.wooddeep.crane.element.CenterCycle;
import com.wooddeep.crane.element.CycleElem;
import com.wooddeep.crane.element.SideArea;
import com.wooddeep.crane.element.SideCycle;
import com.wooddeep.crane.persist.entity.AlarmSet;
import com.wooddeep.crane.persist.entity.Calibration;
import com.wooddeep.crane.persist.entity.Load;

import org.greenrobot.eventbus.EventBus;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Alarm {


    public static void startAlarm(Activity activity, int weightId, int picId) {
        ImageView left = (ImageView) activity.findViewById(weightId);

        //ObjectAnimator oa = ObjectAnimator.ofFloat(left, "scaleX", 0.98f, 1.02f);
        //oa.setDuration(500);
        //ObjectAnimator oa2 = ObjectAnimator.ofFloat(left, "scaleY", 0.98f, 1.02f);
        //oa2.setDuration(500);

        left.setImageDrawable(activity.getResources().getDrawable(picId));

        //oa.start();
        //oa2.start();

        //oa = ObjectAnimator.ofFloat(left, "scaleX", 1.02f, 0.98f);
        //oa.setDuration(500);
        //oa2 = ObjectAnimator.ofFloat(left, "scaleY", 1.02f, 0.98f);
        //oa2.setDuration(500);

        //oa.start();
        //oa2.start();
    }

    public static void stopAlarm(Activity activity, int weightId, int picId) {
        ImageView left = (ImageView) activity.findViewById(weightId);
        left.setImageDrawable(activity.getResources().getDrawable(picId));
    }

    public static AlarmEvent alarmEvent = new AlarmEvent();
    public static AlarmEvent weightalarmEvent = new AlarmEvent();

    public static CenterCycle craneToCraneAlarm(HashMap<String, CycleElem> craneMap, String no, float alarmDis, AlarmSet alarmSet) throws Exception {
        CenterCycle cc = (CenterCycle) craneMap.get(no);
        Geometry gcc = cc.getArmGeo(0f);
        float myHeight = cc.height; // 本塔高度
        Coordinate myCenter = new Coordinate(cc.x, cc.y);

        Set<String> idSet = craneMap.keySet();
        for (String id : idSet) {
            if (id.equals(no)) continue;
            BaseElem elem = craneMap.get(id);
            if (elem instanceof SideCycle) { // cycle
                SideCycle sc = (SideCycle) craneMap.get(id);

                Coordinate sideCenter = new Coordinate(sc.x, sc.y);
                double cTocDist = myCenter.distance(sideCenter);
                if (cTocDist > (cc.r + sc.r)) { // 两塔基距离大于两塔基大臂之和, 则不进行判断
                    //System.out.println("## center to center distance big than arms sum!");
                    continue;
                }

                Geometry gsc = sc.getArmGeo(0f);
                float sideHeight = sc.height;

                if (Math.abs(myHeight - sideHeight) <= 1) { // 高度差相差1m, 当成等高, 查看当前圆心和对端 大臂端点的距离, 无前后告警, 只有左右告警
                    float distance = (float) gcc.distance(gsc);
                    if (distance < alarmDis) {
                        if (cc.prevHangle < cc.hAngle) {
                            Geometry gPredect = cc.getArmGeo(0.1f); // 逆时针旋转
                            float distPred = (float) gPredect.distance(gsc);
                            if (distPred < distance) { // 逆时针旋转 距离告警，则是 左转告警
                                //System.out.printf("### center turn left to [%s] alarm!!!\n", id);
                                alarmEvent.leftAlarm = true;
                            }
                        }
                        if (cc.prevHangle > cc.hAngle) {
                            Geometry gPredect = cc.getArmGeo(-0.1f); // 逆时针旋转
                            float distPred = (float) gPredect.distance(gsc);
                            if (distPred < distance) { // 逆时针旋转 距离告警，则是 左转告警
                                alarmEvent.rightAlarm = true;
                                //System.out.printf("### center turn right to [%s] alarm!!!\n", id);
                            }
                        }
                    }

                } else if ((myHeight - sideHeight) > 1) { // 中心塔基比边缘塔基高, 计算中心塔基小车位置和 边缘塔基距离
                    Geometry carPos = cc.getCarGeo(0f, 0f);
                    float carToArmDis = (float) carPos.distance(gsc); // 本机小车 到 旁机 大臂的距离
                    System.out.printf("### center car to side[%s] arm distance: %f \n", id, carToArmDis);
                    if (carToArmDis < alarmDis) { // 小车到塔基的距离小于安全距离
                        if (cc.prevHangle < cc.hAngle) {
                            Geometry gPredect = cc.getCarGeo(0.1f, 0f); // 逆时针旋转
                            float distPred = (float) gPredect.distance(gsc);
                            if (distPred < carToArmDis) { // 逆时针旋转 距离告警，则是 左转告警
                                System.out.printf("### center turn left to [%s] alarm!!!\n", id);
                                alarmEvent.leftAlarm = true;
                            }
                        }
                        if (cc.prevHangle > cc.hAngle) {
                            Geometry gPredect = cc.getCarGeo(-0.1f, 0f); // 逆时针旋转
                            float distPred = (float) gPredect.distance(gsc);
                            if (distPred < carToArmDis) { // 逆时针旋转 距离告警，则是 左转告警
                                alarmEvent.rightAlarm = true;
                                System.out.printf("### center turn right to [%s] alarm!!!\n", id);
                            }
                        }
                    }

                    float carSpeedDownDist = alarmSet.getCarSpeedDownDist(); // 小车减速距离
                    if (carToArmDis < carSpeedDownDist) {
                        int level = carToArmDis < alarmSet.getCarStopDist() ? 1 : 2; // 1挡停车距离，2挡减速距离
                        if (cc.prevCarRange < cc.carRange) {
                            Geometry gPredect = cc.getCarGeo(0.0f, 0.1f); // 向外运行
                            float distPred = (float) gPredect.distance(gsc);
                            if (distPred < carToArmDis) { // 逆时针旋转 距离告警，则是 左转告警
                                System.out.printf("### center turn left to [%s] alarm!!!\n", id);
                                alarmEvent.forwardAlarm = true;
                                alarmEvent.forwardAlarmLevel = level;
                            }
                        }
                        if (cc.prevCarRange > cc.carRange) {
                            Geometry gPredect = cc.getCarGeo(0.0f, -0.1f); // 向外运行
                            float distPred = (float) gPredect.distance(gsc);
                            if (distPred < carToArmDis) { // 逆时针旋转 距离告警，则是 左转告警
                                alarmEvent.backendAlarm = true;
                                alarmEvent.backendAlarmLevel = level;
                                System.out.printf("### center turn right to [%s] alarm!!!\n", id);
                            }
                        }
                    }
                } else { // 中心塔基低于旁边塔基, 以中心塔基的臂和 旁机的小车 计算距离
                    Geometry carPos = sc.getCarGeo(0f, 0f);
                    float armToCarDis = (float) gcc.distance(carPos); // 本机大臂 到 旁机 小车的距离
                    //System.out.printf("### center car to side[%s] car distance: %f \n", id, armToCarDis);
                    if (armToCarDis < alarmDis) {
                        if (cc.prevHangle < cc.hAngle) {
                            Geometry gPredect = cc.getArmGeo(0.1f); // 逆时针旋转
                            float distPred = (float) gPredect.distance(carPos);
                            if (distPred < armToCarDis) { // 逆时针旋转 距离告警，则是 左转告警
                                //System.out.printf("### center turn left to [%s] alarm!!!\n", id);
                                alarmEvent.leftAlarm = true;
                            }
                        }
                        if (cc.prevHangle > cc.hAngle) {
                            Geometry gPredect = cc.getArmGeo(-0.1f); // 逆时针旋转
                            float distPred = (float) gPredect.distance(carPos);
                            if (distPred < armToCarDis) { // 逆时针旋转 距离告警，则是 左转告警
                                alarmEvent.rightAlarm = true;
                                //System.out.printf("### center turn right to [%s] alarm!!!\n", id);
                            }
                        }
                    }
                }
            }
        }

        return cc;
    }

    public static void craneToAreaAlarm(List<BaseElem> elems, CenterCycle cc, float alarmDis, AlarmSet alarmSet) throws Exception {
        Geometry gcc = cc.getArmGeo(0f);

        for (BaseElem elem : elems) {
            SideArea sa = (SideArea) elem;
            Geometry sideGeo = sa.getGeometry();
            if (sideGeo == null) continue;
            if ((cc.height - sa.height) > 1) { // 本机高于区域， 判断小车和区域的距离
                Geometry carPos = cc.getCarGeo(0f, 0f);
                float carToAreaDis = (float) carPos.distance(sideGeo); // 本机小车 到 旁机 大臂的距离
                System.out.printf("### center car to side[%s] arm distance: %f \n", "TODO", carToAreaDis);
                if (carToAreaDis < alarmDis) {
                    // 判断左告警 还是 右告警
                    if (cc.prevHangle < cc.hAngle) {
                        Geometry gPredect = cc.getCarGeo(0.1f, 0f); // 小车旋转后的位置
                        float distPred = (float) gPredect.distance(sideGeo);
                        if (distPred < carToAreaDis) { // 逆时针旋转 距离告警，则是 左转告警
                            System.out.printf("### center turn left to [%s] alarm!!!\n", "TODO");
                            alarmEvent.leftAlarm = true;
                        }
                    }

                    if (cc.prevHangle > cc.hAngle) {
                        Geometry gPredect = cc.getCarGeo(-0.1f, 0f); // 小车旋转后的位置
                        float distPred = (float) gPredect.distance(sideGeo);
                        if (distPred < carToAreaDis) { // 逆时针旋转 距离告警，则是 左转告警
                            alarmEvent.rightAlarm = true;
                            System.out.printf("### center turn right to [%s] alarm!!!\n", "TODO");
                        }
                    }
                }

                float carSpeedDownDist = alarmSet.getCarSpeedDownDist(); // 小车减速距离
                if (carToAreaDis < carSpeedDownDist) {
                    int level = carToAreaDis < alarmSet.getCarStopDist() ? 1 : 2;
                    if (cc.prevCarRange < cc.carRange) {
                        Geometry gPredect = cc.getCarGeo(0.0f, 0.1f); // 向外运行
                        float distPred = (float) gPredect.distance(sideGeo);
                        if (distPred < carToAreaDis) { // 逆时针旋转 距离告警，则是 左转告警
                            System.out.printf("### center turn left to [%s] alarm!!!\n", "TODO");
                            alarmEvent.forwardAlarm = true;
                            alarmEvent.forwardAlarmLevel = level;
                        }
                    }

                    if (cc.prevCarRange > cc.carRange) {
                        Geometry gPredect = cc.getCarGeo(0.0f, -0.1f); // 向外运行
                        float distPred = (float) gPredect.distance(sideGeo);
                        if (distPred < carToAreaDis) { // 逆时针旋转 距离告警，则是 左转告警
                            alarmEvent.backendAlarm = true;
                            alarmEvent.backendAlarmLevel = level;
                            System.out.printf("### [1]center turn right to [%s] alarm!!!\n", "TODO");
                        }
                    }
                }
            } else {
                Geometry core = cc.getCenterGeo(); // 本塔圆心
                float ctoaDis = (float) core.distance(sideGeo); // 圆心到区域的距离
                if (ctoaDis > cc.r) continue; // 永远不会撞上

                float dis = (float) gcc.distance(sideGeo); // 本机大臂到区域的距离
                //System.out.println("## big arm to area distance: " + dis);
                if (dis <= alarmDis) {
                    //System.out.println("##### distance alarm .....");
                    // 根据数据的变化方向判断
                    if (cc.prevHangle < cc.hAngle) {
                        Geometry gPredect = cc.getArmGeo(0.1f); // 逆时针旋转
                        float distPred = (float) gPredect.distance(sideGeo);
                        if (distPred < dis) {
                            //System.out.printf("### center turn left to [%s] alarm!!!\n", "TODO");
                            alarmEvent.leftAlarm = true;
                        }
                    }

                    if (cc.prevHangle > cc.hAngle) {
                        Geometry gPredect = cc.getArmGeo(-0.1f); // 顺时针旋转
                        float distPred = (float) gPredect.distance(sideGeo);
                        if (distPred < dis) {
                            alarmEvent.rightAlarm = true;
                            //System.out.printf("### [2]center turn right to [%s] alarm!!!\n", "TODO");
                        }
                    }
                }
            }
        }
    }

    public static void alarmDetect(Calibration calibration, List<BaseElem> elemList, HashMap<String, CycleElem>
        craneMap, String no, AlarmSet alarmSet, EventBus eventBus) throws Exception {

        CenterCycle cc = (CenterCycle) craneMap.get(no);
        if (cc == null) return;
        if (System.currentTimeMillis() - cc.prevMsec == 0) return;

        // 告警清零
        alarmEvent.backendAlarm = false;
        alarmEvent.forwardAlarm = false;
        alarmEvent.rightAlarm = false;
        alarmEvent.leftAlarm = false;

        // 角度 -> 幅度 -> 采样值
        double angleDelta = cc.hAngle - cc.prevHangle;
        double radiansDelta = Math.toRadians(angleDelta);
        double dataDelat = Math.abs(radiansDelta / calibration.getRotateRate());
        float rotateRate = (float) dataDelat * 1000 / (System.currentTimeMillis() - cc.prevMsec); // 回转幅度变化率
        //System.out.printf("%f  ---  %f --- %f\n", (float)dataDelat * 1000 , (float)(System.currentTimeMillis() - cc.prevMsec), rotateRate);
        float tTotAlarmDis = 10f; // 回转告警距离
        float tTocAlarmDis = 10f; // 回转告警距离

        if (rotateRate <= calibration.getGearRate1()) {
            tTotAlarmDis = alarmSet.getT2tDistGear1();
            tTocAlarmDis = alarmSet.getT2cDistGear1();
            alarmEvent.leftAlarmLevel = 1;
            alarmEvent.rightAlarmLevel = 1;
            //System.out.printf("@@@@@@@@ gear1: %f", calibration.getGearRate1());
        } else if (rotateRate <= calibration.getGearRate2()) {
            tTotAlarmDis = alarmSet.getT2tDistGear2();
            tTocAlarmDis = alarmSet.getT2cDistGear2();
            alarmEvent.leftAlarmLevel = 2;
            alarmEvent.rightAlarmLevel = 2;
            //System.out.printf("@@@@@@@@ gear2: %f", calibration.getGearRate2());
        } else if (rotateRate <= calibration.getGearRate3()) {
            tTotAlarmDis = alarmSet.getT2tDistGear3();
            tTocAlarmDis = alarmSet.getT2cDistGear3();
            alarmEvent.leftAlarmLevel = 3;
            alarmEvent.rightAlarmLevel = 3;
            //System.out.printf("@@@@@@@@ gear3: %f", calibration.getGearRate3());
        } else if (rotateRate <= calibration.getGearRate4()) {
            tTotAlarmDis = alarmSet.getT2tDistGear4();
            tTocAlarmDis = alarmSet.getT2cDistGear4();
            alarmEvent.leftAlarmLevel = 4;
            alarmEvent.rightAlarmLevel = 4;
            //System.out.printf("@@@@@@@@ gear4: %f", calibration.getGearRate4());
        } else {
            tTotAlarmDis = alarmSet.getT2tDistGear5();
            tTocAlarmDis = alarmSet.getT2cDistGear5();
            alarmEvent.leftAlarmLevel = 5;
            alarmEvent.rightAlarmLevel = 5;
            //System.out.printf("@@@@@@@@ gear5: %f", calibration.getGearRate5());
        }

        Alarm.craneToCraneAlarm(craneMap, no, tTotAlarmDis, alarmSet); // 塔基到塔基的距离
        Alarm.craneToAreaAlarm(elemList, cc, tTocAlarmDis, alarmSet);  // 塔基到区域的距离

        eventBus.post(alarmEvent);

        cc.prevCarRange = cc.carRange; // 记录上一次小车位置
        cc.prevHangle = cc.hAngle; // 记录上一次告警回转
        cc.prevMsec = System.currentTimeMillis(); // 上次一计算时间
    }

    public static void weightAlarmDetect(Calibration calibration, List<Load> loads, AlarmSet alarmSet,
                                         EventBus eventBus, float curWeight, float cc) {

        float maxWeight = Float.parseFloat(loads.get(0).getWeight());

        weightalarmEvent.weightAlarm = false;
        weightalarmEvent.momentAlarm = false;
        weightalarmEvent.weightAlarmDispearLevel = 100;
        weightalarmEvent.momentAlarmDispearLevel = 100;
        float weight3 = maxWeight * alarmSet.getWeight3() / 100;
        float weight2 = maxWeight * alarmSet.getWeight2() / 100;
        float weight1 = maxWeight * alarmSet.getWeight1() / 100;

        if (curWeight >= weight3) {
            //System.out.printf("## %f -- %f : ", curWeight, maxWeight * alarmSet.getWeight1() / 100);
            System.out.println("@@@ weight overload 3");
            weightalarmEvent.weightAlarm = true;
            weightalarmEvent.weightAlarmLevel = 3;
        } else if (curWeight >= weight2) {
            //System.out.printf("## %f -- %f : ", curWeight, maxWeight * alarmSet.getWeight2() / 100);
            System.out.println("@@@ weight overload 2");
            weightalarmEvent.weightAlarm = true;
            weightalarmEvent.weightAlarmLevel = 2;
        } else if (curWeight >= weight1) {
            //System.out.printf("## %f -- %f : ", curWeight, maxWeight * alarmSet.getWeight3() / 100);
            System.out.println("@@@ weight overload 1");
            weightalarmEvent.weightAlarm = true;
            weightalarmEvent.weightAlarmLevel = 1;
        }

        if (curWeight <= (weight2 + weight3) / 2) {
            weightalarmEvent.weightAlarmDispearLevel = 3; // 3挡吊重告警消失
        } else if (curWeight <= (weight2 + weight1) / 2) {
            weightalarmEvent.weightAlarmDispearLevel = 2; // 2挡吊重告警消失
        } else if (curWeight < 0.9 * weight1) {
            weightalarmEvent.weightAlarmDispearLevel = 1; // 1挡吊重告警消失
        }

        for (int i = 0; i < loads.size() - 1; i++) {
            float sc = Float.parseFloat(loads.get(i).getCoordinate());
            float ec = Float.parseFloat(loads.get(i + 1).getCoordinate());
            if (sc <= cc && ec >= cc) { // 处于告警判断位置
                float sw = Float.parseFloat(loads.get(i).getWeight());
                float ew = Float.parseFloat(loads.get(i + 1).getWeight());
                float ww = 1; // 标准重量
                if (ec - cc == 0) {
                    ww = ew;
                } else {
                    float rate = (cc - sc) / (ec - cc);
                    ww = (rate * ew + sw) / (1 + rate);
                }

                //System.out.printf("## %f -- %f : ", curWeight, ww);
                float moment3 = ww * alarmSet.getMoment3() / 100;
                float moment2 = ww * alarmSet.getMoment2() / 100;
                float moment1 = ww * alarmSet.getMoment1() / 100;
                if (curWeight >= moment3) {
                    //System.out.printf("## %f -- %f : ", curWeight, maxWeight * alarmSet.getWeight1() / 100);
                    System.out.println("@@@ moment overload 3");
                    weightalarmEvent.momentAlarm = true;
                    weightalarmEvent.momentAlarmLevel = 3;
                } else if (curWeight >= moment2) {
                    //System.out.printf("## %f -- %f : ", curWeight, maxWeight * alarmSet.getWeight2() / 100);
                    System.out.println("@@@ moment overload 2");
                    weightalarmEvent.momentAlarm = true;
                    weightalarmEvent.momentAlarmLevel = 2;
                } else if (curWeight >= moment1) {
                    //System.out.printf("## %f -- %f : ", curWeight, maxWeight * alarmSet.getWeight3() / 100);
                    System.out.println("@@@ moment overload 1");
                    weightalarmEvent.momentAlarm = true;
                    weightalarmEvent.momentAlarmLevel = 1;
                }

                if (curWeight <= (moment2 + moment3) / 2) {
                    weightalarmEvent.momentAlarmDispearLevel = 3; // 3挡吊重告警消失
                    //System.out.println("momentAlarmDispearLevel3");
                } else if (curWeight <= (moment2 + moment1) / 2) {
                    weightalarmEvent.momentAlarmDispearLevel = 2; // 3挡吊重告警消失
                    //System.out.println("momentAlarmDispearLevel2");
                } else if (curWeight < 0.9 * moment1) {
                    weightalarmEvent.momentAlarmDispearLevel = 1; // 1挡吊重告警消失
                    //System.out.println("momentAlarmDispearLevel1");
                }
            }
        }

        eventBus.post(weightalarmEvent);
    }

    public static void rotateControl(AlarmEvent alarmEvent, ControlProto controlProto) {
        if (alarmEvent.leftAlarm || alarmEvent.rightAlarm) {

            if (alarmEvent.leftAlarm && alarmEvent.leftAlarmLevel == 1) {
                controlProto.setLeftRote(true);
            } else {
                controlProto.setLeftRote(false);
            }

            if (alarmEvent.leftAlarm && alarmEvent.rightAlarmLevel == 1) {
                controlProto.setRightRote(true);
            } else {
                controlProto.setRightRote(false);
            }

            switch (alarmEvent.leftAlarmLevel) {
                case 2:
                    controlProto.setLeftRote(false);  // 清除一档左告警
                    controlProto.setRightRote(false); // 清除一档右告警
                    controlProto.setRotate2(true);
                    controlProto.setRotate3(true);
                    controlProto.setRotate4(true);
                    controlProto.setRotate5(true);
                    break;
                case 3:
                    controlProto.setLeftRote(false);  // 清除一档左告警
                    controlProto.setRightRote(false); // 清除一档右告警
                    controlProto.setRotate2(false); // 清除2挡告警
                    controlProto.setRotate3(true);
                    controlProto.setRotate4(true);
                    controlProto.setRotate5(true);
                    break;
                case 4:
                    controlProto.setLeftRote(false);  // 清除一档左告警
                    controlProto.setRightRote(false); // 清除一档右告警
                    controlProto.setRotate2(false); // 清除2挡告警
                    controlProto.setRotate3(false); // 清除2挡告警
                    controlProto.setRotate4(true);
                    controlProto.setRotate5(true);
                    break;
                case 5:
                    controlProto.setLeftRote(false);  // 清除一档左告警
                    controlProto.setRightRote(false); // 清除一档右告警
                    controlProto.setRotate2(false); // 清除2挡告警
                    controlProto.setRotate3(false); // 清除2挡告警
                    controlProto.setRotate4(false);
                    controlProto.setRotate5(true);
                    break;
            }
        }

        if (!alarmEvent.leftAlarm && !alarmEvent.rightAlarm) {
            controlProto.setLeftRote(false);  // 清除一档左告警
            controlProto.setRightRote(false); // 清除一档右告警
            controlProto.setRotate2(false); // 清除2挡告警
            controlProto.setRotate3(false); // 清除2挡告警
            controlProto.setRotate4(false);
            controlProto.setRotate5(false);
        }

    }

    public static void weightControl(AlarmEvent alarmEvent, ControlProto controlProto) {
        if (alarmEvent.weightAlarm) {
            if (alarmEvent.weightAlarmLevel == 3) {
                controlProto.setWeight1(true);
            }
        }

        if (alarmEvent.weightAlarmDispearLevel <= 3) {
            controlProto.setWeight1(false);
        }
    }

    public static void momentControl(AlarmEvent alarmEvent, ControlProto controlProto) {
        if (alarmEvent.momentAlarm) {
            switch (alarmEvent.momentAlarmLevel) {
                case 3:
                    controlProto.setCarOut1(true);
                    controlProto.setCarOut2(true);
                    controlProto.setMoment3(true);
                case 2:
                    controlProto.setMoment2(true);
                case 1:
                    controlProto.setMoment1(true);
                    break;
            }
        }

        switch (alarmEvent.momentAlarmDispearLevel) {
            case 1:
                controlProto.setMoment1(false);
            case 2:
                controlProto.setMoment2(false);
            case 3:
                controlProto.setCarOut1(false);
                controlProto.setCarOut2(false);
                controlProto.setMoment3(false);
                break;
        }
    }

    public static void carBackControl(AlarmEvent event, ControlProto controlProto) {
        if (event.forwardAlarm) {
            switch (event.forwardAlarmLevel) {
                case 1:
                    controlProto.setCarBack1(true); // 停车距离
                    controlProto.setCarBack2(true); // 减速距离
                    break;
                case 2:
                    controlProto.setCarBack1(false); // 停车距离
                    controlProto.setCarBack2(true); // 减速距离
                    break;
            }
        } else {
            controlProto.setCarBack1(false);
            controlProto.setCarBack2(false);
        }
    }

    public static void controlSet(AlarmEvent event, ControlProto controlProto) {
        rotateControl(event, controlProto);
        weightControl(event, controlProto);
        momentControl(event, controlProto);
        carBackControl(event, controlProto);
        for (int i = 0; i < controlProto.control.length; i++) {
            System.out.printf("%02x ", controlProto.control[i]);
        }
        System.out.println("");
    }
}
