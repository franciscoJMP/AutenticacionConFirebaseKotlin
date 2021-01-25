package com.example.autenticacionconfirebase


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    val SINGINGOOGLE: Int = 1
    private val callbackManager = CallbackManager.Factory.create()

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configurarAccesoGoogle()
        firebaseAuth = FirebaseAuth.getInstance()
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        sesion()
        setup()
    }


    private fun sesion() {
        val prefs: SharedPreferences =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

    }

    private fun configurarAccesoGoogle() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }


    private fun setup() {
        bt_registrar.setOnClickListener {
            if (et_email.text.isNotEmpty() && et_pass.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    et_email.text.toString(), et_pass.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showOkMessage("Registro","Usuario registrado con exito")
                        persistencia()
                        sesion()
                    } else {
                        showFailMessage("Error","Error al registrar este usuario")
                    }
                }
            }
        }
        bt_acceder.setOnClickListener {
            if (et_email.text.isNotEmpty() && et_pass.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    et_email.text.toString(), et_pass.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showOkMessage("Acceso","Acceso correcto")
                        persistencia()
                        sesion()
                    } else {
                        showFailMessage("Error","Error al acceder, compruebe que este usuario este registrado y que la contraseña sea correcta")
                    }
                }
            }
        }
        bt_accGoogle.setOnClickListener {
            val intentGoogle: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(intentGoogle, SINGINGOOGLE)
        }
        bt_accFB.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {
                        result?.let {
                            val token: AccessToken = it.accessToken
                            val credential: AuthCredential =
                                FacebookAuthProvider.getCredential(token.token)
                            FirebaseAuth.getInstance().signInWithCredential(credential)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        showOkMessage("Acceso","Acceso con su usuario de Facebook correcto")

                                    } else {
                                        showFailMessage("Error","Error al acceder con su usario de Facebook")
                                    }
                                }
                        }
                    }

                    override fun onCancel() {

                    }

                    override fun onError(error: FacebookException?) {

                    }
                })
        }

        bt_restablecerPass.setOnClickListener {
            val intent = Intent(this, RestablecerPass::class.java)
            startActivity(intent)
        }


    }

    private fun persistencia() {
        val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", et_email.text.toString())
        prefs.apply()
    }

    private fun showFailMessage(title: String,message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showOkMessage(title: String,message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
            .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
            })

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }



    private fun firebaseAutConGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                showOkMessage("Acceso","Acceso con usuario de google correcto")
            } else {
                showFailMessage("Error","Error al conectar con su usuaro de Google")
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SINGINGOOGLE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val cuenta = task.getResult(ApiException::class.java)
                firebaseAutConGoogle(cuenta)

            } catch (e: ApiException) {
                showFailMessage("Error","Error al conectar con su usuaro de Google")
            }
        }
    }

    override fun onBackPressed() {}



}