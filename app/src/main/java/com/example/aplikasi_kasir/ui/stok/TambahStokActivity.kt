package com.example.aplikasi_kasir.ui.stok

import PathUtils
import android.R.attr.path
import android.app.Activity
import android.content.ContentResolver
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.ViewSizeResolver
import com.example.aplikasi_kasir.R
import com.example.aplikasi_kasir.database.DBHandler
import com.example.aplikasi_kasir.database.ProdukModel
import com.example.aplikasi_kasir.database.StokModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.destination
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.round


class TambahStokActivity : AppCompatActivity() {

    internal var output: File? = null
    val REQUEST_CODE = 200
    lateinit var edt_stok: EditText
    lateinit var edt_nama_produk: EditText
    lateinit var edt_harga: EditText
    lateinit var tambah: Button
    lateinit var kurang: Button
    lateinit var btn_upload_foto: Button
    lateinit var image: ImageView
    lateinit var btn_submit: Button
    lateinit var extension: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_stok)
        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        formatRupiah.maximumFractionDigits = 0
        edt_stok = findViewById(R.id.edt_stok)
        edt_harga = findViewById(R.id.edt_harga)
        edt_nama_produk = findViewById(R.id.edt_nama_produk)
        tambah = findViewById(R.id.tambah)
        kurang = findViewById(R.id.kurang)
        btn_upload_foto  = findViewById(R.id.btn_upload_foto)
        image = findViewById(R.id.gambar)
        btn_submit = findViewById(R.id.btn_submit)
        var imageUri: Uri? = null
        val uri: String = Uri.parse(imageUri.toString()).toString()
        var count: Int = 0

        image.setImageResource(R.drawable.ic_android_black_24dp)

        tambah.setOnClickListener {
            count++
            edt_stok.setText("" + count)
        }

        kurang.setOnClickListener {
            if (count <= 0) {
                count = 0
            } else {
                count--
                edt_stok.setText("" + count)
            }
        }

        btn_upload_foto.setOnClickListener {
            ambilFoto()
        }

        btn_submit.setOnClickListener {
            if(edt_nama_produk.text.toString().trim()==""){
                Toast.makeText(this,"Kolom Nama Produk Harus Diisi",Toast.LENGTH_LONG).show()
            }else if(edt_harga.text.toString().trim()==""){
                Toast.makeText(this,"Kolom Harga Harus Diisi",Toast.LENGTH_LONG).show()
            }else if(edt_harga.text.toString().trim()=="Rp0"){
                Toast.makeText(this,"Kolom Harga Harus Diisi",Toast.LENGTH_LONG).show()
            }else if(edt_stok.text.toString().trim()=="0"){
                Toast.makeText(this,"Kolom Stok Tidak Boleh 0",Toast.LENGTH_LONG).show()
            }else if(uri=="null"){
                Toast.makeText(this,"Gambar Harus Diupload",Toast.LENGTH_LONG).show()
            }
        }

        edt_harga.addTextChangedListener(object : TextWatcher {
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
                    edt_harga.removeTextChangedListener(this)
                    val cleanString: String = s.toString().replace("""[Rp,.]""".toRegex(), "")
                    if(cleanString.length>1){
                        val parsed = cleanString.toDouble()
                        val formatted = formatRupiah.format(round(parsed))
//                    formatRupiah.format(round(edt_harga.toString().toDouble()))
                        current = formatted
                        edt_harga.setText(formatted)
                        edt_harga.setSelection(formatted.length)

                        edt_harga.addTextChangedListener(this)
                    }else{
                        if(cleanString.length==0){
                            val zero = 0
                            val parsed = zero.toDouble()
                            val formatted = formatRupiah.format(round(parsed))
                            current = formatted
                            edt_harga.setText(formatted)
                            edt_harga.setSelection(formatted.length)

                            edt_harga.addTextChangedListener(this)
                        }else{
                            val zero = cleanString
                            val parsed = zero.toDouble()
                            val formatted = formatRupiah.format(round(parsed))
                            current = formatted
                            edt_harga.setText(formatted)
                            edt_harga.setSelection(formatted.length)

                            edt_harga.addTextChangedListener(this)
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

    }

    @Suppress("DEPRECATION")
    fun ambilFoto(){

        if (Build.VERSION.SDK_INT < 19) {
            var intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose Pictures")
                , REQUEST_CODE
            )
        }
        else { // For latest versions API LEVEL 19+
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE);
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            val cw = ContextWrapper(applicationContext)
            val directory = cw.getDir("image", MODE_PRIVATE)
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            val tanggal = LocalDateTime.now().format(formatter)
            var fos: FileOutputStream? = null
            var imageUri: Uri = data!!.data!!
            var mime: MimeTypeMap= MimeTypeMap.getSingleton();
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

            if(imageUri.scheme.equals(ContentResolver.SCHEME_CONTENT)){
                extension = mime.getExtensionFromMimeType(this.contentResolver.getType(imageUri))!!;
            }else{
                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile( File(imageUri.getPath())).toString());
            }
            val image_name = File(directory, tanggal+"."+extension)
            image = findViewById(R.id.gambar)
            btn_submit = findViewById(R.id.btn_submit)

            val imageLoader = image.context.imageLoader
            val request = ImageRequest.Builder(image.context)
                .data(imageUri)
                .size(ViewSizeResolver(image))
                .target(image)
                .build()
            imageLoader.enqueue(request)

            val databaseHandler: DBHandler= DBHandler(this)

            btn_submit.setOnClickListener {
                edt_harga = findViewById(R.id.edt_harga)
                edt_nama_produk = findViewById(R.id.edt_nama_produk)
                edt_stok = findViewById(R.id.edt_stok)
                val harga: String = edt_harga.text.toString().replace("""[Rp,.]""".toRegex(), "")

                if(edt_nama_produk.text.toString().trim()==""){
                    Toast.makeText(this,"Kolom Nama Produk Harus Diisi",Toast.LENGTH_LONG).show()
                }else if(edt_harga.text.toString().trim()==""){
                    Toast.makeText(this,"Kolom Harga Harus Diisi",Toast.LENGTH_LONG).show()
                }else if(edt_harga.text.toString().trim()=="Rp0"){
                    Toast.makeText(this,"Kolom Harga Harus Diisi",Toast.LENGTH_LONG).show()
                }else if(edt_stok.text.toString().trim()=="0"){
                    Toast.makeText(this,"Kolom Stok Tidak Boleh 0",Toast.LENGTH_LONG).show()
                }else {
                    var produk_id = databaseHandler.tambahProduk(
                        ProdukModel(
                            0,
                            0,
                            0,
                            0,
                            edt_nama_produk.text.toString(),
                            Integer.parseInt(harga),
                            image_name.toString()
                        )
                    )
                    databaseHandler.tambahStok(
                        StokModel(
                            0,
                            Integer.parseInt(produk_id.toString()),
                            Integer.parseInt(edt_stok.text.toString())
                        )
                    )

                    val directory_cache = this.cacheDir
                    val path_image = PathUtils.getRealPath(applicationContext, imageUri).toString()
                    val filename = path_image.substring(path_image.lastIndexOf("/")+1)
                    val cache_image = File(directory_cache, "compressor/"+filename)
                    val job =   GlobalScope.launch {
                            val compressedImageFile = Compressor.compress(
                                applicationContext,
                                File(PathUtils.getRealPath(applicationContext, imageUri))
                            ) {
                                quality(0) // combine with compressor constraint
                                format(Bitmap.CompressFormat.JPEG)
                                destination(image_name)
                            }
                            cache_image.delete()

                        }
                    runBlocking {
                        job.join()
                    }
                    finish()

                }
                }
            }
        }
    }



private fun ImageView.setImageDrawable(icAndroidBlack24dp: Int) {

}




