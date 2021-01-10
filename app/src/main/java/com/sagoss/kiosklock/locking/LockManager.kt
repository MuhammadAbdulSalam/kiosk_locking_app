package com.sagoss.kiosklock.locking

import android.app.admin.DevicePolicyManager
import android.app.admin.SystemUpdatePolicy
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.UserManager
import androidx.annotation.RequiresApi
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.sagoss.kiosklock.DeviceAdminReceiver


//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun onStart() {
//        super.onStart()
//        if (mDevicePolicyManager!!.isLockTaskPermitted(this.packageName)) {
//            val am =
//                getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//            if (am.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_NONE) {
//                setDefaultCosuPolicies(true)
//                startLockTask()
//            }
//        }
//    }


@RequiresApi(Build.VERSION_CODES.M)
class LockManager(private val mActivity: AppCompatActivity) {

    private var mAdminComponentName: ComponentName? = null
    private lateinit var mDevicePolicyManager: DevicePolicyManager

    /**
     * Initialise and check for locking authority
     *
     */
    fun initialise(): Boolean{
        mAdminComponentName = ComponentName(mActivity, DeviceAdminReceiver::class.java)
        mDevicePolicyManager =
            mActivity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (mDevicePolicyManager!!.isDeviceOwnerApp(mActivity.packageName)) {
            // App is whitelisted
            // setDefaultCosuPolicies(pref.getBoolean(Util.SYSTEM_LOCK, true))
            setDefaultCosuPolicies(true)
            return true
        }
        return false
    }

    /**
     * @param boolean: set locking state to boolean
     */
    fun enableLock(isLock: Boolean){
        when{
            isLock ->
            {
                setDefaultCosuPolicies(true)
                mActivity.startLockTask()
            }
            else ->{
                setDefaultCosuPolicies(false)
               // mActivity.stopLockTask()
            }
        }
    }

    /**
     * @param active: is locking to be set to active or not
     * Set default policies
     */
    private fun setDefaultCosuPolicies(active: Boolean) {
        // Set user restrictions
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active)
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active)
        setUserRestriction(UserManager.DISALLOW_ADD_USER, active)
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active)
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active)

        // Disable keyguard and status bar
        mDevicePolicyManager!!.setKeyguardDisabled(mAdminComponentName!!, active)
        mDevicePolicyManager!!.setStatusBarDisabled(mAdminComponentName!!, active)

        // Enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(active)

        if (active) {
            mDevicePolicyManager!!.setSystemUpdatePolicy(
                mAdminComponentName!!,
                SystemUpdatePolicy.createWindowedInstallPolicy(60, 120)
            )
        } else {
            mDevicePolicyManager!!.setSystemUpdatePolicy(mAdminComponentName!!, null)
        }

        // set this Activity as a lock task package
        mDevicePolicyManager!!.setLockTaskPackages(
            mAdminComponentName!!,
            if (active) arrayOf(mActivity.packageName) else arrayOf()
        )
        val intentFilter = IntentFilter(Intent.ACTION_MAIN)
        intentFilter.addCategory(Intent.CATEGORY_HOME)
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        if (active) {
            mDevicePolicyManager!!.addPersistentPreferredActivity(
                mAdminComponentName!!, intentFilter, ComponentName(
                    mActivity.packageName,
                    mActivity::class.java.name
                )
            )
        } else {
            mDevicePolicyManager!!.clearPackagePersistentPreferredActivities(
                mAdminComponentName!!,
                mActivity.packageName
            )
        }
    }

    /**
     * Set restriction to user
     */
    private fun setUserRestriction(
        restriction: String,
        disallow: Boolean
    ) {
        if (disallow) {
            mDevicePolicyManager!!.addUserRestriction(mAdminComponentName!!, restriction)
        } else {
            mDevicePolicyManager!!.clearUserRestriction(mAdminComponentName!!, restriction)
        }
    }

    /**
     * Enable screen awake on or off according to charging status
     */
    private fun enableStayOnWhilePluggedIn(enabled: Boolean) {
        if (enabled) {
            mDevicePolicyManager!!.setGlobalSetting(
                mAdminComponentName!!,
                Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                (BatteryManager.BATTERY_PLUGGED_AC or BatteryManager.BATTERY_PLUGGED_USB or BatteryManager.BATTERY_PLUGGED_WIRELESS).toString()
            )
        } else {
            mDevicePolicyManager!!.setGlobalSetting(
                mAdminComponentName!!,
                Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                "0"
            )
        }
    }


}