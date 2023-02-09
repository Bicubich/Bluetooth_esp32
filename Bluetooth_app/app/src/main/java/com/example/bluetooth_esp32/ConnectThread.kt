package com.example.bluetooth_esp32

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class ConnectThread(private val device: BluetoothDevice): Thread() {
    val uuid = "11010000-0000-1000-8000-00805F9B34FB"
    var mySocked: BluetoothSocket? = null
    lateinit var rThread: ReceiveThread

    init {
        try {
            mySocked = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuid))
        } catch (i: IOException){
            Log.d("MyLog", "ConnectThread init warning")
        }
    }

    override fun run() {
        super.run()
        try {
            Log.d("MyLog", "Connecting...")
            mySocked?.connect()
            Log.d("MyLog", "Connected")
            rThread = ReceiveThread(mySocked!!)
            rThread.start()
        } catch (i: IOException){
            Log.d("MyLog", i.stackTraceToString())
            Log.d("MyLog", "Can not connect to device")
            Log.d("MyLog", "Trying one more time...")

            try {
                mySocked = device.javaClass.getMethod(
                    "createRfcommSocket", *arrayOf<Class<*>?>(
                        Int::class.javaPrimitiveType
                    )
                ).invoke(device, 1) as BluetoothSocket
                mySocked?.connect()
                Log.d("MyLog", "Connected")
                rThread = ReceiveThread(mySocked!!)
                rThread.start()
            } catch (i: IOException) {
                Log.d("MyLog", i.stackTraceToString())
                Log.d("MyLog", "Finaly: Can not connect to device")
                closeConnection()
            }
        }
    }

    fun closeConnection(){
        try {
            mySocked?.close()
        } catch (i: IOException){
            Log.d("MyLog", "ConnectThread closeConnection() warning")
        }
    }
}