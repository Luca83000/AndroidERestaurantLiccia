package fr.isen.liccia.androiderestaurant.model

import java.io.Serializable

data class Basket(
    val data: List<BasketItems>
) : Serializable
