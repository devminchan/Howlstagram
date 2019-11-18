package com.syubsyubboy.howlstagram

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.syubsyubboy.howlstagram.navigation.AlarmFragment
import com.syubsyubboy.howlstagram.navigation.DetailViewFragment
import com.syubsyubboy.howlstagram.navigation.GridFragment
import com.syubsyubboy.howlstagram.navigation.UserFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.action_home -> {
                val fg = DetailViewFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, fg).commit()
                return true
            }
            R.id.action_search -> {
                val fg = GridFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, fg).commit()
                return true
            }
            R.id.action_add_photo -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    startActivity(Intent(this, AddPhotoActivity::class.java))
                }

                return true
            }
            R.id.action_favorite_alarm -> {
                val fg = AlarmFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, fg).commit()
                return true
            }
            R.id.action_account -> {
                val fg = UserFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, fg).commit()
                return true
            }
        }

        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation_view.setOnNavigationItemSelectedListener(this)

        setSupportActionBar(toolbar)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
    }
}
