package com.sagoss.kiosklock.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.google.android.material.button.MaterialButton
import com.sagoss.kiosklock.MainActivity
import com.sagoss.kiosklock.R
import com.sagoss.kiosklock.custompager.NavigationHelper
import com.sagoss.kiosklock.utils.RuntimeDataHelper
import com.sagoss.kiosklock.utils.SharedPrefManager


class AdminPasscodeFragment(activity: AppCompatActivity) : Fragment() {

    val mActivity = activity
    lateinit var patternLockView: PatternLockView
    val navigationHelper = NavigationHelper()
    lateinit var sharedPrefManager: SharedPrefManager

    lateinit var tvTitleText: TextView
    private lateinit var btnStartAgain: MaterialButton
    lateinit var btnNext: MaterialButton

    var entryCount = 0
    var selectedPattern = ""
    var confirmPattern = ""
    var isPatternConfirm = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_admin_passcode, container, false)
        tvTitleText = view.findViewById(R.id.tv_pattren_title)
        btnStartAgain = view.findViewById(R.id.btn_passcode_try_again)
        btnNext = view.findViewById(R.id.btn_pattren_next)
        patternLockView = view.findViewById(R.id.plv_lockView)
        sharedPrefManager = SharedPrefManager(mActivity)

        patternLockView.addPatternLockListener(patternLockListener())

        btnStartAgain.setOnClickListener {
            patternLockView.clearPattern()
            tvTitleText.text = "Please create a pattern to Unlock device"
            entryCount = 0;
            selectedPattern = ""
            confirmPattern = ""
            isPatternConfirm = false
        }

        btnNext.setOnClickListener{
            if(isPatternConfirm)
            {
                sharedPrefManager.setPatternLock(confirmPattern)
                sharedPrefManager.setLockType(RuntimeDataHelper.TYPE_PATTERN)
                (context as MainActivity).setFragment(MainActivity.AppFragments.HOME)
            }
        }


        return view
    }


    /**
     * @return pattern listener and matcher
     */
    private fun patternLockListener(): PatternLockViewListener {

        return object : PatternLockViewListener {

            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                entryCount++
                if (entryCount == 1) {
                    selectedPattern = PatternLockUtils.patternToString(patternLockView, pattern)
                    tvTitleText.text = "Please re-enter pattern to confirm"
                    patternLockView.clearPattern()
                    isPatternConfirm = false
                } else if (entryCount == 2) {
                    confirmPattern = PatternLockUtils.patternToString(patternLockView, pattern)
                    if (selectedPattern == confirmPattern) {
                        tvTitleText.text = "Pattern Matched Please press NEXT to continue"
                        patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT)
                        isPatternConfirm = true
                    } else {
                        tvTitleText.text = "Pattern do not match, Please start again"
                        patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG)
                        isPatternConfirm = false
                    }
                    entryCount = 0
                }
            }

            override fun onCleared() {

            }

            override fun onStarted() {

            }

            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {

            }

        }
    }




}

