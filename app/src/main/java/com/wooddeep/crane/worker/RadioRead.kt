package com.wooddeep.crane.worker

import android.graphics.Color
import com.example.x6.serialportlib.SerialPort
import com.wooddeep.crane.MainProp
import com.wooddeep.crane.comm.RadioProto
import com.wooddeep.crane.ebus.RadioEvent
import com.wooddeep.crane.main.SavedData
import com.wooddeep.crane.tookit.CommTool
import com.wooddeep.crane.tookit.MathTool

object RadioRead {

    fun RadioDateEventOps(event: RadioEvent) {
        val cmdRet = MainProp.radioProto.parse(event.getData()) // 解析电台数据

        //System.out.printf("## cmdRet = %d\n", cmdRet);
        if (cmdRet == -1) return

        val currTime = System.currentTimeMillis()// 当前时间

        if (MainProp.iAmMaster.get() && MainProp.radioProto.isQuery) return

        if (cmdRet == RadioProto.CMD_START_MASTER && MainProp.waitFlag == true) { // 启动主机命令
            MainProp.iAmMaster.set(true)
            MainProp.activity?.runOnUiThread { MainProp.masterNoView?.setText(MainProp.myCraneNo) }
            MainProp.waitFlag = false
            return
        }

        //if (radioProto.sourceNo.equals(radioProto.targetNo)) return; // TODO 再次验证

        if (MainProp.radioProto.sourceNo == MainProp.myCraneNo) return  // TODO 再次验证

        if (MainProp.radioProto.isQuery) { // 收到主机的查询命令，本机必然为从机
            MainProp.waitFlag = false
            val master = MainProp.craneMap.get(MainProp.radioProto.getSourceNo()) // 作为从机, 更新主机的信息 // TODO 根据塔基类型，计算仰角
            if (master != null) master!!.setOnline(true)

            val currShowMasterNo = MainProp.masterNoView!!.getText().toString()
            if (currShowMasterNo == null || currShowMasterNo != MainProp.radioProto.getSourceNo()) {
                MainProp.activity?.runOnUiThread { MainProp.masterNoView!!.setText(MainProp.radioProto.getSourceNo()) } // TODO 缓存
            }

            if (master != null) {
                MainProp.activity?.runOnUiThread { master!!.setColor(Color.rgb(46, 139, 87)) }

                var savedData: SavedData? = MainProp.savedDataMap.get(MainProp.radioProto.getSourceNo())
                if (savedData == null) {
                    savedData = SavedData(0f, 0f)
                    MainProp.savedDataMap.put(MainProp.radioProto.getSourceNo(), savedData)
                }

                if (Math.abs(MainProp.radioProto.getRange() - savedData.range) >= 0.1f) {
                    MainProp.activity?.runOnUiThread {
                        if (master!!.type == 1) { // 动臂式
                            val vangle = MathTool.calcVAngle(master!!.getBigArmLen(), MainProp.radioProto.getRange(), master!!.archPara)
                            master!!.setVAngle(vangle.toFloat()) // 设置动臂式的仰角
                            master!!.setHeight(master!!.getOrgHeight() + master!!.getBigArmLen() * Math.sin(Math.toRadians(vangle)).toFloat()) // 0 -> vangle
                            master!!.setCarRange(master!!.getBigArmLen()) // 动臂式 幅度最大
                        } else {
                            master!!.setCarRange(MainProp.radioProto.getRange()) // 平臂式实际幅度
                        }
                    }
                    savedData.range = MainProp.radioProto.getRange()
                }

                val hangle = MathTool.radiansToAngle(MainProp.radioProto.getRotate())
                if (Math.abs(hangle - savedData.angle) >= 0.1) {
                    MainProp.activity?.runOnUiThread {
                        //System.out.println("## rotateShow, angle = " + hangle);
                        master!!.setHAngle(hangle)
                    }
                    savedData.angle = hangle
                }
            }

            if (MainProp.radioProto.getTargetNo() == null || MainProp.radioProto.getSourceNo() == null) return

            if (MainProp.radioProto.getSourceNo() == MainProp.radioProto.getTargetNo() // 源ID和目标ID相同

                    || MainProp.radioProto.getTargetNo() == MainProp.myCraneNo || MainProp.radioProto.getPermitNo() == MainProp.myCraneNo) { // 目标ID相同是本机

                MainProp.slaveRadioProto.setSourceNo(Integer.parseInt(MainProp.myCraneNo))
                MainProp.slaveRadioProto.setTargetNo(0) // 固定为0
                MainProp.slaveRadioProto.setPermitNo(0)

                if (MainProp.centerCycle!!.type == 1) { // 动臂式, 计算投影值
                    var shadow = MathTool.calcShadow(MainProp.centerCycle!!.getBigArmLen(), MainProp.currProto.getRealVAngle(), MainProp.centerCycle!!.archPara).toFloat()
                    shadow = Math.round(shadow * 10) / 10.0f
                    MainProp.slaveRadioProto.setRange(Math.max(shadow, 0f))
                } else {
                    MainProp.slaveRadioProto.setRange(Math.max(MainProp.centerCycle!!.carRange, 0f))
                }

                MainProp.slaveRadioProto.setRotate(Math.max(0f, MainProp.currXAngle!! % 360 * 2f * Math.PI.toFloat() / 360))
                MainProp.slaveRadioProto.packReply() // 生成回应报文
                try {
                    if (MainProp.ttyS1OutputStream != null) {
                        MainProp.ttyS1OutputStream!!.write(MainProp.slaveRadioProto.modleBytes, 0, 39)
                        MainProp.ttyS1OutputStream!!.flush()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    return
                }

            }

            MainProp.radioStatusMap.put(MainProp.radioProto.getSourceNo(), currTime)
        }

        if (!MainProp.radioProto.isQuery) { // 收到其他从机的回应命令 & 分自己的 主从 身份
            MainProp.waitFlag = false
            // 更新从机

            val slave = MainProp.craneMap.get(MainProp.radioProto.getSourceNo())
            if (slave != null) {

                var savedData: SavedData? = MainProp.savedDataMap.get(MainProp.radioProto.getSourceNo())
                if (savedData == null) {
                    savedData = SavedData(0f, 0f)
                    MainProp.savedDataMap.put(MainProp.radioProto.getSourceNo(), savedData)
                }

                MainProp.activity?.runOnUiThread { slave!!.setColor(Color.rgb(46, 139, 87)) }

                if (Math.abs(MainProp.radioProto.getRange() - savedData.range) >= 0.1f) {
                    MainProp.activity?.runOnUiThread {
                        if (slave!!.type == 1) { // 动臂式
                            println("## slave0: " + slave!!.getBigArmLen() + "@@" + MainProp.radioProto.getRange() + "$$" + slave!!.archPara)
                            val vangle = MathTool.calcVAngle(slave!!.getBigArmLen(), MainProp.radioProto.getRange(), slave!!.archPara)
                            slave!!.setVAngle(vangle.toFloat()) // 设置动臂式的仰角
                            println("## slave1: " + slave!!.getOrgHeight() + "@@" + slave!!.getBigArmLen() + "$$" + vangle)
                            slave!!.setHeight(slave!!.getOrgHeight() + slave!!.getBigArmLen() * Math.sin(Math.toRadians(vangle)).toFloat()) // 0 -> vangle
                            slave!!.setCarRange(slave!!.getBigArmLen()) // 动臂式 幅度最大
                        } else {
                            slave!!.setCarRange(MainProp.radioProto.getRange()) // 平臂式实际幅度
                        }

                    }
                    savedData.range = MainProp.radioProto.getRange()
                }

                val hangle = MathTool.radiansToAngle(MainProp.radioProto.getRotate()) // 水平方向的夹角
                if (Math.abs(hangle - savedData.angle) >= 0.1) {
                    MainProp.activity?.runOnUiThread { slave!!.setHAngle(hangle) }
                    savedData.angle = hangle
                }

                slave!!.setOnline(true) // 设置离线状态
                MainProp.radioStatusMap.put(MainProp.radioProto.getSourceNo(), currTime)
            }
        }
    }

    fun startRadioReadThread() { // 收线程

        Thread {

            while (!MainProp.sysExit && !MainProp.channelOps.get() && !MainProp.netRadio) { // 电台通信
                if (MainProp.ttyS0InputStream == null || MainProp.ttyS1InputStream == null || MainProp.ttyS2InputStream == null) {
                    CommTool.sleep(100)
                    continue
                }

                try {
                    if (MainProp.ttyS1InputStream!!.available() > 0) {
                        val len = MainProp.ttyS1InputStream!!.read(MainProp.radioXBuff, 0, 1024)
                        MainProp.dataUtil.add(MainProp.radioXBuff, len)
                        if (MainProp.dataUtil.check()) { // 协议类型判断
                            MainProp.radioEvent.setData(MainProp.dataUtil.get())
                            RadioDateEventOps(MainProp.radioEvent)
                        }
                    } else {
                        val currTime = System.currentTimeMillis() // 当前时间
                        val radioRecSet = MainProp.radioStatusMap.keys
                        for (no in radioRecSet) {
                            val prevRecTimer = MainProp.radioStatusMap.get(no) as Long// 上次记录时间
                            if (currTime - prevRecTimer > 300000) { // 通信10失联，判断超时
                                MainProp.activity?.runOnUiThread {
                                    MainProp.craneMap.get(no)?.setColor(Color.LTGRAY)
                                    MainProp.craneMap.get(no)?.setCarRange(0f) // 失联设备, 设置小车幅度为0
                                }
                                MainProp.craneMap.get(no)?.setOnline(false) // 设置离线状态
                            }
                        }
                    }

                    CommTool.sleep(5)
                } catch (e: Exception) {
                    e.printStackTrace()
                    continue
                }

            }

            if (MainProp.channelOps.get()) {
                try {
                    MainProp.ttyS1InputStream?.close()
                    MainProp.ttyS1OutputStream?.close()
                    MainProp.serialttyS1?.closeSerial()

                    MainProp.serialttyS1 = SerialPort(
                            "S2", 9600, 8, 1, 'n'.toInt(), false)
                    MainProp.ttyS1InputStream = MainProp.serialttyS1?.getInputStream()
                    MainProp.ttyS1OutputStream = MainProp.serialttyS1?.getOutputStream()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            while (!MainProp.sysExit && MainProp.netRadio) {  // 网络通信
                try {
                    //netRadioProto.lock.writeLock().lock();
                    MainProp.netRadioProto.lock.writeLock().tryLock()
                    if (MainProp.netRadioProto.length > 0) {
                        val frameNum = MainProp.netRadioProto.length / 39
                        val tmpBuff = ByteArray(39)
                        for (i in 0 until frameNum) {
                            System.arraycopy(MainProp.netRadioProto.netBuffer, 39 * i, tmpBuff, 0, 39)
                            MainProp.radioEvent.setData(tmpBuff)
                            RadioDateEventOps(MainProp.radioEvent)
                            println(String(tmpBuff))
                        }
                        MainProp.netRadioProto.length = 0
                    } else {
                        val currTime = System.currentTimeMillis() // 当前时间
                        val radioRecSet = MainProp.radioStatusMap.keys
                        for (no in radioRecSet) {
                            val prevRecTimer = MainProp.radioStatusMap.get(no) as Long // 上次记录时间
                            if (currTime - prevRecTimer > 300000) { // 通信10失联，判断超时
                                MainProp.activity?.runOnUiThread {
                                    MainProp.craneMap.get(no)?.setColor(Color.LTGRAY)
                                    MainProp.craneMap.get(no)?.setCarRange(0f) // 失联设备, 设置小车幅度为0
                                }
                                MainProp.craneMap.get(no)?.setOnline(false) // 设置离线状态
                            }
                        }
                    }

                    MainProp.netRadioProto.lock.writeLock().unlock()

                    CommTool.sleep(5)
                } catch (e: Exception) {
                    //e.printStackTrace();
                    println(e.message)
                    continue
                }

            }

        }.start()
    }
}