package es.ua.eps.raw_filmoteca

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.common.SignInButton
import es.ua.eps.raw_filmoteca.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivityLoginBinding.inflate(layoutInflater)

        with(bindings) {
            setContentView(root)

            signInButton.setSize(SignInButton.SIZE_STANDARD)
            signInButton.setOnClickListener() {
                Log.d(TAG, "onCreate: SIGN IN")
            }
        }
    }
}