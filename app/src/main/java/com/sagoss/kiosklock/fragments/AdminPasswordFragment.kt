package com.sagoss.kiosklock.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.sagoss.kiosklock.R
import com.sagoss.kiosklock.custompager.NavigationHelper
import com.sagoss.kiosklock.utils.SharedPrefManager

class AdminPasswordFragment(private val mActivity: AppCompatActivity) : Fragment() {

    private val navigationHelper = NavigationHelper()
    private lateinit var sharedPrefManager: SharedPrefManager

    private lateinit var tlPassword: TextInputLayout
    private lateinit var tlConfirmPassword: TextInputLayout
    private lateinit var tlMemorableWord: TextInputLayout
    private lateinit var tvPassword: TextInputEditText
    private lateinit var tvConfirmPassword: TextInputEditText
    private lateinit var tvMemorableWord: TextInputEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_admin_password, container, false)

        sharedPrefManager = SharedPrefManager(mActivity)

        tlPassword = view.findViewById(R.id.til_password)
        tlConfirmPassword = view.findViewById(R.id.til_confirmPassword)
        tlMemorableWord = view.findViewById(R.id.til_memorable_word)

        tvPassword = view.findViewById(R.id.tv_password)
        tvConfirmPassword = view.findViewById(R.id.tv_confirm_password)
        tvMemorableWord = view.findViewById(R.id.tv_memorable_word)

        tvPassword.setOnFocusChangeListener{ _: View, b: Boolean ->
            if(b)
            {
                tlPassword.error = null
            }
        }

        tvConfirmPassword.setOnFocusChangeListener{ _: View, b: Boolean ->
            if(b)
            {
                tlConfirmPassword.error = null
            }
        }

        tvMemorableWord.setOnFocusChangeListener { _: View, b: Boolean ->
            if (b) {
                tlMemorableWord.error = null
                if(!tvPassword.text?.isEmpty()!!)
                {
                    if ( tvPassword.text.toString() != tvConfirmPassword.text.toString()) {
                        tlConfirmPassword.error = "Password do not match"
                    }
                }
            }
        }

        view.findViewById<MaterialButton>(R.id.btn_admin_pass_next).setOnClickListener {
            view.findViewById<MaterialButton>(R.id.btn_admin_pass_next).requestFocus()
            tlMemorableWord.error = null
            tlConfirmPassword.error = null
            tlPassword.error = null
            when {
                tvPassword.text?.isEmpty()!! -> {tlPassword.error = "cannot be left blank"}
                tvConfirmPassword.text?.isEmpty()!! -> tlConfirmPassword.error = "cannot be left blank"
                tvMemorableWord.text?.isEmpty()!! -> tlMemorableWord.error = "cannot be left blank"
                tvPassword.text.toString() != tvConfirmPassword.text.toString() -> tlConfirmPassword.error = "Password do not match"
                else -> {
                    sharedPrefManager.setPassword(tvPassword.text.toString())
                    sharedPrefManager.setMemorableWord(tvMemorableWord.text.toString())
                    navigationHelper.navigatePage(NavigationHelper.FORWARD)
                }
            }
            view.findViewById<MaterialButton>(R.id.btn_admin_pass_next).requestFocus()
        }

        return view
    }
}