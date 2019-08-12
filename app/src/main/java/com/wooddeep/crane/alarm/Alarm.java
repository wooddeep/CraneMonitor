package com.wooddeep.crane.alarm;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.widget.ImageView;

import com.wooddeep.crane.ebus.AlarmEvent;
import com.wooddeep.crane.element.BaseElem;
import com.wooddeep.crane.element.CenterCycle;
import com.wooddeep.crane.element.CycleElem;
import com.wooddeep.crane.element.SideArea;
import com.wooddeep.crane.element.SideCycle;
import com.wooddeep.crane.persist.entity.AlarmSet;
import com.wooddeep.crane.persist.entity.Calibration;

import org.greenrobot.eventbus.EventBus;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Alarm {

    public static void startAlarm(Activity activity, int weightId, int picId) {
        ImageView left = (ImageView) activity.findViewById(weightId);

        ObjectAnimator oa = ObjectAnimator.ofFloat(left, "scaleX", 0.98f, 1.02f);
        oa.setDuration(500);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(left, "scaleY", 0.98f, 1.02f);
        oa2.setDuration(500);

        left.setImageDrawable(activity.getResources().getDrawable(picId));

        oa.start();
        oa2.start();

        oa = ObjectAnimator.ofFloat(left, "scaleX", 1.02f, 0.98f);
        oa.setDuration(500);
        oa2 = ObjectAnimator.ofFloat(left, "scaleY", 1.02f, 0.98f);
        oa2.setDuration(500);

        oa.start();
        oa2.start();
    }

    public static void stopAlarm(Activity activity, int weightId, int picId) {
        ImageView left = (ImageView) activity.findViewById(weightId);
        left.setImageDrawable(activity.getResources().getDrawable(picId));
    }

    public static AlarmEvent alarmEvent = new AlarmEvent();


    public static CenterCycle craneToCraneAlarm(HashMap<String, CycleElem> craneMap, String no, float alarmDis) throws Exception {
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
                    System.out.println("## center to center distance big than arms sum!");
                    continue;
                }

                Geometry gsc = sc.getArmGeo(0f);
                float sideHeight = sc.height;

                if (Math.abs(myHeight - sideHeight) <= 1) { // 高度差相差1m, 当成等高, 查看当前圆心和对端 大臂端点的距离, 无前后告警, 只有左右告警
                    float distance = (float) gcc.distance(gsc);
                    if (distance < alarmDis) {
                        if (cc.prvHangle < cc.hAngle) {
                            Geometry gPredect = cc.getArmGeo(0.1f); // 逆时针旋转
                            float distPred = (float) gPredect.distance(gsc);
                            if (distPred < distance) { // 逆时针旋转 距离告警，则是 左转告警
                                System.out.printf("### center turn left to [%s] alarm!!!\n", id);
                                alarmEvent.leftAlarm = true;
                            }
                        }
                        if (cc.prvHangle > cc.hAngle) {
                            Geometry gPredect = cc.getArmGeo(-0.1f); // 逆时针旋转
                            float distPred = (float) gPredect.distance(gsc);
                            if (distPred < distance) { // 逆时针旋转 距离告警，则是 左转告警
                                alarmEvent.rightAlarm = true;
                                System.out.printf("### center turn right to [%s] alarm!!!\n", id);
                            }
                        }
                    }

                } else if ((myHeight - sideHeight) > 1) { // 中心塔基比边缘塔基高, 计算中心塔基小车位置和 边缘塔基距离
                    Geometry carPos = cc.getCarGeo(0f, 0f);
                    float carToArmDis = (float) carPos.distance(gsc); // 本机小车 到 旁机 大臂的距离
                    //System.out.printf("### center car to side[%s] arm distance: %f \n", id, carToArmDis);
                    if (carToArmDis < alarmDis) {
                        if (cc.prvHangle < cc.hAngle) {
                            Geometry gPredect = cc.getCarGeo(0.1f, 0f); // 逆时针旋转
                            float distPred = (float) gPredect.distance(gsc);
                            if (distPred < carToArmDis) { // 逆时针旋转 距离告警，则是 左转告警
                                System.out.printf("### center turn left to [%s] alarm!!!\n", id);
                                alarmEvent.leftAlarm = true;
                            }
                        }
                        if (cc.prvHangle > cc.hAngle) {
                            Geometry gPredect = cc.getCarGeo(-0.1f, 0f); // 逆时针旋转
                            float distPred = (float) gPredect.distance(gsc);
                            if (distPred < carToArmDis) { // 逆时针旋转 距离告警，则是 左转告警
                                alarmEvent.rightAlarm = true;
                                System.out.printf("### center turn right to [%s] alarm!!!\n", id);
                            }
                        }

                        if (cc.prvCarRange < cc.carRange) {
                            Geometry gPredect = cc.getCarGeo(0.0f, 0.1f); // 向外运行
                            float distPred = (float) gPredect.distance(gsc);
                            if (distPred < carToArmDis) { // 逆时针旋转 距离告警，则是 左转告警
                                System.out.printf("### center turn left to [%s] alarm!!!\n", id);
                                alarmEvent.forwardAlarm = true;
                            }
                        }
                        if (cc.prvCarRange > cc.carRange) {
                            Geometry gPredect = cc.getCarGeo(0.0f, -0.1f); // 向外运行
                            float distPred = (float) gPredect.distance(gsc);
                            if (distPred < carToArmDis) { // 逆时针旋转 距离告警，则是 左转告警
                                alarmEvent.backendAlarm = true;
                                System.out.printf("### center turn right to [%s] alarm!!!\n", id);
                            }
                        }
                    }

                } else { // 中心塔基低于旁边塔基, 以中心塔基的臂和 旁机的小车 计算距离
                    Geometry carPos = sc.getCarGeo(0f, 0f);
                    float armToCarDis = (float) gcc.distance(carPos); // 本机大臂 到 旁机 小车的距离
                    //System.out.printf("### center car to side[%s] car distance: %f \n", id, armToCarDis);
                    if (armToCarDis < alarmDis) {
                        if (cc.prvHangle < cc.hAngle) {
                            Geometry gPredect = cc.getArmGeo(0.1f); // 逆时针旋转
                            float distPred = (float) gPredect.distance(carPos);
                            if (distPred < armToCarDis) { // 逆时针旋转 距离告警，则是 左转告警
                                System.out.printf("### center turn left to [%s] alarm!!!\n", id);
                                alarmEvent.leftAlarm = true;
                            }
                        }
                        if (cc.prvHangle > cc.hAngle) {
                            Geometry gPredect = cc.getArmGeo(-0.1f); // 逆时针旋转
                            float distPred = (float) gPredect.distance(carPos);
                            if (distPred < armToCarDis) { // 逆时针旋转 距离告警，则是 左转告警
                                alarmEvent.rightAlarm = true;
                                System.out.printf("### center turn right to [%s] alarm!!!\n", id);
                            }
                        }
                    }
                }
            }
        }

        return cc;
    }

    public static void craneToAreaAlarm(List<BaseElem> elems, CenterCycle cc, float alarmDis) throws Exception {
        Geometry gcc = cc.getArmGeo(0f);

        for (BaseElem elem : elems) {
            SideArea sa = (SideArea) elem;
            Geometry sideGeo = sa.getGeometry();

            if ((cc.height - sa.height) > 1) { // 本机高于区域， 判断小车和区域的距离
                Geometry carPos = cc.getCarGeo(0f, 0f);
                float carToAreaDis = (float) carPos.distance(sideGeo); // 本机小车 到 旁机 大臂的距离
                //System.out.printf("### center car to side[%s] arm distance: %f \n", "TODO", carToAreaDis);
                if (carToAreaDis < alarmDis) {
                    // 判断左告警 还是 右告警
                    if (cc.prvHangle < cc.hAngle) {
                        Geometry gPredect = cc.getCarGeo(0.1f, 0f); // 小车旋转后的位置
                        float distPred = (float) gPredect.distance(sideGeo);
                        if (distPred < carToAreaDis) { // 逆时针旋转 距离告警，则是 左转告警
                            //System.out.printf("### center turn left to [%s] alarm!!!\n", "TODO");
                            alarmEvent.leftAlarm = true;
                        }
                    }

                    if (cc.prvHangle > cc.hAngle) {
                        Geometry gPredect = cc.getCarGeo(-0.1f, 0f); // 小车旋转后的位置
                        float distPred = (float) gPredect.distance(sideGeo);
                        if (distPred < carToAreaDis) { // 逆时针旋转 距离告警，则是 左转告警
                            alarmEvent.rightAlarm = true;
                            //System.out.printf("### center turn right to [%s] alarm!!!\n", "TODO");
                        }
                    }

                    if (cc.prvCarRange < cc.carRange) {
                        Geometry gPredect = cc.getCarGeo(0.0f, 0.1f); // 向外运行
                        float distPred = (float) gPredect.distance(sideGeo);
                        if (distPred < carToAreaDis) { // 逆时针旋转 距离告警，则是 左转告警
                            //System.out.printf("### center turn left to [%s] alarm!!!\n", "TODO");
                            alarmEvent.forwardAlarm = true;
                        }
                    }

                    if (cc.prvCarRange > cc.carRange) {
                        Geometry gPredect = cc.getCarGeo(0.0f, -0.1f); // 向外运行
                        float distPred = (float) gPredect.distance(sideGeo);
                        if (distPred < carToAreaDis) { // 逆时针旋转 距离告警，则是 左转告警
                            alarmEvent.backendAlarm = true;
                            //System.out.printf("### [1]center turn right to [%s] alarm!!!\n", "TODO");
                        }
                    }
                }

            } else {
                Geometry core = cc.getCenterGeo(); // 本塔圆心
                float ctoaDis = (float) core.distance(sideGeo); // 圆心到区域的距离
                if (ctoaDis > cc.r) continue; // 永远不会撞上

                float dis = (float) gcc.distance(sideGeo); // 本机大臂到区域的距离
                if (dis <= alarmDis) {
                    //System.out.println("##### distance alarm .....");
                    // 根据数据的变化方向判断
                    if (cc.prvHangle < cc.hAngle) {
                        Geometry gPredect = cc.getArmGeo(0.1f); // 逆时针旋转
                        float distPred = (float) gPredect.distance(sideGeo);
                        if (distPred < dis) {
                            //System.out.printf("### center turn left to [%s] alarm!!!\n", "TODO");
                            alarmEvent.leftAlarm = true;
                        }
                    }

                    if (cc.prvHangle > cc.hAngle) {
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

        // 告警清零
        alarmEvent.backendAlarm = false;
        alarmEvent.forwardAlarm = false;
        alarmEvent.rightAlarm = false;
        alarmEvent.leftAlarm = false;

        // 角度 -> 幅度 -> 采样值
        double angleDelta = cc.hAngle - cc.prvHangle;
        double radiansDelta = Math.toRadians(angleDelta);
        double dataDelat = Math.abs(radiansDelta / calibration.getRotateRate());
        //System.out.printf("%f  ---  %f\n", (float)dataDelat * 1000 , (float)(System.currentTimeMillis() - cc.prvMsec));
        float rotateRate = (float) dataDelat * 1000 / (System.currentTimeMillis() - cc.prvMsec); // 回转幅度变化率
        float tTotAlarmDis = 10f;
        float tTocAlarmDis = 10f;
        if (rotateRate <= calibration.getGearRate1()) {
            tTotAlarmDis = alarmSet.getT2tDistGear1();
            tTocAlarmDis = alarmSet.getT2cDistGear1();
            //System.out.println("@@@@@@@@ gear1");
        } else if (rotateRate <= calibration.getGearRate2()) {
            tTotAlarmDis = alarmSet.getT2tDistGear2();
            tTocAlarmDis = alarmSet.getT2cDistGear2();
            //System.out.println("@@@@@@@@ gear2");
        } else if (rotateRate <= calibration.getGearRate3()) {
            tTotAlarmDis = alarmSet.getT2tDistGear3();
            tTocAlarmDis = alarmSet.getT2cDistGear3();
            //System.out.println("@@@@@@@@ gear3");
        } else if (rotateRate <= calibration.getGearRate4()) {
            tTotAlarmDis = alarmSet.getT2tDistGear4();
            tTocAlarmDis = alarmSet.getT2cDistGear4();
            //System.out.println("@@@@@@@@ gear4");
        } else {
            tTotAlarmDis = alarmSet.getT2tDistGear5();
            tTocAlarmDis = alarmSet.getT2cDistGear5();
            //System.out.println("@@@@@@@@ gear5");
        }

        Alarm.craneToCraneAlarm(craneMap, no, tTotAlarmDis);
        Alarm.craneToAreaAlarm(elemList, cc, tTocAlarmDis);

        eventBus.post(alarmEvent);

        cc.prvCarRange = cc.carRange; // 记录上一次小车位置
        cc.prvHangle = cc.hAngle; // 记录上一次回转
        cc.prvMsec = System.currentTimeMillis(); // 上次一计算时间
    }
}
