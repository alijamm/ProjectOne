package com.learn.utilities

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


class AppSharedPreferences private constructor(context: Context) {

    companion object : SingletonHolder<AppSharedPreferences, Context>(::AppSharedPreferences)

}