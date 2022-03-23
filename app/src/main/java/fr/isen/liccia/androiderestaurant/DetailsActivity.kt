package fr.isen.liccia.androiderestaurant

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import fr.isen.liccia.androiderestaurant.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityDetailsBinding
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.detailTitle.text = intent.getStringExtra(MenuActivity.ITEM_KEY)

        val actionBar = supportActionBar
        //actionBar!!.title = "Commande"
        actionBar!!.title = intent.getStringExtra(MenuActivity.ITEM_KEY)


        button = findViewById(R.id.buttonPrix)
        button.setOnClickListener {
            val snackBar = Snackbar.make(
                it, "C'est dans le panier mon frère",
                Snackbar.LENGTH_LONG
            ).setAction("Action", null)
            val snackBarView = snackBar.view
            snackBarView.setBackgroundColor(Color.LTGRAY)
            val textView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            textView.setTextColor(Color.BLACK)
            snackBar.show()
        }

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

}