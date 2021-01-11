package com.sagoss.kiosklock.fragments

import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.google.android.material.button.MaterialButton
import com.sagoss.kiosklock.R
import com.sagoss.kiosklock.locking.LockManager
import com.sagoss.kiosklock.utils.RuntimeDataHelper
import com.sagoss.kiosklock.utils.SharedPrefManager
import com.sagoss.kiosklock.utils.Utility


class HomeFragment(private val mContext: Context, private val mMainActivity: AppCompatActivity) :
    Fragment() {

    private var utility = Utility(mContext)
    private var isLocked = true
    private lateinit var updateUiThread: Thread
    private lateinit var sharedPrefManager: SharedPrefManager
    private val lockManager = LockManager(mMainActivity)

    //View variables
    private lateinit var dialog: Dialog
    private lateinit var btnUnlock: MaterialButton

    private lateinit var imgPatrollaRing: ImageView
    private lateinit var imgInternetIcon: ImageView
    private lateinit var imgBluetoothIcon: ImageView
    private lateinit var imgLockIcon: ImageView
    private lateinit var imgLocationIcon: ImageView

    private lateinit var tvInternetStatus: TextView
    private lateinit var tvBluetoothStatus: TextView
    private lateinit var tvLockStatus: TextView
    private lateinit var tvLocationStatus: TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        lockManager.initialise()

        sharedPrefManager = SharedPrefManager(mMainActivity)
        imgPatrollaRing = view.findViewById(R.id.img_sagoss_ring)
        btnUnlock = view.findViewById(R.id.btn_unlock)
        imgInternetIcon = view.findViewById(R.id.img_intenet_icon)
        imgBluetoothIcon = view.findViewById(R.id.img_bluetooth_icon)
        imgLockIcon = view.findViewById(R.id.img_lock_icon)
        imgLocationIcon = view.findViewById(R.id.img_location_icon)
        tvInternetStatus = view.findViewById(R.id.tv_intenet_status)
        tvBluetoothStatus = view.findViewById(R.id.tv_bluetooth_status)
        tvLockStatus = view.findViewById(R.id.tv_lock_status)
        tvLocationStatus = view.findViewById(R.id.tv_location_status)

        view.findViewById<FrameLayout>(R.id.btn_bluetooth).setOnClickListener { handleBluetooth() }
        view.findViewById<FrameLayout>(R.id.btn_status_lock).setOnClickListener { handleLock() }
        btnUnlock.setOnClickListener { handleLock() }
        if(!sharedPrefManager.getLockStatus()){
            setButtonUnlocked()
            lockManager.enableLock(false)
        }
        else
        {
            lockManager.enableLock(true)
        }
        statusBarUpdate()

        view.findViewById<FrameLayout>(R.id.btn_internet).setOnClickListener {
            connectivityCheckMessage(
                utility.isInternetConnected(),
                getString(R.string.internet_ok_message),
                getString(R.string.internet_no_msg)
            )
        }

        view.findViewById<FrameLayout>(R.id.btn_location).setOnClickListener {
            connectivityCheckMessage(
                utility.isLocationEnabled(),
                getString(R.string.location_ok_message),
                getString(R.string.location_no_msg)
            )
        }

        if(!sharedPrefManager.getIsAdmin())
        {
           // Runtime.getRuntime().exec("dpm set-device-owner com.sagoss.kiosklock/locking.DeviceAdminReceiver")

//            lockManager.enableLock(true)
//            sharedPrefManager.setIsDeviceAdmin(true)
        }

        view.findViewById<LinearLayout>(R.id.btn_start_patrolla).setOnClickListener{
            val i: Intent? =
                mMainActivity.packageManager.getLaunchIntentForPackage("com.sagoss.patrolla")
            mMainActivity.startActivity(i)

        }


        return view
    }

    /**
     * Update status bar every second
     */
    private fun startUiUpdateThread() {
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
        } catch (e: Exception) {
        }
    }

    /**
     * Update statusbar icons and text
     */
    private fun statusBarUpdate() {

        tvInternetStatus.text = if (utility.isInternetConnected()) "Enabled" else "Disabled"
        tvBluetoothStatus.text = if (utility.isBluetoothConnected()) "Enabled" else "Disabled"
        tvLocationStatus.text = if (utility.isLocationEnabled()) "Enabled" else "Disabled"
        tvLockStatus.text = if (sharedPrefManager.getLockStatus()) "Enabled" else "Disabled"

        imgInternetIcon.setImageResource(if (utility.isInternetConnected()) R.drawable.ic_outline_swap_vertical_circle_24 else R.drawable.ic_outline_swap_vert_24_red)
        imgBluetoothIcon.setImageResource(if (utility.isBluetoothConnected()) R.drawable.ic_baseline_bluetooth_24 else R.drawable.ic_baseline_bluetooth_disabled_24)
        imgLocationIcon.setImageResource(if (utility.isLocationEnabled()) R.drawable.ic_outline_location_on_24 else R.drawable.ic_outline_location_off_24)
        imgLockIcon.setImageResource(if (sharedPrefManager.getLockStatus()) R.drawable.ic_outline_lock_24 else R.drawable.ic_baseline_lock_open_24)

    }

    /**
     * Handle bluetooth connection on and off
     */
    private fun handleBluetooth() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        when {
            bluetoothAdapter.isEnabled -> bluetoothAdapter.disable()
            else -> bluetoothAdapter.enable()
        }
    }

    /**
     * @param isConnected: state of IO to check
     * @param positiveMessage: if connected then message
     * @param negativeMessage: if not connected message
     */
    private fun connectivityCheckMessage(isConnected: Boolean, positiveMessage: String, negativeMessage: String) {

        dialog = Dialog(mContext)
        var onSecondaryClickListner: View.OnClickListener? = null
        val onPositiveClickListner: View.OnClickListener = View.OnClickListener {
            dialog.cancel()
        }

        if (isConnected) {
            dialog.show()
            showInfoDialog(positiveMessage, false, onPositiveClickListner, onSecondaryClickListner)
        } else {
            dialog.show()
            onSecondaryClickListner = View.OnClickListener {
                //TODO add unlocking
                dialog.cancel()
            }
            showInfoDialog(negativeMessage, true, onPositiveClickListner, onSecondaryClickListner)
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
    private fun handleLock() {

        if (sharedPrefManager.getLockType() == RuntimeDataHelper.TYPE_PATTERN) {
            btnUnlock.visibility = View.INVISIBLE
            dialog = Dialog(mContext)
            enterPatternDialog()
            dialog.show()

            dialog.setOnDismissListener {
                btnUnlock.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Set button view to unlocked
     */
    private fun setButtonUnlocked(){
        btnUnlock.setIconResource(R.drawable.ic_baseline_lock_open_24)
        btnUnlock.setIconTintResource(R.color.sagoss_green)
        btnUnlock.text = getString(R.string.loc_now)
    }

    /**
     * Set button view to unlocked
     */
    private fun setButtonLocked(){
        btnUnlock.setIconResource(R.drawable.ic_outline_lock_24)
        btnUnlock.setIconTintResource(R.color.sagoss_red)
        btnUnlock.text = getString(R.string.unlock)
    }

    /**
     * If lock type was selected to be pattern, Pattern dialog will show up
     * User can then use saved pattern to lock or unlock phone
     */
    private fun enterPatternDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_pattern)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, 1500)
        val window: Window? = dialog.window
        val windowLayout = window?.attributes

        windowLayout?.gravity = Gravity.BOTTOM
        windowLayout?.flags =
            windowLayout?.flags?.and(WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv())
        window?.attributes = windowLayout

        dialog.findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener {
            dialog.cancel()
        }

        val userCurrentPattern = sharedPrefManager.getPattern()
        val patternLockView: PatternLockView = dialog.findViewById(R.id.ptl_enter_pattern)
        patternLockView.addPatternLockListener(object : PatternLockViewListener {
            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                if (userCurrentPattern.equals(PatternLockUtils.patternToString(patternLockView, pattern))) {
                    when {
                        sharedPrefManager.getLockStatus() ->
                        {
                            lockManager.enableLock(false)
                            setButtonUnlocked()
                            sharedPrefManager.setLockStatus(false)
                            dialog.dismiss()
                        }
                        else ->
                        {
                            lockManager.enableLock(true)
                            setButtonLocked()
                            sharedPrefManager.setLockStatus(true)
                            dialog.dismiss()
                        }
                    }
                }
                else{
                    patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG)
                }
            }
            override fun onCleared() {}
            override fun onStarted() {}
            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {}
        })
    }


    /**
     * Info Dialog with 1 or 2 buttons and selected message
     */
    private fun showInfoDialog(msg: String, isSecondaryNeeded: Boolean, onPositiveClickListener: View.OnClickListener, onSecondaryListener: View.OnClickListener?) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_info)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvMessage = dialog.findViewById(R.id.tv_msg) as TextView
        val btnPositive = dialog.findViewById(R.id.btn_positive) as MaterialButton
        val btnSecondary = dialog.findViewById(R.id.btn_secondary) as MaterialButton

        btnSecondary.visibility = View.GONE
        tvMessage.text = msg

        btnPositive.setOnClickListener(onPositiveClickListener)
        if (isSecondaryNeeded) {
            btnSecondary.visibility = View.VISIBLE
            btnSecondary.text = "unlock"
            btnSecondary.setOnClickListener(onSecondaryListener)
        }
    }


    override fun onPause() {
        super.onPause()
        if (updateUiThread.isAlive) {
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
//        when {
//            !sharedPrefManager.getLockStatus() -> {
//                setButtonUnlocked()
//                lockManager.enableLock(false)
//            }
//            else -> {setButtonLocked()
//            lockManager.enableLock(true)
//            }
//        }
        if (imgPatrollaRing.animation == null) imgPatrollaRing.startAnimation(startLogoAnimation())
    }

}