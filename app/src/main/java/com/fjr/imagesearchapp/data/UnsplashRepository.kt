package com.fjr.imagesearchapp.data

import com.fjr.imagesearchapp.api.UnsplashApi
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by Franky Wijanarko on 26/09/21.
 * frank.jr.619@gmail.com
 */
@Singleton
class UnsplashRepository @Inject constructor(private val unsplashApi: UnsplashApi) {
}