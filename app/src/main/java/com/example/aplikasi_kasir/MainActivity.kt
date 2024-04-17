package com.example.aplikasi_kasir

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.aplikasi_kasir.database.DBHandler
import com.example.aplikasi_kasir.database.OrderModel
import com.example.aplikasi_kasir.ui.laporan.LaporanActivity
import com.example.aplikasi_kasir.ui.produk.ProdukActivity
import com.example.aplikasi_kasir.ui.stok.StokActivity

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    var PREFS_KEY = "prefs"
    var IS_LOGIN = "is_login"
    var NAMA = "nama"
    var ALAMAT = "alamat"
    var NO_HP = "no_hp"

    private val permission = 14
    private val listPermission = if (Build.VERSION.SDK_INT >= 30) {
        arrayListOf(
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_VIDEO,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_ADVERTISE
        )
    } else {
        arrayListOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val databaseHandler: DBHandler= DBHandler(this)

        val btn_produk: Button = findViewById(R.id.btn_produk)
        val btn_laporan: Button = findViewById(R.id.btn_laporan)
        val btn_stok: Button = findViewById(R.id.btn_stok)
        val btn_tentang_aplikasi: Button = findViewById(R.id.btn_tentang_aplikasi)
        val btn_logout: Button = findViewById(R.id.btn_logout)

        sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val is_login: String = sharedPreferences.getString(IS_LOGIN, null)!!
        val nama: String = sharedPreferences.getString(NAMA, null)!!
        val alamat: String = sharedPreferences.getString(ALAMAT, null)!!
        val no_hp: String = sharedPreferences.getString(NO_HP, null)!!

        btn_produk.setOnClickListener {
            val intent = Intent(this, ProdukActivity::class.java)
            startActivity(intent)
        }

        btn_laporan.setOnClickListener {
            val intent = Intent(this, LaporanActivity::class.java)
            startActivity(intent)
        }

        btn_stok.setOnClickListener {
            val intent = Intent(this, StokActivity::class.java)
            startActivity(intent)
        }

        btn_tentang_aplikasi.setOnClickListener {
            val intent = Intent(this, TentangActivity::class.java)
            startActivity(intent)
        }

        btn_logout.setOnClickListener {
            editor.putString(IS_LOGIN, "false")
            editor.putString(NAMA, "")
            editor.putString(ALAMAT, "")
            editor.putString(NO_HP, "")
            editor.apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }


    override fun onStart() {
        super.onStart()
        checkMultiplePermission()
        val databaseHandler: DBHandler = DBHandler(this)
        val cekOrder: List<OrderModel> = databaseHandler.cekOrder()
        val cekKeranjang: List<OrderModel> = databaseHandler.cekKeranjang()
        if(cekOrder.size>0){
            databaseHandler.hapusOrder()
        }
        if(cekKeranjang.size>0){
            for (e in cekOrder) {
                databaseHandler.flushKeranjang(e.id)
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
    }
