package com.example.bluetooth_esp32

import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
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
        val buffer = ByteArray(256) // buffer store for the stream
        var bytes = 0 // bytes returned from read()
        var message = ""

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = inStream!!.read(buffer, 0, inStream!!.available())
                message = String(buffer, 0, bytes)
            } catch (e: IOException) {
                Log.d("MyLog", "The message was not received")
                break
            }
            if (bytes > 0) {
                Log.d("MyLog", "Message: $message [$bytes]")
                inputMsg = message
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

    fun returnMessage(): String{
        return inputMsg
    }
}