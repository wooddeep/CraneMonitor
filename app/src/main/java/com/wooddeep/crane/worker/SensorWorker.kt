package com.wooddeep.crane.worker

import com.wooddeep.crane.MainProp
import com.wooddeep.crane.tookit.CommTool
import com.wooddeep.crane.tookit.MathTool
import java.io.IOException
import java.util.*

object SensorWorker {

    fun setCommValue() {
        MainProp.currProto.parse(MainProp.adRBuff)
        MainProp.currProto.calcRealLength(MainProp.calibration)
        MainProp.currProto.calcRealWeigth(MainProp.calibration)
        MainProp.currProto.calcRealVAngle(MainProp.calibration)
        MainProp.currProto.calcRealHookHeight(MainProp.calibration,
                MainProp.centerCycle!!.getType(), MainProp.centerCycle!!.getBigArmLen(),
                MainProp.currProto.getRealVAngle()) // 计算吊钩高度
    }

    fun weigthChangeShow(moment: Float, ratedWeight: Float) {
        MainProp.momentView?.setText("$moment%")
        MainProp.ratedWeightView?.setText(ratedWeight.toString() + "t")
    }

    fun setFlatCrane() {
        if (Math.abs(MainProp.currProto.getRealVAngle() - MainProp.prevProto.getRealVAngle()) > 0.05f) {
            MainProp.prevProto.setRealVAngle(MainProp.currProto.getRealVAngle())
            MainProp.activity!!.runOnUiThread {
                MainProp.craneView?.setArmAngle(MainProp.currProto.getRealVAngle())
                MainProp.centerCycle?.setCarRange(MainProp.centerCycle!!.getBigArmLen())
                MainProp.centerCycle?.setVAngle(MainProp.currProto.getRealVAngle())
                MainProp.vAngleView!!.setText("${MainProp.currProto.getRealVAngle()}°")
                val deltaHeight = MainProp.centerCycle!!.getBigArmLen() * Math.sin(Math.toRadians(MainProp.currProto.getRealVAngle().toDouble()))

                MainProp.centerCycle!!.setHeight(MainProp.centerCycle!!.getOrgHeight() + 0.toFloat())

                println("### " + MainProp.centerCycle!!.getOrgHeight() + "@@" + deltaHeight + "$$" + MainProp.centerCycle!!.height + "&&" + MainProp.currProto.getRealVAngle())

                var shadow = MainProp.centerCycle!!.getBigArmLen() * Math.cos(Math.toRadians(MainProp.currProto.getRealVAngle().toDouble())).toFloat() - MainProp.centerCycle!!.getArchPara()
                shadow = Math.round(shadow * 10) / 10.0f
                MainProp.shadowLength = shadow
                MainProp.lengthEvent.setLength(shadow)
                MainProp.eventBus.post(MainProp.lengthEvent)
                val moment = MathTool.momentCalc(MainProp.loadParas, MainProp.currProto.getRealWeight(), shadow)
                weigthChangeShow(moment.moment, moment.ratedWeight)
            }
        }
    }

    fun setUnFlatCrane() {
        if (Math.abs(MainProp.currProto.getRealLength() - MainProp.prevProto.getRealLength()) > 0.05f) {
            MainProp.lengthEvent.setLength(MainProp.currProto.getRealLength())
            MainProp.prevProto.setRealLength(MainProp.currProto.getRealLength())
            MainProp.shadowLength = MainProp.currProto.getRealLength()
            MainProp.eventBus.post(MainProp.lengthEvent)
            val moment = MathTool.momentCalc(MainProp.loadParas, MainProp.currProto.getRealWeight(), MainProp.currProto.getRealLength())
            MainProp.activity!!.runOnUiThread {
                weigthChangeShow(moment.moment, moment.ratedWeight)
                MainProp.vAngleView?.setText("0°")
            }
        }
    }

    private fun recPowerOnOff(date: Date) {
        val realData = MainProp.realDataDao?.queryLatestOne()
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
                MainProp.switchRecDao?.insert(MainProp.switchRec)
                MainProp.switchRec.setTime(MainProp.sdf.format(date))
                MainProp.switchRec.setAction("power on")
                MainProp.switchRecDao?.insert(MainProp.switchRec)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun momentShow(moment: Float) {
        MainProp.currMoment = moment
        if (MainProp.currMoment > 40.0f && MainProp.currMoment > MainProp.saveMoment) { // 力矩 > 40% 认为开始工作
            val date = Date()
            recPowerOnOff(date)
            val dateNowStr = MainProp.sdf.format(date)
            MainProp.workRec.setTime(dateNowStr)
            MainProp.workRec.setRopenum(MainProp.iPower.toFloat()) // 倍率
            MainProp.workRec.setHeigth(java.lang.Float.parseFloat(MainProp.heightView!!.getText().toString().split("m".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]))
            if (MainProp.centerCycle != null) {
                if (MainProp.centerCycle!!.getType() == 1) {
                    MainProp.workRec.setRange(MainProp.shadowLength)
                } else {
                    MainProp.workRec.setRange(MainProp.centerCycle!!.carRange)
                }

                MainProp.workRec.setRotate(MainProp.centerCycle!!.getHAngle())
                MainProp.workRec.setDipange(MainProp.centerCycle!!.vAngle)
            }
            MainProp.workRec.setRatedweight(java.lang.Float.parseFloat(MainProp.ratedWeightView!!.getText().toString().split("t".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]))
            MainProp.workRec.setWeight(MainProp.currWeight)
            MainProp.workRec.setMoment(MainProp.currMoment)
            MainProp.workRec.setWindspeed(java.lang.Float.parseFloat(MainProp.windSpeedView!!.getText().toString().split("m".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]))
        }

        if (MainProp.currMoment < 15f && MainProp.workRec.getMoment() > 40) { // 力矩 < 15% 任务工作结束
            MainProp.workRecDao?.insert(MainProp.workRec)
            MainProp.workRec.setMoment(0f) // 清除 记录 条件
        }

        MainProp.saveMoment = MainProp.currMoment
        MainProp.momentView?.setText("$moment%")
    }

    fun refreshShow() {
        if (Math.abs(MainProp.currProto.getRealHookHeight() - MainProp.prevProto.getRealHookHeight()) > 0.05f) { // 吊钩高度变化
            MainProp.heightEvent.setHeight(MainProp.currProto.getRealHookHeight())
            MainProp.prevProto.setRealHookHeight(MainProp.currProto.getRealHookHeight()) // 替换吊钩实际高度
            MainProp.eventBus.post(MainProp.heightEvent)
        }

        if (Math.abs(MainProp.currProto.getRealWeight() - MainProp.prevProto.getRealWeight()) > 0.05f) {
            MainProp.weightEvent.setWeight(MainProp.currProto.getRealWeight())
            MainProp.prevProto.setRealWeight(MainProp.currProto.getRealWeight())
            MainProp.eventBus.post(MainProp.weightEvent)

            if (MainProp.centerCycle!!.getType() == 1) {
                val moment = MathTool.momentCalc(MainProp.loadParas, MainProp.currProto.getRealWeight(), MainProp.shadowLength)
                MainProp.activity!!.runOnUiThread {
                    momentShow(moment.moment)
                }
            } else {
                val moment = MathTool.momentCalc(MainProp.loadParas, MainProp.currProto.getRealWeight(), MainProp.currProto.getRealLength())
                MainProp.activity!!.runOnUiThread {
                    momentShow(moment.moment)
                }
            }
        }

        if (Math.abs(MainProp.currProto.getWindSpeed() - MainProp.prevProto.getWindSpeed()) >= 17) { // 风速
            MainProp.prevProto.setWindSpeed(MainProp.currProto.getWindSpeed())
            MainProp.activity!!.runOnUiThread {
                var windSpeed = (MainProp.currProto.getWindSpeed() * 30 / 4096).toFloat()
                windSpeed = Math.round(windSpeed * 10) / 10.0f
                MainProp.windSpeedView?.setText("${windSpeed}m/s")

            }
        }
    }

    fun sendSensorEvent() {
        MainProp.uartEvent.craneType = MainProp.centerCycle!!.getType()
        MainProp.uartEvent.bigArmLength = MainProp.centerCycle!!.getBigArmLen()
        MainProp.uartEvent.setData(MainProp.adRBuff)
        if (MainProp.calibrationFlag.get()) {
            MainProp.eventBus.post(MainProp.uartEvent) // 发送通知标定模块数据
        }
    }

    fun rotateShow(angle: Float) {
        MainProp.centerCycle?.setHAngle(angle)
        MainProp.angleView?.setText("$angle°")
        MainProp.showData.setShowHAngle(angle)
    }

    fun setRotate() {
        // 编码器100毫秒一次读一次
        MainProp.ttyS2OutputStream?.write(MainProp.rotateCmd)
        if (MainProp.ttyS2InputStream!!.available() > 0) { // 回转数据
            for (i in 0..8) {
                MainProp.rotateRBuff[i] = MainProp.rotateXBuff[i]
            }

            if (MainProp.rotateRBuff[0].toInt() == 0x01 && MainProp.rotateRBuff[1].toInt() == 0x04 && MainProp.rotateRBuff[2].toInt() == 0x04) {
                MainProp.currRotateProto.parse(MainProp.rotateRBuff)
                MainProp.currRotateProto.calcAngle(MainProp.calibration)

                var xangle = MainProp.currRotateProto.getAngle()
                if (xangle < 0) {
                    xangle = 360 - Math.abs(xangle) % 360
                } else {
                    xangle = xangle % 360
                }

                MainProp.currXAngle = xangle.toFloat()

                if (Math.abs(MainProp.currRotateProto.getAngle() - MainProp.prevRotateProto.getAngle()) >= 0.1f || MainProp.calibrationFlag.get()) {
                    MainProp.rotateEvent.setCenterX(MainProp.mainCrane!!.getCoordX1())
                    MainProp.rotateEvent.setCenterY(MainProp.mainCrane!!.getCoordY1())
                    MainProp.rotateEvent.setAngle(MainProp.currRotateProto.getAngle().toFloat())
                    MainProp.rotateEvent.setData(MainProp.rotateRBuff)
                    MainProp.prevRotateProto.setAngle(MainProp.currRotateProto.getAngle())

                    if (MainProp.calibrationFlag.get()) {
                        MainProp.eventBus.post(MainProp.rotateEvent) // 发送给标定Activity
                    }

                    var angle = MainProp.currRotateProto.getAngle()
                    if (angle < 0) {
                        angle = 360 - Math.abs(angle) % 360
                    } else {
                        angle = angle % 360
                    }
                    angle = (Math.round(angle * 10) / 10.0f).toDouble()

                    val minAngle = angle.toFloat()
                    MainProp.activity!!.runOnUiThread {
                        rotateShow(minAngle)
                    }
                }
            }
        }
    }

    fun startSensorThread() {
        Thread {
            var counter = 0
            while (!MainProp.sysExit) {

                if (MainProp.centerCycle == null) {
                    CommTool.sleep(100)
                    continue
                }

                if (MainProp.ttyS0InputStream == null || MainProp.ttyS1InputStream == null || MainProp.ttyS2InputStream == null) {
                    CommTool.sleep(100)
                    continue
                }

                try {
                    if (MainProp.ttyS0InputStream?.available()!! > 0) { // AD数据
                        for (i in 0..19) {
                            MainProp.adRBuff[i] = MainProp.adXBuff[i]
                        }

                        setCommValue()

                        if (MainProp.centerCycle!!.getType() == 1) { // 动臂式
                            setFlatCrane()
                        } else { // 平臂式
                            setUnFlatCrane()
                        }

                        refreshShow()

                        sendSensorEvent()
                    }

                    setRotate()

                    CommTool.sleep(100)
                    counter++

                } catch (ioe: IOException) {
                    System.exit(0)
                } catch (e: Exception) {
                    e.printStackTrace()
                    continue
                }

            }

        }.start()
    }
}