package com.erdinger.devhelperDemo.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.erdinger.devhelperDemo.showToast

class DouYInService: AccessibilityService() {

    val TAG = "DouYInService"

    companion object{
        var mService:DouYInService? = null
        var isStart = mService != null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        mService = this
        showToast("服务连接")
    }

    private var isClickAdd = false

    //通过 AccessibilityService 点击屏幕底部中间的位置

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val nodeInfo = rootInActiveWindow
        if (isClickAdd.not()){
            val byViewId = nodeInfo.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/yp_")?.firstOrNull()
            byViewId?.apply {
                isClickAdd = true
                showToast("模拟点击")
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }

        event?.apply {
            when(eventType){
                //通知栏消息
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {

                }

            }


        }

    }

    override fun onInterrupt() {
        showToast("服务中断")
        mService = null
    }

    override fun onDestroy() {
        super.onDestroy()
        showToast("服务关闭")
        mService = null
    }


}