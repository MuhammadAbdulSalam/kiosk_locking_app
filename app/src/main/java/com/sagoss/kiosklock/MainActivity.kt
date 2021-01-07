package com.sagoss.kiosklock

/**
 * @author Muhammad Abdul Salam
 */

import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sagoss.kiosklock.fragments.HomeFragment
import com.sagoss.kiosklock.fragments.SettingsFragment


class MainActivity : AppCompatActivity() {

    enum class AppFragments { HOME, SETTINGS, WIFI }
    private val fragmentId: Int = R.id.fragment_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        setFragment(AppFragments.HOME)

    }

    /**
     * @param fragment: Fragmemnt name that needs to be updated
     */
    private fun setFragment(fragment: AppFragments) {
        runOnUiThread {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            val fragmentCheck: Fragment? = supportFragmentManager.findFragmentByTag(fragment.name)

            when (fragment) {
                AppFragments.HOME -> {
                    val homeFragment = HomeFragment(this)
                    transaction.replace(fragmentId, homeFragment, AppFragments.HOME.name)
                }
                AppFragments.SETTINGS -> {
                    val settingsFragment: SettingsFragment = SettingsFragment()
                    if (fragmentCheck == null || !fragmentCheck.isVisible) {
                        transaction.replace(fragmentId, settingsFragment, AppFragments.SETTINGS.name)
                    }
                }
                else -> {} //TODO
            }
            if (fragment != AppFragments.HOME) {
                transaction.addToBackStack(null)
            }
            transaction.commit()
        }
    }

}