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
import com.erdinger.kuaijinhelper.recycler.currentAdapter
import com.erdinger.kuaijinhelper.recycler.emptyView
import com.erdinger.kuaijinhelper.recycler.itemDecoration
import com.erdinger.kuaijinhelper.recycler.linear
import com.erdinger.kuaijinhelper.recycler.onItemClick
import com.erdinger.kuaijinhelper.recycler.submitLiveData
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



        binding.recycler
            .linear()
            .currentAdapter<BluetoothDevice, ListItemBinding> { itemBinding, position, item ->
                itemBinding.text.text = "${item?.name}\n${item?.address}"
            }
            .onItemClick<BluetoothDevice> { position, item ->
                item.apply {
                    val remoteDevice = bluetoothAdapter?.getRemoteDevice(address)
                    if (remoteDevice?.createBond() == true) {

                        val socket = remoteDevice.createRfcommSocketToServiceRecord(uuidService)
                        Thread {
                            try {
                                socket?.connect()
                                while (true) {
                                    socket?.outputStream?.write("hellow world".toByteArray())
                                    Thread.sleep(3000)
                                }
                            } catch (e: Exception) {
                                socket?.close()
                                runOnUiThread { showToast("连接失败") }

                            }
                        }.start()
                    } else {
                        showToast("配对失败")
                    }
                }
            }
            .itemDecoration(5F, 16F, R.color.red)
            .emptyView(R.layout.empty_view)
            .submitLiveData(this, bleDevices)

        binding.reachList.setOnClickListener {
            launchActivity<AccessibilityServiceActivity>(this)
        }
        binding.stopList.setOnClickListener {
            bluetoothAdapter?.cancelDiscovery()
        }

        registerReceiver(receiver, IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        })

    }

    var isService = true

    @SuppressLint("MissingPermission")
    private fun startBle() {
        bluetoothAdapter =
            getSystemService(Context.BLUETOOTH_SERVICE).safeAs<BluetoothManager>().adapter
        if (bluetoothAdapter!!.isEnabled.not()) {
            startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        } else if (bluetoothAdapter?.scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                // 0为一直开启 其他数值为开始时间最多 300秒
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0)
            })
        } else {
//                    val value = bleDevices.value
//                    bluetoothAdapter?.bondedDevices?.apply {
//                        value?.addAll(this)
//                    }
//                    bleDevices.postValue(value)
            if (isService) {
                bluetoothAdapter?.listenUsingRfcommWithServiceRecord("我的藍牙", uuidService)
                    ?.apply {
                        accept().apply {
                            Thread {
                                while (true) {
                                    try {
                                        val buffer = ByteArray(1024)
                                        val read = inputStream.read(buffer)
                                        runOnUiThread {
                                            if (read > 0) showToast(String(buffer))
                                        }
                                    } catch (e: Exception) {
                                        close()
                                        runOnUiThread {
                                            showToast("连接断开")
                                        }
                                    }
                                    Thread.sleep(3000)
                                }
                            }.start()
                        }
                    }
            } else {
                bluetoothAdapter!!.startDiscovery()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    fun showToast(content: String) {
        Toast.makeText(this@MainActivity, content, Toast.LENGTH_SHORT).show()
    }
}