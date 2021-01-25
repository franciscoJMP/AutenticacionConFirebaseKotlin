package com.example.autenticacionconfirebase


import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private lateinit var emailUsuario:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        firebaseAuth = FirebaseAuth.getInstance()
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        emailUsuario=firebaseAuth.currentUser?.email.toString()
        tx_correo.text = emailUsuario
        cargarDatos()
        setup()

    }

    private fun setup() {
        bt_cerrarSesion.setOnClickListener {
            firebaseAuth.signOut()
            GoogleSignIn.getClient(
                this,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            ).signOut()
            LoginManager.getInstance().logOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        bt_guardar.setOnClickListener {
            guardarDatos()
            cargarDatos()
            showMessage("Exito", "Datos guardados con exito")

        }

        bt_eliminar.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Desea eliminar todos los datos de este usuario?")
                .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                    eliminarDatos()
                    cargarDatos()

                }).setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, id ->
                })

            val dialog: AlertDialog = builder.create()
            dialog.show()

        }
    }

    private fun cargarDatos(){
        db.collection("usuarios").document(emailUsuario).get().addOnSuccessListener {
            tx_nombre.setText(it.get("nombre") as String?)
            tx_apellidos.setText(it.get("apellidos") as String?)
            tx_telefono.setText(it.get("telefono") as String?)
            tx_direccion.setText(it.get("direccion") as String?)
        }
    }
    private fun guardarDatos() {
        db.collection("usuarios").document(emailUsuario).set(
            hashMapOf(
                "nombre" to tx_nombre.text.toString(),
                "apellidos" to tx_apellidos.text.toString(),
                "telefono" to tx_telefono.text.toString(),
                "direccion" to tx_direccion.text.toString()
            )
        )


    }

    private fun eliminarDatos() {
        db.collection("usuarios").document(emailUsuario).delete()
    }

    private fun showMessage(s: String, s1: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(s)
        builder.setMessage(s1)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }


    override fun onBackPressed() {}
}