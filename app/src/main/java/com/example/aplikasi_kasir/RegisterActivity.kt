package com.example.aplikasi_kasir

import android.content.Intent
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.aplikasi_kasir.database.DBHandler
import com.example.aplikasi_kasir.database.UserModel

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btn_login: TextView = findViewById(R.id.login)
        val btn_register: Button = findViewById(R.id.btn_register)

        btn_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btn_register.setOnClickListener {
            saveRecord()
        }
    }

    private fun saveRecord() {
        val edt_nama: EditText = findViewById(R.id.edt_nama)
        val edt_email: TextView = findViewById(R.id.edt_email)
        val edt_password: TextView = findViewById(R.id.edt_password)
        val edt_no_hp: TextView = findViewById(R.id.edt_no_hp)
        val edt_alamat: TextView = findViewById(R.id.edt_alamat)
        val nama = edt_nama.text.toString()
        val email = edt_email.text.toString()
        val password = edt_password.text.toString()
        val no_hp = edt_no_hp.text.toString()
        val alamat = edt_alamat.text.toString()
        val databaseHandler: DBHandler= DBHandler(this)

        if(nama.trim()!="" && email.trim()!="" && password.trim()!="" && no_hp.trim()!="" && alamat.trim()!=""){
            if (Patterns.EMAIL_ADDRESS.matcher(edt_email.text.toString()).matches()) {
                val status = databaseHandler.register(UserModel(0,nama,email, password, no_hp, alamat))
                if(status > -1){
                    Toast.makeText(applicationContext,"Berhasil Register, Silahkan Login Menggunakan Akun Anda!",Toast.LENGTH_LONG).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this, "Format Email Salah", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(applicationContext,"Kolom Tidak Boleh Kosong",Toast.LENGTH_LONG).show()
        }

    }
    }
