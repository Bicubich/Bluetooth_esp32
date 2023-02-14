package com.example.bluetooth_esp32

import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class ReceiveThread(bSocket: BluetoothSocket) : Thread(){
    var outStream: OutputStream? = null
    var inStream: InputStream? = null
    var inputMsg: String = ""

    init {
        try {
            outStream = bSocket.outputStream
        } catch (i: IOException){
            Log.d("MyLog", "[ReceiveThread] OutputStream warning")
        }
        try {
            inStream = bSocket.inputStream
        } catch (i: IOException){
            Log.d("MyLog", "[ReceiveThread] InputStream warning")
        }
    }

    override fun run() {
        val buffer = ByteArray(1024) // buffer store for the stream
        var bytes: Int // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = inStream!!.read(buffer)
                Log.d("MyLog", "Message: ${buffer.contentToString()}")
                // Send the obtained bytes to the UI activity
                //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                //    .sendToTarget()
            } catch (e: IOException) {
                Log.d("MyLog", "The message was not received")
                break
            }
        }
    }

    fun sendMessage(byteArray: ByteArray){
        try {
            outStream?.write(byteArray)
        } catch (i: IOException){
            Log.d("MyLog", "Failed to send a message: " + i.stackTraceToString())
        }
    }
}