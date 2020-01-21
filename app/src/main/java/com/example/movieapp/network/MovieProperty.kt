package com.example.movieapp.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieProperty(
    @Json(name = "imdbID") val id: String,

    @Json(name = "Title") val title: String,
    @Json(name = "Poster") val poster: String,
    @Json(name = "Year") val year: Int,
    @Json(name = "Rated") val rated: String,
    @Json(name = "Plot") val plot: String
) : Parcelable {
    val isR
        get() = rated == "R"
}
