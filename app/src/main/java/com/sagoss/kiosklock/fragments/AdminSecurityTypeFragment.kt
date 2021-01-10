package com.sagoss.kiosklock.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import com.google.android.material.button.MaterialButton
import com.sagoss.kiosklock.MainActivity
import com.sagoss.kiosklock.R
import com.sagoss.kiosklock.custompager.NavigationHelper
import com.sagoss.kiosklock.utils.RuntimeDataHelper
import com.sagoss.kiosklock.utils.SharedPrefManager

class AdminSecurityTypeFragment(activity: AppCompatActivity) : Fragment() {

    private val mActivity = activity
    private val navigationHelper = NavigationHelper()
    private lateinit var sharedPrefManager: SharedPrefManager

    private lateinit var btnUsePassword : MaterialButton
    private lateinit var btnUserPattern : MaterialButton

    private var isPassword = false
    private var isPattern = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_admin_security_type, container, false)
        sharedPrefManager = SharedPrefManager(mActivity)
        btnUsePassword = view.findViewById(R.id.btn_use_password)
        btnUserPattern = view.findViewById(R.id.btn_use_pattern)

        btnUsePassword.setOnClickListener()
        {
            isPassword = true
            isPattern = false
            btnUsePassword.setBackgroundColor(getColor(mActivity, R.color.sagoss_green))
            btnUserPattern.setBackgroundColor(getColor(mActivity, R.color.light_grey))
        }

        btnUserPattern.setOnClickListener()
        {
            isPassword = false
            isPattern = true
            btnUsePassword.setBackgroundColor(getColor(mActivity, R.color.light_grey))
            btnUserPattern.setBackgroundColor(getColor(mActivity, R.color.sagoss_green))
        }


        view.findViewById<MaterialButton>(R.id.btn_security_type_next).setOnClickListener()
        {

            when{
                isPassword && !isPattern ->
                {
                    sharedPrefManager.setLockType(RuntimeDataHelper.TYPE_PASSWORD)
                    sharedPrefManager.setIsFirstUse(false)
                    (context as MainActivity).setFragment(MainActivity.AppFragments.HOME)
                }
                !isPassword && isPattern ->
                {
                    sharedPrefManager.setLockType(RuntimeDataHelper.TYPE_PATTERN)
                    navigationHelper.navigatePage(NavigationHelper.FORWARD)
                }
                else -> Toast.makeText(mActivity, "Please select one of the options on screen to continue", Toast.LENGTH_LONG).show()

            }
        }

        view.findViewById<MaterialButton>(R.id.btn_security_type_back).setOnClickListener()
        {
            navigationHelper.navigatePage(NavigationHelper.BACKWARD)
        }

        return view
    }
}