package fr.isen.liccia.androiderestaurant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import fr.isen.liccia.androiderestaurant.ble.BLEScanActivity
import fr.isen.liccia.androiderestaurant.databinding.ActivityHomeBinding

class HomeActivity : MenuBaseActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HomeActivity", "onCreate Called")

        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar!!.title = "Menu"

        binding.cauchemarHome.setOnClickListener {
            goToHome()
        }

        binding.entreeText.setOnClickListener {
            goToCategory(getString(R.string.text_entree))
            binding.entreeText.movementMethod = LinkMovementMethod.getInstance()
        }

        binding.platText.setOnClickListener {
            goToCategory(getString(R.string.text_plats))
            binding.platText.movementMethod = LinkMovementMethod.getInstance()
        }


        binding.dessertText.setOnClickListener {
            goToCategory(getString(R.string.text_desserts))
            binding.dessertText.movementMethod = LinkMovementMethod.getInstance()
        }

        binding.bleText.setOnClickListener {
            goToBluetooth()
            binding.dessertText.movementMethod = LinkMovementMethod.getInstance()
        }

        binding.mapsText.setOnClickListener {
            Toast.makeText(this, "Maps", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,MapsActivity::class.java)
            startActivity(intent)
        }

    }

    private fun goToHome() {
        val myIntent = Intent(this, MainActivity::class.java)
        Toast.makeText(
            this@HomeActivity,
            "Redirection vers la page d'accueil",
            Toast.LENGTH_SHORT
        ).show()
        startActivity(myIntent)
    }

    private fun goToCategory(category: String) {
        val myIntent = Intent(this, MenuActivity::class.java)
        Toast.makeText(
            this@HomeActivity,
            category,
            Toast.LENGTH_SHORT
        ).show()
        myIntent.putExtra("category", category)
        startActivity(myIntent)
    }

    private fun goToBluetooth() {
        val myIntent = Intent(this, BLEScanActivity::class.java)
        Toast.makeText(
            this,
            "Redirection vers le BLE",
            Toast.LENGTH_SHORT
        ).show()
        startActivity(myIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Home", "onDestroy Called")
    }
}


