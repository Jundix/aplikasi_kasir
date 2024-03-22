package com.example.aplikasi_kasir.adapter.stok

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.example.aplikasi_kasir.R
import com.example.aplikasi_kasir.database.DBHandler
import com.example.aplikasi_kasir.database.ProdukModel
import com.example.aplikasi_kasir.database.StokModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.round


class StokAdapter : RecyclerView.Adapter<StokAdapter.ViewHolder>() {
    private var mList: ArrayList<ProdukModel> = ArrayList()
    var listener: StokInterface.StokInterface? = null
    fun setData(listStok: ArrayList<ProdukModel>) {
        this.mList.clear()
        this.mList.addAll(listStok)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_stok, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        formatRupiah.maximumFractionDigits = 0
        val databaseHandler: DBHandler = DBHandler(holder.context)
        val item = mList[position]
        holder.produk.text = item.nama_produk
        holder.harga.text = formatRupiah.format(round(item.harga.toDouble()))
        holder.stok.text = "Stok: "+item.stok.toString()
        val image_name = File(item.gambar)

        val imageLoader = holder.gambar.context.imageLoader
        val request = ImageRequest.Builder(holder.gambar.context)
            .data(image_name)
            .size(ViewSizeResolver(holder.gambar))
            .target(holder.gambar)
            .build()
        imageLoader.enqueue(request)

        holder.itemView.setOnClickListener {
            listener?.onItemClicked(it, mList[position])
        }
        holder.btn_hapus.setOnClickListener {
            val builder = AlertDialog.Builder(holder.context, R.style.alert)
            builder.setTitle("Hapus Data")
            builder.setMessage("Apakah anda yakin ingin menghapus data ini?")
            builder.setPositiveButton(Html.fromHtml("<font color='#000000'>Ya</font>")) { dialog, which ->
                databaseHandler.hapusStok(StokModel(0,Integer.parseInt(item.produk_id.toString()),0))
                val stok = databaseHandler.dataStok()
                setData(stok)
                val image_name = File(item.gambar)
                image_name.delete()
            }
            builder.setNegativeButton(Html.fromHtml("<font color='#000000'>Batal</font>")) { dialog, which ->

            }
            val dialog = builder.create()
            dialog.show()
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }
    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val context: Context = itemView.context
        val gambar: ImageView = itemView.findViewById(R.id.gambar)
        val produk: TextView = itemView.findViewById(R.id.produk)
        val harga: TextView = itemView.findViewById(R.id.edt_harga)
        val stok: TextView = itemView.findViewById(R.id.stok)
        val btn_hapus: Button = itemView.findViewById(R.id.btn_hapus)

        }
    }

private fun ImageView.setImageBitmap(gambar: ByteArray) {

}

