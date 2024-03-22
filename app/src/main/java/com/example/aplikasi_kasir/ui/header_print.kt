package com.example.aplikasi_kasir.ui

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.view.View.MeasureSpec
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasi_kasir.R
import com.pranavpandey.android.dynamic.util.DynamicUnitUtils


class header_print : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    var PREFS_KEY = "prefs"
    var IS_LOGIN = "is_login"
    var NAMA = "nama"
    var ALAMAT = "alamat"
    var NO_HP = "no_hp"
    lateinit var txt_alamat: TextView
    lateinit var txt_no_hp: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_header_print)

        sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        val alamat: String = sharedPreferences.getString(ALAMAT, null)!!
        val no_hp: String = sharedPreferences.getString(NO_HP, null)!!

        txt_alamat = findViewById(R.id.alamat)
        txt_no_hp = findViewById(R.id.nomor_hp)

        txt_alamat.setText(alamat)
        txt_no_hp.setText(no_hp)

        val linearLayout: LinearLayout = findViewById(R.id.header_print)
        val bitmap = createBitmapFromView(linearLayout, 500,500)

//        val image:ImageView =  findViewById(R.id.image)
//        image.setImageBitmap(bitmap)

    }

    fun createBitmapFromView(view: View, width: Int, height: Int): Bitmap {
        if (width > 0 && height > 0) {
            view.measure(
                MeasureSpec.makeMeasureSpec(
                    DynamicUnitUtils
                        .convertDpToPixels(width.toFloat()), MeasureSpec.EXACTLY
                ),
                MeasureSpec.makeMeasureSpec(
                    DynamicUnitUtils
                        .convertDpToPixels(height.toFloat()), MeasureSpec.EXACTLY
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