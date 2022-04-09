package fr.isen.liccia.androiderestaurant

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.isen.liccia.androiderestaurant.databinding.ActivityMapsBinding


class MapsActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMapsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar!!.title = "Maps"

        binding.btnMaps.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=Le+KUDETA")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }


    }

}