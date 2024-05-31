@file:DependsOn("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")
@file:DependsOn("com.fasterxml.jackson.core:jackson-databind:2.17.1")

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

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
			val address: String,
			val latitude: Double,
			val longitude: Double,
		)
	}
}

val cinemas = jacksonObjectMapper().readValue<CinemasResponse>("[]")
