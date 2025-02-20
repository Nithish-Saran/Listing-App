package com.listingapp

import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ApiRepo {
    suspend fun fetchWeather (app: ListApp, lat: Double, lon: Double) : JSONObject? = suspendCoroutine {
        httpGetJSON(
            app = app,
            url = "${Constant.WEATHER_API}lat=$lat&lon=$lon&units=metric&appid=${Constant.API_KEY}"
        ) {_, _, jsonObject, _ -> it.resume(jsonObject) }
    }
    suspend fun fetchWeatherCity (app: ListApp, lat: Double, lon: Double) : JSONArray? = suspendCoroutine {
        httpGetJSON(
            app = app,
            url = "${Constant.CITY_API}lat=$lat&lon=$lon&limit=1&appid=${Constant.API_KEY}"
        ) {_, _, _, jsonArray -> it.resume(jsonArray) }
    }
    suspend fun fetchUser (app: ListApp) : JSONObject? = suspendCoroutine {
        httpGetJSON(
            app = app,
            url = Constant.USER_API
        ) {_, _, jsonObject, _ -> it.resume(jsonObject) }
    }
}

private fun httpGetJSON(app: ListApp, url: String, responder: (Boolean, Int, JSONObject?, JSONArray?) -> Unit) {
    AsyncHttpClient().let {
        it.setMaxRetriesAndTimeout(2, 0)
        it.get(app, url, object: JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int, headers: Array<out Header>?,
                response: JSONArray?
            ) {
                responder(true, statusCode, null, response)
            }

            override fun onSuccess(
                statusCode: Int, headers: Array<out Header>?,
                response: JSONObject?
            ) {
                responder(true, statusCode, response, null)
            }

            override fun onSuccess(
                statusCode: Int, headers: Array<out Header>?,
                responseString: String?
            ) {
                responder(true, statusCode, null, null)
            }

            override fun onFailure(
                statusCode: Int, headers: Array<out Header>?,
                responseString: String?, throwable: Throwable?
            ) {
//                log("HTTP Error: Code: $statusCode, Response: $responseString")
//                logE(throwable, "HTTP Error")
                responder(false, statusCode, null, null)
            }

            override fun onFailure(
                statusCode: Int, headers: Array<out Header>?,
                throwable: Throwable?, errorResponse: JSONArray?
            ) {
//                log("HTTP Error: Code: $statusCode, Response: $errorResponse")
//                logE(throwable, "HTTP Error")
                responder(false, statusCode, null, null)
            }

            override fun onFailure(
                statusCode: Int, headers: Array<out Header>?,
                throwable: Throwable?, errorResponse: JSONObject?
            ) {
//                log("HTTP Error: Code: $statusCode, Response: $errorResponse")
//                logE(throwable, "HTTP Error")
                responder(false, statusCode, null, null)
            }
        })
    }
}