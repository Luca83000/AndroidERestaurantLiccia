package fr.isen.liccia.androiderestaurant.ble

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
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

        val device = intent.getParcelableExtra<BluetoothDevice>(BLEScanActivity.DEVICE_KEY)
        binding.deviceName.text = device?.name ?: "Nom inconnu"
        binding.deviceStatus.text = getString(R.string.ble_device_disconnected)

        connectToDevice(device)
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice?) {
        device?.connectGatt(this, true, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                connectionStateChange(gatt, newState)
            }

            private fun connectionStateChange(gatt: BluetoothGatt?, newState: Int) {
                val state = if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt?.discoverServices()
                    getString(R.string.ble_device_connected)
                } else {
                    getString(R.string.ble_device_disconnected)
                }
                runOnUiThread {
                    binding.deviceStatus.text = state
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)
                val bleServices =
                    gatt?.services?.map { BLEService(it.uuid.toString(), it.characteristics) }
                        ?: listOf()
               // val adapter = BleServiceAdapter(applicationContext,
                 //   bleServices as MutableList<BLEService>, onCharacteristicRead(),writeIntoCharacteristic(),toggleNotificationOnCharacteristic())
                runOnUiThread {
                    binding.serviceList.layoutManager = LinearLayoutManager(this@BLEDeviceActivity)
                    binding.serviceList.adapter = adapter
                }
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                super.onCharacteristicRead(gatt, characteristic, status)
            }
        })
        bluetoothGatt?.connect()
    }

    @SuppressLint("MissingPermission")
    private fun onServicesDiscovered(gatt: BluetoothGatt?) {
        gatt?.services?.let {
            runOnUiThread {
                adapter = BleServiceAdapter(
                    this,
                    it.map { service ->
                        BLEService(service.uuid.toString(), service.characteristics)
                    }.toMutableList(),
                    { characteristic -> gatt.readCharacteristic(characteristic) },
                    { characteristic -> writeIntoCharacteristic(gatt, characteristic) },
                    { characteristic, enable ->
                        toggleNotificationOnCharacteristic(
                            gatt,
                            characteristic,
                            enable
                        )
                    }
                )
                binding.serviceList.adapter = adapter
                binding.serviceList.addItemDecoration(
                    DividerItemDecoration(
                        this@BLEDeviceActivity,
                        LinearLayoutManager.VERTICAL
                    )
                )
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
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}