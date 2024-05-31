@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("io.ktor:ktor-client-java-jvm:2.3.11")
@file:DependsOn("io.ktor:ktor-client-content-negotiation-jvm:2.3.11")
@file:DependsOn("io.ktor:ktor-serialization-jackson-jvm:2.3.11")

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.serialization.jackson.jackson
import kotlinx.coroutines.runBlocking
import java.io.File
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

data class CinemasResponse(
	val body: CinemasBody,
) {

	data class CinemasBody(
		val cinemas: List<Cinema>,
	) {

		data class Cinema(
			val id: String,
			val groupId: String,
			val displayName: String,
			val link: URI,
			val address: String,
			val latitude: Double,
			val longitude: Double,
		)
	}
}

fun ObjectMapper.cineworldConfig() {
	configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}

fun loadFile(): List<CinemasResponse.CinemasBody.Cinema> {
	val client = jacksonObjectMapper().apply { cineworldConfig() }.reader()
	return client.readValue(File("cinemas.json"), CinemasResponse::class.java).body.cinemas
}

fun loadNetwork(now: LocalDate): List<CinemasResponse.CinemasBody.Cinema> {
	val client = HttpClient {
		install(ContentNegotiation) {
			jackson {
				cineworldConfig()
			}
		}
		expectSuccess = true
	}

	fun cinemas(url: String): List<CinemasResponse.CinemasBody.Cinema> {
		println("Loading... ${url}")
		return runBlocking { client.get(url).body<CinemasResponse>().body.cinemas }
	}

	val nextYear = now.plusYears(1).toString()

	@Suppress("MaxLineLength")
	val uk =
		cinemas("https://www.cineworld.co.uk/uk/data-api-service/v1/quickbook/10108/cinemas/with-event/until/${nextYear}?attr=&lang=en_GB")

	@Suppress("MaxLineLength")
	val ie =
		cinemas("https://www.cineworld.ie/ie/data-api-service/v1/quickbook/10109/cinemas/with-event/until/${nextYear}?attr=&lang=en_IE")
	return uk + ie
}

val now = LocalDate.now()
val cinemas = loadNetwork(now)