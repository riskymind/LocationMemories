package com.asterisk.locationmemories.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.asterisk.locationmemories.MapsAdapter
import com.asterisk.locationmemories.OnClickListener
import com.asterisk.locationmemories.databinding.ActivityMainBinding
import com.asterisk.locationmemories.models.Place
import com.asterisk.locationmemories.models.SampleData
import com.asterisk.locationmemories.models.UserMap
import com.asterisk.locationmemories.other.Constants.EXTRA_USER_MAP

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userMaps = SampleData.generateSampleData()

        // Set layout manager
        binding.rvMaps.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MapsAdapter(this@MainActivity, userMaps, object : OnClickListener {
                override fun onItemClick(position: Int) {
                    Intent(this@MainActivity, DisplayMapActivity::class.java).also {
                        it.putExtra(EXTRA_USER_MAP, userMaps[position])
                        startActivity(it)
                    }
                }
            })
        }
    }

}