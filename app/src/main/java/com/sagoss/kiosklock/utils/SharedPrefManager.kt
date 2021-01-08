package com.sagoss.kiosklock.utils

import android.app.Activity
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity


class SharedPrefManager(activity: AppCompatActivity) {

    private var pref: SharedPreferences = activity.applicationContext.getSharedPreferences("MyPref", 0)
    var editor: SharedPreferences.Editor = pref.edit()

    private val PASSWORD = "password"
    private val LOCK_STATUS = "lock_status"
    private val IS_FIRST_USE = "is_first_use"
    private val PATTERN = "pattren"
    private val LOCK_TYPE = "lock_type"

    /**
     * @param password: save password
     */
    fun setPassword(password: String){
        editor.putString(PASSWORD, password)
        editor.commit()
    }

    /**
     * @return stored password
     */
    fun getPassword(): String{
        return pref.getString(PASSWORD, "default").toString()
    }

    /**
     * @param lockStatus: true or false current locked status
     */
    fun setLockStatus(lockStatus: Boolean){
        editor.putBoolean(LOCK_STATUS, lockStatus)
        editor.commit()
    }

    /**
     * @return lock status
     */
    fun getLockStatus(): Boolean{
        return pref.getBoolean(LOCK_STATUS, true)
    }

    /**
     * @param isFirstUse: true or false if first install
     */
    fun setIsFirstUse(isFirstUse: Boolean){
        editor.putBoolean(IS_FIRST_USE, isFirstUse)
        editor.commit()
    }

    /**
     * @return if first install or not
     */
    fun getIsFirstUse(): Boolean{
        return pref.getBoolean(IS_FIRST_USE, true)
    }

    /**
     * @param type: type of locking selected
     */
    fun setLockType(type: String){
        editor.putString(LOCK_TYPE, type)
        editor.commit()
    }

    /**
     * @return type of locking
     */
    fun getLockType(): String{
        return pref.getString(LOCK_TYPE, "default").toString()
    }


    /**
     * @param pattern: true or false if first install
     */
    fun setPatternLock(pattern: String){
        editor.putString(PATTERN, pattern)
        editor.commit()
    }

    /**
     * @return current saved pattren
     */
    fun getPattern(): String{
        return pref.getString(PATTERN, "default").toString()
    }







}