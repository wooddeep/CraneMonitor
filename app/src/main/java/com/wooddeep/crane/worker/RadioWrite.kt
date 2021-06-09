package com.wooddeep.crane.worker

import com.wooddeep.crane.MainProp
import com.wooddeep.crane.net.NetClient
import com.wooddeep.crane.tookit.CommTool

object RadioWrite {
    fun startRadioWriteThread() {
        Thread {

            while (!MainProp.sysExit && !MainProp.channelOps.get() && !MainProp.netRadio) {

                if (MainProp.ttyS0InputStream == null || MainProp.ttyS1InputStream == null || MainProp.ttyS2InputStream == null) {
                    CommTool.sleep(100)
                    continue
                }

                try {
                    if (MainProp.iAmMaster.get() && MainProp.craneNumbers.size >= 1) { // 主机, 发送查询, 发送【1】
                        val iMyCraneNo = Integer.parseInt(MainProp.myCraneNo)
                        MainProp.currSlaveIndex = (MainProp.currSlaveIndex + 1) % MainProp.craneNumbers.size
                        val targetNo = Integer.parseInt(MainProp.craneNumbers.get(MainProp.currSlaveIndex))
                        MainProp.masterRadioProto.setSourceNo(iMyCraneNo)
                        MainProp.masterRadioProto.setTargetNo(targetNo)
                        MainProp.masterRadioProto.setPermitNo(targetNo)

                        val currAngle = Math.max(Math.toRadians(MainProp.currXAngle!!.toDouble()), 0.0) // 当前回转角度
                        val currRange = Math.max(MainProp.shadowLength, 0f).toDouble()

                        MainProp.masterRadioProto.setRotate(Math.round(currAngle * 10) / 10.0f)
                        MainProp.masterRadioProto.setRange(Math.round(currRange * 10) / 10.0f)
                        MainProp.masterRadioProto.packReply() // 生成回应报文

                        try {
                            MainProp.ttyS1OutputStream!!.write(MainProp.masterRadioProto.modleBytes, 0, 39) // TODO 39字节转换为44字节
                            MainProp.ttyS1OutputStream!!.flush()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            continue
                        }

                    }

                    CommTool.sleep(130)
                } catch (e: Exception) {
                    e.printStackTrace()
                    continue
                }

            }

            while (!MainProp.sysExit && MainProp.netRadio) {

                try {
                    if (MainProp.iAmMaster.get() && MainProp.craneNumbers.size >= 1) { // 不管主机还是从机，上报数据
                        val iMyCraneNo = Integer.parseInt(MainProp.myCraneNo)
                        MainProp.currSlaveIndex = (MainProp.currSlaveIndex + 1) % MainProp.craneNumbers.size
                        val targetNo = Integer.parseInt(MainProp.craneNumbers.get(MainProp.currSlaveIndex))
                        MainProp.masterRadioProto.setSourceNo(iMyCraneNo)
                        MainProp.masterRadioProto.setTargetNo(targetNo)
                        MainProp.masterRadioProto.setPermitNo(targetNo)

                        val currAngle = Math.max(Math.toRadians(MainProp.currXAngle!!.toDouble()), 0.0) // 当前回转角度
                        val currRange = Math.max(MainProp.shadowLength, 0f).toDouble()

                        MainProp.masterRadioProto.setRotate(Math.round(currAngle * 10) / 10.0f)
                        MainProp.masterRadioProto.setRange(Math.round(currRange * 10) / 10.0f)
                        MainProp.masterRadioProto.packReply() // 生成回应报文

                        try {
                            // 通过网络写
                            NetClient.mq.offer(MainProp.masterRadioProto.modleBytes)

                        } catch (e: Exception) {
                            e.printStackTrace()
                            continue
                        }

                    }

                    CommTool.sleep(130)
                } catch (e: Exception) {
                    e.printStackTrace()
                    continue
                }

            }

        }.start()
    }
}