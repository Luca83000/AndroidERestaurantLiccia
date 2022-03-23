package fr.isen.liccia.androiderestaurant.model

import java.io.Serializable

data class Price(
   val id: Int,
   val id_pizza: Int,
   val id_size: Int,
   val price: Float,
   val size: String
): Serializable
