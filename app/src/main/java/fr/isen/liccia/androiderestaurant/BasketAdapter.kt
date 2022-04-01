package fr.isen.liccia.androiderestaurant

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.isen.liccia.androiderestaurant.databinding.BasketCellBinding
import fr.isen.liccia.androiderestaurant.model.BasketItems

class BasketAdapter(
    private val baskets: List<BasketItems>,
    private val onBasketClick: (BasketItems) -> Unit
) : RecyclerView.Adapter<BasketAdapter.BasketViewHolder>() {


    class BasketViewHolder(binding: BasketCellBinding) : RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.basketCellTitle
        val price: TextView = binding.basketCellPrice
        val quantity: TextView = binding.basketCellQuantity
        val delete: ImageView = binding.basketCellIconDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketViewHolder {
        return BasketViewHolder(
            BasketCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BasketViewHolder, position: Int) {
        val basket = baskets[position]

        holder.name.text = basket.item.name_fr

        val price = " Total : ${basket.item.prices[0].price.toFloat() * basket.quantity} €"
        holder.price.text = price

        val quantity = "Quantité : ${basket.quantity}"
        holder.quantity.text = quantity

        holder.delete.setOnClickListener {
            onBasketClick(basket)
        }
    }

    override fun getItemCount(): Int {
        return baskets.size
    }
}
