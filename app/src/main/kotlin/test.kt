package com.wooddeep.crane

import kotlinx.coroutines.*
import java.util.concurrent.Executors

// kotlin的协程使用
// https://zhuanlan.zhihu.com/p/101489392
// https://www.jianshu.com/p/6e6835573a9c

class xyz {

    fun showMessage() {
        println("hello world!")
        coroutine()

        // Dispatchers.Main：使用这个调度器在 Android 主线程上运行一个协程。可以用来更新UI 。在UI线程中执行
        //
        // Dispatchers.IO：这个调度器被优化在主线程之外执行磁盘或网络 I/O。在线程池中执行
        //
        // Dispatchers.Default：这个调度器经过优化，可以在主线程之外执行 cpu 密集型的工作。例如对列表进行排序和解析 JSON。在线程池中执行。
        //
        // Dispatchers.Unconfined：在调用的线程直接执行。

        GlobalScope.launch(Dispatchers.Main) {
            sleep()
        }

        GlobalScope.launch {
            delay(1000)
            async { sleep() }
        }

    }

    suspend fun sleep() {
        delay(1000)
    }

    // 自定义协程池
    fun coroutine() {

        runBlocking {
            //创建自定义线程池
            val coroutineDispatcher = Executors.newFixedThreadPool(3).asCoroutineDispatcher()
            launch {
                //在父协程中创建子协程
                repeat(5) {
                    //使用自定义线程池执行协程
                    async(coroutineDispatcher) {
                        Thread.sleep(1000)
                        println("${Thread.currentThread().name}")
                    }
                }
            }.join()//等待子协程执行完毕
            //关闭自定义线程池
            coroutineDispatcher.close()
            println("子协程执行完啦!!!!")
        }
    }

}

fun main() {
    xyz().showMessage()
}