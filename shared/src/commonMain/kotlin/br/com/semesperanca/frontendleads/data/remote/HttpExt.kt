package br.com.semesperanca.frontendleads.data.remote

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val message: String, val status: Int? = null) : ApiResult<Nothing>
}

private val apiJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

internal suspend inline fun <reified T> ApiClient.get(
    path: String,
    crossinline block: io.ktor.client.request.HttpRequestBuilder.() -> Unit = {},
): ApiResult<T> = try {
    val response = client.get(path) { block() }
    handle(response)
} catch (t: Throwable) {
    if (t is kotlinx.coroutines.CancellationException) throw t
    ApiResult.Error("Sem conexão com o servidor")
}

internal suspend inline fun <reified Req, reified Res> ApiClient.post(
    path: String,
    body: Req,
): ApiResult<Res> = try {
    val response = client.post(path) {
        contentType(ContentType.Application.Json)
        setBody(apiJson.encodeToString(body))
    }
    handle(response)
} catch (t: Throwable) {
    if (t is kotlinx.coroutines.CancellationException) throw t
    ApiResult.Error("Sem conexão com o servidor")
}

internal suspend inline fun <reified Req, reified Res> ApiClient.put(
    path: String,
    body: Req,
): ApiResult<Res> = try {
    val response = client.put(path) {
        contentType(ContentType.Application.Json)
        setBody(apiJson.encodeToString(body))
    }
    handle(response)
} catch (t: Throwable) {
    if (t is kotlinx.coroutines.CancellationException) throw t
    ApiResult.Error("Sem conexão com o servidor")
}

internal suspend fun ApiClient.delete(path: String): ApiResult<Unit> = try {
    val response = client.delete(path) {
        contentType(ContentType.Application.Json)
    }
    handle(response)
} catch (t: Throwable) {
    if (t is kotlinx.coroutines.CancellationException) throw t
    ApiResult.Error("Sem conexão com o servidor")
}

internal suspend fun ApiClient.patch(
    path: String,
): ApiResult<Unit> = try {
    val response = client.patch(path) {
        contentType(ContentType.Application.Json)
    }
    handle(response)
} catch (t: Throwable) {
    if (t is kotlinx.coroutines.CancellationException) throw t
    ApiResult.Error("Sem conexão com o servidor")
}

internal suspend inline fun <reified Req, reified Res> ApiClient.patch(
    path: String,
    body: Req,
): ApiResult<Res> = try {
    val response = client.patch(path) {
        contentType(ContentType.Application.Json)
        setBody(apiJson.encodeToString(body))
    }
    handle(response)
} catch (t: Throwable) {
    if (t is kotlinx.coroutines.CancellationException) throw t
    ApiResult.Error("Sem conexão com o servidor")
}

private suspend inline fun <reified T> handle(response: HttpResponse): ApiResult<T> {
    val status = response.status
    return if (status.isSuccess()) {
        if (T::class == Unit::class) {
            @Suppress("UNCHECKED_CAST")
            ApiResult.Success(Unit as T)
        } else {
            try {
                ApiResult.Success(apiJson.decodeFromString<T>(response.bodyAsText()))
            } catch (t: Throwable) {
                ApiResult.Error("Falha ao interpretar resposta: ${t.message}", status.value)
            }
        }
    } else {
        val body = runCatching { response.bodyAsText() }.getOrNull()
        ApiResult.Error(decodeErrorMessage(body) ?: "Erro HTTP ${status.value}", status.value)
    }
}

private fun decodeErrorMessage(body: String?): String? {
    if (body.isNullOrBlank()) return null
    return try {
        val node = apiJson.parseToJsonElement(body)
        if (node !is JsonObject) return null

        val fieldMessages = (node["fields"] as? JsonObject)
            ?.values
            ?.mapNotNull { (it as? JsonPrimitive)?.content?.takeIf { s -> s.isNotBlank() } }
            .orEmpty()
        if (fieldMessages.isNotEmpty()) {
            fieldMessages.joinToString("; ")
        } else {
            (node["message"] ?: node["error"])?.let {
                if (it is JsonPrimitive) it.content.takeIf { s -> s.isNotBlank() } else null
            }
        }
    } catch (_: Throwable) {
        null
    }
}
