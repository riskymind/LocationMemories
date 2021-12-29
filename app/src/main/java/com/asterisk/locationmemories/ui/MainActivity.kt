package com.asterisk.locationmemories.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.asterisk.locationmemories.MapsAdapter
import com.asterisk.locationmemories.OnClickListener
import com.asterisk.locationmemories.R
import com.asterisk.locationmemories.databinding.ActivityMainBinding
import com.asterisk.locationmemories.models.Place
import com.asterisk.locationmemories.models.SampleData
import com.asterisk.locationmemories.models.UserMap
import com.asterisk.locationmemories.other.Constants.EXTRA_MAP_TITLE
import com.asterisk.locationmemories.other.Constants.EXTRA_USER_MAP
import com.asterisk.locationmemories.other.Constants.FILENAME
import com.asterisk.locationmemories.other.Constants.REQUEST_CODE
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userMaps: MutableList<UserMap>
    private lateinit var mapsAdapter: MapsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userMaps = deSerializeUserMap(this).toMutableList()
        mapsAdapter = MapsAdapter(this@MainActivity, userMaps, object : OnClickListener {
            override fun onItemClick(position: Int) {
                Intent(this@MainActivity, DisplayMapActivity::class.java).also {
                    it.putExtra(EXTRA_USER_MAP, userMaps[position])
                    startActivity(it)
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                }
            }
        })
        // Set layout manager
        binding.rvMaps.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mapsAdapter
        }

        binding.fabCreateMap.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun serializeUserMap(context: Context, userMaps: List<UserMap>) {
        ObjectOutputStream(FileOutputStream(getDataFile(context))).use { it.writeObject(userMaps) }
    }

    private fun deSerializeUserMap(context: Context): List<UserMap> {
        val dataFile = getDataFile(context)
        if (!dataFile.exists()) {
            return emptyList()
        }
        ObjectInputStream(FileInputStream(dataFile)).use {
            return it.readObject() as List<UserMap>
        }
    }

    private fun getDataFile(context: Context): File {
        return File(context.filesDir, FILENAME)
    }

    private fun showAlertDialog() {
        val placeFormView = LayoutInflater.from(this).inflate(R.layout.dialog_create_map, null)
        AlertDialog.Builder(this)
            .setTitle("Create a marker")
            .setView(placeFormView)
            .setNegativeButton("cancel", null)
            .setPositiveButton("yes") { dialogInterface, _ ->
                val title = placeFormView.findViewById<EditText>(R.id.et_title).text.toString()

                if (title.isEmpty()) {
                    Toast.makeText(
                        this,
                        "map must not have empty title",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val intent = Intent(this, CreateMapActivity::class.java)
                intent.putExtra(EXTRA_MAP_TITLE, title)
                startActivityForResult(intent, REQUEST_CODE)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

                dialogInterface.dismiss()
            }
            .show()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val userMap = data?.getSerializableExtra(EXTRA_USER_MAP) as UserMap
            userMaps.add(userMap)
            mapsAdapter.notifyItemChanged(userMaps.size - 1)
            serializeUserMap(this, userMaps)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}