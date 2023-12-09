package ru.netology.singlealbumapp.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.singlealbumapp.BuildConfig
import ru.netology.singlealbumapp.dto.Album
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

class AlbumRepositoryImpl : AlbumRepository {

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor(::println).apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val typeToken = object : TypeToken<Album>() {}
    private val gson = Gson()

    companion object {
        private const val JSON_URL =
            "https://github.com/netology-code/andad-homeworks/raw/master/09_multimedia/data/album.json"
    }

    override suspend fun getAlbum(): Album {
        val request = Request.Builder()
            .url(JSON_URL)
            .addHeader("Authorization", "token ${BuildConfig.GIT_TOKEN}")
            .build()

        return client.newCall(request)
            .execute()
            .let {
                it.body?.string() ?: throw RuntimeException("body is null")
            }.let {
                gson.fromJson(it, typeToken.type)
            }
    }

    override suspend fun getLimit() {
        val request = Request.Builder()
            .url("https://api.github.com/rate_limit")
            .addHeader("Authorization", "token ${BuildConfig.GIT_TOKEN}")
            .build()
        client.newCall(request)
            .execute()
    }
}