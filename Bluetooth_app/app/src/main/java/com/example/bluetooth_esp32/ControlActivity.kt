package com.example.bluetooth_esp32

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.bluetooth_esp32.databinding.ActivityControlBinding

class ControlActivity : AppCompatActivity() {
    private lateinit var binding: ActivityControlBinding
    private lateinit var actListLauncher: ActivityResultLauncher<Intent>
    lateinit var btConnection: BtConnection
    private var listItem: ListItem? = null

    private var spinnerAdapter: ArrayAdapter<String>? = null

    private var pref: SharedPreferences? = null
    var commands: Array<String> = arrayOf()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBtListResult()
        init()
        initMemoryData()
        initSpinner()
        binding.bSendMessage.setOnClickListener{
            var msg = binding.etMessage.text.toString()
            // we save the command when it is not in the last 10 in memory
            if (!isContainsComand(msg))
                saveData(msg)
            btConnection.sendMessage(msg)
            binding.tvChatHostory.text.toString().contains("\n")
            binding.tvChatHostory.text = binding.tvChatHostory.text.toString() + "\nUser: $msg"
            binding.etMessage.setText("")
        }
    }

    private fun init(){
        val btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val btAdapter = btManager.adapter
        btConnection = BtConnection(btAdapter)
        //binding.bSendMessage.isEnabled = false
        //binding.etMessage.isEnabled = false
        pref = getSharedPreferences("TABLE", Context.MODE_PRIVATE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.control_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_list){
            actListLauncher.launch(Intent(this, BtListActivity::class.java))
        } else if(item.itemId == R.id.id_connect){
            listItem.let {
                if (it?.mac != null) {
                    btConnection.connect(it.mac!!)

                    if (btConnection.returnSocketStatus()){
                        binding.bSendMessage.isEnabled = true
                        binding.etMessage.isEnabled = true
                    }
                    else{
                        binding.bSendMessage.isEnabled = false
                        binding.etMessage.isEnabled = false
                    }

                }
                else{
                    Log.d("MyLog", "Sync status: No device has been selected yet")
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onBtListResult(){
        actListLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == RESULT_OK){
                listItem = it.data?.getSerializableExtra(BtListActivity.DEVICE_KEY) as ListItem
                binding.tvCurrentDevice.text =  "Current device: " + listItem!!.name
            }
        }
    }

    // saving commands in the phone memory
    private fun saveData(comm: String){
        replaceData()
        val editor = pref?.edit()
        editor?.putString("C1", comm)
        editor?.apply()

        val keys: Map<String, *> = pref?.all as Map<String, *>
        for ((key, value) in keys) {

            Log.d(
                "MyLog", key + ": " +
                        value.toString()
            )

        }

        commands = getSpinnerData()
        initSpinner()

        editor?.apply()
    }

    // function initializes keys in phone memory
    private fun initMemoryData(){
        // check memory sheet
        if (!pref?.contains("C1")!!) {
            val editor = pref?.edit()
            editor?.putString("C1", "")
            editor?.putString("C2", "")
            editor?.putString("C3", "")
            editor?.putString("C4", "")
            editor?.putString("C5", "")
            editor?.putString("C6", "")
            editor?.putString("C7", "")
            editor?.putString("C8", "")
            editor?.putString("C9", "")
            editor?.putString("C10", "")
            editor?.apply()
        }
        else{
            commands = getSpinnerData()
        }
    }

    // function moves the elements forward by one position
    private fun replaceData(){
        var bufLastCommand = ""
        var bufNextCommand = ""

        val memoryCell = MemoryCell()

        val editor = pref?.edit()
        for (mCell in memoryCell.listSheets) {
            bufLastCommand = pref?.getString(mCell, "").toString()
            editor?.putString(mCell, bufNextCommand)
            bufNextCommand = bufLastCommand
        }
        editor?.apply()
    }

    // function check repeat commands in spinner
    fun isContainsComand(comm: String): Boolean{
        val keys: Map<String, *> = pref?.all as Map<String, *>
        for ((key, value) in keys) {
            if (comm == value.toString())
                return true
        }
        return false
    }

    // function return actual saved commands to spinner's data array
    private fun getSpinnerData(): Array<String> {
        val listCommand: MutableList<String> = mutableListOf()
        val memoryCell = MemoryCell()
        var value = ""
        for (mCell in memoryCell.listSheets) {
            value = pref?.getString(mCell, "").toString()
            if (value != "")
                listCommand.add(value.toString())
        }

        commands = listCommand.toTypedArray()

        return commands
    }

    // initialization spinner
    private fun initSpinner() {
        spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, commands)
        binding.spinner.adapter = spinnerAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.etMessage.setText(commands[position].toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

    }
}