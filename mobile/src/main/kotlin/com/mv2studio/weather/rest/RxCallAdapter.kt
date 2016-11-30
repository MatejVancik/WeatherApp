package com.mv2studio.weather.rest

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

/**
 * Created by matej on 28/11/2016.
 */
class RxCallAdapter : CallAdapter.Factory() {

    val rxFactory = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.newThread())

    override fun get(returnType: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): CallAdapter<*> {
        val callAdapter = rxFactory.get(returnType, annotations, retrofit) as CallAdapter<Observable<*>>
        return MyCallAdapter(retrofit, callAdapter)
    }

    private class MyCallAdapter(val retrofit: Retrofit?, val delegateAdapter: CallAdapter<Observable<*>>) : CallAdapter<Observable<*>> {

        override fun responseType(): Type = delegateAdapter.responseType()

        override fun <R : Any?> adapt(call: Call<R>?): Observable<*> {
            return delegateAdapter.adapt(call)
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext { error : Throwable -> Observable.error { asRestException(error) } }
        }

        private fun asRestException(throwable: Throwable) : RestException {

            // We had non-200 http error
            if (throwable is HttpException) {
                val response = throwable.response()
                return RestException.httpError(response.raw().request().url().toString(), response, retrofit)
            }

            // A network error happened
            if (throwable is IOException) {
                return RestException.networkError(throwable)
            }

            // We don't know what happened. We need to simply convert to an unknown error
            return RestException.unexpectedError(throwable)
        }
    }

}

