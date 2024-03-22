package com.example.aplikasi_kasir.ui.laporan

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.example.aplikasi_kasir.MainActivity
import com.example.aplikasi_kasir.R
import com.example.aplikasi_kasir.adapter.laporan.DetailLaporanAdapter
import com.example.aplikasi_kasir.adapter.laporan.DetailLaporanInterface
import com.example.aplikasi_kasir.appSettingOpen
import com.example.aplikasi_kasir.bluetooth.ConnectedThread
import com.example.aplikasi_kasir.bluetooth.DevicesListActivity
import com.example.aplikasi_kasir.database.DBHandler
import com.example.aplikasi_kasir.database.DetailLaporanModel
import com.example.aplikasi_kasir.warningPermissionDialog
import com.pranavpandey.android.dynamic.util.DynamicUnitUtils
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID
import kotlin.math.round


class DetailLaporanActivity : AppCompatActivity(), DetailLaporanInterface.DetailLaporanInterface {

    lateinit var recyclerView: RecyclerView
    lateinit var detailLaporanAdapter: DetailLaporanAdapter
    lateinit var btn_print: Button
    lateinit var btn_kembali: Button
    lateinit var btn_hitung: Button
    lateinit var txt_total: TextView
    lateinit var txt_kembalian: TextView
    lateinit var txt_pembayaran: TextView
    lateinit var edt_uang: EditText
    val REQUEST_CODE = 200
    private var btAdapter: BluetoothAdapter? = null
    lateinit var list: ListView
    private val permission = 14
    private var arrayAdapter: ArrayAdapter<String>? = null
    private var btSocket: BluetoothSocket? = null
    private val BT_MODULE_UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val CONNECTING_STATUS = 3
    private var mConnectedThread: ConnectedThread? = null
    private var mHandler: Handler? = null
    lateinit var print_content: String
    lateinit var uang:String
    private lateinit var sharedPreferences: SharedPreferences
    var PREFS_KEY = "prefs"
    var IS_LOGIN = "is_login"
    var NAMA = "nama"
    var ALAMAT = "alamat"
    var NO_HP = "no_hp"
    lateinit var txt_alamat: TextView
    lateinit var txt_no_hp: TextView
    lateinit var header: Bitmap

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
        setContentView(R.layout.activity_detail_laporan)
        setupRecyclerview()
        content()
        print_content = content().joinToString(separator = "\n")

        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        formatRupiah.maximumFractionDigits = 0

        val total_order: Int = intent.getIntExtra("total_order",0)
        val request_code: Int = intent.getIntExtra("REQUEST_CODE",0)
        sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        txt_total=findViewById(R.id.txt_total_order)
        btn_kembali=findViewById(R.id.btn_kembali)
        btn_print=findViewById(R.id.btn_print)
        btn_hitung=findViewById(R.id.btn_hitung)
        txt_pembayaran=findViewById(R.id.txt_pembayaran)
        txt_kembalian=findViewById(R.id.txt_kembalian)
        edt_uang=findViewById(R.id.edt_uang)
        btAdapter= BluetoothAdapter.getDefaultAdapter()
        txt_total.setText(formatRupiah.format(round(total_order.toDouble())))

        val alamat: String = sharedPreferences.getString(ALAMAT, null)!!
        val no_hp: String = sharedPreferences.getString(NO_HP, null)!!

        val view: View = View.inflate(this, R.layout.activity_header_print, null);
        val linearLayout: LinearLayout = view.findViewById(R.id.header_print)
        txt_alamat = view.findViewById(R.id.alamat)
        txt_no_hp = view.findViewById(R.id.nomor_hp)

        txt_alamat.setTypeface(txt_alamat.getTypeface(), Typeface.BOLD)
        txt_no_hp.setTypeface(txt_no_hp.getTypeface(), Typeface.BOLD)

        txt_alamat.setText(alamat)
        txt_no_hp.setText(no_hp)

        header = createBitmapFromView(linearLayout,450,150)

//        val image: ImageView =  findViewById(R.id.image)
//        image.setImageBitmap(header)

        val databaseHandler: DBHandler = DBHandler(this)

        val id: Int = intent.getIntExtra("id", 0)
        val detailLaporan: List<DetailLaporanModel> = databaseHandler.detailLaporan(id)
        val nama_produk = Array<String>(detailLaporan.size) { "null" }
        val jumlah = Array<String>(detailLaporan.size) { "null" }
        var index = 0

        Log.d("message", txt_pembayaran.text.toString())

        btn_print.setOnClickListener {
            if (btAdapter!!.isEnabled){
                listDevice()

            }else{
                checkMultiplePermission()
                val onBT= Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(onBT,0)
            }
        }

        btn_kembali.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                if (msg.what === DevicesListActivity.MESSAGE_READ) {
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

        if(request_code == REQUEST_CODE){
            btn_hitung.isVisible=true
            edt_uang.isVisible=true

            edt_uang.addTextChangedListener(object : TextWatcher {
                private var current: String = ""
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s.toString() != current) {
                        edt_uang.removeTextChangedListener(this)
                        val cleanString: String = s.toString().replace("""[Rp,.]""".toRegex(), "")
                        if(cleanString.length>1){
                            val parsed = cleanString.toDouble()
                            val formatted = formatRupiah.format(round(parsed))
//                    formatRupiah.format(round(edt_harga.toString().toDouble()))
                            current = formatted
                            edt_uang.setText(formatted)
                            edt_uang.setSelection(formatted.length)

                            edt_uang.addTextChangedListener(this)
                        }else{
                            if(cleanString.length==0){
                                val zero = 0
                                val parsed = zero.toDouble()
                                val formatted = formatRupiah.format(round(parsed))
                                current = formatted
                                edt_uang.setText(formatted)
                                edt_uang.setSelection(formatted.length)

                                edt_uang.addTextChangedListener(this)
                            }else{
                                val zero = cleanString
                                val parsed = zero.toDouble()
                                val formatted = formatRupiah.format(round(parsed))
                                current = formatted
                                edt_uang.setText(formatted)
                                edt_uang.setSelection(formatted.length)

                                edt_uang.addTextChangedListener(this)
                            }

                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            btn_hitung.setOnClickListener {
                uang = edt_uang.text.toString().replace("""[Rp,.]""".toRegex(), "")
                val kembalian = Integer.parseInt(uang) - total_order
                Log.d("message", kembalian.toString())
                if(Integer.parseInt(uang)>total_order){
                    txt_kembalian.isVisible=true
                    txt_pembayaran.isVisible=true
                    txt_kembalian.setText("Kembalian: "+formatRupiah.format(round(kembalian.toString().toDouble())))
                    txt_pembayaran.setText("Uang dibayarkan: "+formatRupiah.format(round(uang.toDouble())))
                }else{
                    Toast.makeText(this, "Nominal Pembaaran Tidak Sesuai", Toast.LENGTH_SHORT).show()
                }

            }
        }

    }
    override fun onStart() {
        super.onStart()
        detailLaporan()
    }


    fun setupRecyclerview(){
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        detailLaporanAdapter = DetailLaporanAdapter()
        recyclerView.adapter = detailLaporanAdapter
    }

    fun detailLaporan(){
        recyclerView = findViewById(R.id.recyclerView)
        val id: Int = intent.getIntExtra("id",0)
        val databaseHandler: DBHandler= DBHandler(this)
        val detailLaporan = databaseHandler.detailLaporan(id)
        detailLaporanAdapter.notifyDataSetChanged()
        detailLaporanAdapter?.setData(detailLaporan)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0){
            listDevice()
        }
    }

    fun print(){
        val nama: String = sharedPreferences.getString(NAMA, null)!!
        val alamat: String = sharedPreferences.getString(ALAMAT, null)!!
        val no_hp: String = sharedPreferences.getString(NO_HP, null)!!
        val request_code: Int = intent.getIntExtra("REQUEST_CODE",0)
        val total_order: Int = intent.getIntExtra("total_order",0)
        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        formatRupiah.maximumFractionDigits = 0
        val databaseHandler: DBHandler = DBHandler(this)
        if (btSocket != null) {
            try {
                val printer = EscPosPrinter(
                    BluetoothPrintersConnections.selectFirstPaired(),
                    203,
                    48f,
                    32
                )
                if (request_code==200){
                    if(txt_pembayaran.text != "pembayaran"){
                        val kembalian = Integer.parseInt(uang) - total_order
                        printer
                            .printFormattedText(
                                "[C]<font size='tall'>"+nama+"</font>\n" +
                                        "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, header)+"</img>\n"+
                                        "[L]\n" +
                                        "[C]<font size='tall'><b>Detail Order</b></font>\n" +
                                        "[C]================================\n" +
                                        "[L]\n" +
                                        print_content+
                                        "[L]\n" +
                                        "[C]--------------------------------\n" +
                                        "[R]TOTAL ORDER:[R]"+formatRupiah.format(round(total_order.toDouble()))+"\n" +
                                        "[L]\n" +
                                        "[R]UANG DIBAYARKAN:[R]"+formatRupiah.format(round(uang.toDouble()))+"\n" +
                                        "[L]\n" +
                                        "[R]KEMBALIAN:[R]"+formatRupiah.format(round(kembalian.toDouble()))+"\n" +
                                        "[L]\n" +
                                        "[C]================================\n" +
                                        "[L]\n" +
                                        "[C]<font size='normal'>Terima Kasih</font>\n"+
                                        "[L]\n" +
                                        "[C]<font size='normal'>Telah Belanja Di sini </font>\n\n"+
                                        "[L]\n"
                            )
                    }else{
                        printer
                            .printFormattedText(
                                "[C]<font size='tall'>"+nama+"</font>\n" +
                                        "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, header)+"</img>\n"+
                                        "[L]\n" +
                                        "[C]<font size='tall'><b>Detail Order</b></font>\n" +
                                        "[C]================================\n" +
                                        "[L]\n" +
                                        print_content+
                                        "[L]\n" +
                                        "[C]--------------------------------\n" +
                                        "[R]TOTAL ORDER:[R]"+formatRupiah.format(round(total_order.toDouble()))+"\n" +
                                        "[L]\n" +
                                        "[C]================================\n" +
                                        "[L]\n" +
                                        "[C]<font size='normal'>Terima Kasih</font>\n"+
                                        "[L]\n" +
                                        "[C]<font size='normal'>Telah Belanja Di sini </font>\n\n"+
                                        "[L]\n"
                            )
                    }
                }else{
                    printer
                        .printFormattedText(
                            "[C]<font size='tall'>"+nama+"</font>\n" +
                                    "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, header)+"</img>\n"+
                                    "[L]\n" +
                                    "[C]<font size='tall'><b>Detail Order</b></font>\n" +
                                    "[C]================================\n" +
                                    "[L]\n" +
                                    print_content+
                                    "[L]\n" +
                                    "[C]--------------------------------\n" +
                                    "[R]TOTAL ORDER:[R]"+formatRupiah.format(round(total_order.toDouble()))+"\n" +
                                    "[L]\n" +
                                    "[C]================================\n" +
                                    "[L]\n" +
                                    "[C]<font size='normal'>Terima Kasih</font>\n"+
                                    "[L]\n" +
                                    "[C]<font size='normal'>Telah Belanja Di sini </font>\n\n"+
                                    "[L]\n"
                        )
                }
            } catch (e: IOException) {
            }
        }
        btSocket?.close()
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
        checkMultiplePermission()
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
                                checkMultiplePermission()
                                var fail = false
                                val device: BluetoothDevice = btAdapter!!.getRemoteDevice(address)
                                try {
                                    btSocket = device.createRfcommSocketToServiceRecord(BT_MODULE_UUID)
                                }catch (e:IOException){

                                }

                                try {
                                    btSocket?.connect()
                                }catch (e:IOException){
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
                                    print()
                                }
                    }
                }.start()
            })
        builderSingle.show()
    }

    fun content(): ArrayList<String> {
        val myArrayList = ArrayList<String>()
        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        formatRupiah.maximumFractionDigits = 0

        val total_order: Int = intent.getIntExtra("total_order", 0)
        val databaseHandler: DBHandler = DBHandler(this)
        val id: Int = intent.getIntExtra("id", 0)
        val detailLaporan: List<DetailLaporanModel> = databaseHandler.detailLaporan(id)
        val nama_produk = Array<String>(detailLaporan.size) { "null" }
        val jumlah = Array<String>(detailLaporan.size) { "null" }
        var index = 0

        if (detailLaporan.isNotEmpty()) {
            for (e in detailLaporan) {
                nama_produk[index] = e.nama_produk
                jumlah[index] = e.jumlah.toString()
                index++
                myArrayList.add("[L]<b>"+e.nama_produk+"</b>"+"[R]"+formatRupiah.format(round(e.harga.toDouble()))+"\n" +
                        "[L]Jumlah :"+e.jumlah+"\n")
            }
        }
        return myArrayList

    }

    companion object {
        val MESSAGE_READ: Int = 2

    }

    fun createBitmapFromView(view: View, width: Int, height: Int): Bitmap {
        if (width > 0 && height > 0) {
            view.measure(
                View.MeasureSpec.makeMeasureSpec(
                    DynamicUnitUtils
                        .convertDpToPixels(width.toFloat()), View.MeasureSpec.EXACTLY
                ),
                View.MeasureSpec.makeMeasureSpec(
                    DynamicUnitUtils
                        .convertDpToPixels(height.toFloat()), View.MeasureSpec.EXACTLY
                )
            )
        }
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val background = view.background
        background?.draw(canvas)
        view.draw(canvas)
        return bitmap
    }
}