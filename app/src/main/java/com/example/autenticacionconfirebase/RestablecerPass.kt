package com.example.autenticacionconfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_restablecer_pass.*

class RestablecerPass : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_restablecer_pass)
        setup()
    }

    private fun setup() {
        bt_restPass.setOnClickListener {
            if(!et_restaEmail.text.isEmpty()){
                firebaseAuth.sendPasswordResetEmail(et_restaEmail.text.toString())
                showMesagge("Aceptar","Mensaje enviado")
            }else{
                showMesagge("Error","Introduzca un email valido")
            }
        }
    }
    private fun showMesagge(titulo: String,mensaje: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}