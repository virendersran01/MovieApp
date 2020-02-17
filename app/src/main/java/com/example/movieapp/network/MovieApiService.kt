package com.example.movieapp.network

import retrofit2.http.GET


//A retrofit service to fetch movie playlist.
interface MovieApiService {
    @GET("movie.json")
    suspend fun getPropertyAsync(): List<MovieProperty>
}


