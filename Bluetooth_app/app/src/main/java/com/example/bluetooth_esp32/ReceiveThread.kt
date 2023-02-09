package com.example.bluetooth_esp32

import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.OutputStream

class ReceiveThread(bSocket: BluetoothSocket) : Thread(){
    var outStream: OutputStream? = null

    init {
        try {
            outStream = bSocket.outputStream
        } catch (i: IOException){
            Log.d("MyLog", "ReceiveThread init warning")
        }
    }

    override fun run() {
        super.run()
    }

    fun sendMessage(byteArray: ByteArray){
        try {
            outStream?.write(byteArray)
        } catch (i: IOException){
            Log.d("MyLog", "ReceiveThread sendMessage() warning")
        }
    }
}