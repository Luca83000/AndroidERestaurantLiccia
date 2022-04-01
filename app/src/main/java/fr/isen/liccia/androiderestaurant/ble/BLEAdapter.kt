package fr.isen.liccia.androiderestaurant.ble


import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import fr.isen.liccia.androiderestaurant.DetailsActivity
import fr.isen.liccia.androiderestaurant.R

class BLEAdapter(
    private var bleList: ArrayList<ScanResult>,
    val result: (BluetoothDevice) -> Unit
) :
    RecyclerView.Adapter<BLEAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nameBleText: TextView = view.findViewById(R.id.nameBleText)
        var macAdressText: TextView = view.findViewById(R.id.macAdressText)
        var rssiText: TextView = view.findViewById(R.id.rssiText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val bleView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_ble, parent, false)
        return MyViewHolder(bleView)
    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.d("XXX", "onBindViewHolder")
        val result = bleList[position]
        holder.macAdressText.text = result.device.address
        holder.nameBleText.text = result.device.name
        holder.nameBleText.text = "Device Unknown"
        holder.rssiText.text = result.rssi.toString()
        holder.itemView.setOnClickListener {
            result(result.device)
        }
    }

    fun addElement(result: ScanResult) {
        val indexOfResult = bleList.indexOfFirst { it.device.address == result.device.address }
        if (indexOfResult != -1) {
            bleList[indexOfResult] = result
        } else {
            bleList.add(result)
        }
    }

    override fun getItemCount(): Int {
        return bleList.size
    }
}
