package com.example.aplikasi_kasir.adapter.laporan

import android.content.Context
import android.graphics.BitmapFactory
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_kasir.R
import com.example.aplikasi_kasir.database.DetailLaporanModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.round

class DetailLaporanAdapter : RecyclerView.Adapter<DetailLaporanAdapter.ViewHolder>() {
    private var mList: ArrayList<DetailLaporanModel> = ArrayList()

    fun setData(list: ArrayList<DetailLaporanModel>) {
        this.mList.clear()
        this.mList.addAll(list)
        this.notifyDataSetChanged()
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val context: Context = itemView.context
        val detail: TextView = itemView.findViewById(R.id.detail)
        val produk: TextView = itemView.findViewById(R.id.produk)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailLaporanAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_detail_laporan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailLaporanAdapter.ViewHolder, position: Int) {
        val item = mList[position]
        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        formatRupiah.maximumFractionDigits = 0
        val total = item.jumlah * item.harga
        holder.produk.text = item.nama_produk
        holder.detail.text = item.jumlah.toString()+ " x " + formatRupiah.format(round(item.harga.toDouble())) +" = "+ formatRupiah.format(round(total.toDouble()))
    }

    override fun getItemCount(): Int {
        return mList.size
    }


}

