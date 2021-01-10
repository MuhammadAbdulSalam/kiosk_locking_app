package com.sagoss.kiosklock.fragments

import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sagoss.kiosklock.R
import com.sagoss.kiosklock.custompager.CurrentViewPager
import com.sagoss.kiosklock.custompager.CustomViewPager
import com.sagoss.kiosklock.custompager.ViewPagerAdapter
import com.sagoss.kiosklock.utils.RuntimeDataHelper

class NewUserFragment(private val mActivity: AppCompatActivity) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_new_user, container, false)

        val viewpager: CustomViewPager = view.findViewById(R.id.viewpager)
        val currentViewPager = CurrentViewPager()
        currentViewPager.setViewPager(viewpager)
        RuntimeDataHelper.currentViewPager = currentViewPager

        val viewPagerAdapter = ViewPagerAdapter(mActivity.supportFragmentManager)

        viewPagerAdapter.addFragment(AdminPasswordFragment(mActivity))
        viewPagerAdapter.addFragment(AdminSecurityTypeFragment(mActivity))
        viewPagerAdapter.addFragment(AdminPatternFragment(mActivity))

        viewpager.setSwipeable(false)
        viewpager.adapter = viewPagerAdapter

        return view
    }


}