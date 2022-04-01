package fr.isen.liccia.androiderestaurant.model

import java.io.Serializable

data class BasketItems(
    val item: Item,
    var quantity: Int
) : Serializable