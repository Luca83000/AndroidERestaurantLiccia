package fr.isen.liccia.androiderestaurant

import android.content.Context
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
import fr.isen.liccia.androiderestaurant.model.Item

class DetailsActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityDetailsBinding
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val item = intent.getSerializableExtra(MenuActivity.ITEM_KEY) as Item

        binding.detailTitle.text = item.name_fr

        binding.ingredientsText.text = item.ingredients.joinToString(", " ){it.name_fr}

        binding.buttonPrix.text = item.prices.joinToString(", " ){"Total  " + it.price.toString() + " €"}

        val carouselAdapter = CarouselAdapter(this,item.images)
        binding.detailSlider.adapter = carouselAdapter

        val actionBar = supportActionBar
        actionBar!!.title = "Commande"


        val quantity = getString(R.string.quantity_text)
        binding.quantityText.text = quantity

        val price = getString(R.string.button_prix)
        binding.buttonPrix.text = price

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

    /*private fun initDetail(dish: Dish) {

        var nbInBucket = 1
        binding.buttonPlus.setOnClickListener {
            changeNumber(dish, 1)
        }
        binding.buttonMoins.setOnClickListener {
            changeNumber(dish, 0)
        }
    }*/

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