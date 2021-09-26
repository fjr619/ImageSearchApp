package com.fjr.imagesearchapp.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.fjr.imagesearchapp.R
import com.fjr.imagesearchapp.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


/**
 * Created by Franky Wijanarko on 26/09/21.
 * frank.jr.619@gmail.com
 */

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    private val viewModel by viewModels<GalleryViewModel>()

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentGalleryBinding.bind(view)

        val adapter = UnsplashPhotoAdapter {
            val action = GalleryFragmentDirections.actionGalleryFragmentToDetailsFragment(it)
            findNavController().navigate(action)

        }
        binding.apply {
            recyclerView.apply {
                setHasFixedSize(true)
                itemAnimator = null
                this.adapter = adapter.withLoadStateHeaderAndFooter(
                    header = UnsplashPhotoLoadStateAdapter { adapter.retry() },
                    footer = UnsplashPhotoLoadStateAdapter { adapter.retry() }
                )
            }

            buttonRetry.setOnClickListener {
                adapter.retry()
            }
        }

//        viewModel.photosLD.observe(viewLifecycleOwner) {
//            adapter.submitData(viewLifecycleOwner.lifecycle, it)
//        }

        lifecycleScope.launchWhenCreated {
            viewModel.photosFLow.collectLatest {
                if (it != null) {
                    adapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
            }
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1) {
                    recyclerView.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_gallery, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                Log.e("TAG","searchView setOnActionExpandListener")
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                Log.e("TAG","searchView onMenuItemActionCollapse")
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    binding.recyclerView.scrollToPosition(0)
                    viewModel.searchPhotos(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}