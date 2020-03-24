package com.htphtp.tools

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*

/**
 * Created by htp on 2018/4/2.
 */

class PermissionManagerActivity : AppCompatActivity() {


    /**
     * 需要申请的权限
     */
    private var mPermissions: Array<String>? = null

    /**
     * 需要申请的权限 对应的 是否必须要授权 与 mPermissions 长度相等.
     */
    private var mMustBeAuths: BooleanArray? = null


    /**
     * 跳转设置的对话框
     */
    protected var mShowToSettingDialog: AlertDialog? = null

    /**
     * 避免系统授权对话框出现多次
     */
    private var isPerformRequestPermission = false

    private var mHandler: Handler? = null

    fun requestPermissions(vararg permission: String) {
        ActivityCompat.requestPermissions(this, permission, REQUEST_CODE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        //        RelativeLayout relativeLayout = new RelativeLayout(this);
        //        relativeLayout.setLayoutParams(
        //                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
        //                                                RelativeLayout.LayoutParams.MATCH_PARENT));
        ////        relativeLayout.setBackgroundColor(Color.parseColor("#55ff0000"));
        //        relativeLayout.setBackgroundColor(Color.parseColor("#00000000"));
        //        setContentView(relativeLayout);

        val intent = intent
        handlerIntent(intent)

        //        L.d(TAG + " onCreate()");

    }

    private fun handlerIntent(intent: Intent) {
        mPermissions = intent.getStringArrayExtra(PERMISSION)
        mMustBeAuths = intent.getBooleanArrayExtra(MUST_BE_AUTH)

        if (mPermissions == null) {
            finishActivity()
            return
        }

        //        showExplainDialog();
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handlerIntent(intent)
    }

    override fun onStart() {
        super.onStart()

        if (isPerformRequestPermission) {
            return
        }

        //        if (mShowExplainDialog != null && mShowExplainDialog.isShowing()) {
        //            return;
        //        }

        if (mShowToSettingDialog != null && mShowToSettingDialog!!.isShowing) {
            return
        }
        shouldShowRequestPermission()
    }

    override fun onStop() {
        super.onStop()
    }

    /**
     * 这里主要判断 用户有没有 将系统的授权对话框, 勾选了
     * "不再询问" 并且 选择了 "拒绝授权"
     */
    private fun shouldShowRequestPermission() {

        //是否全部授权
        var isAllPermissionGrant = true
        for (i in mPermissions!!.indices) {
            val s = mPermissions!![i]

            val checkSelfPermission = checkSelfPermission(this, s)
            if (!checkSelfPermission) {
                isAllPermissionGrant = false
                /**
                 * 这里其实是这样
                 * 第一次调用,返回 false, 然后去请求权限, 用户选择了 "拒绝", 会返回true, 这个
                 * 时候再请求一次权限, 用户 勾选了 "不再询问" 并 选择了 "拒绝", 会返回 false.
                 * 这时系统授权对话框就不会再弹出, 所以直接跳转到应用设置页面, 让用户去操作权限.
                 */
                val b = ActivityCompat.shouldShowRequestPermissionRationale(this, s)
                //                L.d("是否应该弹出解释对话框  " + b);
                if (b) {
                    //                    L.d("permission = " + s);
                    //                    L.d("isShowExplain xxxxx  = " + true);
                    putWasShowSettingDialog(this, s, true)
                    request()
                } else {
                    /**
                     * 为什么要这样呢,就是,比如你请求一组权限, 用户对第一个权限勾选了 "不再询问"并且拒绝了,
                     * 第二个权限 只是 "拒绝" 没有勾选
                     * 所以再次请求权限时, 希望弹出 系统授权对话框, 而不是直接跳转到 应用设置界面
                     */
                    val isShowExplain = getWasShowSettingDialog(this, s)
                    //                    L.d("permission = " + s);
                    //                    L.d("isShowExplain = " + isShowExplain);

                    if (isShowExplain) {
                        /**
                         * 说明用户 勾选了 "不再询问" 并且 选择了 "拒绝".
                         * 这样的话,系统授权对话框就无法再打开, 跳转到 设置 页面, 让用户去操作权限.
                         */
                        showToSettingDialog()
                    } else {
                        //                        L.d("shouldShowRequestPermission request permission");
                        request()
                    }
                }
                break
            }
        }

        if (isAllPermissionGrant) {
            sendBroadcastEventPermissionSuccess()
        }

        //        /**
        //         * 这里其实是这样
        //         * 第一次调用,返回 false, 然后去请求权限, 用户选择了 "拒绝", 会返回true, 这个
        //         * 时候再请求一次权限, 用户 勾选了 "不再询问" 并 选择了 "拒绝", 会返回 false.
        //         * 这时系统授权对话框就不会再弹出, 所以直接跳转到应用设置页面, 让用户去操作权限.
        //         */
        //        boolean b = ActivityCompat.shouldShowRequestPermissionRationale(this, mPermissions[0]);
        //        L.d("shouldShowRequestPermission : " + b);
        //        L.d("shouldShowRequestPermission dialogShowCount : " + dialogShowCount);
        //        if (b) {
        //            requestPermissions(mPermissions);
        //        } else {
        //            if (dialogShowCount > 0) {
        //                /*当 dialogShowCount 大于 0 , 说明显示过 用来给用户解释为什么需要权限的对话框,
        //                   然后又走到这里, 说明用户 勾选了 "不再询问" 并且 选择了 "拒绝".
        //                   这样的话,系统授权对话框就无法再打开, 跳转到 设置 页面, 让用户去操作权限.
        //                */
        //                showToSettingDialog();
        //            } else {
        //                L.d("shouldShowRequestPermission request permission");
        //                requestPermissions(mPermissions);
        //            }
        //        }
    }

    private fun request() {
        requestPermissions(*mPermissions!!)
        isPerformRequestPermission = true
    }

    private fun sendBroadcastEventPermissionSuccess() {
        sendBroadcast(EVENT_PERMISSION_SUCCESS)
        finishActivity()
    }

    /**
     * 这里弹出对话框, 告诉用户, 去设置 页面操作权限.
     */
    private fun showToSettingDialog() {
        //        L.d("设置对话框");
        if (mShowToSettingDialog == null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("提示")
            builder.setMessage("缺少必要权限,请点击\"设置\"-\"权限\"打开所需权限")

            builder.setPositiveButton("设置") { dialog, which ->
                dialog.dismiss()
                toSettingActivity()
            }

            builder.setNegativeButton("退出") { dialog, which ->
                sendBroadcast(EVENT_SETTING_DIALOG_CLICK_EXIT)
                dialog.dismiss()
                finishActivity()
            }

            builder.setCancelable(false)
            //            builder.setOnCancelListener(dialog -> {
            //                dialog.dismiss();
            //                finish();
            //            });

            mShowToSettingDialog = builder.create()
        }

        if (mShowToSettingDialog!!.isShowing) {
            mShowToSettingDialog!!.dismiss()
        }

        mShowToSettingDialog!!.show()
    }

    private fun toSettingActivity() {
        val packageURI = Uri.parse("package:" + this.packageName)
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }


    /**
     * 授权结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            isPerformRequestPermission = false

            //是否全部是必须授权
            var allMustBeAuth = true
            for (b in mMustBeAuths!!) {
                if (!b) {
                    allMustBeAuth = false
                    break
                }
            }

            if (allMustBeAuth) { //必须要全部授权

                var denied = false
                for (i in grantResults.indices) {
                    val grantResult = grantResults[i] //授权结果
                    if (grantResult == PackageManager.PERMISSION_DENIED) { //未授权
                        denied = true
                        break
                    }
                }

                if (!denied) {
                    //                    L.d("授权动作成功");
                    sendBroadcastEventPermissionSuccess()
                } else {
                    //                    L.d("授权动作失败");
                    sendBroadcastEventPermissionFailure()
                }
            } else {

                for (i in grantResults.indices) {
                    val auth = mMustBeAuths!![i] //是否必须授权
                    val grantResult = grantResults[i] //授权结果
                    if (grantResult == PackageManager.PERMISSION_DENIED && auth) { //未授权 但是 是必须授权的 一个权限
                        //                        L.d("授权动作失败");
                        sendBroadcastEventPermissionFailure()
                        return
                    }
                }

                //                L.d("授权动作成功");
                sendBroadcastEventPermissionSuccess()
            }

        }
    }

    private fun sendBroadcastEventPermissionFailure() {
        sendBroadcast(EVENT_PERMISSION_FAILURE)
        finishActivity()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPermissions = null
        mShowToSettingDialog = null
        unregisterReceiver()
    }

    override fun onBackPressed() {

    }

    fun sendBroadcast(event: Int) {
        val intent = Intent(ACTION_PERMISSION_MANAGER_BROADCAST_RECEIVER)
        intent.putExtra(EVENT, event)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return true
    }

    fun finishActivity() {

        //这里为了避免闪屏, 延迟100ms关闭
        if (mHandler == null) {
            mHandler = Handler()
        }
        mHandler!!.postDelayed({
            finish()
            overridePendingTransition(0, 0)
            mHandler!!.removeCallbacksAndMessages(null)
            mHandler = null
        }, 100)
    }


    interface OnPermissionManagerEvent {
        fun onPermissionSuccess()

        fun onPermissionFailure()

        fun onSettingDialogClickExit()

        //        void onExplainDialogClickExit();
    }

    private class PermissionManagerBroadcastReceiver : BroadcastReceiver() {

        private var onEvent: OnPermissionManagerEvent? = null


        fun setOnPermissionManagerEvent(onEvent: OnPermissionManagerEvent) {
            this.onEvent = onEvent
        }


        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action === ACTION_PERMISSION_MANAGER_BROADCAST_RECEIVER) {
                val event = intent.getIntExtra(EVENT, -1)
                when (event) {
                    EVENT_PERMISSION_SUCCESS //授权成功
                    -> onEvent!!.onPermissionSuccess()
                    EVENT_PERMISSION_FAILURE //授权失败
                    -> onEvent!!.onPermissionFailure()
                    EVENT_SETTING_DIALOG_CLICK_EXIT //跳转设置的对话框, 点击了退出
                    -> {
                        onEvent!!.onSettingDialogClickExit()
                        onEvent!!.onPermissionFailure()
                    }
                }
            }
        }

    }

    companion object {

        //    private static final String TAG = "PermissionManagerActivity";

        private val PERMISSION = "PERMISSION"
        private val PERMISSION_SHARED_PREFERENCES = "PERMISSION_SHARED_PREFERENCES"
        private val REQUEST_CODE = 0x11
        private val MUST_BE_AUTH = "MUST_BE_AUTH"

        private val ACTION_PERMISSION_MANAGER_BROADCAST_RECEIVER = PermissionManagerActivity::class.java.name
        private val EVENT_PERMISSION_SUCCESS = 0
        private val EVENT_PERMISSION_FAILURE = 1
        private val EVENT_SETTING_DIALOG_CLICK_EXIT = 2
        //    private static final int EVENT_EXPLAIN_DIALOG_CLICK_EXIT = 3;
        private val EVENT = "EVENT"


        /**
         * @param context
         * @param permission
         * @return true Granted; false Denied
         */
        fun checkSelfPermission(context: Context, vararg permission: String): Boolean {
            return permission.all {
                PermissionChecker.checkSelfPermission(
                    context,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }
        }

        fun requestPermissions(context: Context, onEvent: OnPermissionManagerEvent, vararg permission: String) {
            registerReceiver(context, onEvent)
            startActivity(context, *permission)
        }

        /**
         * All permission must be authorized
         *
         * @param context
         * @param permission
         */
        private fun startActivity(context: Context, vararg permission: String) {

            val mustBeAuth = BooleanArray(permission.size)

            for (i in permission.indices) {
                mustBeAuth[i] = true
            }

            start(context, permission as Array<String>, mustBeAuth)
        }

        fun requestPermissions(
                context: Context,
                onEvent: OnPermissionManagerEvent,
                permissionMap: LinkedHashMap<String, Boolean>
        ) {
            registerReceiver(context, onEvent)
            startActivity(context, permissionMap)
        }

        /**
         * @param context
         * @param permissionMap key is permission; value is Must Be Authorized?
         */
        private fun startActivity(context: Context, permissionMap: LinkedHashMap<String, Boolean>) {

            val permission = Array(permissionMap.size) { "" }
            val mustBeAuth = BooleanArray(permissionMap.size)

            val entries = permissionMap.entries
            val iterator = entries.iterator()

            var index = 0
            while (iterator.hasNext()) {
                val entry = iterator.next()
                permission[index] = entry.key
                mustBeAuth[index] = entry.value
                index++
            }


            start(context, permission, mustBeAuth)
        }

        private fun start(context: Context, permission: Array<String>, mustBeAuth: BooleanArray) {
            val intent = Intent(context, PermissionManagerActivity::class.java)
            intent.putExtra(PERMISSION, permission)
            intent.putExtra(MUST_BE_AUTH, mustBeAuth)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            context.startActivity(intent)
            if (context is Activity) {
                context.overridePendingTransition(0, 0)
            }
        }

        /**
         * ActivityCompat.shouldShowRequestPermissionRationale()
         * 上面这个方法, 第一次调用, 返回 false, 然后去请求权限, 用户选择了 "拒绝", 会返回true, 这个
         * 时候再请求一次权限, 用户 勾选了 "不再询问" 并 选择了 "拒绝", 会返回 false.
         * 这时系统授权对话框就不会再弹出, 所以直接跳转到应用设置页面, 让用户去操作权限.
         *
         *
         * 大致上就是 false --> true --> false
         *
         *
         * 所以这里用来记录 这个权限 是否 返回过 true
         *
         * @param permission
         * @param isShowExplain
         */
        fun putWasShowSettingDialog(ctx: Context, permission: String, isShowExplain: Boolean) {
            val sp = ctx.getSharedPreferences(
                    PERMISSION_SHARED_PREFERENCES,
                Context.MODE_PRIVATE
            )
            val edit = sp.edit()
            edit.putBoolean(permission, isShowExplain)
            edit.commit()
        }

        fun getWasShowSettingDialog(ctx: Context, permission: String): Boolean {
            val sp = ctx.getSharedPreferences(
                    PERMISSION_SHARED_PREFERENCES,
                Context.MODE_PRIVATE
            )
            return sp.getBoolean(permission, false)
        }

        private var sReceiver: MutableMap<Context, MutableList<PermissionManagerBroadcastReceiver>>? = null

        private fun registerReceiver(context: Context, onEvent: OnPermissionManagerEvent) {
            if (sReceiver == null) {
                sReceiver = HashMap()
            }

            sReceiver?.let {
                val receiver = PermissionManagerBroadcastReceiver()
                receiver.setOnPermissionManagerEvent(onEvent)

                LocalBroadcastManager.getInstance(context).registerReceiver(
                    receiver,
                    IntentFilter(ACTION_PERMISSION_MANAGER_BROADCAST_RECEIVER)
                )

                if (it.containsKey(context)) {
                    it[context]!!.add(receiver)
                } else {
                    it[context] = arrayListOf(receiver)
                }
            }

        }

        private fun unregisterReceiver() {
            sReceiver?.let { map ->
                map.forEach {
                    val context = it.key
                    val receivers = it.value

                    for (receiver in receivers) {
                        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
                    }
                }
            }

            sReceiver = null
        }
    }
}