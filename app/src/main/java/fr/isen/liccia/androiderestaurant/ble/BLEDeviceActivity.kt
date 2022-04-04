package fr.isen.liccia.androiderestaurant.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import fr.isen.liccia.androiderestaurant.R
import fr.isen.liccia.androiderestaurant.ble.BLEScanActivity.Companion.DEVICE_KEY
import fr.isen.liccia.androiderestaurant.databinding.ActivityBledeviceBinding

class BLEDeviceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBledeviceBinding
    private var bluetoothGatt: BluetoothGatt? = null

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
                val adapter = BleServiceAdapter(bleServices)
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