package money.terra.model

open class ResultWrapper<T>(
    val height: String,
    val result: T
)