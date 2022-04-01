package fr.isen.liccia.androiderestaurant.model

import java.io.Serializable

data class Data(
    var name_fr: String,
    var name_en: String,
    var items: ArrayList<Item>
) : Serializable

