package fr.isen.liccia.androiderestaurant

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


open class CartCompactActivity : AppCompatActivity() {
    private var textCartItemCount: TextView? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        val menuItem = menu!!.findItem(R.id.panier)
        val actionView = menuItem.actionView
        textCartItemCount = actionView.findViewById<View>(R.id.cartBadge) as TextView
        setupBadge()
        actionView.setOnClickListener { onOptionsItemSelected(menuItem) }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =when(item.itemId) {
        R.id.panier -> {
            startActivity(Intent(this, BasketActivity::class.java))
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        setupBadge()
    }

    protected fun setupBadge() {
        if (textCartItemCount != null) {
            val mCartItemCount = getSharedPreferences(
                "baskets",
                Context.MODE_PRIVATE
            ).getInt("nombre total", 0)
                textCartItemCount!!.text = java.lang.String.valueOf(mCartItemCount.coerceAtMost(99))
                if (textCartItemCount!!.visibility != View.VISIBLE) {
                    textCartItemCount!!.visibility = View.VISIBLE
            }
        }
    }

}