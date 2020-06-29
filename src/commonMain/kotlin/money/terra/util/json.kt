package money.terra.util

expect fun Any.toJson(): String

expect fun Any.toSortedJson(): String

expect val String.asUrlEncode: String