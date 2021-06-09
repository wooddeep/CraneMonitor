package com.wooddeep.crane.worker

import android.widget.TextView
import com.wooddeep.crane.MainProp
import com.wooddeep.crane.R
import com.wooddeep.crane.alarm.Alarm
import com.wooddeep.crane.alarm.AlertSound
import com.wooddeep.crane.net.NetClient
import com.wooddeep.crane.tookit.CommTool
import java.util.*

object  TimerWorker {

    private fun recPowerOnOff(date: Date) {
        val realData = MainProp.realDataDao!!.queryLatestOne()
        try {
            var latest = Date()
            if (realData != null) {
                latest = MainProp.sdf.parse(realData!!.getTime())
            }

            val prevMsec = latest.time
            val currMesc = System.currentTimeMillis()

            if (currMesc - prevMsec > 60 * 1000) { // 大于1分钟, 则记录
                MainProp.switchRec.setTime(realData!!.getTime())
                MainProp.switchRec.setAction("power off")
                MainProp.switchRecDao!!.insert(MainProp.switchRec)
                MainProp.switchRec.setTime(MainProp.sdf.format(date))
                MainProp.switchRec.setAction("power on")
                MainProp.switchRecDao!!.insert(MainProp.switchRec)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setCurrTime() {
        val date = Date()
        val dateNowStr = MainProp.sdf.format(date)
        val currDate = MainProp.activity?.findViewById(R.id.currDate) as TextView
        MainProp.activity?.runOnUiThread { currDate.setText(dateNowStr.substring(0, dateNowStr.length - 3)) }
    }

    fun startTimerThread() {
        AlertSound.init(MainProp.context)
        Thread {
            var count = 0

            while (true && !MainProp.sysExit) {
                CommTool.sleep(100)
                count++

                if (MainProp.ttyS0InputStream == null) { // 兼容模拟器
                    MainProp.rotateEvent.data = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
                    MainProp.uartEvent.data = byteArrayOf(0xAA.toByte(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x55.toByte())
                }

                if (MainProp.netCalibFlag.get()) {
                    MainProp.gCenterX = MainProp.rotateEvent.centerX
                    MainProp.gCenterY = MainProp.rotateEvent.centerY
                    MainProp.protocol.setCalibData(
                            MainProp.rotateEvent.centerX,
                            MainProp.rotateEvent.centerX,
                            MainProp.rotateEvent.angle,
                            MainProp.uartEvent.craneType,
                            MainProp.uartEvent.bigArmLength,
                            MainProp.rotateEvent.data,
                            MainProp.uartEvent.data
                    )
                    MainProp.calibCraneType.set(MainProp.uartEvent.craneType)
                    val body = MainProp.protocol.getCalibData(MainProp.paraDao)
                    NetClient.mq.offer(body) // 发送传感器数据给服务器
                }

                // 实时数据
                if (NetClient.timeSlot == 0) NetClient.timeSlot = 5000
                if (count % (NetClient.timeSlot / 100) == 0 && NetClient.netOk && MainProp.centerCycle != null) {
                    //System.out.println(Math.round(currWeight * 10) / 10.0f);
                    MainProp.protocol.setRealData(
                            java.lang.Float.parseFloat(MainProp.angleView!!.getText().toString().split("°".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]), // 回转角度
                            MainProp.centerCycle!!.vAngle,
                            MainProp.mainCrane!!.getBigArmLength(),
                            MainProp.shadowLength,
                            Math.round(MainProp.currWeight * 10) / 10.0f, //  Math.round(ww * 10) / 10.0f;
                            java.lang.Float.parseFloat(MainProp.momentView!!.getText().toString().split("%".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]), // 吊钩高度
                            java.lang.Float.parseFloat(MainProp.windSpeedView!!.getText().toString().split("m".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]), // 风速
                            MainProp.mainCrane!!.getCraneHeight(),
                            java.lang.Float.parseFloat(MainProp.heightView!!.getText().toString().split("m".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]), // 吊钩高度
                            MainProp.iPower, // 倍率
                            MainProp.isMasterCrane,
                            if (MainProp.isRvcMode.get()) "rcv" else "freq",
                            if (MainProp.centerCycle!!.getType() == 0) "平臂" else "动臂"
                    )
                    val body = MainProp.protocol.getRealData(MainProp.paraDao)
                    NetClient.mq.offer(body)
                }

                //倍率，力矩，高度，幅度，额定重量，重量，回转，行走，仰角，风速，备注
                if (count % 50 == 0) { // 没5秒钟记录一次
                    val date = Date()
                    recPowerOnOff(date)
                    val dateNowStr = MainProp.sdf.format(date)
                    MainProp.realData.setTime(dateNowStr)
                    MainProp.realData.setRopenum(MainProp.iPower.toFloat()) // 倍率
                    MainProp.realData.setHeigth(java.lang.Float.parseFloat(MainProp.heightView!!.getText().toString().split("m".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]))
                    if (MainProp.centerCycle != null) {
                        if (MainProp.centerCycle!!.getType() == 1) {
                            MainProp.realData.setRange(MainProp.shadowLength)
                        } else {
                            MainProp.realData.setRange(MainProp.centerCycle!!.carRange)
                        }
                        MainProp.realData.setRotate(MainProp.centerCycle!!.getHAngle())
                        MainProp.realData.setDipange(MainProp.centerCycle!!.vAngle)
                    }
                    MainProp.realData.setMoment(java.lang.Float.parseFloat(MainProp.momentView!!.getText().toString().split("%".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]))
                    MainProp.realData.setRatedweight(java.lang.Float.parseFloat(MainProp.ratedWeightView!!.getText().toString().split("t".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]))
                    MainProp.realData.setWeight(java.lang.Float.parseFloat(MainProp.weightView!!.getText().toString().split("t".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]))
                    MainProp.realData.setWindspeed(java.lang.Float.parseFloat(MainProp.windSpeedView!!.getText().toString().split("m".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]))
                    MainProp.realDataDao?.insert(MainProp.realData)
                }

                if (count % 500 == 0) {
                    setCurrTime()
                }

                if (count % 20 == 0) {
                    AlertSound.open(MainProp.alarmLevel.get())
                }

                if (MainProp.ttyS0InputStream == null || MainProp.ttyS1InputStream == null || MainProp.ttyS2InputStream == null) {
                    continue
                }

                if (MainProp.craneMap.isEmpty()) {
                    continue
                }

                if (count % 6 == 0) { // 告警判断
                    try {
                        Alarm.alarmDetect(MainProp.calibration, MainProp.currProto.getRealHookHeight(), MainProp.shadowLength,
                                MainProp.elemList, MainProp.craneMap, MainProp.myCraneNo, MainProp.alarmSet, MainProp.eventBus) // 回转告警判断
                        Alarm.weightAlarmDetect(MainProp.calibration, MainProp.loadParas, MainProp.alarmSet, MainProp.eventBus,
                                MainProp.currProto.getRealWeight(), MainProp.shadowLength) // 吊重告警判断
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

        }.start()
    }
}