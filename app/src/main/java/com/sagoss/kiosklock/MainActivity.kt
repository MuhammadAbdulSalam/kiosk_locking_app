package com.sagoss.kiosklock

/**
 * @author Muhammad Abdul Salam
 */

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.sagoss.kiosklock.fragments.HomeFragment
import com.sagoss.kiosklock.fragments.NewUserFragment
import com.sagoss.kiosklock.utils.SharedPrefManager
import com.sagoss.kiosklock.utils.Utility


class MainActivity : AppCompatActivity() {

    enum class AppFragments { HOME, NEW_USER }
    private val fragmentId: Int = R.id.fragment_layout
    private val utility = Utility(this)
    private lateinit var sharedPrefManager : SharedPrefManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        utility.setStatusBarGradient(this)

        sharedPrefManager = SharedPrefManager(this)
        if(sharedPrefManager.getIsFirstUse())
        {
            setFragment(AppFragments.NEW_USER)
        }
        else
        {
            setFragment(AppFragments.HOME)
        }


    }

    /**
     * @param fragment: Fragmemnt name that needs to be updated
     */
     fun setFragment(fragment: AppFragments) {
        runOnUiThread {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            val fragmentCheck: Fragment? = supportFragmentManager.findFragmentByTag(fragment.name)

            when (fragment) {
                AppFragments.HOME -> {
                    val homeFragment = HomeFragment(this, this)
                    transaction.replace(fragmentId, homeFragment, AppFragments.HOME.name)
                }
                AppFragments.NEW_USER -> {
                    val settingsFragment = NewUserFragment(this)
                    if (fragmentCheck == null || !fragmentCheck.isVisible) {
                        transaction.replace(fragmentId, settingsFragment, AppFragments.NEW_USER.name)
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

    override fun onBackPressed() {
    }

}