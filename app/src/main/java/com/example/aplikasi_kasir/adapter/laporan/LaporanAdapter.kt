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
import com.example.aplikasi_kasir.adapter.stok.StokAdapter
import com.example.aplikasi_kasir.database.LaporanModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.round

class LaporanAdapter : RecyclerView.Adapter<LaporanAdapter.ViewHolder>() {
    private var mList: ArrayList<LaporanModel> = ArrayList()
    var listener: LaporanInterface.LaporanInterface? = null

    fun setData(list: ArrayList<LaporanModel>) {
        this.mList.clear()
        this.mList.addAll(list)
        this.notifyDataSetChanged()
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val context: Context = itemView.context
        val tanggal: TextView = itemView.findViewById(R.id.tanggal)
        val pembayaran: TextView = itemView.findViewById(R.id.pembayaran)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporanAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_laporan, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: LaporanAdapter.ViewHolder, position: Int) {
        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        formatRupiah.maximumFractionDigits = 0
        val item = mList[position]
        holder.tanggal.text = item.tanggal
        holder.pembayaran.text = formatRupiah.format(round(item.pembayaran.toDouble()))
        holder.itemView.setOnClickListener {
            listener?.onItemClicked(it, mList[position])
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


}

