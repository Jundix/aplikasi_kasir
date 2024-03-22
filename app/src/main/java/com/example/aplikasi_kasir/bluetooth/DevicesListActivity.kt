package com.example.aplikasi_kasir.bluetooth

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.example.aplikasi_kasir.R
import com.example.aplikasi_kasir.appSettingOpen
import com.example.aplikasi_kasir.warningPermissionDialog
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.UUID


class DevicesListActivity : Activity() {

    private var btAdapter: BluetoothAdapter? = null
    lateinit var btn_scan: Button
    lateinit var btn_on: Button
    lateinit var list: ListView
    private val permission = 14
    private var arrayAdapter: ArrayAdapter<String>? = null
    private var btSocket: BluetoothSocket? = null
    private val BT_MODULE_UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val CONNECTING_STATUS = 3
    private var mConnectedThread: ConnectedThread? = null
    private var mHandler: Handler? = null


    private val listPermission = if (Build.VERSION.SDK_INT >= 30) {
        arrayListOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_ADVERTISE,
//            android.Manifest.permission.ACCESS_FINE_LOCATION,
//            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    } else {
        arrayListOf(
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
//            android.Manifest.permission.ACCESS_FINE_LOCATION,
//            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices_list)
        btn_scan=findViewById(R.id.btn_scan)
        btn_on=findViewById(R.id.btn_on)
        list=findViewById(R.id.list)
        btAdapter= BluetoothAdapter.getDefaultAdapter()

        if (btAdapter==null){
            Toast.makeText(this, "Bluetooth tidak support", Toast.LENGTH_SHORT).show()
            finish()
        }

        mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                if (msg.what === MESSAGE_READ) {
                    var readMessage: String? = null
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        readMessage = String((msg.obj as ByteArray), StandardCharsets.UTF_8)
                    }
                }
                if (msg.what === CONNECTING_STATUS) {
                    if (msg.arg1 === 1) {
                        Toast.makeText(
                            applicationContext,
                            "Connected to " + msg.obj,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(applicationContext, "Connection Failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }


        btn_on.setOnClickListener {
            if (btAdapter!!.isEnabled){
                listDevice()
            }else{
//                val job =   GlobalScope.launch {
//
//                }
                checkMultiplePermission()
                val onBT= Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(onBT,0)
            }
        }

        btn_scan.setOnClickListener {
//            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
//            registerReceiver(receiver, filter)
//            btAdapter!!.startDiscovery()
            if (btSocket != null) {
                try {
                    // Create the command that will be sent to arduino.
                    // String must be converted in its bytes to be sent on serial
                    // communication
                    val printer = EscPosPrinter(
                        BluetoothPrintersConnections.selectFirstPaired(),
                        203,
                        48f,
                        32
                    )
                    printer
                        .printFormattedText(
                            "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
                                    "[L]\n" +
                                    "[C]================================\n" +
                                    "[L]\n" +
                                    "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n" +
                                    "[L]\n" +
                                    "[C]--------------------------------\n" +
                                    "[R]TOTAL PRICE :[R]34.98e\n" +
                                    "[L]\n" +
                                    "[C]================================\n" +
                                    "[L]\n" +
                                    "[C]<u><font size='normal'>Terima Kasih</font></u>\n"+
                                    "[L]\n" +
                                    "[C]<u><font size='normal'>Telah Belanja Di sini </font></u>\n"
                        )
                } catch (e: IOException) {
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val Message: String? = "hehehe"
        if (requestCode == 0){
            listDevice()

        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    val list_device: ArrayList<String> = ArrayList()
                    list_device.add("Name: " + deviceName + "MAC Address: " + deviceHardwareAddress)
                    arrayAdapter = ArrayAdapter<String>(
                        applicationContext,
                        android.R.layout.simple_list_item_1,
                        list_device
                    )
                    list.adapter=arrayAdapter
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permission) {
            if (grantResults.isNotEmpty()) {
                var isGrant = true
                for (element in grantResults) {
                    if (element == PackageManager.PERMISSION_DENIED) {
                        isGrant = false
                    }
                }
                if (isGrant) {
                    // here all permission granted successfully

                } else {
                    var someDenied = false
                    for (permission in permissions) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                permission
                            )
                        ) {
                            if (ActivityCompat.checkSelfPermission(
                                    this,
                                    permission
                                ) == PackageManager.PERMISSION_DENIED
                            ) {
                                someDenied = true
                            }
                        }
                    }
                    if (someDenied) {
                        // here app Setting open because all permission is not granted
                        // and permanent denied
                        appSettingOpen(this)
                    } else {
                        // here warning permission show
                        warningPermissionDialog(this) { _: DialogInterface, which: Int ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE ->
                                    checkMultiplePermission()
                            }
                        }
                    }
                }
            }
        }

    }

    private fun checkMultiplePermission(): Boolean {
        val listPermissionNeeded = arrayListOf<String>()
        for (permission in listPermission) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionNeeded.add(permission)
            }
        }
        if (listPermissionNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionNeeded.toTypedArray(),
                permission
            )
            return false
        }
        return true
    }

    @Throws(IOException::class)
    private fun createBluetoothSocket(device: BluetoothDevice): BluetoothSocket? {
        checkMultiplePermission()
        try {
            val m = device.javaClass.getMethod(
                "createInsecureRfcommSocketToServiceRecord",
                UUID::class.java
            )
            return m.invoke(device, BT_MODULE_UUID) as BluetoothSocket
        } catch (e: Exception) {
            Log.e("message", "Could not create Insecure RFComm Connection", e)
        }

        return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID)
    }

    fun listDevice(){
        val builderSingle: AlertDialog.Builder = AlertDialog.Builder(this)
        builderSingle.setIcon(R.drawable.ic_android_black_24dp)
        builderSingle.setTitle("Select One Name:-")
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice)
        val pairedDevices: Set<BluetoothDevice> = btAdapter!!.getBondedDevices()

        for (device in pairedDevices) {
            val devicename = device.name
            val macAddress = device.address
            arrayAdapter.add("Name: " + devicename + "MAC Address: " + macAddress)
        }
        builderSingle.setNegativeButton(
            "cancel"
        ) { dialog, which -> dialog.dismiss() }
        builderSingle.setAdapter(arrayAdapter,
            DialogInterface.OnClickListener { dialog, which ->
                if (!btAdapter!!.isEnabled()) {
                    Toast.makeText(baseContext, "Bluetooth Belum Aktif", Toast.LENGTH_SHORT)
                        .show()
                    return@OnClickListener
                }
                val strName = arrayAdapter.getItem(which)
                val address = strName!!.substring(strName!!.length - 17)
                val name = strName!!.substring(0, strName!!.length - 17)

                object : Thread() {
                    override fun run() {
                        var fail = false
                        val device: BluetoothDevice = btAdapter!!.getRemoteDevice(address)
                        try {
                            btSocket = createBluetoothSocket(device)
                        } catch (e: IOException) {
                            fail = true
                            Toast.makeText(
                                baseContext,
                                "error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        try {
                            checkMultiplePermission()
                            btSocket!!.connect()
                        } catch (e: IOException) {
                            try {
                                fail = true
                                btSocket?.close()
                                mHandler!!.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget()
                            } catch (e2: IOException) {
                                //insert code to deal with this
                                Toast.makeText(
                                    baseContext,
                                    "error",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        if (!fail) {
                            dialog.dismiss()
                            mConnectedThread = ConnectedThread(btSocket!!, mHandler!!)
                            mConnectedThread!!.start()
                            mHandler!!.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                        }
                    }
                }.start()
            })
        builderSingle.show()
    }

    companion object {
        val MESSAGE_READ: Int = 2

    }

}
