package br.com.fiap.inovagab.data.local

import android.content.Context

class TokenManager(context: Context) {

    private val preferences = context.getSharedPreferences(
        "inovagab_preferences",
        Context.MODE_PRIVATE
    )

    fun salvarToken(token: String) {
        preferences.edit()
            .putString("jwt_token", token)
            .apply()
    }

    fun obterToken(): String? {
        return preferences.getString("jwt_token", null)
    }

    fun removerToken() {
        preferences.edit()
            .remove("jwt_token")
            .apply()
    }
}