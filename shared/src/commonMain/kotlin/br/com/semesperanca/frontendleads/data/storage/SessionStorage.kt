package br.com.semesperanca.frontendleads.data.storage

class SessionStorage(
    private val store: KeyValueStore,
) {
    private val tokenKey = "auth_token"

    var token: String?
        get() = store.get(tokenKey)
        set(value) {
            if (value.isNullOrBlank()) store.remove(tokenKey) else store.set(tokenKey, value)
        }

    val isLoggedIn: Boolean
        get() = !token.isNullOrBlank()

    fun clear() {
        store.remove(tokenKey)
    }
}
