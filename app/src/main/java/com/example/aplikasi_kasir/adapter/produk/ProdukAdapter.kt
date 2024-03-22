package com.example.aplikasi_kasir.adapter.produk

import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.ViewSizeResolver
import com.example.aplikasi_kasir.R
import com.example.aplikasi_kasir.database.DBHandler
import com.example.aplikasi_kasir.database.KeranjangModel
import com.example.aplikasi_kasir.database.OrderModel
import com.example.aplikasi_kasir.database.ProdukModel
import com.google.android.material.card.MaterialCardView
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.round


class ProdukAdapter : RecyclerView.Adapter<ProdukAdapter.ViewHolder>() {
    private var mList: ArrayList<ProdukModel> = ArrayList()
    var keranjang_by_id: ArrayList<OrderModel>  = ArrayList()
    var produk_dibeli : ArrayList<String> = ArrayList()
    var order_id : String = ""
    var keranjang_id : String = ""
    lateinit var produk_id : String
    lateinit var helper_check1: String
    lateinit var helper_check2: String

    fun setData(listProduk: ArrayList<ProdukModel>) {
        this.mList.clear()
        this.mList.addAll(listProduk)
        this.notifyDataSetChanged()
    }

    fun getProduk(): ArrayList<String>{
        return produk_dibeli
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_produk, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        formatRupiah.maximumFractionDigits = 0
        val item = mList[position]
        var count: Int = 0
        val databaseHandler: DBHandler = DBHandler(holder.context)
        var input = holder.jumlah.text
        Log.d("message", item.harga.toString())
        holder.produk.text = item.nama_produk
        holder.harga.text = formatRupiah.format(round(item.harga.toDouble()))
        holder.stok.text = "Stok: "+item.stok.toString()
        holder.checkBox.buttonTintList = holder.context.getColorStateList(R.color.black)
        val image_name = File(item.gambar)

        val imageLoader = holder.gambar.context.imageLoader
        val request = ImageRequest.Builder(holder.gambar.context)
            .data(image_name)
            .size(ViewSizeResolver(holder.gambar))
            .target(holder.gambar)
            .build()
        imageLoader.enqueue(request)

        val getKeranjagById: List<OrderModel> = databaseHandler.getKeranjangById(item.produk_id)

        if(item.stok==0){
            holder.cardView.setStrokeColor(Color.LTGRAY)
            holder.cardView.setCardBackgroundColor(Color.LTGRAY)
            holder.checkBox.setOnClickListener {
                holder.checkBox.isChecked= false
                Toast.makeText(
                    holder.context,
                    "Stok Habis!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }else{
            if(getKeranjagById.size==0){
                holder.checkBox.setOnClickListener {
                    holder.cardView.setStrokeColor(Color.BLUE)
                    if(holder.checkBox.isChecked()){
                        val order: List<OrderModel> = databaseHandler.cekOrder()
                        val id = Array<String>(order.size) { "0" }
                        val tanggal = Array<String>(order.size) { "null" }
                        val pembayaran = Array<String>(order.size) { "0" }
                        val is_done = Array<String>(order.size) { "0" }
                        var index = 0
                        for (e in order) {
                            Log.d("message0000000",e.id.toString())
                            id[index] = e.id.toString()
                            tanggal[index] = e.tanggal
                            pembayaran[index] = e.pembayaran.toString()
                            is_done[index] = e.is_done.toString()
                            order_id = id[0]
                            index++
                        }

                        if (holder.jumlah.text.toString()==""){
                            keranjang_id = databaseHandler.tambahKeranjang(KeranjangModel(0, Integer.parseInt(order_id), item.produk_id,
                                0, item.harga * 0, 0)).toString()
                        }else{
                            keranjang_id = databaseHandler.tambahKeranjang(KeranjangModel(0, Integer.parseInt(order_id), item.produk_id,
                                Integer.parseInt(holder.jumlah.text.toString()), item.harga * Integer.parseInt(holder.jumlah.text.toString()), 0)).toString()
                        }
                    }else{
                        databaseHandler.hapusKeranjang(item.produk_id)
                        holder.cardView.setStrokeColor(Color.WHITE)
                    }
                }
        }


            holder.jumlah.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if (event?.action == MotionEvent.ACTION_DOWN) {
                        val order: List<OrderModel> = databaseHandler.cekOrder()
                        holder.checkBox!!.isChecked = true
                        val id = Array<String>(order.size) { "0" }
                        val tanggal = Array<String>(order.size) { "null" }
                        val pembayaran = Array<String>(order.size) { "0" }
                        val is_done = Array<String>(order.size) { "0" }
                        var index = 0
                        for (e in order) {
                            id[index] = e.id.toString()
                            tanggal[index] = e.tanggal
                            pembayaran[index] = e.pembayaran.toString()
                            is_done[index] = e.is_done.toString()
                            order_id=id[0]
                            val checker1: List<OrderModel> = databaseHandler.getKeranjangById(item.produk_id)
                            if(checker1.size==0){
                                databaseHandler.tambahKeranjang(
                                    KeranjangModel(
                                        0,
                                        e.id,
                                        item.produk_id,
                                        Integer.parseInt(holder.jumlah.text.toString()),
                                        item.harga * Integer.parseInt(holder.jumlah.text.toString()),
                                        0
                                    ))

                            }
                            }


                        holder.cardView.setStrokeColor(Color.BLUE)


                    }
                return v?.onTouchEvent(event) ?: true
            }
            })
        }

        holder.btn_tambah.setOnClickListener {
            val check: List<OrderModel> = databaseHandler.getKeranjangById(item.produk_id)
            if(check.size>0){
                if (count >= item.stok) {
                    count = item.stok
                    Toast.makeText(
                        holder.context,
                        "Tidak bisa pesan lebih dari stok!",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (order_id.isNotEmpty()) {
//                        databaseHandler.updateKeranjang(
//                            KeranjangModel(
//                                item.produk_id,
//                                Integer.parseInt(order_id),
//                                item.produk_id,
//                                Integer.parseInt(holder.jumlah.text.toString()),
//                                item.harga * Integer.parseInt(holder.jumlah.text.toString()),
//                                0
//                            )
//                        )
                    }

                } else {
                    count = Integer.parseInt(holder.jumlah.text.toString())
                    count++
                    holder.jumlah.setText("" + count)
                    if (order_id.isNotEmpty()) {
//                        databaseHandler.updateKeranjang(
//                            KeranjangModel(
//                                item.produk_id,
//                                Integer.parseInt(order_id),
//                                item.produk_id,
//                                Integer.parseInt(holder.jumlah.text.toString()),
//                                item.harga * Integer.parseInt(holder.jumlah.text.toString()),
//                                0
//                            )
//                        )
                    }
                }
            }
        }
        holder.btn_kurang.setOnClickListener {
            val check: List<OrderModel> = databaseHandler.getKeranjangById(item.produk_id)
            if(check.size>0){
                if (count <= 0) {
                    count = 0
                } else {
                    count = Integer.parseInt(holder.jumlah.text.toString())
                    count--
                    holder.jumlah.setText("" + count)
//                    databaseHandler.updateKeranjang(
//                        KeranjangModel(
//                            item.produk_id,
//                            Integer.parseInt(order_id),
//                            item.produk_id,
//                            Integer.parseInt(holder.jumlah.text.toString()),
//                            item.harga * Integer.parseInt(holder.jumlah.text.toString()),
//                            0
//                        )
//                    )
                }
            }

            }

            holder.jumlah.addTextChangedListener(object : TextWatcher {
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
                    input = s as Editable?
                    val length = s.toString().length

                    if(input.isEmpty()){
                        holder.jumlah.setText("0")
                    }else{
                        holder.jumlah.setSelection(s.toString().length)
                        if ((s.toString()[0] == '0')) {
                            if (length > 1) {
                                holder.jumlah.setText(
                                    s.toString().substring(
                                        1,
                                        length
                                    )
                                )
                            }
                        }else{
                            if(Integer.parseInt(s.toString())>item.stok){
                                holder.jumlah.setText(item.stok.toString())
                                Toast.makeText(
                                    holder.context,
                                    "Tidak bisa pesan lebih dari stok!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    if (order_id.isNotEmpty()) {
                        if (holder.jumlah.text.toString()==""){
                            databaseHandler.updateKeranjang(
                                KeranjangModel(
                                    item.produk_id,
                                    Integer.parseInt(order_id),
                                    item.produk_id,
                                    0,
                                    0,
                                    0
                                )
                            )
                        }else{
                            databaseHandler.updateKeranjang(
                                KeranjangModel(
                                    item.produk_id,
                                    Integer.parseInt(order_id),
                                    item.produk_id,
                                    Integer.parseInt(holder.jumlah.text.toString()),
                                    item.harga * Integer.parseInt(holder.jumlah.text.toString()),
                                    0
                                )
                            )
                        }
                        Log.d("berhasil", "berhasil")
                    }

                }
            })
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }
    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val context: Context = itemView.context
        val cardView: MaterialCardView = itemView.findViewById(R.id.cardview)
        val gambar: ImageView = itemView.findViewById(R.id.gambar)
        val produk: TextView = itemView.findViewById(R.id.produk)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
        val helper_checkBox: CheckBox = itemView.findViewById(R.id.checkbox1)
        val harga: TextView = itemView.findViewById(R.id.edt_harga)
        val stok: TextView = itemView.findViewById(R.id.stok)
        val jumlah: EditText = itemView.findViewById(R.id.edt_jumlah)
        val btn_kurang: Button = itemView.findViewById(R.id.btn_kurang)
        val btn_tambah: Button = itemView.findViewById(R.id.btn_tambah)

    }
}

private fun Editable?.replace(s: String, s1: String): Any {
    TODO("Not yet implemented")
}

private fun ImageView.setImageBitmap(gambar: ByteArray) {

}

