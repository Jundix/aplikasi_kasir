package com.example.aplikasi_kasir.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.Cursor
import android.database.sqlite.SQLiteException


class DBHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "db_kasir"

        private val TABLE_ORDER = "pesanan"
        private val TABLE_KERANJANG = "keranjang"
        private val TABLE_PRODUK = "produk"
        private val TABLE_USER = "user"
        private val TABLE_STOK = "stok"

        private val ID = "id"
        private val NO_HP = "no_hp"
        private val ALAMAT = "alamat"
        private val PRODUK = "nama_produk"
        private val PRODUK_ID = "produk_id"
        private val ORDER_ID = "pesanan_id"
        private val HARGA = "harga"
        private val GAMBAR = "gambar"
        private val CUSTOMER = "customer"
        private val PEMBAYARAN = "pembayaran"
        private val STOK = "stok"
        private val JUMLAH = "jumlah"
        private val TANGGAL = "tanggal"
        private val USER_ID = "user_id"
        private val EMAIL = "email"
        private val PASSWORD = "password"
        private val NAMA = "nama"
        private val IS_DONE = "is_done"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_PRODUK_TABLE = ("CREATE TABLE " + TABLE_PRODUK + "("
                + ID + " INTEGER PRIMARY KEY," + PRODUK + " TEXT,"
                + HARGA + " INTEGER," + GAMBAR + " STRING"+ ")")

        val CREATE_STOK_TABLE = ("CREATE TABLE " + TABLE_STOK + "("
                + ID + " INTEGER PRIMARY KEY," + PRODUK_ID + " INTEGER,"
                + STOK + " INTEGER"+ ")")

        val CREATE_ORDER_TABLE = ("CREATE TABLE " + TABLE_ORDER + "("
                + ID + " INTEGER PRIMARY KEY," + TANGGAL + " TEXT," + PEMBAYARAN + " INTEGER,"+ IS_DONE + " INTEGER"+ ")")

        val CREATE_KERANJANG_TABLE = ("CREATE TABLE " + TABLE_KERANJANG + "("
                + ID + " INTEGER PRIMARY KEY," + ORDER_ID + " INTEGER,"
                + PRODUK_ID + " INTEGER," + JUMLAH + " INTEGER," + HARGA + " INTEGER," + IS_DONE + " INTEGER"+ ")")

        val CREATE_USER_TABLE = ("CREATE TABLE " + TABLE_USER + "("
                + ID + " INTEGER PRIMARY KEY," + NAMA + " TEXT,"
                + EMAIL + " TEXT," + PASSWORD + " TEXT," + NO_HP + " TEXT," + ALAMAT + " TEXT"+ ")")

        db?.execSQL(CREATE_PRODUK_TABLE)
        db?.execSQL(CREATE_STOK_TABLE)
        db?.execSQL(CREATE_KERANJANG_TABLE)
        db?.execSQL(CREATE_ORDER_TABLE)
        db?.execSQL(CREATE_USER_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER)
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_KERANJANG)
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUK)
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_STOK)
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_USER)
        onCreate(db)
    }

    @SuppressLint("Range")
    fun dataUser():List<UserModel>{
        val userList:ArrayList<UserModel> = ArrayList<UserModel>()
        val selectQuery = "SELECT  * FROM $TABLE_USER"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var nama: String
        var email: String
        var password: String
        var no_hp: String
        var alamat: String
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                nama = cursor.getString(cursor.getColumnIndex("nama"))
                email = cursor.getString(cursor.getColumnIndex("email"))
                password = cursor.getString(cursor.getColumnIndex("password"))
                no_hp = cursor.getString(cursor.getColumnIndex("no_hp"))
                alamat = cursor.getString(cursor.getColumnIndex("alamat"))
                val user= UserModel(id = id, nama = nama, email = email, password=password,no_hp=no_hp, alamat=alamat)
                userList.add(user)
            } while (cursor.moveToNext())
        }
        return userList
    }

    fun register(user: UserModel):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NAMA, user.nama)
        contentValues.put(EMAIL, user.email)
        contentValues.put(PASSWORD, user.password )
        contentValues.put(ALAMAT, user.alamat )
        contentValues.put(NO_HP, user.no_hp )
        val success = db.insert(TABLE_USER, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun login(email: String):List<UserModel>{
        val contentValues = ContentValues()
        // Updating Row
        val userList:ArrayList<UserModel> = ArrayList<UserModel>()
        val selectQuery = "SELECT  * FROM $TABLE_USER where $EMAIL="+"'"+email+"'"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var nama: String
        var email: String
        var password: String
        var no_hp: String
        var alamat: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                nama = cursor.getString(cursor.getColumnIndex("nama"))
                email = cursor.getString(cursor.getColumnIndex("email"))
                password = cursor.getString(cursor.getColumnIndex("password"))
                no_hp = cursor.getString(cursor.getColumnIndex("no_hp"))
                alamat = cursor.getString(cursor.getColumnIndex("alamat"))
                val user= UserModel(id = id, nama = nama, email = email, password=password,no_hp=no_hp, alamat=alamat)
                userList.add(user)
            } while (cursor.moveToNext())
        }
        return userList
    }

    @SuppressLint("Range")
    fun laporan():ArrayList<LaporanModel>{
        val laporanList:ArrayList<LaporanModel> = ArrayList<LaporanModel>()
        val selectQuery = "SELECT  * FROM $TABLE_ORDER"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var tanggal: String
        var pembayaran: Int
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                tanggal = cursor.getString(cursor.getColumnIndex("tanggal"))
                pembayaran = cursor.getInt(cursor.getColumnIndex("pembayaran"))
                val laporan= LaporanModel(id = id, tanggal = tanggal, pembayaran = pembayaran)
                laporanList.add(laporan)
            } while (cursor.moveToNext())
        }
        return laporanList
    }

    @SuppressLint("Range")
    fun dataStok():ArrayList<ProdukModel>{
        val stokList:ArrayList<ProdukModel> = ArrayList<ProdukModel>()
        val selectQuery = "SELECT  $TABLE_PRODUK.gambar, $TABLE_PRODUK.harga,$TABLE_PRODUK.nama_produk, $TABLE_STOK.id, $TABLE_STOK.produk_id, $TABLE_STOK.stok FROM $TABLE_STOK inner join $TABLE_PRODUK on $TABLE_STOK.produk_id = $TABLE_PRODUK.id"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var stok_id: Int
        var produk_id: Int
        var stok: Int
        var nama_produk: String
        var harga: Int
        var gambar: String
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                stok_id = cursor.getInt(cursor.getColumnIndex("id"))
                produk_id = cursor.getInt(cursor.getColumnIndex("produk_id"))
                stok = cursor.getInt(cursor.getColumnIndex("stok"))
                nama_produk = cursor.getString(cursor.getColumnIndex("nama_produk"))
                harga = cursor.getInt(cursor.getColumnIndex("harga"))
                gambar = cursor.getString(cursor.getColumnIndex("gambar"))

                val stok= ProdukModel(id = id, stok_id=stok_id,produk_id = produk_id, stok = stok, nama_produk=nama_produk, harga=harga, gambar = gambar)
                stokList.add(stok)
            } while (cursor.moveToNext())
        }
        return stokList
    }

    @SuppressLint("Range")
    fun getStokById(id: Int):ArrayList<ProdukModel>{
        val stokList:ArrayList<ProdukModel> = ArrayList<ProdukModel>()
        val selectQuery = "SELECT  $TABLE_PRODUK.gambar, $TABLE_PRODUK.harga,$TABLE_PRODUK.nama_produk, " +
                "$TABLE_STOK.id, $TABLE_STOK.produk_id, $TABLE_STOK.stok FROM $TABLE_STOK " +
                "inner join $TABLE_PRODUK on $TABLE_STOK.produk_id = $TABLE_PRODUK.id where $TABLE_STOK.$PRODUK_ID="+id
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var stok_id: Int
        var produk_id: Int
        var stok: Int
        var nama_produk: String
        var harga: Int
        var gambar: String
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                stok_id = cursor.getInt(cursor.getColumnIndex("id"))
                produk_id = cursor.getInt(cursor.getColumnIndex("produk_id"))
                stok = cursor.getInt(cursor.getColumnIndex("stok"))
                nama_produk = cursor.getString(cursor.getColumnIndex("nama_produk"))
                harga = cursor.getInt(cursor.getColumnIndex("harga"))
                gambar = cursor.getString(cursor.getColumnIndex("gambar"))

                val stok= ProdukModel(id = id, stok_id=stok_id,produk_id = produk_id, stok = stok, nama_produk=nama_produk, harga=harga, gambar = gambar)
                stokList.add(stok)
            } while (cursor.moveToNext())
        }
        return stokList
    }

    fun tambahProduk(produkModel: ProdukModel):Long{
        val db = this.writableDatabase
        val valuesProduk = ContentValues()
        valuesProduk.put(PRODUK, produkModel.nama_produk)
        valuesProduk.put(HARGA, produkModel.harga)
        valuesProduk.put(GAMBAR, produkModel.gambar)

        val produk = db.insert(TABLE_PRODUK, null, valuesProduk)
        db.close()
        return produk
    }

    fun tambahStok(stokModel: StokModel):Long{
        val db = this.writableDatabase
        val valuesStok = ContentValues()
        valuesStok.put(PRODUK_ID, stokModel.produk_id)
        valuesStok.put(STOK, stokModel.stok)

        val stok = db.insert(TABLE_STOK, null, valuesStok)
        db.close()
        return stok
    }

    fun hapusStok(stokModel: StokModel):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(PRODUK_ID, stokModel.produk_id) // EmpModelClass UserId
        // Deleting Row
        val produk = db.delete(TABLE_STOK,"produk_id="+stokModel.produk_id,null)
        val stok = db.delete(TABLE_PRODUK,"id="+stokModel.produk_id,null)
        db.close()
        return produk
    }

    fun updateStok(stokModel: StokModel):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, stokModel.id)
        contentValues.put(PRODUK_ID, stokModel.produk_id)
        contentValues.put(STOK,stokModel.stok )

        // Updating Row
        val success = db.update(TABLE_STOK, contentValues,"id="+stokModel.id,null)
        db.close()
        return success
    }

    fun updateProduk(produkModel: ProdukModel):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, produkModel.id)
        contentValues.put(PRODUK, produkModel.nama_produk)
        contentValues.put(HARGA, produkModel.harga)
        contentValues.put(GAMBAR, produkModel.gambar)

        val success = db.update(TABLE_PRODUK, contentValues,"id="+produkModel.id,null)
        db.close()
        return success
    }

    fun tambahKeranjang(keranjangModel: KeranjangModel):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ORDER_ID, keranjangModel.pesanan_id)
        contentValues.put(PRODUK_ID, keranjangModel.produk_id)
        contentValues.put(JUMLAH, keranjangModel.jumlah)
        contentValues.put(HARGA, keranjangModel.harga)
        contentValues.put(IS_DONE, keranjangModel.is_done)

        val success = db.insert(TABLE_KERANJANG, null, contentValues)
        db.close()
        return success
    }

    fun tambahOrder(orderModel: OrderModel):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(TANGGAL, orderModel.tanggal)
        contentValues.put(PEMBAYARAN, orderModel.pembayaran)
        contentValues.put(IS_DONE, orderModel.is_done)

        val success = db.insert(TABLE_ORDER, null, contentValues)
        db.close()
        return success
    }

    fun hapusOrder():Int{
        val db = this.writableDatabase
        val succes = db.delete(TABLE_ORDER,"is_done=0",null)
        db.close()
        return succes
    }

    fun hapusKeranjang(id: Int):String{
        val db = this.writableDatabase
        val query = "DELETE FROM $TABLE_KERANJANG" +
        " WHERE $PRODUK_ID = " + id + " and $IS_DONE=0"
        db.execSQL(query)
        db.close()
        return "success"
    }

    fun flushKeranjang(id: Int):String{
        val db = this.writableDatabase
        val query = "DELETE FROM $TABLE_KERANJANG" +
                " WHERE $ORDER_ID = " + id + " and $IS_DONE=0"
        db.execSQL(query)
        db.close()
        return "success"
    }

    fun updateKeranjang(keranjangModel: KeranjangModel):String{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, keranjangModel.id)
        contentValues.put(ORDER_ID, keranjangModel.pesanan_id)
        contentValues.put(PRODUK_ID, keranjangModel.produk_id)
        contentValues.put(JUMLAH, keranjangModel.jumlah)
        contentValues.put(HARGA, keranjangModel.harga)

        val query = "update $TABLE_KERANJANG set $JUMLAH="+"'"+keranjangModel.jumlah+"', $HARGA="+"'"+keranjangModel.harga+"' where $PRODUK_ID="+keranjangModel.produk_id+" and is_done = 0"
        db.execSQL(query)
        db.close()
        return "success"
    }

    fun updateOrder(orderModel: OrderModel):String{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, orderModel.id)
        contentValues.put(TANGGAL, orderModel.tanggal)
        contentValues.put(PEMBAYARAN, orderModel.pembayaran)
        contentValues.put(IS_DONE, orderModel.is_done)

        val update_order = "update $TABLE_ORDER set $PEMBAYARAN="+"'"+orderModel.pembayaran+"', $IS_DONE="+"'"+1+"' where $ID="+orderModel.id
        val update_keranjang = "update $TABLE_KERANJANG set $IS_DONE="+"'"+1+"' where $ORDER_ID="+orderModel.id
        db.execSQL(update_order)
        db.execSQL(update_keranjang)
        db.close()
        return "success"
    }

    @SuppressLint("Range")
    fun totalBayar():ArrayList<KeranjangModel>{
        val bayar:ArrayList<KeranjangModel> = ArrayList<KeranjangModel>()
        val selectQuery = "SELECT $ID, $ORDER_ID, $PRODUK_ID, sum(harga) as total_bayar from keranjang where is_done = 0"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var order_id: Int
        var total_bayar: Int
        var produk_id: Int
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                order_id = cursor.getInt(cursor.getColumnIndex("pesanan_id"))
                total_bayar = cursor.getInt(cursor.getColumnIndex("total_bayar"))
                produk_id = cursor.getInt(cursor.getColumnIndex("produk_id"))
//                stok = cursor.getInt(cursor.getColumnIndex("stok"))
//                nama_produk = cursor.getString(cursor.getColumnIndex("nama_produk"))
//                harga = cursor.getInt(cursor.getColumnIndex("harga"))
//                gambar = cursor.getBlob(cursor.getColumnIndex("gambar"))

                val totak_bayar= KeranjangModel(id = id, pesanan_id = order_id, produk_id = produk_id, jumlah =0 , harga = total_bayar, is_done = 0)
                bayar.add(totak_bayar)
            } while (cursor.moveToNext())
        }
        return bayar
    }

    @SuppressLint("Range")
    fun cekKeranjang():List<OrderModel>{
        val contentValues = ContentValues()
        val orderList:ArrayList<OrderModel> = ArrayList<OrderModel>()
        val selectQuery = "SELECT  $TABLE_ORDER.*, $TABLE_KERANJANG.id as keranjang_id, $TABLE_KERANJANG.produk_id,$TABLE_KERANJANG.jumlah  FROM $TABLE_ORDER inner join $TABLE_KERANJANG on $TABLE_KERANJANG.pesanan_id = $TABLE_ORDER.id"+ " where $TABLE_ORDER.$IS_DONE="+"'"+0+"'"

        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var tanggal: String
        var pembayaran: Int
        var is_done: Int
        var keranjang_id: Int
        var produk_id: Int
        var jumlah: Int
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                tanggal = cursor.getString(cursor.getColumnIndex("tanggal"))
                pembayaran = cursor.getInt(cursor.getColumnIndex("pembayaran"))
                is_done = cursor.getInt(cursor.getColumnIndex("is_done"))
                keranjang_id = cursor.getInt(cursor.getColumnIndex("keranjang_id"))
                produk_id = cursor.getInt(cursor.getColumnIndex("produk_id"))
                jumlah = cursor.getInt(cursor.getColumnIndex("jumlah"))
                val order= OrderModel(id = id, tanggal = tanggal, pembayaran = pembayaran, is_done=is_done, keranjang_id = keranjang_id, produk_id=produk_id, jumlah=jumlah)
                orderList.add(order)
            } while (cursor.moveToNext())
        }
        return orderList
    }

    @SuppressLint("Range")
    fun getKeranjangById(id: Int):ArrayList<OrderModel>{
        val contentValues = ContentValues()
        val orderList:ArrayList<OrderModel> = ArrayList<OrderModel>()
        val selectQuery = "SELECT  $TABLE_ORDER.*, $TABLE_KERANJANG.id as keranjang_id, $TABLE_KERANJANG.produk_id,$TABLE_KERANJANG.jumlah  FROM $TABLE_ORDER inner join $TABLE_KERANJANG on $TABLE_KERANJANG.pesanan_id = $TABLE_ORDER.id"+
                " where $TABLE_ORDER.$IS_DONE="+0+" and $TABLE_KERANJANG.$PRODUK_ID="+id

        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var tanggal: String
        var pembayaran: Int
        var is_done: Int
        var keranjang_id: Int
        var produk_id: Int
        var jumlah: Int
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                tanggal = cursor.getString(cursor.getColumnIndex("tanggal"))
                pembayaran = cursor.getInt(cursor.getColumnIndex("pembayaran"))
                is_done = cursor.getInt(cursor.getColumnIndex("is_done"))
                keranjang_id = cursor.getInt(cursor.getColumnIndex("keranjang_id"))
                produk_id = cursor.getInt(cursor.getColumnIndex("produk_id"))
                jumlah = cursor.getInt(cursor.getColumnIndex("jumlah"))
                val order= OrderModel(id = id, tanggal = tanggal, pembayaran = pembayaran, is_done=is_done, keranjang_id = keranjang_id, produk_id=produk_id, jumlah=jumlah)
                orderList.add(order)
            } while (cursor.moveToNext())
        }
        return orderList
    }

    @SuppressLint("Range")
    fun cekOrder():List<OrderModel>{
        val orderList:ArrayList<OrderModel> = ArrayList<OrderModel>()
        val selectQuery = "SELECT  * FROM $TABLE_ORDER where $IS_DONE="+"'"+0+"'"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var tanggal: String
        var pembayaran: Int
        var is_done: Int
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                tanggal = cursor.getString(cursor.getColumnIndex("tanggal"))
                pembayaran = cursor.getInt(cursor.getColumnIndex("pembayaran"))
                is_done = cursor.getInt(cursor.getColumnIndex("is_done"))
                val order= OrderModel(id = id, tanggal = tanggal, pembayaran = pembayaran, is_done=is_done, keranjang_id = 0, produk_id=0, jumlah=0)
                orderList.add(order)
            } while (cursor.moveToNext())
        }
        return orderList
    }

    @SuppressLint("Range")
    fun detailLaporan(id: Int):ArrayList<DetailLaporanModel>{
        val detailLaporan:ArrayList<DetailLaporanModel> = ArrayList<DetailLaporanModel>()
        val selectQuery = "SELECT  $TABLE_ORDER.id, $TABLE_ORDER.tanggal, $TABLE_PRODUK.nama_produk,$TABLE_KERANJANG.jumlah, $TABLE_PRODUK.harga  FROM $TABLE_ORDER " +
                "inner join $TABLE_KERANJANG on $TABLE_KERANJANG.pesanan_id = $TABLE_ORDER.id" +
                " inner join $TABLE_PRODUK on $TABLE_PRODUK.id = $TABLE_KERANJANG.produk_id"+
                " where $TABLE_ORDER.id="+id+" and not $TABLE_KERANJANG.jumlah = 0 "

        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var tanggal: String
        var nama_produk: String
        var jumlah: Int
        var harga: Int
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                tanggal = cursor.getString(cursor.getColumnIndex("tanggal"))
                nama_produk = cursor.getString(cursor.getColumnIndex("nama_produk"))
                jumlah = cursor.getInt(cursor.getColumnIndex("jumlah"))
                harga = cursor.getInt(cursor.getColumnIndex("harga"))
                val detail= DetailLaporanModel(pesanan_id = id, tanggal = tanggal, nama_produk = nama_produk, jumlah=jumlah, harga = harga)
                detailLaporan.add(detail)
            } while (cursor.moveToNext())
        }
        return detailLaporan
    }

}
