package com.happyhappyyay.android.dreamdiary.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import androidx.preference.EditTextPreference
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class CustomEditTextPreference: EditTextPreference{
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )
    constructor(context: Context?,attrs: AttributeSet?,defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun getSharedPreferences(): SharedPreferences {
        Log.d("custom","shared pref")
        return if(Build.VERSION.SDK_INT >= 23){
            val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
            val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
            EncryptedSharedPreferences.create(
                "default_values",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } else{
            super.getSharedPreferences()
        }
    }
}