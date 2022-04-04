package fr.isen.liccia.androiderestaurant.ble


import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.isen.liccia.androiderestaurant.*
import fr.isen.liccia.androiderestaurant.databinding.ActivityBleBinding
import fr.isen.liccia.androiderestaurant.model.Item
import java.util.ArrayList


class BLEScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBleBinding
    private val ENABLE_BLUETOOTH_REQUEST_CODE = 1
    private val ALL_PERMISSION_REQUEST_CODE = 100
    private var isScanning = false
    private var adapter : BLEAdapter? = null
    private lateinit var monRecycler: RecyclerView


    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBleBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar!!.title = "BLE"

        when {
            bluetoothAdapter?.isEnabled == true ->
                startLeScanWithPermission(true)
            bluetoothAdapter != null ->
                askBluetoothPermission()
            else -> {
                displayBLEUnAvailable()
            }
        }

        binding.playIcon.setOnClickListener {
            startLeScanWithPermission(!isScanning)
        }

        binding.bleScanStateTitle.setOnClickListener {
            startLeScanWithPermission(!isScanning)
        }

        monRecycler = findViewById(R.id.recyclerViewBLE)
        binding.recyclerViewBLE.layoutManager = LinearLayoutManager(this)
        adapter = BLEAdapter(arrayListOf()) {
            val intent = Intent(this, BLEDeviceActivity::class.java)
            intent.putExtra(DEVICE_KEY, it)
            startActivity(intent)
        }
        binding.recyclerViewBLE.adapter = adapter
    }

    override fun onStop() {
        super.onStop()
        startLeScanWithPermission(false)
    }

    private fun startLeScanWithPermission(enable: Boolean) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLeScanBLE(enable)
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), ALL_PERMISSION_REQUEST_CODE
            )
        }

    }

    @SuppressLint("MissingPermission")
    private fun startLeScanBLE(enable: Boolean) {
        binding.bleScanStateTitle.isVisible = false

        bluetoothAdapter?.bluetoothLeScanner?.apply {
            if (enable) {
                isScanning = true
                startScan(scanCallback)
            } else {
                isScanning = false
                stopScan(scanCallback)
            }
            playToPause()
        }
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            Log.d("BLEScanActivity", "result: ${result?.device?.address}, rssi : ${result?.rssi}")
            if (result != null) {
                adapter?.addElement(result)
                adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun displayBLEUnAvailable() {
        binding.bleScanStateTitle.isVisible = true
        binding.playIcon.isVisible = false
        binding.progressBarId.isVisible = false
    }


    private fun askBluetoothPermission() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }

    private fun playToPause() {
        if (isScanning) {
            binding.bleScanText.text = "Scan BLE en cours ..."
            binding.progressBarId.isIndeterminate = true
            binding.playIcon.setImageResource(R.drawable.ic_pause)
        } else {
            binding.bleScanText.text = "Lancer le Scan BLE"
            binding.progressBarId.isIndeterminate = false
            binding.playIcon.setImageResource(R.drawable.ic_play)
        }

    }

    companion object {
        const val DEVICE_KEY = "device"
    }
}
