package com.example.aplikasi_kasir.adapter.laporan

import android.view.View
import com.example.aplikasi_kasir.database.LaporanModel
import com.example.aplikasi_kasir.database.ProdukModel

class LaporanInterface {
    interface LaporanInterface {
        fun onItemClicked(view: View, LaporanModel:LaporanModel)
    }
}