package com.example.bluetooth_esp32

import android.bluetooth.BluetoothAdapter

class BtConnection(val adapter: BluetoothAdapter) {
    private var cThread: ConnectThread? = null
    fun connect(mac: String) {
        // Bluetooth is enabled
        if (adapter.isEnabled && mac.isNotEmpty()) {
            val device = adapter.getRemoteDevice(mac)
            device.let {
                cThread = ConnectThread(device)
                cThread?.start()
            }
        }
    }

    fun sendMessage(message: String){
        cThread?.rThread?.sendMessage(message.toByteArray())
    }
}