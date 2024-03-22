package com.example.aplikasi_kasir.ui.produk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.aplikasi_kasir.MainActivity
import com.example.aplikasi_kasir.R
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.round

class PembayaranActivity : AppCompatActivity() {
    lateinit var btn_hitung: Button
    lateinit var btn_kembali: Button
    lateinit var txt_total_order: TextView
    lateinit var txt_kembalian: TextView
    lateinit var edt_uang: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)
        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        formatRupiah.maximumFractionDigits = 0
        btn_hitung = findViewById(R.id.btn_hitung)
        btn_kembali = findViewById(R.id.btn_kembali)
        txt_total_order = findViewById(R.id.txt_total_order)
        txt_kembalian = findViewById(R.id.txt_kembalian)
        edt_uang = findViewById(R.id.uang)
        val total_pembayaran: Int = intent.getIntExtra("total_pembayaran",0)

        txt_total_order.setText("Total yang dibayarkan: "+formatRupiah.format(round(total_pembayaran.toString().toDouble())))
        btn_kembali.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_hitung.setOnClickListener {
            val uang: String = edt_uang.text.toString().replace("""[Rp,.]""".toRegex(), "")
            val kembalian = Integer.parseInt(uang) - total_pembayaran
            Log.d("message", kembalian.toString())
            txt_kembalian.isVisible=true
            txt_kembalian.setText("Kembalian: "+formatRupiah.format(round(kembalian.toString().toDouble())))
        }

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
    }
}