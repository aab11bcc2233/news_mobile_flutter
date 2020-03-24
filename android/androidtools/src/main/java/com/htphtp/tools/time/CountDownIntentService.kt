package com.htphtp.tools.time

import android.app.IntentService
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 *
 *
 * helper methods.
 */
class CountDownIntentService : Service() {
//    override fun onHandleIntent(intent: Intent?) {
//        handleIntent(intent)
//    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val countDowns: MutableList<CountDownTimer> = mutableListOf()
    private val actions: MutableList<String> = mutableListOf()
    private val taskReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val stopAction = intent!!.getStringExtra(EXTRA_STOP)

            if (!actions.isEmpty()) {
                val indexOf = actions.indexOf(stopAction)

                if (indexOf != -1) {
                    countDowns[indexOf].cancel()

                    countDowns.removeAt(indexOf)
                    actions.removeAt(indexOf)

                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        LocalBroadcastManager.getInstance(this).registerReceiver(taskReceiver, IntentFilter(TASK_ACTION))
    }


    private fun handleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (actions.contains(action)) {
                throw IllegalArgumentException("action($action) already exists")
            }

            var millisInFuture = intent.getLongExtra(EXTRA_MILLISINFUTURE, 0)
            val countDownInterval = intent.getLongExtra(EXTRA_COUNTDOWNINTERVAL, 0)

            if (millisInFuture > 0) {
                millisInFuture += 1000L
            }

            handleActionCountDown(action, millisInFuture, countDownInterval)

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIntent(intent)
        return super.onStartCommand(intent, flags, startId)
    }


    private fun handleActionCountDown(action: String, millisInFuture: Long, countDownInterval: Long) {
        val countDown: CountDownTimer = object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onFinish() {
                sendMillisUntil(action, 0)
                cancel()

                val indexOf = countDowns.indexOf(this)
                if (indexOf != -1) {
                    countDowns.remove(this)
                    actions.removeAt(indexOf)
                }

                if (countDowns.isEmpty()) {
                    stopSelf()
                }
            }

            override fun onTick(millisUntilFinished: Long) {
                Log.d(CountDownIntentService::class.java.simpleName, "millisUntilFinished=$millisUntilFinished")
                sendMillisUntil(action, millisUntilFinished)
            }

        }.start()

        countDowns.add(countDown)
        actions.add(action)
    }

    private fun sendMillisUntil(action: String, millisUntilFinished: Long) {
        val intent = Intent(action)
        intent.putExtra(EXTRA_MILLISUNTILFINISHED, millisUntilFinished)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(taskReceiver)

        for (countDown in countDowns) {
            countDown.cancel()
        }

        actions.clear()
    }


    companion object {
        private const val ACTION_COUNT_DOWN = "com.newboomutils.tools.action.COUNTDOWN"

        private const val EXTRA_MILLISINFUTURE = "com.newboomutils.tools.extra.MILLISINFUTURE"
        private const val EXTRA_COUNTDOWNINTERVAL = "com.newboomutils.tools.extra.COUNTDOWNINTERVAL"

        const val EXTRA_MILLISUNTILFINISHED = "com.newboomutils.tools.extra.EXTRA_MILLISUNTILFINISHED"

        private const val TASK_ACTION = "com.newboomutils.tools.action.TASK"
        private const val EXTRA_STOP = "stop"

        fun stopAction(context: Context, action: String = ACTION_COUNT_DOWN) {
            val intent = Intent(TASK_ACTION)
            intent.putExtra(EXTRA_STOP, action)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }


        fun startAction(context: Context, action: String = ACTION_COUNT_DOWN, millisInFuture: Long, countDownInterval: Long) {
            val intent = Intent(context, CountDownIntentService::class.java)
            intent.action = action
            intent.putExtra(EXTRA_MILLISINFUTURE, millisInFuture)
            intent.putExtra(EXTRA_COUNTDOWNINTERVAL, countDownInterval)
            context.startService(intent)
        }

    }
}
