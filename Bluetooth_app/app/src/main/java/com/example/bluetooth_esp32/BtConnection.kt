package com.example.bluetooth_esp32

import android.bluetooth.BluetoothAdapter
import android.util.Log

class BtConnection(private val adapter: BluetoothAdapter) {
    private var cThread: ConnectThread? = null
    private var isSocketStatus = false
    fun connect(mac: String) {
            if (adapter.isEnabled && mac.isNotEmpty()) {
                val device = adapter.getRemoteDevice(mac)
                device.let {
                    if (cThread != null){
                        if (!cThread?.isAlive!!){
                            cThread?.start()
                            Log.d("MyLog", "Thread Start")
                        }
                        else{
                            cThread?.closeConnection()
                            Log.d("MyLog", "Thread Close")
                        }
                        isSocketStatus = cThread!!.returnSocketStatus()
                    }
                    else{
                        cThread = ConnectThread(device)
                        cThread?.start()
                        Log.d("MyLog", "Thread Start")
                        isSocketStatus = cThread!!.returnSocketStatus()
                    }

                }
            }
    }

    fun sendMessage(message: String){
        cThread?.rThread?.sendMessage(message.toByteArray())
    }

    fun returnSocketStatus(): Boolean{
       return isSocketStatus
    }
}