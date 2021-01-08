package com.sagoss.kiosklock.fragments

import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.sagoss.kiosklock.R
import com.sagoss.kiosklock.utils.Utility
import java.lang.Exception


class HomeFragment(context: Context, mainActivity: AppCompatActivity) : Fragment() {

    private var mContext: Context = context
    private var utility = Utility(mContext)
    private var isLocked = true
    private var mMainActivity = mainActivity
    private lateinit var updateUiThread: Thread
    //View variables
    private lateinit var dialog             :Dialog
    private lateinit var btnUnlock          :MaterialButton

    private lateinit var imgPatrollaRing    :ImageView
    private lateinit var imgInternetIcon    :ImageView
    private lateinit var imgBluetoothIcon   :ImageView
    private lateinit var imgLockIcon        :ImageView
    private lateinit var imgLocationIcon    :ImageView

    private lateinit var tvInternetStatus   :TextView
    private lateinit var tvBluetoothStatus  :TextView
    private lateinit var tvLockStatus       :TextView
    private lateinit var tvLocationStatus   :TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        imgPatrollaRing    = view.findViewById(R.id.img_sagoss_ring)
        btnUnlock          = view.findViewById(R.id.btn_unlock)
        imgInternetIcon    = view.findViewById(R.id.img_intenet_icon)
        imgBluetoothIcon   = view.findViewById(R.id.img_bluetooth_icon)
        imgLockIcon        = view.findViewById(R.id.img_lock_icon)
        imgLocationIcon    = view.findViewById(R.id.img_location_icon)
        tvInternetStatus   = view.findViewById(R.id.tv_intenet_status)
        tvBluetoothStatus  = view.findViewById(R.id.tv_bluetooth_status)
         tvLockStatus      = view.findViewById(R.id.tv_lock_status)
        tvLocationStatus   = view.findViewById(R.id.tv_location_status)

        view.findViewById<FrameLayout>(R.id.btn_internet).setOnClickListener{ connectivityCheckMessage(
            utility.isInternetConnected(),
            getString(R.string.internet_ok_message),
            getString(R.string.internet_no_msg) ) }

        view.findViewById<FrameLayout>(R.id.btn_location).setOnClickListener{ connectivityCheckMessage(
            utility.isLocationEnabled(),
            getString(R.string.location_ok_message),
            getString(R.string.location_no_msg) ) }

        view.findViewById<FrameLayout>(R.id.btn_bluetooth).setOnClickListener{ handleBluetooth() }

        btnUnlock.setOnClickListener { handleLock() }

        statusBarUpdate()

        return view
    }

    /**
     * Update status bar every second
     */
    private fun startUiUpdateThread(){
            try {
                updateUiThread = object : Thread() {
                    override fun run() {
                        try {
                            while (!this.isInterrupted) {
                                sleep(1000)
                                mMainActivity.runOnUiThread {
                                    statusBarUpdate()
                                }
                            }
                        } catch (e: InterruptedException) {
                        }
                    }
                }

                updateUiThread.start()
            }
            catch (e: Exception){}
    }

    /**
     * Update statusbar icons and text
     */
    private fun statusBarUpdate(){

        tvInternetStatus.text = if(utility.isInternetConnected()) "Enabled" else "Disabled"
        tvBluetoothStatus.text = if(utility.isBluetoothConnected()) "Enabled" else "Disabled"
        tvLocationStatus.text = if(utility.isLocationEnabled()) "Enabled" else "Disabled"
        tvLockStatus.text = if(utility.isLocationEnabled()) "Enabled" else "Disabled" //TODO

        imgInternetIcon.setImageResource(if(utility.isInternetConnected()) R.drawable.ic_outline_swap_vertical_circle_24 else R.drawable.ic_outline_swap_vert_24_red)
        imgBluetoothIcon.setImageResource(if(utility.isBluetoothConnected()) R.drawable.ic_baseline_bluetooth_24 else R.drawable.ic_baseline_bluetooth_disabled_24)
        imgLocationIcon.setImageResource(if(utility.isLocationEnabled()) R.drawable.ic_outline_location_on_24 else R.drawable.ic_outline_location_off_24)
        imgLockIcon.setImageResource(if(utility.isInternetConnected()) R.drawable.ic_outline_lock_24 else R.drawable.ic_outline_location_off_24) //TODO

    }

    override fun onPause() {
        super.onPause()
        if(updateUiThread.isAlive)
        {
            updateUiThread.interrupt()
        }
        imgPatrollaRing.clearAnimation()

    }

    override fun onStart() {
        super.onStart()
        startUiUpdateThread()
        imgPatrollaRing.startAnimation(startLogoAnimation())
    }


    override fun onResume() {
        super.onResume()
        startUiUpdateThread()
        if(!imgPatrollaRing.animation.isInitialized) imgPatrollaRing.startAnimation(startLogoAnimation())
    }


    /**
     * Handle bluetooth connection on and off
     */
    private fun handleBluetooth(){
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter.isEnabled)
        {
            bluetoothAdapter.disable()
        }
        else
        {
            bluetoothAdapter.enable()
        }
    }

    /**
     * @param isConnected: state of IO to check
     * @param positiveMessage: if connected then message
     * @param negativeMessage: if not connected message
     */
    private fun connectivityCheckMessage(isConnected :Boolean, positiveMessage: String, negativeMessage: String){

        dialog = Dialog(mContext)
        var onSecondaryClickListner: View.OnClickListener? = null
        val onPositiveClickListner: View.OnClickListener = View.OnClickListener {
            dialog.cancel()
        }

        if(isConnected)
        {
            dialog.show()
            showDialog(positiveMessage, false, onPositiveClickListner, onSecondaryClickListner)
        }
        else{
            dialog.show()
            onSecondaryClickListner = View.OnClickListener {
                //TODO add unlocking
                dialog.cancel()
            }
            showDialog(negativeMessage, true, onPositiveClickListner, onSecondaryClickListner)
        }

    }

    /**
     * Rotation Animation
     */
    private fun startLogoAnimation(): Animation {
        return AnimationUtils.loadAnimation(
            mContext,
            R.anim.roatate_clockwise
        )
    }

    /**
     * Handle Locking and Unlocking
     */
    private fun handleLock(){
        if (isLocked) {
            btnUnlock.setIconResource(R.drawable.ic_baseline_lock_open_24)
            btnUnlock.setIconTintResource(R.color.sagoss_green)
            btnUnlock.text = getString(R.string.loc_now)
            isLocked = false
        } else {
            btnUnlock.setIconResource(R.drawable.ic_outline_lock_24)
            btnUnlock.setIconTintResource(R.color.sagoss_purple)
            btnUnlock.text = getString(R.string.unlock)

            isLocked = true
        }
    }

    /**
     * Info Dialog with 1 or 2 buttons and selected message
     */
    private fun showDialog(msg: String, isSecondaryNeeded: Boolean ,onPositiveClickListner: View.OnClickListener, onSecondaryListner: View.OnClickListener? ) {

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_info)

        val tvMessage = dialog.findViewById(R.id.tv_msg) as TextView
        val btnPositive = dialog.findViewById(R.id.btn_positive) as MaterialButton
        val btnSecondary = dialog.findViewById(R.id.btn_secondary) as MaterialButton

        btnSecondary.visibility = View.GONE
        tvMessage.text = msg

        btnPositive.setOnClickListener(onPositiveClickListner)
        if(isSecondaryNeeded)
        {
            btnSecondary.visibility = View.VISIBLE
            btnSecondary.text = "unlock"
            btnSecondary.setOnClickListener(onSecondaryListner)
        }
    }


}