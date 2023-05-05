package io.github.okafke.aapi.app.util

import android.content.Context
import androidx.preference.PreferenceManager
import io.github.okafke.aapi.app.R

object AApiUtil {
    @JvmStatic
    fun getInputs(ctx: Context, forApi: Boolean = true): Int {
        val preference = PreferenceManager.getDefaultSharedPreferences(ctx)
        val pref = PreferenceManager.getDefaultSharedPreferences(ctx)
        val backAction = pref.getBoolean(ctx.getString(R.string.back_action_key), false)
        val result = preference.getInt(ctx.getString(R.string.number_of_inputs_key), 4)
        return if (backAction && forApi) result - 1 else result
    }
}
