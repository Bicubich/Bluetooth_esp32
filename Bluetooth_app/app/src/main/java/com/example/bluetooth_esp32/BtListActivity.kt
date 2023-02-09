package com.example.bluetooth_esp32

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluetooth_esp32.databinding.ActivityMainBinding

class BtListActivity() : AppCompatActivity(), RcAdapter.Listener {

    private var btAdapter: BluetoothAdapter? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermissions()
        init()
    }

    private fun init(){
        val btManager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager.adapter
        adapter = RcAdapter(this)
        binding.rcView.layoutManager = LinearLayoutManager(this)
        binding.rcView.adapter = adapter
        getPairedDevices()
    }

    @SuppressLint("MissingPermission")
    private fun getPairedDevices() {
        if (!checkPermissions()){
            val pairedDevices: Set<BluetoothDevice>? = btAdapter?.bondedDevices
            val tempList = ArrayList<ListItem>()
            pairedDevices?.forEach {
                tempList.add(
                    ListItem(
                        it.name,
                        it.address
                    )
                )
            }
            adapter.submitList(tempList)
        }
        else
            requestPermissions()
    }

    private fun requestPermissions(){
        if (checkPermissions())
            if (isModernSDK())
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.ACCESS_FINE_LOCATION), 10)
            else
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 10)
    }

    private fun checkPermissions(): Boolean{
        var bl_conn = false;
        var cpar_loc = false;

        if (isModernSDK()){
            bl_conn = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED

            cpar_loc = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED

            return bl_conn || cpar_loc

            //return (ActivityCompat.checkSelfPermission(
            //        this,
            //        Manifest.permission.BLUETOOTH_CONNECT
            //    ) != PackageManager.PERMISSION_GRANTED
            //            ) && (ActivityCompat.checkSelfPermission(
            //        this,
            //        Manifest.permission.ACCESS_COARSE_LOCATION
            //    ) != PackageManager.PERMISSION_GRANTED)
        }
        else{
            return (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        }
    }

    private fun isModernSDK() : Boolean{
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    }

    companion object{
        const val DEVICE_KEY = "device_key"
    }

    override fun onClick(item: ListItem) {
        val i = Intent().apply {
            putExtra(DEVICE_KEY, item)
        }
        setResult(RESULT_OK, i)
        finish()
    }
}