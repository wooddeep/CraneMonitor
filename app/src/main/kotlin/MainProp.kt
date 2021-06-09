package com.wooddeep.crane

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.widget.TextView
import com.example.x6.serialportlib.SerialPort
import com.wooddeep.crane.comm.*
import com.wooddeep.crane.ebus.*
import com.wooddeep.crane.element.BaseElem
import com.wooddeep.crane.element.CenterCycle
import com.wooddeep.crane.element.CycleElem
import com.wooddeep.crane.element.ElemMap
import com.wooddeep.crane.main.SavedData
import com.wooddeep.crane.main.ShowData
import com.wooddeep.crane.persist.dao.*
import com.wooddeep.crane.persist.dao.log.CtrlRecDao
import com.wooddeep.crane.persist.dao.log.RealDataDao
import com.wooddeep.crane.persist.dao.log.SwitchRecDao
import com.wooddeep.crane.persist.dao.log.WorkRecDao
import com.wooddeep.crane.persist.entity.AlarmSet
import com.wooddeep.crane.persist.entity.Calibration
import com.wooddeep.crane.persist.entity.Crane
import com.wooddeep.crane.persist.entity.TcParam
import com.wooddeep.crane.persist.entity.log.CtrlRec
import com.wooddeep.crane.persist.entity.log.RealData
import com.wooddeep.crane.persist.entity.log.SwitchRec
import com.wooddeep.crane.persist.entity.log.WorkRecrod
import com.wooddeep.crane.simulator.SimulatorFlags
import com.wooddeep.crane.simulator.UartEmitter
import com.wooddeep.crane.tookit.DataUtil
import com.wooddeep.crane.views.CraneView
import org.greenrobot.eventbus.EventBus
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@SuppressLint("StaticFieldLeak")
@Suppress("unused")
object MainProp {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val TAG = "MainActivity"
    @Volatile

    var activity: android.app.Activity? = null // = this
    var context: Context? = null

    val savedDataMap = HashMap<String, SavedData>()
    val craneMap = HashMap<String, CycleElem>()
    val slaveMap = HashMap<String, SavedData>()
    val radioStatusMap = HashMap<String, Long>()
    val craneNumbers = ArrayList<String>()
    val elemList = ArrayList<BaseElem>()
    var loadParas: List<TcParam>? = ArrayList()
    val elemMap = ElemMap()

    var currSlaveIndex = 0 // 当前和本主机通信的从机名称
    var shadowLength = 0f
    var myCraneNo = ""
    var oscale = 1.0f
    var craneType = 0 // 塔基类型: 0 ~ 平臂式, 2动臂式
    var iPower = 2 // 倍率

    val currRotateProto = RotateProto()
    val prevRotateProto = RotateProto()
    val radioProto = RadioProto()
    val prevProto = Protocol() // 记录前次ad数据解析结果
    val currProto = Protocol() // 记录当次ad数据解析结果

    var centerCycle: CenterCycle? = null // 本塔基圆环
    var mainCrane: Crane? = null   // 本塔基参数

    val eventBus = EventBus.getDefault()
    val emitter = UartEmitter()
    var calibration: Calibration? = Calibration()
    var alarmSet = AlarmSet()

    val iAmMaster = AtomicBoolean(false) // 本机是否为通信主机
    val flags = SimulatorFlags()
    var calibrationFlag = AtomicBoolean(false)
    var netCalibFlag = AtomicBoolean(false)
    var calibCraneType = AtomicInteger(0)

    val isMasterCrane = false // 是否主塔机
    var waitFlag = true // 等待主机信号标识
    var superSuper = false
    var isRvcMode = AtomicBoolean(false)

    val controlProto = ControlProto()
    val slaveRadioProto = RadioProto()  // 本机作为从机时，需要radio通信的对象
    val masterRadioProto = RadioProto() // 本机作为主机时，需要radio通信的对象

    var serialttyS0: SerialPort? = null
    var serialttyS1: SerialPort? = null
    var serialttyS2: SerialPort? = null
    var ttyS0InputStream: InputStream? = null
    var ttyS0OutputStream: OutputStream? = null
    var ttyS1InputStream: InputStream? = null
    var ttyS1OutputStream: OutputStream? = null
    var ttyS2OutputStream: OutputStream? = null
    var ttyS2InputStream: InputStream? = null

    val rotateCmd = byteArrayOf(0x01, 0x04, 0x00, 0x01, 0x00, 0x02, 0x20, 0x0B)
    val adXBuff = ByteArray(2048)
    val rotateXBuff = ByteArray(1024)
    val radioXBuff = ByteArray(1024)
    val radioYBuff = ByteArray(1024)
    val adRBuff = ByteArray(20)
    val rotateRBuff = ByteArray(10)
    val radioRBuff = ByteArray(40)

    val alarmDetectEvent = AlarmDetectEvent()
    val heightEvent = HeightEvent()
    val weightEvent = WeightEvent()
    val lengthEvent = LengthEvent()
    val rotateEvent = RotateEvent()
    val radioEvent = RadioEvent()
    val uartEvent = UartEvent()
    var alarmEvent: AlarmEvent? = null

    var paraDao: SysParaDao? = null // 系统参数
    var tcParamDao: TcParamDao? = null
    var realDataDao: RealDataDao? = null // 工作记录DAO
    val realData = RealData() // 工作记录
    var ctrlRecDao: CtrlRecDao? = null
    var calibDao: CalibrationDao? = null
    var areaDao: AreaDao? = null
    var protectDao: ProtectDao? = null
    var protectAreaDao: ProtectAreaDao? = null
    var alarmSetDao: AlarmSetDao? = null
    var craneDao: CraneDao? = null
    var calibrationDao: CalibrationDao?= null


    val ctrlRec = CtrlRec() // 控制记录
    var workRecDao: WorkRecDao? = null // 工作记录DAO
    val workRec = WorkRecrod() // 工作记录

    var switchRecDao: SwitchRecDao? = null
    val switchRec = SwitchRec()

    var craneView: CraneView? = null
    var angleView: TextView? = null
    var vAngleView: TextView? = null
    var leftAlarmView: TextView? = null
    var rightAlarmView: TextView? = null
    var forwardAlarmView: TextView? = null
    var backwardAlarmView: TextView? = null
    var weightAlarmView: TextView? = null
    var hookAlarmView: TextView? = null
    var momentAlarmView: TextView? = null
    var momentView: TextView? = null
    var ratedWeightView: TextView? = null
    var weightView: TextView? = null
    var masterNoView: TextView? = null
    var heightView: TextView? = null
    var windSpeedView: TextView? = null
    var player: MediaPlayer? = null
    var devNoView: TextView? = null

    @Volatile
    var sysExit = false
    val intent: Intent? = null

    var currWeight = 0.3f // 当前总量
    var currMoment = 0.0f // 当前力矩
    var saveMoment = 0.0f

    var mPackageManager: PackageManager? = null
    val dataUtil = DataUtil()
    //private MixDataUtil mixDataUtil = new MixDataUtil();
    var showData = ShowData()
    var alarmLevel = AtomicInteger(100)
    var currXAngle: Float? = 0.0f
    var registered = AtomicBoolean(false)

    var channelOps = AtomicBoolean(false)

    var netRadioProto = NetRadioProto()

    var gCenterX: Float = 0.toFloat()
    var gCenterY: Float = 0.toFloat()

    var netRadio = false // 调试

    val protocol = com.wooddeep.crane.net.network.Protocol()
}