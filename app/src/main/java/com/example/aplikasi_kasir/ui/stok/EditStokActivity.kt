package com.example.aplikasi_kasir.ui.stok

import android.app.Activity
import android.content.ContentResolver
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.round

class EditStokActivity : AppCompatActivity() {
    internal var output: File? = null
    val REQUEST_CODE = 200
    lateinit var edt_nama_produk: EditText
    lateinit var edt_harga: EditText
    lateinit var edt_stok: EditText
    lateinit var kurang: Button
    lateinit var tambah: Button
    lateinit var btn_submit: Button
    lateinit var btn_upload_foto: Button
    lateinit var image_view: ImageView
    var byteArrayImage: ByteArray? = null
    var count: Int = 0
    lateinit var newValue: String
    lateinit var extension: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_stok)
        edt_stok = findViewById(R.id.edt_stok)
        edt_harga = findViewById(R.id.edt_harga)
        edt_nama_produk = findViewById(R.id.edt_nama_produk)
        tambah = findViewById(R.id.tambah)
        kurang = findViewById(R.id.kurang)
        btn_upload_foto  = findViewById(R.id.btn_upload_foto)
        image_view = findViewById(R.id.gambar)
        btn_submit = findViewById(R.id.btn_submit)
        val databaseHandler: DBHandler = DBHandler(this)

        btn_upload_foto.setOnClickListener {
            ambilFoto()
        }

        val gambar : String?= intent.getStringExtra("gambar")
        val txt_nama_produk: String? = intent.getStringExtra("nama_produk")
        val txt_harga: Int = intent.getIntExtra("harga", 0)
        val txt_stok: Int = intent.getIntExtra("stok",0)
        val txt_produk_id: Int = intent.getIntExtra("produk_id",0)
        val txt_stok_id: Int = intent.getIntExtra("stok_id",0)
        count = txt_stok

        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        formatRupiah.maximumFractionDigits = 0

        val image_name = File(gambar)

        val imageLoader = image_view.context.imageLoader
        val request = ImageRequest.Builder(image_view.context)
            .data(image_name)
            .size(ViewSizeResolver(image_view))
            .target(image_view)
            .build()
        imageLoader.enqueue(request)

        edt_nama_produk.setText(txt_nama_produk)
        edt_harga.setText(formatRupiah.format(round(txt_harga.toString().toDouble())))
//        edt_harga.setText(txt_harga.toString())

        edt_stok.setText(txt_stok.toString())

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
                        val zero = 0
                        val parsed = zero.toDouble()
                        val formatted = formatRupiah.format(round(parsed))
                        current = formatted
                        edt_harga.setText(formatted)
                        edt_harga.setSelection(formatted.length)

                        edt_harga.addTextChangedListener(this)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        btn_submit.setOnClickListener {
            edt_harga = findViewById(R.id.edt_harga)
            edt_nama_produk = findViewById(R.id.edt_nama_produk)
            edt_stok = findViewById(R.id.edt_stok)
            val harga: String = edt_harga.text.toString().replace("""[Rp,.]""".toRegex(), "")

            if(edt_harga.text.toString().trim()=="Rp0" && edt_nama_produk.text.toString().trim()==""){
                Toast.makeText(this,"Kolom Tidak Boleh Kosong", Toast.LENGTH_LONG).show()
            }else {
                databaseHandler.updateStok(
                    StokModel(
                        txt_stok_id, txt_produk_id,
                        Integer.parseInt(edt_stok.text.toString())
                    )
                )
                databaseHandler.updateProduk(
                    ProdukModel(
                        txt_produk_id,
                        txt_stok_id,
                        txt_produk_id,
                        0,
                        edt_nama_produk.text.toString(),
                        Integer.parseInt(harga),
                        gambar.toString()
                    )
                )
                finish()
            }
    }
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
        val txt_produk_id: Int = intent.getIntExtra("produk_id",0)
        val txt_stok_id: Int = intent.getIntExtra("stok_id",0)
        val databaseHandler: DBHandler = DBHandler(this)
//        val gam = intent.getByteArrayExtra("gambar")
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            btn_submit = findViewById(R.id.btn_submit)
            image_view = findViewById(R.id.gambar)
            var imageUri: Uri = data!!.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

            val imageLoader = image_view.context.imageLoader
            val request = ImageRequest.Builder(image_view.context)
                .data(imageUri)
                .size(ViewSizeResolver(image_view))
                .target(image_view)
                .build()
            imageLoader.enqueue(request)

            val cw = ContextWrapper(applicationContext)
            val directory = cw.getDir("image", MODE_PRIVATE)
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            val tanggal = LocalDateTime.now().format(formatter)
            var mime: MimeTypeMap = MimeTypeMap.getSingleton();

            if(imageUri.scheme.equals(ContentResolver.SCHEME_CONTENT)){
                extension = mime.getExtensionFromMimeType(this.contentResolver.getType(imageUri))!!;
            }else{
                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile( File(imageUri.getPath())).toString());
            }

            btn_submit.setOnClickListener {
                edt_harga = findViewById(R.id.edt_harga)
                val harga: String = edt_harga.text.toString().replace("""[Rp,.]""".toRegex(), "")
                edt_nama_produk = findViewById(R.id.edt_nama_produk)
                edt_stok = findViewById(R.id.edt_stok)

                if(edt_harga.text.toString().trim()=="" && edt_nama_produk.text.toString().trim()==""){
                    Toast.makeText(this,"Kolom Tidak Boleh Kosong", Toast.LENGTH_LONG).show()
                }else{
                    val gambar : String?= intent.getStringExtra("gambar")
                    val old_gambar = File(gambar)
                    val image_name = File(directory, tanggal+"."+extension)

                    val directory_cache = this.cacheDir
                    val path_image = PathUtils.getRealPath(applicationContext, imageUri).toString()
                    val filename = path_image.substring(path_image.lastIndexOf("/")+1)
                    val cache_image = File(directory_cache, "compressor/"+filename)

                    databaseHandler.updateStok(StokModel(txt_stok_id,txt_produk_id,
                        Integer.parseInt(edt_stok.text.toString())))
                    databaseHandler.updateProduk(ProdukModel(txt_produk_id,txt_stok_id,txt_produk_id,
                        0,edt_nama_produk.text.toString(),Integer.parseInt(harga),
                        image_name.toString()
                    ))

                    old_gambar.delete()

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
        }else{
            val harga: String = edt_harga.text.toString().replace("""[Rp,.]""".toRegex(), "")
            btn_submit.setOnClickListener {
                edt_harga = findViewById(R.id.edt_harga)
                edt_nama_produk = findViewById(R.id.edt_nama_produk)
                edt_stok = findViewById(R.id.edt_stok)
                val gambar : String?= intent.getStringExtra("gambar")

                if(edt_harga.text.toString().trim()=="" && edt_nama_produk.text.toString().trim()==""){
                    Toast.makeText(this,"Kolom Tidak Boleh Kosong", Toast.LENGTH_LONG).show()
                }else{
                    databaseHandler.updateStok(StokModel(txt_stok_id,txt_produk_id,
                        Integer.parseInt(edt_stok.text.toString())))
                    databaseHandler.updateProduk(ProdukModel(txt_produk_id,txt_stok_id,txt_produk_id,
                        0,edt_nama_produk.text.toString(),Integer.parseInt(harga),
                        gambar!!
                    ))
                    try {
                        val image_name = File(gambar)
                        val bitmap = BitmapFactory.decodeStream(FileInputStream(image_name))
                        image_view.setImageBitmap(bitmap)
                    }catch (e: FileNotFoundException){
                        e.printStackTrace()
                    }
                    finish()
                }
            }
        }
    }
}