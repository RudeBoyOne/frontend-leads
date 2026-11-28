package br.com.semesperanca.frontendleads.data.remote

import br.com.semesperanca.frontendleads.apiBaseUrl
import br.com.semesperanca.frontendleads.createHttpClientEngine
import br.com.semesperanca.frontendleads.data.storage.SessionStorage
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiClient(
    val engine: HttpClientEngine = createHttpClientEngine(),
    private val session: SessionStorage,
) {
    val client: HttpClient by lazy {
        HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
            install(Logging) {
                level = LogLevel.INFO
            }
            defaultRequest {
                url(apiBaseUrl)
                contentType(ContentType.Application.Json)
                header("X-Client", "frontend-leads")
                session.token?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }
        }
    }
}