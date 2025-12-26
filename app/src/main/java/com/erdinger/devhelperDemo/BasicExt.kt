package com.erdinger.devhelperDemo

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity

fun showToast(content: String?){
    Toast.makeText(MyApp.instance, content, Toast.LENGTH_SHORT).show()
}

inline fun <reified T:ComponentActivity> launchActivity(context: Context){
    context.startActivity(Intent(context, T::class.java))
}