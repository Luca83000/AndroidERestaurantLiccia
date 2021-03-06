package fr.isen.liccia.androiderestaurant

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import fr.isen.liccia.androiderestaurant.databinding.ActivityBasketBinding
import fr.isen.liccia.androiderestaurant.model.Basket
import fr.isen.liccia.androiderestaurant.model.BasketItems
import java.io.File

class BasketActivity : MenuBaseActivity() {
    private lateinit var binding: ActivityBasketBinding
    private val itemsList = ArrayList<BasketItems>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBasketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar!!.title = "Panier"

        val file = File(cacheDir.absolutePath + "/basket.json")

        if (file.exists()) {
            val basketItems: List<BasketItems> =
                Gson().fromJson(file.readText(), Basket::class.java).data
            display(basketItems)
        }

        val quantity = getString(R.string.basket_total_quantity) + " " + this.getSharedPreferences(
            getString(R.string.sp_file_name),
            Context.MODE_PRIVATE
        ).getInt(getString(R.string.sp_total_quantity), 0).toString() + " article(s)"
        binding.basketTotalQuantity.text = quantity

        val price = getString(R.string.total_price) + " " + this.getSharedPreferences(
            getString(R.string.sp_file_name),
            Context.MODE_PRIVATE
        ).getFloat(getString(R.string.sp_total_price), 0.0f).toString() + " € "
        binding.basketTotalPrice.text = price


        binding.basketButtonDeleteAll.setOnClickListener {
            deleteBasketData()
            finish()
            changeActivity()
        }

        binding.homeButton.setOnClickListener {
            finish()
            changeActivity()
        }

        binding.basketButtonBuy.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.linkedin.com/in/luca-liccia/")
                )
            )
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun display(itemsList: List<BasketItems>) {
        binding.basketList.layoutManager = LinearLayoutManager(this)
        binding.basketList.adapter = BasketAdapter(itemsList as ArrayList<BasketItems>) {
            deleteItemBasket(it)
        }
        setupBadge()
        binding.basketList.adapter?.notifyDataSetChanged()
    }

    private fun deleteItemBasket(item: BasketItems) {
        val file = File(cacheDir.absolutePath + "/basket.json")
        var itemBasket: List<BasketItems> = ArrayList()

        if (file.exists()) {
            itemBasket = Gson().fromJson(file.readText(), Basket::class.java).data
            itemBasket = itemBasket - item
            updateSharedPreferences(item.quantity, item.item.prices[0].price.toFloat())
            setupBadge()
        }

        setupBadge()
        file.writeText(Gson().toJson(Basket(itemBasket)))

        finish()
        this.recreate()
    }

    private fun deleteBasketData() {
        File(cacheDir.absolutePath + "/basket.json").delete()
        this.getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE).edit()
            .remove(getString(R.string.sp_total_price)).apply()
        this.getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE).edit()
            .remove(getString(R.string.sp_total_quantity)).apply()
        Toast.makeText(this, getString(R.string.basket_delete_all_txt), Toast.LENGTH_SHORT).show()
        setupBadge()
    }

    private fun updateSharedPreferences(quantity: Int, price: Float) {
        val sharedPreferences =
            this.getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE)

        val oldQuantity = sharedPreferences.getInt(getString(R.string.sp_total_quantity), 0)
        val newQuantity = oldQuantity - quantity
        sharedPreferences.edit().putInt(getString(R.string.sp_total_quantity), newQuantity).apply()

        val oldPrice = sharedPreferences.getFloat(getString(R.string.sp_total_price), 0.0f)
        val newPrice = oldPrice - (quantity * price)
        sharedPreferences.edit().putFloat(getString(R.string.sp_total_price), newPrice).apply()

        setupBadge()
    }

    private fun changeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}
