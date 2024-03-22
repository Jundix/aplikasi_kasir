package com.example.aplikasi_kasir.ui.laporan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_kasir.MainActivity
import com.example.aplikasi_kasir.R
import com.example.aplikasi_kasir.adapter.laporan.LaporanAdapter
import com.example.aplikasi_kasir.adapter.laporan.LaporanInterface
import com.example.aplikasi_kasir.adapter.stok.StokAdapter
import com.example.aplikasi_kasir.adapter.stok.StokInterface
import com.example.aplikasi_kasir.database.DBHandler
import com.example.aplikasi_kasir.database.LaporanModel
import com.example.aplikasi_kasir.database.ProdukModel
import com.example.aplikasi_kasir.ui.laporan.DetailLaporanActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class LaporanActivity : AppCompatActivity(), LaporanInterface.LaporanInterface {

    lateinit var recyclerView: RecyclerView
    lateinit var laporanAdapter: LaporanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)
        setupRecyclerview()

    }
    override fun onStart() {
        super.onStart()
        dataLaporan()
    }

    fun setupRecyclerview(){
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        laporanAdapter = LaporanAdapter()
        recyclerView.adapter = laporanAdapter
        laporanAdapter.listener=this
    }

    fun dataLaporan(){
        recyclerView = findViewById(R.id.recyclerView)
        val data_kosong: ConstraintLayout = findViewById(R.id.data_kosong)

        val databaseHandler: DBHandler= DBHandler(this)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        val laporan = databaseHandler.laporan()
        if(laporan.isNotEmpty()){
            data_kosong.isVisible=false
            recyclerView.isVisible=true
            laporanAdapter.notifyDataSetChanged()
            laporanAdapter?.setData(laporan)

        }else{
            data_kosong.isVisible=true
            recyclerView.isVisible=false
            Toast.makeText(this,
                "Belum Ada Data",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onItemClicked(view: View, laporanModel: LaporanModel) {
        val intent = Intent(this, DetailLaporanActivity::class.java)
        intent.putExtra("id", laporanModel.id)
        intent.putExtra("total_order", laporanModel.pembayaran)
        startActivity(intent)
    }

}