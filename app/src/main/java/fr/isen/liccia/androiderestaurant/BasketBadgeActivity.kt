package fr.isen.liccia.androiderestaurant

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity


open class CartAppCompatActivity(@MenuRes private val menuRes: Int) : AppCompatActivity() {
    private var textCartItemCount: TextView? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(menuRes, menu)

        val menuItem = menu!!.findItem(R.id.panier)

        val actionView = menuItem.actionView
        textCartItemCount = actionView.findViewById<View>(R.id.cart_badge) as TextView

        //setupBadge()

        actionView.setOnClickListener { onOptionsItemSelected(menuItem) }

        return true
    }

    override fun onResume() {
        //setupBadge()
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.panier -> {
            startActivity(Intent(this, BasketActivity::class.java))
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    /*protected fun setupBadge() {
        if (textCartItemCount != null) {
            val mCartItemCount = BasketActivity().getNbItems()
            if (mCartItemCount == 10) {
                if (textCartItemCount!!.visibility != View.INVISIBLE) {
                    textCartItemCount!!.visibility = View.INVISIBLE
                }
            } else {
                textCartItemCount!!.text = java.lang.String.valueOf(mCartItemCount.coerceAtMost(99))
                if (textCartItemCount!!.visibility != View.VISIBLE) {
                    textCartItemCount!!.visibility = View.VISIBLE
                }
            }
        }
    }*/
}