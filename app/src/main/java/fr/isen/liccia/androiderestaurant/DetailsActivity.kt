package fr.isen.liccia.androiderestaurant

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import fr.isen.liccia.androiderestaurant.databinding.ActivityDetailsBinding
import fr.isen.liccia.androiderestaurant.model.Basket
import fr.isen.liccia.androiderestaurant.model.BasketItems
import fr.isen.liccia.androiderestaurant.model.Item
import java.io.File

class DetailsActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityDetailsBinding
    private lateinit var button: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val item = intent.getSerializableExtra(MenuActivity.ITEM_KEY) as Item

        binding.detailTitle.text = item.name_fr

        binding.ingredientsText.text = item.ingredients.joinToString(", ") { it.name_fr }

        binding.buttonPrix.text =
            item.prices.joinToString(", ") { "Total : " + it.price.toString() + " €" }

        val carouselAdapter = CarouselAdapter(this, item.images)
        binding.detailSlider.adapter = carouselAdapter

        val actionBar = supportActionBar
        actionBar!!.title = "Commande"


        val quantity = getString(R.string.quantity_text)
        binding.quantityText.text = quantity

        val price = getString(R.string.button_prix)
        binding.buttonPrix.text = price

        initDetail(item)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ble -> Toast.makeText(this, "BLE sélectionné", Toast.LENGTH_SHORT).show()
            R.id.panier -> Toast.makeText(this, "Panier sélectionné", Toast.LENGTH_SHORT).show()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun initDetail(item: Item) {

        var nbInBucket = 1
        binding.buttonplus.setOnClickListener {
            changeNumber(item, 1)
        }
        binding.buttonmoins.setOnClickListener {
            changeNumber(item, 0)
        }
        binding.detailTitle.text = item.name_fr

        val txt = getString(R.string.totalPrice) + item.prices[0].price + " €"
        binding.buttonPrix.text = txt

        binding.buttonPrix.setOnClickListener {
            button = findViewById(R.id.buttonPrix)
            button.setOnClickListener {
                val snackBar = Snackbar.make(
                    it, "C'est dans le panier mon frère",
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null)
                val snackBarView = snackBar.view
                snackBarView.setBackgroundColor(Color.LTGRAY)
                val textView =
                    snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                textView.setTextColor(Color.BLACK)
                snackBar.show()
            }
            Toast.makeText(this, getString(R.string.addToBasket), Toast.LENGTH_SHORT).show()
            updateFile(BasketItems(item, nbInBucket))
            updateSharedPreferences(nbInBucket, (item.prices[0].price.toFloat() * nbInBucket))
            finish()
            changeActivity()
        }

    }


    private fun changeNumber(item: Item, minusOrplus: Int) {
        var nb = (binding.quantityText.text as String).toInt()
        if (minusOrplus == 0) {
            if (nb == 1) {
            } else {
                nb--
            }
        } else {
            nb++
        }
        binding.quantityText.text = nb.toString()
        val price = item.prices[0].price.toFloat()
        val totalPrice = getString(R.string.totalPrice) + price * nb + " €"
        binding.buttonPrix.text = totalPrice
        changePrice(item, nb)
    }


    @SuppressLint("SetTextI18n")
    private fun changePrice(item: Item, nb: Int) {
        var newPrice = item.prices[0].price.toFloatOrNull()
        newPrice = newPrice?.times(nb)
        binding.buttonPrix.text = "Total : $newPrice €"
    }

    private fun updateFile(itemBasket: BasketItems) {
        val file = File(cacheDir.absolutePath + "/basket.json")
        var dishesBasket: List<BasketItems> = ArrayList()

        if (file.exists()) {
            dishesBasket = Gson().fromJson(file.readText(), Basket::class.java).data
        }

        var dupli = false
        for (i in dishesBasket.indices) {
            if (dishesBasket[i].item == itemBasket.item) {
                dishesBasket[i].quantity += itemBasket.quantity
                dupli = true
            }
        }

        if (!dupli) {
            dishesBasket = dishesBasket + itemBasket
        }

        file.writeText(Gson().toJson(Basket(dishesBasket)))
    }

    private fun updateSharedPreferences(quantity: Int, price: Float) {
        val sharedPreferences = this.getSharedPreferences(getString(R.string.spFileName), Context.MODE_PRIVATE)

        val oldQuantity = sharedPreferences.getInt(getString(R.string.spTotalQuantity), 0)
        val newQuantity = oldQuantity + quantity
        sharedPreferences.edit().putInt(getString(R.string.spTotalQuantity), newQuantity).apply()

        val oldPrice = sharedPreferences.getFloat(getString(R.string.spTotalPrice), 0.0f)
        val newPrice = oldPrice + price
        sharedPreferences.edit().putFloat(getString(R.string.spTotalPrice), newPrice).apply()
    }

    private fun changeActivity() {
        val intent = Intent(this@DetailsActivity, BasketActivity::class.java)
        startActivity(intent)
    }

}