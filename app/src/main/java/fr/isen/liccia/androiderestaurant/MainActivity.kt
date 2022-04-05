package fr.isen.liccia.androiderestaurant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainActivity", "onCreate Called")
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar!!.title = "Accueil"
        val secondActivityBtn = findViewById<View>(R.id.buttonMain)

        secondActivityBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("MainActivity", "onDestroy Called")
    }
}

