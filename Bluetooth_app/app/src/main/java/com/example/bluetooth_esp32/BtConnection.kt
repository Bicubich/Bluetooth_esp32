package com.example.bluetooth_esp32

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Handler
import android.util.Log

class BtConnection(private val adapter: BluetoothAdapter, val intput_handler: Handler, val handler_bt_connection: Handler) {
    private var cThread: ConnectThread? = null
    private var isSocketStatus = false
    private var currentDevice: BluetoothDevice? = null

    fun connect(mac: String) {
            if (adapter.isEnabled && mac.isNotEmpty()) {
                val device = adapter.getRemoteDevice(mac)
                device.let {
                    if (cThread == null) {
                        cThread = ConnectThread(intput_handler, handler_bt_connection)
                        cThread?.start()
                        currentDevice = device
                        Log.d("MyLog", "Thread Started")
                    }

                    if (currentDevice?.address != device?.address) {
                        cThread?.closeConnection()
                        cThread!!.threadIsActive = false
                        currentDevice = device
                    }

                    if (!cThread?.threadIsActive!!) {
                        cThread?.openConnection(device)
                        cThread!!.threadIsActive = true
                    }
                    else {
                        cThread?.closeConnection()
                        cThread!!.threadIsActive = false
                    }
                    isSocketStatus = cThread!!.returnSocketStatus()
                    }
            }
    }

    fun sendMessage(message: String){
        cThread?.rThread?.sendMessage(message.toByteArray())
    }

    fun returnSocketStatus(): Boolean{
       return cThread?.threadIsActive!!
    }
}