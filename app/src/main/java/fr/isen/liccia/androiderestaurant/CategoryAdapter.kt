package fr.isen.liccia.androiderestaurant


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.isen.liccia.androiderestaurant.model.Item


class CategoryAdapter(private var arrayListOf: ArrayList<Item>, val clickListener: (Item)-> Unit) : RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemTextView: TextView = view.findViewById(R.id.categoryTitle)
        var imageViewSelect: ImageView = view.findViewById(R.id.imageViewSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_category, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.d("XXX","onBindViewHolder")
        val item = arrayListOf[position]
        holder.itemTextView.text = item.name_fr
        holder.itemView.setOnClickListener {
            clickListener(item)
        }

       /* holder.imageViewSelect.image = item.images

        Picasso.get()
            .load(item.images[0])
            .placeholder(R.drawable._834760_logo_de_l_emission_cauchemar_en_cuisine_950x0_2)
            .fit()
            .error(R.drawable.philippe_etchebest_mentor_background)
            .into(imageViewSelect)*/
    }

    override fun getItemCount(): Int {
        return arrayListOf.size
    }
}
