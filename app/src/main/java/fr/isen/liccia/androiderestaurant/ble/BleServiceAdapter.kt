package fr.isen.liccia.androiderestaurant.ble

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import fr.isen.liccia.androiderestaurant.R
import java.util.*
import kotlin.collections.ArrayList

class BleServiceAdapter(
    private val context: Context,
    private val bleServices: MutableList<BLEService>,
    private val readCharacteristicCallback: (BluetoothGattCharacteristic) -> Unit,
    private val writeCharacteristicCallback: (BluetoothGattCharacteristic) -> Unit,
    private val notifyCharacteristicCallback: (BluetoothGattCharacteristic, Boolean) -> Unit
) :
    ExpandableRecyclerViewAdapter<BleServiceAdapter.ServiceViewHolder, BleServiceAdapter.CharacteristicViewHolder>(
        bleServices
    ) {


    class ServiceViewHolder(itemView: View) : GroupViewHolder(itemView) {
        val serviceName: TextView = itemView.findViewById(R.id.serviceName)
        val serviceUuid: TextView = itemView.findViewById(R.id.serviceUuID)
        private val serviceArrow: View = itemView.findViewById(R.id.serviceArrow)
        override fun expand() {
            serviceArrow.animate().rotation(-180f).setDuration(400L).start()
        }

        override fun collapse() {
            serviceArrow.animate().rotation(0f).setDuration(400L).start()
        }
    }

    class CharacteristicViewHolder(itemView: View) : ChildViewHolder(itemView) {
        val characteristicName: TextView = itemView.findViewById(R.id.characteristicName)
        val characteristicUuid: TextView = itemView.findViewById(R.id.characteristicUuid)
        val characteristicProperties: TextView = itemView.findViewById(R.id.characteristicProperties)
        val characteristicValue: TextView = itemView.findViewById(R.id.characteristicValue)

        val characteristicReadAction: Button = itemView.findViewById(R.id.readAction)
        val characteristicWriteAction: Button = itemView.findViewById(R.id.writeAction)
        val characteristicNotifyAction: Button = itemView.findViewById(R.id.notifyAction)
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): ServiceViewHolder =
        ServiceViewHolder(
            LayoutInflater.from(parent?.context)
                .inflate(R.layout.cell_ble_service, parent, false)
        )

    override fun onCreateChildViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CharacteristicViewHolder =
        CharacteristicViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_ble_characteristics, parent, false)
        )

    override fun onBindGroupViewHolder(
        holder: ServiceViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>
    ) {
        val title =
            BLEUUIDAttributes.getBLEAttributeFromUUID(group.title).title
        holder.serviceName.text = title
        holder.serviceUuid.text = group.title

    }

    override fun onBindChildViewHolder(
        holder: CharacteristicViewHolder?,
        flatPosition: Int,
        group: ExpandableGroup<*>?,
        childIndex: Int
    ) {
        val characteristic = (group?.items?.get(childIndex) as BluetoothGattCharacteristic)
        val title =
            BLEUUIDAttributes.getBLEAttributeFromUUID(characteristic.uuid.toString()).title

        val uuidMessage = "UUID : ${characteristic.uuid}"
        if (holder != null) {
            holder.characteristicUuid.text = uuidMessage
        }

        if (holder != null) {
            holder.characteristicName.text = title
        }
        val properties = arrayListOf<String>()

        if (holder != null) {
            addPropertyFromCharacteristic(
                characteristic,
                properties,
                "Lire",
                BluetoothGattCharacteristic.PROPERTY_READ,
                holder.characteristicReadAction,
                readCharacteristicCallback
            )
        }

        if (holder != null) {
            addPropertyFromCharacteristic(
                characteristic,
                properties,
                "Ecrire",
                (BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE),
                holder.characteristicWriteAction,
                writeCharacteristicCallback
            )
        }

        if (holder != null) {
            addPropertyNotificationFromCharacteristic(
                characteristic,
                properties,
                holder.characteristicNotifyAction,
                notifyCharacteristicCallback
            )
        }


        val proprietiesMessage = "Proprietés : ${properties.joinToString()}"
        if (holder != null) {
            holder.characteristicProperties.text = proprietiesMessage
        }
        characteristic.value?.let {
            val hex = it.joinToString("") { byte -> "%02x".format(byte) }.uppercase(Locale.FRANCE)
            print("")
            val value = "Valeur : ${String(it)} Hex : 0x$hex"
            if (holder != null) {
                holder.characteristicValue.visibility = View.VISIBLE
            }
            if (holder != null) {
                holder.characteristicValue.text = value
            }
        }
    }

    private fun addPropertyFromCharacteristic(
        characteristic: BluetoothGattCharacteristic,
        properties: ArrayList<String>,
        propertyName: String,
        propertyType: Int,
        propertyAction: Button,
        propertyCallBack: (BluetoothGattCharacteristic) -> Unit
    ) {
        if ((characteristic.properties and propertyType) != 0) {
            properties.add(propertyName)
            propertyAction.isEnabled = true
            propertyAction.alpha = 1f
            propertyAction.setOnClickListener {
                propertyCallBack.invoke(characteristic)
            }
        }
    }

    private fun addPropertyNotificationFromCharacteristic(
        characteristic: BluetoothGattCharacteristic,
        properties: ArrayList<String>,
        propertyAction: Button,
        propertyCallBack: (BluetoothGattCharacteristic, Boolean) -> Unit
    ) {
        if ((characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
            properties.add("Notifier")
            propertyAction.isEnabled = true
            propertyAction.alpha = 1f
            val isNotificationEnable = characteristic.descriptors.any {
                it.value?.contentEquals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE) ?: false
            }
            if (isNotificationEnable) {
                propertyAction.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
                propertyAction.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
            } else {
                propertyAction.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light))
                propertyAction.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            propertyAction.setOnClickListener {
                propertyCallBack.invoke(characteristic, !isNotificationEnable)
            }
        }
    }

    fun updateFromChangedCharacteristic(characteristic: BluetoothGattCharacteristic?) {
        bleServices.forEach {
            val index = it.items.indexOf(characteristic)
            if (index != -1) {
                it.items.removeAt(index)
                it.items.add(index, characteristic)
            }
        }
    }
}
