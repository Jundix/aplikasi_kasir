package com.example.aplikasi_kasir.ui.produk

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
import com.example.aplikasi_kasir.adapter.produk.ProdukAdapter
import com.example.aplikasi_kasir.adapter.produk.ProdukInterface
import com.example.aplikasi_kasir.adapter.stok.StokAdapter
import com.example.aplikasi_kasir.adapter.stok.StokInterface
import com.example.aplikasi_kasir.database.DBHandler
import com.example.aplikasi_kasir.database.KeranjangModel
import com.example.aplikasi_kasir.database.OrderModel
import com.example.aplikasi_kasir.database.ProdukModel
import com.example.aplikasi_kasir.database.StokModel
import com.example.aplikasi_kasir.ui.laporan.DetailLaporanActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ProdukActivity : AppCompatActivity(), ProdukInterface.ProdukInterface {
    lateinit var recyclerView: RecyclerView
    lateinit var produkAdapter: ProdukAdapter
    lateinit var btn_bayar: Button
    lateinit var keranjang_id: String
    lateinit var total_pembayaran: String
    lateinit var order_id: String
    val REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produk)
        btn_bayar = findViewById(R.id.btn_bayar)
        val databaseHandler: DBHandler = DBHandler(this)
        val cekKeranjang: List<OrderModel> = databaseHandler.cekKeranjang()

        btn_bayar.setOnClickListener {
            if(cekKeranjang.size==0){
                val cekKeranjang: List<OrderModel> = databaseHandler.cekKeranjang()
                if (cekKeranjang.size>0){
                    val bayar: List<KeranjangModel> = databaseHandler.totalBayar()
                    val id = Array<String>(bayar.size) { "0" }
                    val pesanan_id = Array<String>(bayar.size) { "0" }
                    val total_bayar = Array<String>(bayar.size) { "0" }
                    var index = 0
                    val cekKeranjang: List<OrderModel> = databaseHandler.cekKeranjang()
                    for (e in bayar) {
                        id[index] = e.id.toString()
                        pesanan_id[index] = e.pesanan_id.toString()
                        total_bayar[index] = e.harga.toString()
                        total_pembayaran = e.harga.toString()
                        order_id = e.pesanan_id.toString()

                        if(Integer.parseInt(total_pembayaran) == 0){
                            Toast.makeText(
                                this,
                                "Tambahkan Jumlah Produk Yang Dipesan",
                                Toast.LENGTH_SHORT
                            ).show()
                        }else{
                            databaseHandler.updateOrder(OrderModel(Integer.parseInt(pesanan_id[0]),"null",Integer.parseInt(total_bayar[0]),1,0,0,0))
                            for (e in cekKeranjang) {
                                val listStok = databaseHandler.getStokById(e.produk_id)
                                for(s in listStok){
                                    databaseHandler.updateStok(
                                        StokModel(s.id,e.produk_id,s.stok - e.jumlah)
                                    )
                                }
                            }
                            val intent = Intent(this, DetailLaporanActivity::class.java)
                            intent.putExtra("total_pembayaran", Integer.parseInt(total_pembayaran))
                            intent.putExtra("total_order", Integer.parseInt(total_pembayaran))
                            intent.putExtra("id", Integer.parseInt(order_id))
                            intent.putExtra("REQUEST_CODE", REQUEST_CODE)
                            startActivity(intent)
                            finish()
                        }
                    }
                }else{
                    Toast.makeText(
                        this,
                        "Tambahkan Produk Terlebih Dahulu",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        }
        setupRecyclerview()
    }

    override fun onStart() {
        super.onStart()
        val databaseHandler: DBHandler = DBHandler(this)
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val tanggal = LocalDateTime.now().format(formatter)
        val cekOrder: List<OrderModel> = databaseHandler.cekOrder()
        val cekKeranjang: List<OrderModel> = databaseHandler.cekKeranjang()
        val id = Array<String>(cekKeranjang.size) { "0" }
        val tgl = Array<String>(cekKeranjang.size) { "null" }
        val pembayaran = Array<String>(cekKeranjang.size) { "0" }
        val is_done = Array<String>(cekKeranjang.size) { "0" }
        var index = 0
        if(cekOrder.size==0){
            databaseHandler.tambahOrder(OrderModel(0,tanggal, 0,0,0,0,0))
        }else{
            databaseHandler.hapusOrder()
            databaseHandler.tambahOrder(OrderModel(0,tanggal, 0,0,0,0,0))
        }
        if(cekKeranjang.size>0){
            for (e in cekOrder) {
                id[index] = e.id.toString()
                tgl[index] = e.tanggal
                pembayaran[index] = e.pembayaran.toString()
                is_done[index] = e.is_done.toString()
                keranjang_id = id[0]
                databaseHandler.flushKeranjang(Integer.parseInt(id[0]))
            }
        }
        dataProduk()
        val stok = databaseHandler.dataStok()
        if (stok.size==0){
            btn_bayar.isVisible=false
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val databaseHandler: DBHandler = DBHandler(this)
        val cekOrder: List<OrderModel> = databaseHandler.cekOrder()
        val cekKeranjang: List<OrderModel> = databaseHandler.cekKeranjang()
        for (e in cekOrder) {
            if(cekKeranjang.size>0){
                databaseHandler.flushKeranjang(e.id)
            }
        }

        databaseHandler.hapusOrder()
    }

    fun setupRecyclerview(){
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        produkAdapter = ProdukAdapter()
        recyclerView.adapter = produkAdapter
    }

    fun dataProduk(){
        recyclerView = findViewById(R.id.recyclerView)
        val data_kosong: ConstraintLayout = findViewById(R.id.data_kosong)

        val databaseHandler: DBHandler= DBHandler(this)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        val stok = databaseHandler.dataStok()
        if(stok.isNotEmpty()){
            produkAdapter?.setData(stok)
        }else{
            data_kosong.isVisible=true
            recyclerView.isVisible=false
            Toast.makeText(this,
                "Belum Ada Data",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}