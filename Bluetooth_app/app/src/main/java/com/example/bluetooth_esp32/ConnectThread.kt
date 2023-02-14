package com.example.bluetooth_esp32

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.example.bluetooth_esp32.databinding.ActivityControlBinding
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class ConnectThread(): Thread() {
    val uuid = "11010000-0000-1000-8000-00805F9B34FB"
    var mySocked: BluetoothSocket? = null
    var threadIsActive = false
    var deviceName = ""
    lateinit var rThread: ReceiveThread

    init {
    }

    fun openConnection(device: BluetoothDevice){
        deviceName = device.name
        try {
            try {
                mySocked = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuid))
            } catch (i: IOException){
                Log.d("MyLog", "ConnectThread init warning")
            }
            Log.d("MyLog", "Connecting to $deviceName ...")
            mySocked?.connect()
            Log.d("MyLog", "Connected to $deviceName")
            rThread = ReceiveThread(mySocked!!)
            rThread.start()
        } catch (i: IOException){
            Log.d("MyLog", i.stackTraceToString())
            Log.d("MyLog", "Trying one more time...")
            try {
                mySocked = device.javaClass.getMethod(
                    "createRfcommSocket", *arrayOf<Class<*>?>(
                        Int::class.javaPrimitiveType
                    )
                ).invoke(device, 1) as BluetoothSocket
                mySocked?.connect()
                Log.d("MyLog", "Connected to $deviceName")
                rThread = ReceiveThread(mySocked!!)
                rThread.start()
            } catch (i: IOException) {
                Log.d("MyLog", "Bluetooth connection failed: " + i.stackTraceToString())
                closeConnection()
            }
        }
    }

    fun closeConnection(){
        try {
            mySocked?.close()
            Log.d("MyLog", "Connection with $deviceName closed")
        } catch (i: IOException){
            Log.d("MyLog", "Bluetooth disconnection failed: " + i.stackTraceToString())
        }
        threadIsActive = false
    }

    fun returnSocketStatus(): Boolean{
        if (mySocked != null)
            return mySocked!!.isConnected
        return false
    }
}