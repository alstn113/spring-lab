package io.github.alstn113.rtr.adapter.`in`.web.common

data class ApiResponse<T> private constructor(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorMessage? = null,
) {

    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(true, data, null)
        }

        fun error(errorMessage: String, errorData: Any? = null): ApiResponse<Unit> {
            return ApiResponse(false, null, ErrorMessage(errorMessage, errorData))
        }
    }
}
