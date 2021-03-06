package fr.isen.liccia.androiderestaurant

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.isen.liccia.androiderestaurant.ble.BLEScanActivity


open class MenuBaseActivity : AppCompatActivity() {
    private var textCartItemCount: TextView? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        val menuItem = menu!!.findItem(R.id.panier)
        val actionView = menuItem.actionView
        textCartItemCount = actionView.findViewById<View>(R.id.cartBadge) as TextView?
        setupBadge()
        actionView.setOnClickListener { onOptionsItemSelected(menuItem) }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.panier -> {
                Toast.makeText(this, "Panier sélectionné", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this, BasketActivity::class.java))
            }
            R.id.ble -> {
                Toast.makeText(this, "BLE sélectionné", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this, BLEScanActivity::class.java))
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        setupBadge()
        super.onResume()
    }

    protected fun setupBadge() {
        if (textCartItemCount != null) {
            val quantityBasket = getSharedPreferences(
                getString(R.string.sp_file_name),
                Context.MODE_PRIVATE
            ).getInt(getString(R.string.sp_total_quantity), 0)
                textCartItemCount!!.text = java.lang.String.valueOf(quantityBasket.coerceAtMost(99))
                if (quantityBasket >= 99) {
                    textCartItemCount!!.text = "+99"
                }
                if (quantityBasket == 0) {
                    textCartItemCount!!.text = "0"
                }
        }
    }

}