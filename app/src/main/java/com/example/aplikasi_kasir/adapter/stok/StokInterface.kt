package com.example.aplikasi_kasir.adapter.stok

import android.view.View
import com.example.aplikasi_kasir.database.ProdukModel

class StokInterface {
    interface StokInterface {
        // method yang akan dipanggil di MainActivity
        fun onItemClicked(view: View, ProdukModel: ProdukModel)

    }
}