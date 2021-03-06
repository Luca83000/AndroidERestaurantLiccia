package fr.isen.liccia.androiderestaurant

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import fr.isen.liccia.androiderestaurant.ble.BLEScanActivity
import fr.isen.liccia.androiderestaurant.databinding.ActivityDetailsBinding
import fr.isen.liccia.androiderestaurant.model.Basket
import fr.isen.liccia.androiderestaurant.model.BasketItems
import fr.isen.liccia.androiderestaurant.model.Item
import java.io.File

@Suppress("ControlFlowWithEmptyBody")
class DetailsActivity : MenuBaseActivity() {
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val item = intent.getSerializableExtra(MenuActivity.ITEM_KEY) as Item

        binding.detailTitle.text = item.name_fr

        binding.ingredientsText.text = item.ingredients.joinToString(", ") { it.name_fr }

        binding.buttonPrix.text =
            item.prices.joinToString(", ") { "Total : " + it.price + " €" }

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


    private fun initDetail(item: Item) {

        var nbInBucket = 1
        binding.buttonplus.setOnClickListener {
            changeNumber(item, 1)
            nbInBucket += 1
        }
        binding.buttonmoins.setOnClickListener {
            changeNumber(item, 0)
            if (nbInBucket==1){}
            else{nbInBucket -= 1}
        }
        binding.detailTitle.text = item.name_fr

        val txt = getString(R.string.total_price) + item.prices[0].price + " €"
        binding.buttonPrix.text = txt

        binding.buttonPrix.setOnClickListener {
           Snackbar.make(
                it, "Article(s) ajouté(s) au panier : " + nbInBucket + " " + binding.detailTitle.text,
                Snackbar.LENGTH_LONG
            ).setAction("Voir le panier"){
                startActivity(Intent(this, BasketActivity::class.java))
            }
            .show()

            updateFile(BasketItems(item, nbInBucket))
            updateSharedPreferences(nbInBucket, (item.prices[0].price.toFloat() * nbInBucket))
            setupBadge()
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
        val totalPrice = getString(R.string.total_price) + price * nb + " €"
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
        var itemsBasket: List<BasketItems> = ArrayList()

        if (file.exists()) {
            itemsBasket = Gson().fromJson(file.readText(), Basket::class.java).data
        }

        var dupli = false
        for (i in itemsBasket.indices) {
            if (itemsBasket[i].item == itemBasket.item) {
                itemsBasket[i].quantity += itemBasket.quantity
                dupli = true
            }
        }

        if (!dupli) {
            itemsBasket = itemsBasket + itemBasket
        }

        file.writeText(Gson().toJson(Basket(itemsBasket)))
    }

    private fun updateSharedPreferences(quantity: Int, price: Float) {
        val sharedPreferences =
            this.getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE)

        val oldQuantity = sharedPreferences.getInt(getString(R.string.sp_total_quantity), 0)
        val newQuantity = oldQuantity + quantity
        sharedPreferences.edit().putInt(getString(R.string.sp_total_quantity), newQuantity).apply()

        val oldPrice = sharedPreferences.getFloat(getString(R.string.sp_total_price), 0.0f)
        val newPrice = oldPrice + price
        sharedPreferences.edit().putFloat(getString(R.string.sp_total_price), newPrice).apply()
    }

}
