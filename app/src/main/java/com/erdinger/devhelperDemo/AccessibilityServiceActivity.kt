package com.erdinger.devhelperDemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.erdinger.devhelperDemo.accessibility.DouYInService
import com.erdinger.devhelperDemo.databinding.ActivityAsBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class AccessibilityServiceActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.reachList.setOnClickListener {
            if (checkEnabled(this).not()){
                showToast("未开启，去开启")
                try {
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }catch (e: Exception){
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                }
            }else if (checkTouch(this).not()){
                showToast("未开启触摸，去开启")
                try {
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }catch (e: Exception){
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                }
            }else if (DouYInService.isStart.not()){
                try {
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }catch (e: Exception){
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                }
            }

        }
    }

    //是否启用无障碍服务
    private fun checkEnabled(context: Context): Boolean {
        //获取无障碍管理器实例
        val manager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager?
        //是否启用无障碍服务
        return manager!!.isEnabled
    }

    //是否启用触摸浏览
    private fun checkTouch(context: Context): Boolean {
        //获取无障碍管理器实例
        val manager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager?
        //是否启用触摸浏览
        return manager!!.isTouchExplorationEnabled
    }



    class ActivityViewBindingProperty<in A: ComponentActivity, out V: ViewBinding>(): ReadOnlyProperty<ComponentActivity,V>{



        override operator fun getValue(thisRef: ComponentActivity, property: KProperty<*>): V {
            TODO("Not yet implemented")
        }

    }

}