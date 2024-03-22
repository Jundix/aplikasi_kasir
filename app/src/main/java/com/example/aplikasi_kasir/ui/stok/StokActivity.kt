package com.example.aplikasi_kasir.ui.stok

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_kasir.R
import com.example.aplikasi_kasir.adapter.stok.StokAdapter
import com.example.aplikasi_kasir.adapter.stok.StokInterface
import com.example.aplikasi_kasir.database.DBHandler
import com.example.aplikasi_kasir.database.ProdukModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class StokActivity : AppCompatActivity(), StokInterface.StokInterface {
    lateinit var btn_tambah: FloatingActionButton
    lateinit var btn_hapus: Button
    lateinit var listStok: RecyclerView
    lateinit var stokAdapter: StokAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stok)
        btn_tambah = findViewById(R.id.btn_tambah)
        setupRecyclerview()
        btn_tambah.setOnClickListener {
            val intent = Intent(this, TambahStokActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        dataStok()
    }

    fun setupRecyclerview(){
        listStok = findViewById(R.id.list_stok)
        listStok.layoutManager = LinearLayoutManager(this)
        stokAdapter = StokAdapter()
        listStok.adapter = stokAdapter
        stokAdapter.listener=this
    }

    fun dataStok(){
        listStok = findViewById(R.id.list_stok)
        val data_kosong: ConstraintLayout = findViewById(R.id.data_kosong)

        val databaseHandler: DBHandler= DBHandler(this)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        val stok = databaseHandler.dataStok()
        if(stok.isNotEmpty()){
            data_kosong.isVisible=false
            listStok.isVisible=true
            //display-update data in RecyclerView
            stokAdapter.notifyDataSetChanged()
            stokAdapter?.setData(stok)

        }else{
            data_kosong.isVisible=true
            listStok.isVisible=false
            Toast.makeText(this,
                "Belum Ada Data",
                Toast.LENGTH_SHORT
            ).show()
        }
        }

    override fun onItemClicked(view: View, produkModel: ProdukModel) {
        val intent = Intent(this, EditStokActivity::class.java)
        intent.putExtra("produk_id", produkModel.id)
        intent.putExtra("nama_produk", produkModel.nama_produk)
        intent.putExtra("harga", produkModel.harga)
        intent.putExtra("stok", produkModel.stok)
        intent.putExtra("stok_id", produkModel.stok_id)
        intent.putExtra("gambar", produkModel.gambar)
        startActivity(intent)
    }

}