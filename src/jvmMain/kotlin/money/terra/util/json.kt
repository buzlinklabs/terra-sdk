package money.terra.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val jsonMapper = jacksonObjectMapper()
    .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
    .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)!!

val sortedJsonMapper = jsonMapper.copy()
    .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)!!

actual fun Any.toJson(): String = jsonMapper.writeValueAsString(this)

actual fun Any.toSortedJson(): String = sortedJsonMapper.writeValueAsString(this)