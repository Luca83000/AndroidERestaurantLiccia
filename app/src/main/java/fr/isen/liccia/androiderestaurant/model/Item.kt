package fr.isen.liccia.androiderestaurant.model

import java.io.Serializable

data class Item(
    val id: Int,
    val name_fr: String,
    val name_en: String,
    val id_category: Int,
    val categ_name_fr: String,
    val categ_name_en: String,
    val images: ArrayList<String>,
    val ingredients: ArrayList<Ingredients>,
    val prices: ArrayList<Price>
) : Serializable
