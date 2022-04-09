package fr.isen.liccia.androiderestaurant.ble

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import fr.isen.liccia.androiderestaurant.R
import fr.isen.liccia.androiderestaurant.ble.BLEScanActivity.Companion.DEVICE_KEY
import fr.isen.liccia.androiderestaurant.databinding.ActivityBledeviceBinding
import java.util.*

class BLEDeviceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBledeviceBinding
    private var bluetoothGatt: BluetoothGatt? = null
    private var timer: Timer? = null
    private lateinit var adapter: BleServiceAdapter

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBledeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar!!.title = "Services et Caractéristiques BLE"

        val device = intent.getParcelableExtra<BluetoothDevice>(BLEScanActivity.DEVICE_KEY)
        binding.deviceName.text = device?.name ?: "Nom inconnu"

        binding.deviceStatus.text =
            getString(
                R.string.ble_device_status,
                getString(BLEConnexionState.STATE_CONNECTING.text)
            )

        binding.progressBarService.visibility = View.VISIBLE
        binding.divider.visibility = View.INVISIBLE

        connectToDevice(device)
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice?) {
        device?.connectGatt(this, true, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                onConnectionStateChange(newState, gatt)
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)
                val bleServices =
                    gatt?.services?.map { BLEService(it.uuid.toString(), it.characteristics) }
                        ?: listOf()
                val adapter = gatt?.services?.map {
                    BLEService(it.uuid.toString(), it.characteristics)
                }?.let {
                    BleServiceAdapter(applicationContext,
                        it.toMutableList(),
                        { characteristic ->
                            gatt.readCharacteristic(characteristic)
                        },
                        { characteristic ->
                            writeIntoCharacteristic(gatt, characteristic)
                        },
                        { characteristic, enable ->
                            toggleNotificationOnCharacteristic(
                                gatt,
                                characteristic,
                                enable
                            )
                        })
                }
                runOnUiThread {
                    binding.serviceList.layoutManager = LinearLayoutManager(this@BLEDeviceActivity)
                    binding.serviceList.adapter = adapter
                    binding.serviceList.addItemDecoration(
                        DividerItemDecoration(
                            this@BLEDeviceActivity,
                            LinearLayoutManager.VERTICAL
                        )
                    )
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                super.onCharacteristicRead(gatt, characteristic, status)
                /*runOnUiThread {
                    adapter.updateFromChangedCharacteristic(characteristic)
                    adapter.notifyDataSetChanged()
                }*/
            }
        })
        bluetoothGatt?.connect()
    }

    @SuppressLint("MissingPermission")
    private fun onConnectionStateChange(newState: Int, gatt: BluetoothGatt?) {
        BLEConnexionState.getBLEConnexionStateFromState(newState)?.let {
            runOnUiThread {
                binding.progressBarService.visibility = View.INVISIBLE
                binding.divider.visibility = View.VISIBLE
                binding.deviceStatus.text =
                    getString(R.string.ble_device_status, getString(it.text))
            }

            if (it.state == BLEConnexionState.STATE_CONNECTED.state) {
                gatt?.discoverServices()
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun toggleNotificationOnCharacteristic(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        enable: Boolean
    ) {
        characteristic.descriptors.forEach {
            it.value =
                if (enable) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(it)
        }
        gatt.setCharacteristicNotification(characteristic, enable)
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                gatt.readCharacteristic(characteristic)
            }
        }, 0, 1000)
    }

    @SuppressLint("MissingPermission")
    private fun writeIntoCharacteristic(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ) {
        runOnUiThread {
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(16, 0, 16, 0)
            input.layoutParams = params

            AlertDialog.Builder(this@BLEDeviceActivity)
                .setTitle(R.string.ble_device_write_characteristic_title)
                .setView(input)
                .setPositiveButton(R.string.ble_device_write_characteristic_confirm) { _, _ ->
                    characteristic.value = input.text.toString().toByteArray()
                    gatt.writeCharacteristic(characteristic)
                    gatt.readCharacteristic(characteristic)
                }
                .setNegativeButton(R.string.ble_device_write_characteristic_cancel) { dialog, _ -> dialog.cancel() }
                .create()
                .show()
        }
    }


    override fun onStop() {
        super.onStop()
        closeBluetoothGatt()
    }

    @SuppressLint("MissingPermission")
    private fun closeBluetoothGatt() {
        timer?.cancel()
        timer = null
        binding.deviceStatus.text =
            getString(
                R.string.ble_device_status,
                getString(BLEConnexionState.STATE_DISCONNECTED.text)
            )
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}
