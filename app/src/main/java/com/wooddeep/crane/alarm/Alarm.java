package com.wooddeep.crane.alarm;

import android.app.Activity;
import android.widget.ImageView;

import com.wooddeep.crane.MainActivity;
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
import com.wooddeep.crane.persist.entity.TcParam;

import org.greenrobot.eventbus.EventBus;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Alarm {

    public static AlarmEvent alarmEvent = new AlarmEvent();
    //public static AlarmEvent weightalarmEvent = new AlarmEvent();

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

    public static int getAlarmLevel(float distance, AlarmSet alarmSet, int type) {
        int level = -1; // 无告警

        float alarmDisGear1 = alarmSet.t2tDistGear1;
        float alarmDisGear2 = alarmSet.t2tDistGear2;
        float alarmDisGear3 = alarmSet.t2tDistGear3;
        float alarmDisGear4 = alarmSet.t2tDistGear4;
        float alarmDisGear5 = alarmSet.t2tDistGear5;

        if (type == 1) { // crane to crane alarm distance
            alarmDisGear1 = alarmSet.t2cDistGear1;
            alarmDisGear2 = alarmSet.t2cDistGear2;
            alarmDisGear3 = alarmSet.t2cDistGear3;
            alarmDisGear4 = alarmSet.t2cDistGear4;
            alarmDisGear5 = alarmSet.t2cDistGear5;
        }

        if (distance <= alarmDisGear1) return 1; // 1挡为最小距离，告警亮红色
        if (distance <= alarmDisGear2) return 2;
        if (distance <= alarmDisGear3) return 3;

        if (!MainActivity.isRvcMode.get()) { // 非RVC模式下，进行4, 5档的判断
            if (distance <= alarmDisGear4) return 4;
            if (distance <= alarmDisGear5) return 5;
        }

        return level;
    }

    public static CenterCycle craneToCraneAlarm(ConcurrentHashMap<String, CycleElem> craneMap,
                                                String no, AlarmSet alarmSet) throws Exception {
        CenterCycle cc = (CenterCycle) craneMap.get(no);
        Geometry gcc = cc.getArmGeo(0f);
        float myHeight = cc.height; // 本塔高度

        //System.out.println("## myHeight: " + myHeight);

        Coordinate myCenter = new Coordinate(cc.x, cc.y);

        Set<String> idSet = craneMap.keySet();
        for (String id : idSet) {
            if (id.equals(no)) continue;
            BaseElem elem = craneMap.get(id);
            if (elem instanceof SideCycle) { // cycle
                SideCycle sc = (SideCycle) craneMap.get(id);
                if (!sc.online) continue; // 离线不判断告警
                // 小车和小车的距离判断
                Geometry cargcc = cc.getCarGeo(0, 0);
                Geometry cargsc = sc.getCarGeo(0, 0);
                double carToCarDis = cargcc.distance(cargsc);
                if (carToCarDis < alarmSet.getCarSpeedDownDist()) { // 小车减速距离

                    int level = carToCarDis < alarmSet.getCarStopDist() ? 1 : 2; // 1挡停车距离，2挡减速距离

                    Geometry gPredect1 = cc.getCarGeo(0.0f, 0.1f); // 向外运行
                    float distPred1 = (float) gPredect1.distance(cargsc);
                    Geometry gPredect2 = cc.getCarGeo(0.0f, -0.1f); // 向内运行
                    float distPred2 = (float) gPredect2.distance(cargsc);

                    if (distPred1 <= carToCarDis) { // 逆时针旋转 距离告警，则是 左转告警
                        //System.out.printf("### car forwardAlarm alarm!!!\n", id);
                        alarmEvent.forwardAlarm = true;
                        alarmEvent.hasAlarm = true;
                        alarmEvent.forwardAlarmLevel = level;
                    }

                    if (distPred2 <= carToCarDis) {
                        //System.out.printf("### car backendAlarm alarm!!!\n", id);
                        alarmEvent.backendAlarm = true;
                        alarmEvent.hasAlarm = true;
                        alarmEvent.backendAlarmLevel = level;
                    }
                }

                Coordinate sideCenter = new Coordinate(sc.x, sc.y);
                double cTocDist = myCenter.distance(sideCenter);
                if (cTocDist > (cc.r * Math.cos(Math.toRadians(cc.getMinVAngle()) - cc.getArchPara())
                    + sc.r * Math.cos(Math.toRadians(sc.getMinVAngle())) - sc.getArchPara())) { // 两塔基距离大于两塔基大臂之和, 则不进行判断
                    //System.out.println("## center to center distance big than arms sum!");
                    continue;
                }

                Geometry gsc = sc.getArmGeo(0f);
                float sideHeight = sc.height;
                //System.out.println("## myHeight: " + myHeight + "  sideHeight = " + sideHeight + "   ## sc.online = " + sc.online);

                if (Math.abs(myHeight - sideHeight) <= 1 && sc.online) { // 高度差相差1m, 当成等高, 查看当前圆心和对端 大臂端点的距离, 无前后告警, 只有左右告警
                    float distance = (float) gcc.distance(gsc); // 计算当前塔机 和 旁边 塔机 的距离
                    int alarmLevel = getAlarmLevel(distance, alarmSet, 0);
                    if (alarmLevel != -1) { // 有距离告警
                        Geometry leftPredect = cc.getArmGeo(0.1f); // 逆时针旋转
                        float leftDistPred = (float) leftPredect.distance(gsc);
                        Geometry rightPredect = cc.getArmGeo(-0.1f); // 逆时针旋转
                        float rightDistPred = (float) rightPredect.distance(gsc);

                        if (leftDistPred <= distance) {
                            //System.out.printf("### center turn left to [%s] alarm!!!\n", id);
                            alarmEvent.leftAlarm = true;
                            alarmEvent.hasAlarm = true;
                            if (alarmLevel < alarmEvent.leftAlarmLevel)
                                alarmEvent.leftAlarmLevel = alarmLevel;
                            if (alarmLevel < alarmEvent.hiPropAlmLevel)
                                alarmEvent.hiPropAlmLevel = alarmLevel;
                        }

                        if (rightDistPred <= distance) {
                            alarmEvent.rightAlarm = true;
                            alarmEvent.hasAlarm = true;
                            if (alarmLevel < alarmEvent.rightAlarmLevel)
                                alarmEvent.rightAlarmLevel = alarmLevel;
                            if (alarmLevel < alarmEvent.hiPropAlmLevel)
                                alarmEvent.hiPropAlmLevel = alarmLevel;
                        }
                    }
                } else if ((myHeight - sideHeight) > 1 && sc.online) { // 中心塔基比边缘塔基高, 计算中心塔基小车位置和 边缘塔基距离
                    Geometry carPos = cc.getCarGeo(0f, 0f);
                    float carToArmDis = (float) carPos.distance(gsc); // 本机小车 到 旁机 大臂的距离
                    //System.out.printf("### center car to side[%s] arm distance: %f \n", id, carToArmDis);
                    int alarmLevel = getAlarmLevel(carToArmDis, alarmSet, 0);
                    if (alarmLevel != -1) { // 小车到塔基的距离小于安全距离
                        Geometry gPredect1 = cc.getCarGeo(0.1f, 0f); // 逆时针旋转
                        float distPred1 = (float) gPredect1.distance(gsc);
                        Geometry gPredect2 = cc.getCarGeo(-0.1f, 0f); // 逆时针旋转
                        float distPred2 = (float) gPredect2.distance(gsc);
                        if (distPred1 <= carToArmDis) { // 逆时针旋转 距离告警，则是 左转告警
                            //System.out.printf("### center turn left to [%s] alarm!!!\n", id);
                            alarmEvent.leftAlarm = true;
                            alarmEvent.hasAlarm = true;
                            if (alarmLevel < alarmEvent.leftAlarmLevel)
                                alarmEvent.leftAlarmLevel = alarmLevel;
                            if (alarmLevel < alarmEvent.hiPropAlmLevel)
                                alarmEvent.hiPropAlmLevel = alarmLevel;
                        }

                        if (distPred2 <= carToArmDis) {
                            alarmEvent.rightAlarm = true;
                            alarmEvent.hasAlarm = true;
                            if (alarmLevel < alarmEvent.rightAlarmLevel)
                                alarmEvent.rightAlarmLevel = alarmLevel;
                            if (alarmLevel < alarmEvent.hiPropAlmLevel)
                                alarmEvent.hiPropAlmLevel = alarmLevel;
                        }
                    }

                    float carSpeedDownDist = alarmSet.getCarSpeedDownDist(); // 小车减速距离
                    //System.out.printf("### carToArmDis: %f -- carSpeedDownDist: %f \n", carToArmDis, carSpeedDownDist);
                    if (carToArmDis < carSpeedDownDist) {
                        int level = carToArmDis < alarmSet.getCarStopDist() ? 1 : 2; // 1挡停车距离，2挡减速距离
                        Geometry gPredect1 = cc.getCarGeo(0.0f, 0.1f); // 向外运行
                        float distPred1 = (float) gPredect1.distance(gsc);
                        Geometry gPredect2 = cc.getCarGeo(0.0f, -0.1f); // 向内运行
                        float distPred2 = (float) gPredect2.distance(gsc);

                        if (distPred1 <= carToArmDis) { // 逆时针旋转 距离告警，则是 左转告警
                            //System.out.printf("### car forwardAlarm alarm!!!\n", id);
                            alarmEvent.forwardAlarm = true;
                            alarmEvent.hasAlarm = true;
                            if (alarmLevel < alarmEvent.forwardAlarmLevel)
                                alarmEvent.forwardAlarmLevel = level;
                            if (alarmLevel < alarmEvent.hiPropAlmLevel)
                                alarmEvent.hiPropAlmLevel = alarmLevel;
                        }

                        if (distPred2 <= carToArmDis) {
                            //System.out.printf("### car backendAlarm alarm!!!\n", id);
                            alarmEvent.backendAlarm = true;
                            alarmEvent.hasAlarm = true;
                            if (alarmLevel < alarmEvent.backendAlarmLevel)
                                alarmEvent.backendAlarmLevel = level;
                            if (alarmLevel < alarmEvent.hiPropAlmLevel)
                                alarmEvent.hiPropAlmLevel = alarmLevel;
                        }
                    }

                } else { // 中心塔基低于旁边塔基, 以中心塔基的臂和 旁机的小车, 以及计算 大臂和 旁边塔机的中心点距离 计算距离

                    Geometry carPos = sc.getCarGeo(0f, 0f);
                    float armToCarDis = (float) gcc.distance(carPos); // 本机大臂 到 旁机 小车的距离
                    //System.out.printf("### center car to side[%s] car distance: %f \n", id, armToCarDis);
                    int alarmLevel = getAlarmLevel(armToCarDis, alarmSet, 0);
                    //System.out.println("## alarmLevel = " + alarmLevel);
                    if (alarmLevel != -1) {
                        Geometry gPredect1 = cc.getArmGeo(0.1f); // 逆时针旋转
                        float distPred1 = (float) gPredect1.distance(carPos);

                        Geometry gPredect2 = cc.getArmGeo(-0.1f); // 逆时针旋转
                        float distPred2 = (float) gPredect2.distance(carPos);

                        //System.out.printf("## distPred1 = %f, distPred2 = %f, armToCarDis = %f\n", distPred1, distPred2, armToCarDis);

                        if (distPred1 <= armToCarDis) { // 逆时针旋转 距离告警，则是 左转告警
                            //System.out.printf("### center turn left to [%s] alarm!!!\n", id);
                            alarmEvent.leftAlarm = true;
                            alarmEvent.hasAlarm = true;
                            if (alarmLevel < alarmEvent.leftAlarmLevel)
                                alarmEvent.leftAlarmLevel = alarmLevel;
                            if (alarmLevel < alarmEvent.hiPropAlmLevel)
                                alarmEvent.hiPropAlmLevel = alarmLevel;
                        }

                        if (distPred2 <= armToCarDis) {
                            alarmEvent.rightAlarm = true;
                            alarmEvent.hasAlarm = true;
                            if (alarmLevel < alarmEvent.rightAlarmLevel)
                                alarmEvent.rightAlarmLevel = alarmLevel;
                            if (alarmLevel < alarmEvent.hiPropAlmLevel)
                                alarmEvent.hiPropAlmLevel = alarmLevel;
                        }
                    }
                }
            }
        }

        return cc;
    }

    public static void craneToAreaAlarm(List<BaseElem> elems, CenterCycle cc, AlarmSet alarmSet) throws Exception {
        Geometry gcc = cc.getArmGeo(0f);
        for (BaseElem elem : elems) {
            SideArea sa = (SideArea) elem;
            Geometry sideGeo = sa.getGeometry();
            if (sideGeo == null) continue;
            if ((cc.height - sa.height) > 1) { // 本机高于区域， 判断小车和区域的距离
                Geometry carPos = cc.getCarGeo(0f, 0f);
                float carToAreaDis = (float) carPos.distance(sideGeo); // 本机小车 到 旁机 大臂的距离
                //System.out.printf("### center car to side[%s] arm distance: %f \n", "TODO", carToAreaDis);
                int alarmLevel = getAlarmLevel(carToAreaDis, alarmSet, 1);
                if (alarmLevel != -1) {
                    // 判断左告警 还是 右告警
                    Geometry gPredect1 = cc.getCarGeo(0.1f, 0f); // 小车旋转后的位置
                    float distPred1 = (float) gPredect1.distance(sideGeo);

                    Geometry gPredect2 = cc.getCarGeo(-0.1f, 0f); // 小车旋转后的位置
                    float distPred2 = (float) gPredect2.distance(sideGeo);

                    if (distPred1 <= carToAreaDis) { // 逆时针旋转 距离告警，则是 左转告警
                        //System.out.printf("### center turn left to [%s] alarm!!!\n", "TODO");
                        alarmEvent.leftAlarm = true;
                        alarmEvent.hasAlarm = true;
                        if (alarmLevel < alarmEvent.leftAlarmLevel)
                            alarmEvent.leftAlarmLevel = alarmLevel;
                        if (alarmLevel < alarmEvent.hiPropAlmLevel)
                            alarmEvent.hiPropAlmLevel = alarmLevel;
                    }

                    if (distPred2 <= carToAreaDis) {
                        alarmEvent.rightAlarm = true;
                        alarmEvent.hasAlarm = true;
                        if (alarmLevel < alarmEvent.rightAlarmLevel)
                            alarmEvent.rightAlarmLevel = alarmLevel;
                        if (alarmLevel < alarmEvent.hiPropAlmLevel)
                            alarmEvent.hiPropAlmLevel = alarmLevel;
                        System.out.printf("### center turn right to [%s] alarm!!!\n", "TODO");
                    }
                }

                float carSpeedDownDist = alarmSet.getCarSpeedDownDist(); // 小车减速距离
                if (carToAreaDis < carSpeedDownDist) {
                    int level = carToAreaDis < alarmSet.getCarStopDist() ? 1 : 2;
                    Geometry gPredect1 = cc.getCarGeo(0.0f, 0.1f); // 向外运行
                    float distPred1 = (float) gPredect1.distance(sideGeo);

                    Geometry gPredect2 = cc.getCarGeo(0.0f, -0.1f); // 向内运行
                    float distPred2 = (float) gPredect2.distance(sideGeo);

                    if (distPred1 <= carToAreaDis) { // 逆时针旋转 距离告警，则是 左转告警
                        //System.out.printf("### center turn left to [%s] alarm!!!\n", "TODO");
                        alarmEvent.forwardAlarm = true;
                        alarmEvent.hasAlarm = true;
                        if (alarmLevel < alarmEvent.forwardAlarmLevel)
                            alarmEvent.forwardAlarmLevel = level;
                        if (alarmLevel < alarmEvent.hiPropAlmLevel)
                            alarmEvent.hiPropAlmLevel = alarmLevel;
                    }

                    if (distPred2 <= carToAreaDis) {
                        alarmEvent.backendAlarm = true;
                        alarmEvent.hasAlarm = true;
                        if (alarmLevel < alarmEvent.backendAlarmLevel)
                            alarmEvent.backendAlarmLevel = level;
                        if (alarmLevel < alarmEvent.hiPropAlmLevel)
                            alarmEvent.hiPropAlmLevel = alarmLevel;
                        System.out.printf("### [1]center turn right to [%s] alarm!!!\n", "TODO");
                    }
                }

            } else { // 区域高于塔基

                //System.out.println("-----A-----");

                Geometry core = cc.getCenterGeo(); // 本塔圆心
                float ctoaDis = (float) core.distance(sideGeo); // 圆心到区域的距离
                if (ctoaDis > cc.r) continue; // 永远不会撞上

                float dis = (float) gcc.distance(sideGeo); // 本机大臂到区域的距离
                //System.out.println("## big arm to area distance: " + dis);
                int alarmLevel = getAlarmLevel(dis, alarmSet, 1);
                if (alarmLevel != -1) {
                    //System.out.println("-----B-----");
                    Geometry gPredect1 = cc.getArmGeo(0.1f); // 逆时针旋转
                    float distPred1 = (float) gPredect1.distance(sideGeo);
                    Geometry gPredect2 = cc.getArmGeo(-0.1f);
                    float distPred2 = (float) gPredect2.distance(sideGeo);

                    if (distPred1 <= dis) {
                        //System.out.printf("### center turn left to [%s] alarm!!!\n", "TODO");
                        alarmEvent.leftAlarm = true;
                        alarmEvent.hasAlarm = true;
                        if (alarmLevel < alarmEvent.leftAlarmLevel)
                            alarmEvent.leftAlarmLevel = alarmLevel;
                        if (alarmLevel < alarmEvent.hiPropAlmLevel)
                            alarmEvent.hiPropAlmLevel = alarmLevel;
                    }

                    if (distPred2 <= dis) {
                        alarmEvent.rightAlarm = true;
                        alarmEvent.hasAlarm = true;
                        if (alarmLevel < alarmEvent.rightAlarmLevel)
                            alarmEvent.rightAlarmLevel = alarmLevel;
                        if (alarmLevel < alarmEvent.hiPropAlmLevel)
                            alarmEvent.hiPropAlmLevel = alarmLevel;
                        //System.out.printf("### [2]center turn right to [%s] alarm!!!\n", "TODO");
                    }
                }

                ////////////////// 小车和区域的距离 //////////////////
                //System.out.println("-----C-----");
                Geometry carPos = cc.getCarGeo(0f, 0f);
                float carToAreaDis = (float) carPos.distance(sideGeo); // 本机小车 到 旁机 大臂的距离
                //System.out.printf("### center car to side[%s] arm distance: %f \n", "TODO", carToAreaDis);
                alarmLevel = getAlarmLevel(carToAreaDis, alarmSet, 1);
                if (alarmLevel != -1) {
                    //System.out.println("-----D-----");
                    // 判断左告警 还是 右告警
                    Geometry gPredect1 = cc.getCarGeo(0.1f, 0f); // 小车旋转后的位置
                    float distPred1 = (float) gPredect1.distance(sideGeo);

                    Geometry gPredect2 = cc.getCarGeo(-0.1f, 0f); // 小车旋转后的位置
                    float distPred2 = (float) gPredect2.distance(sideGeo);

                    if (distPred1 <= carToAreaDis) { // 逆时针旋转 距离告警，则是 左转告警
                        //System.out.printf("### center turn left to [%s] alarm!!!\n", "TODO");
                        alarmEvent.leftAlarm = true;
                        alarmEvent.hasAlarm = true;
                        if (alarmLevel < alarmEvent.leftAlarmLevel)
                            alarmEvent.leftAlarmLevel = alarmLevel;
                        if (alarmLevel < alarmEvent.hiPropAlmLevel)
                            alarmEvent.hiPropAlmLevel = alarmLevel;
                    }

                    if (distPred2 <= carToAreaDis) {
                        alarmEvent.rightAlarm = true;
                        alarmEvent.hasAlarm = true;
                        if (alarmLevel < alarmEvent.rightAlarmLevel)
                            alarmEvent.rightAlarmLevel = alarmLevel;
                        if (alarmLevel < alarmEvent.hiPropAlmLevel)
                            alarmEvent.hiPropAlmLevel = alarmLevel;
                        //System.out.printf("### center turn right to [%s] alarm!!!\n", "TODO");
                    }
                }

                float carSpeedDownDist = alarmSet.getCarSpeedDownDist(); // 小车减速距离
                if (carToAreaDis < carSpeedDownDist) {
                    //System.out.println("-----E-----");
                    int level = carToAreaDis < alarmSet.getCarStopDist() ? 1 : 2;
                    Geometry gPredect1 = cc.getCarGeo(0.0f, 0.1f); // 向外运行
                    float distPred1 = (float) gPredect1.distance(sideGeo);

                    Geometry gPredect2 = cc.getCarGeo(0.0f, -0.1f); // 向内运行
                    float distPred2 = (float) gPredect2.distance(sideGeo);

                    System.out.printf("### carToAreaDis = %f, distPred1[out] = %f, distPred2[back] = %f\n", carToAreaDis, distPred1, distPred2);

                    if (distPred1 <= carToAreaDis) { // 出告警
                        //System.out.printf("### center turn left to [%s] alarm!!!\n", "TODO");
                        alarmEvent.forwardAlarm = true;
                        alarmEvent.hasAlarm = true;
                        if (alarmLevel < alarmEvent.forwardAlarmLevel)
                            alarmEvent.forwardAlarmLevel = level;
                        if (alarmLevel < alarmEvent.hiPropAlmLevel)
                            alarmEvent.hiPropAlmLevel = alarmLevel;
                    }

                    if (distPred2 <= carToAreaDis) { // 回告警
                        alarmEvent.backendAlarm = true;
                        alarmEvent.hasAlarm = true;
                        if (alarmLevel < alarmEvent.backendAlarmLevel)
                            alarmEvent.backendAlarmLevel = level;
                        if (alarmLevel < alarmEvent.hiPropAlmLevel)
                            alarmEvent.hiPropAlmLevel = alarmLevel;
                        //System.out.printf("### [1]car back alarm !!!\n");
                    }
                }

            }
        }
    }

    // @height: 塔高
    public static void alarmDetect(Calibration calibration, float hookHeight, float length,
                                   List<BaseElem> elemList, ConcurrentHashMap<String, CycleElem>
                                       craneMap, String no, AlarmSet alarmSet, EventBus eventBus) throws Exception {

        CenterCycle cc = (CenterCycle) craneMap.get(no);
        if (cc == null) return;

        // 告警清零
        alarmEvent.hasAlarm = false;
        alarmEvent.hookMinHightAlarm = false;
        alarmEvent.hookMaxHightAlarm = false;
        alarmEvent.backendAlarm = false;
        alarmEvent.forwardAlarm = false;
        alarmEvent.rightAlarm = false;
        alarmEvent.leftAlarm = false;
        alarmEvent.rightAlarmLevel = 100;
        alarmEvent.leftAlarmLevel = 100;
        alarmEvent.forwardAlarmLevel = 100;
        alarmEvent.backendAlarmLevel = 100;

        Alarm.extremumDetect(hookHeight, length, alarmSet, eventBus); // 最大最小
        Alarm.craneToCraneAlarm(craneMap, no, alarmSet); // 塔基到塔基的距离
        Alarm.craneToAreaAlarm(elemList, cc, alarmSet);  // 塔基到区域的距离
    }

    private static void extremumDetect(float hookHigth, float carRange, AlarmSet alarmSet, EventBus eventBus) throws Exception {

        if (hookHigth >= alarmSet.getHookHeightMax()) { // 最大吊钩告警
            alarmEvent.hasAlarm = true;
            alarmEvent.hookMaxHightAlarm = true;
        }

        if (hookHigth <= alarmSet.getHookHeightMin()) { // 最小吊钩告警
            alarmEvent.hasAlarm = true;
            alarmEvent.hookMinHightAlarm = true;
        }

        if (carRange >= alarmSet.getArmLengthMax()) {  // 小车最大幅度告警
            alarmEvent.hasAlarm = true;
            alarmEvent.forwardAlarm = true;
            alarmEvent.forwardAlarmLevel = 1; // out
        }

        if (carRange <= alarmSet.getArmLengthMin()) { // 小车最小幅度告警
            alarmEvent.hasAlarm = true;
            alarmEvent.backendAlarm = true;
            alarmEvent.backendAlarmLevel = 1;
        }
    }

    public static void weightAlarmDetect(Calibration calibration, List<TcParam> loads, AlarmSet alarmSet,
                                         EventBus eventBus, float curWeight, float cc) {

        if (loads == null || loads.size() <= 0) return;

        float maxWeight = Float.parseFloat(loads.get(0).getWeight());

        alarmEvent.weightAlarm = false;
        alarmEvent.momentAlarm = false;
        alarmEvent.weightAlarmLevel = 100;
        alarmEvent.momentAlarmLevel = 100;
        alarmEvent.weightAlarmDispearLevel = 100;
        alarmEvent.momentAlarmDispearLevel = 100;
        float weight3 = maxWeight * alarmSet.getWeight3() / 100;
        float weight2 = maxWeight * alarmSet.getWeight2() / 100;
        float weight1 = maxWeight * alarmSet.getWeight1() / 100;

        if (curWeight >= weight3) {
            alarmEvent.weightAlarm = true;
            alarmEvent.weightAlarmLevel = 3;
        } else if (curWeight >= weight2) {
            alarmEvent.weightAlarm = true;
            alarmEvent.weightAlarmLevel = 2;
        } else if (curWeight >= weight1) {
            alarmEvent.weightAlarm = true;
            alarmEvent.weightAlarmLevel = 1;
        }

        if (curWeight <= (weight2 + weight3) / 2) {
            alarmEvent.weightAlarmDispearLevel = 3; // 3挡吊重告警消失
        } else if (curWeight <= (weight2 + weight1) / 2) {
            alarmEvent.weightAlarmDispearLevel = 2; // 2挡吊重告警消失
        } else if (curWeight < 0.9 * weight1) {
            alarmEvent.weightAlarmDispearLevel = 1; // 1挡吊重告警消失
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
                //System.out.printf("## %f - %f - %f : %f - %f - %f\n", sc, cc, ec, sw, ww, ew);
                //System.out.printf("## %f -- %f : ", curWeight, ww);
                float moment3 = ww * alarmSet.getMoment3() / 100;
                float moment2 = ww * alarmSet.getMoment2() / 100;
                float moment1 = ww * alarmSet.getMoment1() / 100;

                // curr: 8.900000, ms = 9.999999, m3: 11.999999, m2: 8.999999, m1: 5.000000
                //System.out.printf("curr: %f, ms = %s, m3: %f, m2: %f, m1: %f\n", curWeight, ww, moment3, moment2, moment1);

                if (curWeight >= moment1) {
                    alarmEvent.momentAlarm = true;
                    alarmEvent.momentAlarmLevel = 1;
                    //alarmEvent.forwardAlarm = true;
                    //alarmEvent.forwardAlarmLevel = 1; // 小车出告警
                }

                if (curWeight >= moment2) {
                    alarmEvent.momentAlarm = true;
                    alarmEvent.momentAlarmLevel = 2;
                }

                if (curWeight >= moment3) {
                    alarmEvent.momentAlarm = true;
                    alarmEvent.momentAlarmLevel = 3;
                }

                if (curWeight < 0.85 * moment3) {
                    alarmEvent.momentAlarmDispearLevel = 3; // 1挡吊重告警消失
                    //System.out.println("momentAlarmDispearLevel3");
                }

                if (curWeight <= 0.85 * moment2) {
                    alarmEvent.momentAlarmDispearLevel = 2; // 3挡吊重告警消失
                    //System.out.println("momentAlarmDispearLevel2");
                }

                if (curWeight <= 0.85 * moment1) {
                    alarmEvent.momentAlarmDispearLevel = 1; // 3挡吊重告警消失
                    //System.out.println("momentAlarmDispearLevel1");
                }
            }
        }

        eventBus.post(alarmEvent);
    }

    public static void rotateControl(AlarmEvent alarmEvent, ControlProto controlProto) {
        if (alarmEvent.leftAlarm || alarmEvent.rightAlarm) {

            if (alarmEvent.leftAlarm && alarmEvent.leftAlarmLevel == 1) {
                controlProto.setLeftRote(true);
                controlProto.setRotate2(true);
                controlProto.setRotate3(true);
                controlProto.setRotate4(true);
                controlProto.setRotate5(true);
            } else {
                controlProto.setLeftRote(false);
            }

            if (alarmEvent.rightAlarm && alarmEvent.rightAlarmLevel == 1) {
                controlProto.setRightRote(true);
                controlProto.setRotate2(true);
                controlProto.setRotate3(true);
                controlProto.setRotate4(true);
                controlProto.setRotate5(true);
            } else {
                controlProto.setRightRote(false);
            }

            int alarmLevel = Math.min(alarmEvent.leftAlarmLevel, alarmEvent.rightAlarmLevel);

            switch (alarmLevel) {
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
                default:
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

    /*
     *   1—左
     *   2—右
     *   3—左二
     *   4—右二
     *   5—左三
     *   6—右三
     *
     * */

    public static void rotateRvcControl(AlarmEvent alarmEvent, ControlProto controlProto) {
        controlProto.setLeftRote(false);  // 左 1 清除一档左告警
        controlProto.setRightRote(false); // 右 1 清除一档右告警
        controlProto.setRotate2(false); //  左 2 清除2挡告警
        controlProto.setRotate3(false); //  右 2 清除2挡告警
        controlProto.setRotate4(false); // 左 3
        controlProto.setRotate5(false); // 右 3

        if (alarmEvent.leftAlarm || alarmEvent.rightAlarm) {

            if (alarmEvent.leftAlarm && alarmEvent.leftAlarmLevel == 1) {
                controlProto.setLeftRote(true);
                controlProto.setRotate3(true);
                controlProto.setRotate5(true);
            }

            if (alarmEvent.leftAlarm && alarmEvent.leftAlarmLevel == 2) {
                controlProto.setLeftRote(false);
                controlProto.setRotate3(true);
                controlProto.setRotate5(true);
            }

            if (alarmEvent.leftAlarm && alarmEvent.leftAlarmLevel == 3) {
                controlProto.setLeftRote(false);
                controlProto.setRotate3(false);
                controlProto.setRotate5(true);
            }

            if (alarmEvent.rightAlarm && alarmEvent.rightAlarmLevel == 1) {
                controlProto.setRightRote(true);
                controlProto.setRotate2(true);
                controlProto.setRotate4(true);
            }

            if (alarmEvent.rightAlarm && alarmEvent.rightAlarmLevel == 2) {
                controlProto.setRightRote(false);
                controlProto.setRotate2(true);
                controlProto.setRotate4(true);
            }

            if (alarmEvent.rightAlarm && alarmEvent.rightAlarmLevel == 3) {
                controlProto.setRightRote(false);
                controlProto.setRotate2(false);
                controlProto.setRotate4(true);
            }
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
            //System.out.println("alarmEvent.momentAlarmLevel = " + alarmEvent.momentAlarmLevel);
            switch (alarmEvent.momentAlarmLevel) {
                case 3:
                    controlProto.setCarOut1(true);
                    controlProto.setCarOut2(true);
                    controlProto.setMoment3(true);
                    controlProto.setMoment2(true);
                    controlProto.setMoment1(true);
                    break;
                case 2:
                    controlProto.setMoment2(true);
                    controlProto.setMoment1(true);
                    break;
                case 1:
                    controlProto.setMoment1(true);
                    break;
            }
        }

        switch (alarmEvent.momentAlarmDispearLevel) {
            case 1:
                controlProto.setMoment1(false);
                controlProto.setMoment2(false);
                controlProto.setMoment3(false);
                controlProto.setCarOut1(false);
                controlProto.setCarOut2(false);
                break;
            case 2:
                controlProto.setMoment2(false);
                controlProto.setMoment3(false);
                controlProto.setCarOut1(false);
                controlProto.setCarOut2(false);
                break;
            case 3:
                controlProto.setCarOut1(false);
                controlProto.setCarOut2(false);
                controlProto.setMoment3(false);
                break;
        }
    }

    public static void carBackControl(AlarmEvent event, ControlProto controlProto) {
        if (alarmEvent.momentAlarm && alarmEvent.momentAlarmLevel == 3) {
            controlProto.setCarOut1(true);
            controlProto.setCarOut2(true);
        } else if (event.forwardAlarm) {
            switch (event.forwardAlarmLevel) {
                case 1:
                    controlProto.setCarOut1(true); // 停车距离
                    controlProto.setCarOut2(true); // 减速距离
                    break;
                case 2:
                    controlProto.setCarOut1(false); // 停车距离
                    controlProto.setCarOut2(true); // 减速距离
                    break;
            }
        } else {
            controlProto.setCarOut1(false);
            controlProto.setCarOut2(false);
        }

        if (event.backendAlarm) {
            switch (event.backendAlarmLevel) {
                case 1:
                    controlProto.setCarBack1(true);
                    controlProto.setCarBack2(true);
                    break;
                case 2:
                    controlProto.setCarBack1(false);
                    controlProto.setCarBack2(true);
                    break;
            }
        } else {
            controlProto.setCarBack1(false);
            controlProto.setCarBack2(false);
        }
    }


    private static byte[] prevControl = new byte[]{0, 0};

    public static boolean controlSet(AlarmEvent event, ControlProto controlProto) {
        if (MainActivity.isRvcMode.get()) {
            rotateRvcControl(event, controlProto);
        } else {
            rotateControl(event, controlProto);
        }
        weightControl(event, controlProto);
        momentControl(event, controlProto);
        carBackControl(event, controlProto);

        if (MainActivity.calibrationFlag.get()) {
            controlProto.control[4] = 0;
            controlProto.control[5] = 0;
        }

        if (controlProto.control[4] != prevControl[0] || controlProto.control[5] != prevControl[1]) {
            prevControl[0] = controlProto.control[4];
            prevControl[1] = controlProto.control[5];
            return true;
        }

        return false;

    }
}
