package com.fjr.imagesearchapp.ui.gallery

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.fjr.imagesearchapp.data.UnsplashPhoto
import com.fjr.imagesearchapp.data.UnsplashRepository
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by Franky Wijanarko on 26/09/21.
 * frank.jr.619@gmail.com
 */

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: UnsplashRepository,
    state: SavedStateHandle
) : ViewModel() {

    private val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)

//    var photosLD: LiveData<PagingData<UnsplashPhoto>> = MutableLiveData<PagingData<UnsplashPhoto>>()

    private var _photosFLow = MutableStateFlow<PagingData<UnsplashPhoto>?>(null)
        val photosFLow = _photosFLow.asStateFlow()

    init {
        getPhotos()
    }

//    val photos = currentQuery.switchMap { queryString ->
//       repository.getSearchResult(queryString).cachedIn(viewModelScope)
//    }

    private fun getPhotos() {
//        photosLD =  repository.getSearchResult(DEFAULT_QUERY).cachedIn(viewModelScope)
        viewModelScope.launch {
            currentQuery.value?.let {
                repository.getSearchResultFlow(it).cachedIn(viewModelScope).collect {
                    _photosFLow.value = it
                }
            }
        }
    }

    fun searchPhotos(query: String) {
        currentQuery.value = query
        getPhotos()
    }

    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val DEFAULT_QUERY = "cats"
    }
}