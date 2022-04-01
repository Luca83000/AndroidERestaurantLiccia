package fr.isen.liccia.androiderestaurant


import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.isen.liccia.androiderestaurant.model.Item


class CategoryAdapter(private var arrayListOf: ArrayList<Item>, val clickListener: (Item) -> Unit) :
    RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemTextView: TextView = view.findViewById(R.id.categoryTitle)
        var prixTextView: TextView = view.findViewById(R.id.prixText)
        var image: ImageView = view.findViewById(R.id.imageViewSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_category, parent, false)

        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.d("XXX", "onBindViewHolder")
        val item = arrayListOf[position]
        holder.itemTextView.text = item.name_fr
        holder.prixTextView.text = item.prices[0].price + " € "
        holder.itemView.setOnClickListener {
            clickListener(item)
        }

        val url = item.images[0]
        Picasso.get()
            .load(url.ifEmpty { null })
            .placeholder(R.drawable.philippe_etchebest_mentor_background)
            .fit().centerCrop()
            .error(R.drawable.philippe_etchebest_mentor_background)
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return arrayListOf.size
    }
}
