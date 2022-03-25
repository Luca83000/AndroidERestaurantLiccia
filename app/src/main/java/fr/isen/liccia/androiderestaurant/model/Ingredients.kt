package fr.isen.liccia.androiderestaurant.model

import java.io.Serializable

data class Ingredients(
    val id: Int,
    val id_shop: Int,
    val name_fr: String,
    val name_en: String
): Serializable
