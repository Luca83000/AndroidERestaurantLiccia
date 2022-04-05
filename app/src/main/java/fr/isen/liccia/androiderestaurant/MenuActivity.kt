package fr.isen.liccia.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.isen.liccia.androiderestaurant.ble.BLEScanActivity
import fr.isen.liccia.androiderestaurant.databinding.ActivityMenuBinding
import fr.isen.liccia.androiderestaurant.model.DataResult
import fr.isen.liccia.androiderestaurant.model.Item
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.ArrayList

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var monRecycler: RecyclerView
    private var itemsList = ArrayList<Item>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val actionBar = supportActionBar
        val categoryName = intent.getStringExtra("category")
        actionBar!!.title = categoryName
        binding.category.text = categoryName


        monRecycler = findViewById(R.id.itemsList)
        binding.itemsList.layoutManager = LinearLayoutManager(applicationContext)
        binding.itemsList.adapter = CategoryAdapter(itemsList) {
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra(ITEM_KEY, it)
            Toast.makeText(this, "Vous avez sélectionné $it", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
        getDataFromApi(intent.getStringExtra("category") ?: "")
    }

    companion object {
        const val ITEM_KEY = "item"
    }

    private fun getDataFromApi(category: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://test.api.catering.bluecodegames.com/" + "menu"
        val jsonObject = JSONObject()
        jsonObject.put("id_shop", "1")
        jsonObject.toString()
        val requestBody = jsonObject.toString()

        val stringReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST, url, jsonObject,
                { response ->
                    // response
                    val strResp = response.toString()
                    Log.d("API", strResp)
                    val dataResult = Gson().fromJson(strResp, DataResult::class.java)

                    val items = dataResult.data.firstOrNull { it.name_fr == category }?.items
                        ?: arrayListOf()
                    binding.itemsList.adapter = CategoryAdapter(items) {
                        val intent = Intent(this, DetailsActivity::class.java)
                        intent.putExtra(ITEM_KEY, it)
                        Toast.makeText(
                            this,
                            "Vous avez sélectionné :  ${it.name_fr}",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(intent)
                    }
                },
                Response.ErrorListener { error ->
                    Log.d("API", "error => $error")
                }
            ) {
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringReq)

    }

    private fun goToBasket() {
        val myIntent = Intent(this, BasketActivity::class.java)
        Toast.makeText(
            this,
            "Redirection vers le panier",
            Toast.LENGTH_SHORT
        ).show()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val menu = menu!!.findItem(R.id.panier)
        val view = menu.actionView
        view.setOnClickListener {
            onOptionsItemSelected(menu)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ble -> {
                goToBluetooth()
            }
            R.id.panier -> {
                goToBasket()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
