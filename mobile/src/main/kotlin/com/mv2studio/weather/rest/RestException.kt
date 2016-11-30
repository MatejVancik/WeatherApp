package com.mv2studio.weather.rest

import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

/**
 * Created by matej on 28/11/2016.
 */
class RestException private constructor(message: String?, val url: String?, val response: Response<*>?,
                                        val kind: Kind, exception: Throwable?, val retrofit: Retrofit?):
        RuntimeException(message, exception) {

    enum class Kind {
        /** An @see IOException occurred while communicating to the server. */
        NETWORK,
        /** A non-200 HTTP status code was received from the server. */
        HTTP,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }

    companion object {
        fun httpError(url: String, response: Response<*>, retorfit: Retrofit?) : RestException {
            return RestException("${response.code()} ${response.message()}", url, response, Kind.HTTP, null, retorfit)
        }

        fun networkError(exception: IOException) : RestException {
            return RestException(exception.message, null, null, Kind.NETWORK, exception, null)
        }

        fun unexpectedError(exception: Throwable) : RestException {
            return RestException(exception.message, null, null, Kind.UNEXPECTED, exception, null)
        }
    }

    private fun <T> getBodyAs(type: Class<T>) : T? {
        val converter = retrofit?.responseBodyConverter<T>(type, arrayOfNulls<Annotation>(0))
        return converter?.convert(response?.errorBody())
    }
}
