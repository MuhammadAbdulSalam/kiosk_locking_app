package com.sagoss.kiosklock.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.sagoss.kiosklock.R
import com.sagoss.kiosklock.custompager.NavigationHelper

class AdminPasswordFragment : Fragment() {

    val navigationHelper = NavigationHelper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_admin_password, container, false)

        view.findViewById<MaterialButton>(R.id.btn_admin_pass_next).setOnClickListener{
            navigationHelper.navigatePage(NavigationHelper.FORWARD)
        }

//        view.findViewById<MaterialButton>(R.id.bac).setOnClickListener{
//            navigationHelper.navigatePage(NavigationHelper.BACKWARD)
//        }

        return view
    }



}