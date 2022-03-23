package fr.isen.liccia.androiderestaurant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import fr.isen.liccia.androiderestaurant.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HomeActivity", "onCreate Called")

        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar!!.title = "Menu" // Menu

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.ble -> Toast.makeText(this, "BLE sélectionné", Toast.LENGTH_SHORT).show()
            R.id.panier -> Toast.makeText(this, "Panier sélectionné", Toast.LENGTH_SHORT).show()
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Home", "onDestroy Called")
    }


}


