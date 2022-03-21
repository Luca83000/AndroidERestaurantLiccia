package fr.isen.liccia.androiderestaurant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val actionBar = supportActionBar
        actionBar!!.title = "Menu" // Menu

        val imageClickable = findViewById<ImageView>(R.id.cauchemarHome)
        imageClickable.setOnClickListener {

            val myIntent2 = Intent(this, MainActivity::class.java)
            Toast.makeText(
                this@HomeActivity,
                "Redirection vers la page d'accueil",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(myIntent2)

        }

        val textClickable = findViewById<TextView>(R.id.EntreeText)
        textClickable.setOnClickListener {

            val myIntent = Intent(this, MenuActivity::class.java)
            Toast.makeText(
                this@HomeActivity,
                "Entrées",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(myIntent)

            textClickable.movementMethod = LinkMovementMethod.getInstance();
        }


            val textClickable2 = findViewById<TextView>(R.id.PlatText)
            textClickable2.setOnClickListener {

                val myIntent2 = Intent(this, MenuActivity::class.java)
                Toast.makeText(
                    this@HomeActivity,
                    "Plats",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(myIntent2)

                textClickable2.movementMethod = LinkMovementMethod.getInstance();


                val textClickable3 = findViewById<TextView>(R.id.DessertText)
                textClickable3.setOnClickListener {

                    val myIntent3 = Intent(this, MenuActivity::class.java)
                    Toast.makeText(
                        this@HomeActivity,
                        "Desserts",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(myIntent3)

                    textClickable3.movementMethod = LinkMovementMethod.getInstance();
                }



                }
            }
        }

