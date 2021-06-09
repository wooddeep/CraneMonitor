package com.wooddeep.crane

import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import com.example.x6.serialportlib.SerialPort
import com.wooddeep.crane.comm.ControlProto
import com.wooddeep.crane.ebus.RestartEvent
import com.wooddeep.crane.ebus.SysParaEvent
import com.wooddeep.crane.main.MainUi
import com.wooddeep.crane.net.NetClient
import com.wooddeep.crane.persist.DatabaseHelper
import com.wooddeep.crane.persist.LogDbHelper
import com.wooddeep.crane.persist.dao.*
import com.wooddeep.crane.persist.dao.log.CtrlRecDao
import com.wooddeep.crane.persist.dao.log.RealDataDao
import com.wooddeep.crane.persist.dao.log.SwitchRecDao
import com.wooddeep.crane.persist.dao.log.WorkRecDao
import com.wooddeep.crane.persist.entity.Calibration
import com.wooddeep.crane.persist.entity.SysPara
import com.wooddeep.crane.persist.entity.log.*
import com.wooddeep.crane.tookit.NetTool
import com.wooddeep.crane.tookit.SysTool
import com.wooddeep.crane.views.CraneView
import com.wooddeep.crane.worker.RadioRead
import com.wooddeep.crane.worker.RadioWrite
import com.wooddeep.crane.worker.SensorWorker
import com.wooddeep.crane.worker.TimerWorker
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*

// by: 委托

class KtDemoActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    // 初始化数据
    fun initTable() {
        val dbFile = File("/data/data/com.wooddeep.crane/databases/crane.db")
        if (!dbFile.exists()) {
            SysTool.copyFilesFromRaw(this, R.raw.tc, "tc.db", "/data/data/com.wooddeep.crane/databases")
            SysTool.copyFilesFromRaw(this, R.raw.crane, "crane.db", "/data/data/com.wooddeep.crane/databases")
            SysTool.copyFilesFromRaw(this, R.raw.crane, "crane.db", "/data/data/com.wooddeep.crane/databases")
        }

        LogDbHelper.getInstance(MainProp.context).createTable(WorkRecrod::class.java)
        LogDbHelper.getInstance(MainProp.context).createTable(RealData::class.java)
        LogDbHelper.getInstance(MainProp.context).createTable(CaliRec::class.java)
        LogDbHelper.getInstance(MainProp.context).createTable(CtrlRec::class.java)
        LogDbHelper.getInstance(MainProp.context).createTable(SwitchRec::class.java)

    }

    fun initDao() {
        MainProp.paraDao = SysParaDao(applicationContext)
        MainProp.calibDao = CalibrationDao(applicationContext)
        MainProp.craneDao = CraneDao(applicationContext)
        MainProp.areaDao = AreaDao(applicationContext)
        MainProp.protectDao = ProtectDao(applicationContext)
        MainProp.alarmSetDao = AlarmSetDao(applicationContext)
        MainProp.switchRecDao = SwitchRecDao(applicationContext)
        MainProp.ctrlRecDao = CtrlRecDao(applicationContext)
        MainProp.workRecDao = WorkRecDao(applicationContext)
        MainProp.calibrationDao = CalibrationDao(applicationContext)
        MainProp.protectAreaDao = ProtectAreaDao(applicationContext)
        MainProp.tcParamDao = TcParamDao(applicationContext)
        MainProp.realDataDao = RealDataDao(applicationContext)
    }

    fun initView() {
        MainProp.angleView = findViewById(R.id.angle) as TextView
        MainProp.angleView = findViewById(R.id.angle) as TextView
        MainProp.vAngleView = findViewById(R.id.vangle) as TextView
        MainProp.leftAlarmView = findViewById(R.id.left_alarm_level) as TextView
        MainProp.rightAlarmView = findViewById(R.id.right_alarm_level) as TextView
        MainProp.weightAlarmView = findViewById(R.id.weight_alarm_level) as TextView
        MainProp.hookAlarmView = findViewById(R.id.hook_alarm) as TextView
        MainProp.momentAlarmView = findViewById(R.id.moment_alarm_level) as TextView
        MainProp.forwardAlarmView = findViewById(R.id.forward_alarm_level) as TextView
        MainProp.backwardAlarmView = findViewById(R.id.back_alarm_level) as TextView
        MainProp.momentView = findViewById(R.id.moment) as TextView
        MainProp.ratedWeightView = findViewById(R.id.rated_weight) as TextView
        MainProp.weightView = findViewById(R.id.weight) as TextView
        MainProp.heightView = findViewById(R.id.height) as TextView
        MainProp.masterNoView = findViewById(R.id.master_no) as TextView
        MainProp.windSpeedView = findViewById(R.id.wind_speed) as TextView
        MainProp.devNoView = findViewById(R.id.devNo) as TextView
    }

    fun initMixPersistParam() {
        MainProp.alarmSet = MainProp.alarmSetDao?.selectAll()!![0]
        val calList = MainProp.calibrationDao?.selectAll()
        if (calList == null || calList.size == 0) {
            DatabaseHelper.getInstance(MainProp.context).createTable(Calibration::class.java) // 标定
            MainProp.calibrationDao?.insert(Calibration.getInitData())
            MainProp.calibration = Calibration.getInitData()
        } else {
            MainProp.calibration = calList[0]
        }
    }

    fun setCurrTime() {
        val date = Date()
        val dateNowStr = MainProp.sdf.format(date)
        val currDate = findViewById(R.id.currDate) as TextView
        runOnUiThread { currDate.setText(dateNowStr.substring(0, dateNowStr.length - 3)) }
    }

    fun initHardWare() {
        val s0Name = "S1" // AD
        val s1Name = "S2" // 电台
        MainProp.serialttyS0 = SerialPort(s0Name, 115200, 8, 0, 'o'.toInt(), true) // 19200 // AD数据
        MainProp.serialttyS1 = SerialPort(s1Name, 19200, 8, 0, 'o'.toInt(), true)

        MainProp.ttyS0InputStream = MainProp.serialttyS0!!.getInputStream()
        MainProp.ttyS0OutputStream = MainProp.serialttyS0!!.getOutputStream()
        if (MainProp.ttyS0OutputStream != null) MainProp.ttyS0OutputStream!!.write(ControlProto.control)
        MainProp.ttyS1InputStream = MainProp.serialttyS1!!.getInputStream()
        MainProp.ttyS1OutputStream = MainProp.serialttyS1!!.getOutputStream()

        MainProp.serialttyS2 = SerialPort("S3", 19200, 8, 1, 'e'.toInt(), true) // 编码
        MainProp.ttyS2OutputStream = MainProp.serialttyS2!!.getOutputStream()
        MainProp.ttyS2InputStream = MainProp.serialttyS2!!.getInputStream()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Suppress("unused")
    fun restartRadioEventBus(event: RestartEvent) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        SysTool.sysScriptInit(applicationContext)

        // 初始化主类相关属性
        MainProp.activity = this
        MainProp.context = applicationContext
        MainProp.mPackageManager = packageManager

        initView()
        setCurrTime()
        initTable() // 初始化表
        initDao()
        initMixPersistParam()

        EventBus.getDefault().register(this)
        MainProp.controlProto.clear()
        MainProp.eventBus.post(SysParaEvent()) // 触发系统参数相关
        MainProp.player = MediaPlayer.create(applicationContext, R.raw.ding) // 声音播放器

        initHardWare()

        // 触发判断本机是否为主机
        Handler().postDelayed({
            SensorWorker.startSensorThread() // 初始化串口线程
            RadioRead.startRadioReadThread()
            RadioWrite.startRadioWriteThread()
            TimerWorker.startTimerThread()

            val mac = NetTool.getMacAddress(applicationContext).replace(":".toRegex(), "")
            MainProp.devNoView?.text = "DN: $mac"

            NetClient.run(mac)
        }, 10)

    }

    //        launch(Dispatchers.Main) {
    //            sleep()
    //        }

    suspend fun sleep() {
        delay(1000)
    }

    fun asyncShowData() = launch {
        // Is invoked in UI context with Activity's job as a parent
        // actual implementation
    }

    suspend fun showIOData() {
        val deferred = async(Dispatchers.IO) {
            // impl
        }
        withContext(Dispatchers.Main) {
            val data = deferred.await()
            // Show data in UI
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        val sSavedScale = MainProp.paraDao!!.queryValueByName("oscale")
        if (sSavedScale == null) {
            MainProp.oscale = 1.0f
            MainProp.paraDao!!.insert(SysPara("oscale", "1"))
        } else {
            MainProp.oscale = java.lang.Float.parseFloat(MainProp.paraDao!!.queryValueByName("oscale"))
        }

        MainUi.renderMain(MainProp.oscale)
        //MainUi.renderMenu()

        val cranes = MainProp.craneDao!!.selectAll()
        if (cranes!!.size == 0) return

        // 查询主塔基的塔基类型
        MainProp.craneType = cranes[0].type
        for (temp in cranes) {
            if (temp.isMain()) {
                MainProp.craneType = temp.type
            }
        }

        //MainProp.craneView!!.setCraneType(MainProp.craneType)
    }


    override fun onDestroy() {
        super.onDestroy()
        MainProp.sysExit = true
    }

    override fun onBackPressed() {
        //按返回键返回桌面
        moveTaskToBack(true)
    }

}
