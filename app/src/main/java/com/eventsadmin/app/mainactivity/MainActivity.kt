package com.eventsadmin.app.presentation.ui.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import com.eventsadmin.app.R
import com.eventsadmin.app.databinding.ActivityAuthBinding
import com.eventsadmin.app.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var fragmentContainerView: FragmentContainerView
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigation = binding.bottomNavigation
        fragmentContainerView = binding.fragmentContainerView

        bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.vendors -> {
                    true
                }
                R.id.
            }
        }
    }
}