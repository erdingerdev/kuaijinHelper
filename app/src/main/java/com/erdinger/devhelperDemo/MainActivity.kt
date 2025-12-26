package com.erdinger.devhelperDemo

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import com.erdinger.kuaijinhelper.basic.safeAs
import com.erdinger.devhelperDemo.databinding.ActivityMainBinding
import com.erdinger.devhelperDemo.databinding.ListItemBinding
import java.util.UUID


class MainActivity : AppCompatActivity() {

    val bleDevices = MutableLiveData(mutableListOf<BluetoothDevice>())
    private var bluetoothAdapter: BluetoothAdapter? = null
    val uuidService = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == BluetoothDevice.ACTION_FOUND) {
                intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)?.apply {
                    val currentValue = bleDevices.value
                    currentValue?.add(this)
                    bleDevices.postValue(currentValue)
//                    if (this.bondState != BluetoothDevice.BOND_BONDED){
//                    }
                }
            } else if (intent?.action == BluetoothAdapter.ACTION_DISCOVERY_STARTED) {
                Toast.makeText(this@MainActivity, "开始扫描", Toast.LENGTH_SHORT).show()
            } else if (intent?.action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
                Toast.makeText(this@MainActivity, "结束扫描", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        registerReceiver(receiver, IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        })

    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

}