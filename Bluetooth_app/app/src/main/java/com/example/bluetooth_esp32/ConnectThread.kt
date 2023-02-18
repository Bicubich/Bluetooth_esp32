package com.example.bluetooth_esp32

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class ConnectThread(val intput_handler: Handler, val handler_bt_connection: Handler): Thread() {
    val uuid = "11010000-0000-1000-8000-00805F9B34FB"
    var mySocked: BluetoothSocket? = null
    var threadIsActive = false
    var deviceName = ""
    var rThread: ReceiveThread? = null

    private lateinit var sendBtn: Button
    private lateinit var chatText: EditText

    var handler_connection = handler_bt_connection

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
            if (rThread == null) {
                rThread = ReceiveThread(mySocked!!, intput_handler)
            }
            rThread!!.start()
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
                if (rThread == null) {
                    rThread = ReceiveThread(mySocked!!, intput_handler)
                }
                rThread!!.start()
            } catch (i: IOException) {
                Log.d("MyLog", "Bluetooth connection failed: " + i.stackTraceToString())
                closeConnection()
            }
        }
    }

    override fun run() {
        while (true) {
            sleep(500)
            if(rThread != null && !rThread?.connectionState!!) {
                closeConnection()
            }
        }
    }

    fun closeConnection(){
        try {
            mySocked?.close()
            rThread?.interrupt()
            rThread = null
            handler_bt_connection.sendMessage(Message.obtain())
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