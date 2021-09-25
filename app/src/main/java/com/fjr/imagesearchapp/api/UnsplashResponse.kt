package com.fjr.imagesearchapp.api

import com.fjr.imagesearchapp.data.UnsplashPhoto


/**
 * Created by Franky Wijanarko on 26/09/21.
 * frank.jr.619@gmail.com
 */
data class UnsplashResponse(
    val results: List<UnsplashPhoto>
)