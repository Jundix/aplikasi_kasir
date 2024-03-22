package com.example.aplikasi_kasir

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.aplikasi_kasir.database.DBHandler
import com.example.aplikasi_kasir.database.UserModel
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    var PREFS_KEY = "prefs"
    var IS_LOGIN = "is_login"
    var is_login = ""
    var NAMA = "nama"
    var NO_HP = "no_hp"
    var ALAMAT = "alamat"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btn_register: TextView = findViewById(R.id.register)
        val btn_login: Button = findViewById(R.id.btn_login)
        val edt_email: EditText = findViewById(R.id.edt_email)
        val edt_password: EditText = findViewById(R.id.edt_password)

        sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        is_login = sharedPreferences.getString(IS_LOGIN, "").toString()
        val nama: String = sharedPreferences.getString(NAMA, "").toString()
        val alamat: String = sharedPreferences.getString(ALAMAT, "").toString()
        val no_hp: String = sharedPreferences.getString(NO_HP, "").toString()

//        Log.d("message", is_login)
//        Log.d("message", nama)
//        Log.d("message", alamat)
//        Log.d("message", no_hp)

        btn_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btn_login.setOnClickListener {
            if(edt_email.text.toString().trim()!="" && edt_password.text.toString().trim()!=""){
                if (Patterns.EMAIL_ADDRESS.matcher(edt_email.text.toString()).matches()) {
                    login()
                } else {
                    Toast.makeText(this, "Format Email Salah", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(applicationContext,"Kolom Tidak Boleh Kosong",Toast.LENGTH_LONG).show()
            }

        }
    }

    override fun onStart() {
        super.onStart()
        if (is_login == "true") {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun login() {
        val databaseHandler: DBHandler = DBHandler(this)
        val edt_email: EditText = findViewById(R.id.edt_email)
        val edt_password: EditText = findViewById(R.id.edt_password)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        val user: List<UserModel> = databaseHandler.login(edt_email.text.toString())
        val id = Array<String>(user.size) { "0" }
        val nama = Array<String>(user.size) { "null" }
        val email = Array<String>(user.size) { "null" }
        val password = Array<String>(user.size) { "null" }
        val no_hp = Array<String>(user.size) { "null" }
        val alamat = Array<String>(user.size) { "null" }
        var index = 0
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        if(user.isNotEmpty()){
            for (e in user) {
                id[index] = e.id.toString()
                nama[index] = e.nama
                alamat[index] = e.alamat
                no_hp[index] = e.no_hp
                email[index] = e.email
                password[index] = e.password
                index++

                if(password[0]==edt_password.text.toString()){
                    editor.putString(IS_LOGIN, "true")
                    editor.putString(NAMA, nama[0])
                    editor.putString(ALAMAT, alamat[0])
                    editor.putString(NO_HP, no_hp[0])
                    editor.apply()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(applicationContext,"Password Yang Anda Masukan Salah",Toast.LENGTH_LONG).show()
                }
            }
        }else{
            Toast.makeText(applicationContext,"Email Tidak Terdaftar",Toast.LENGTH_LONG).show()
        }
    }
    }
