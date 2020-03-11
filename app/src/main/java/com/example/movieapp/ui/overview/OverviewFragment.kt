package com.example.movieapp.ui.overview

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp.R
import com.example.movieapp.dagger.App
import com.example.movieapp.dagger.module.viewModule.ViewModelFactory
import com.example.movieapp.databinding.OverviewFragmentBinding
import com.example.movieapp.ui.detail.DetailActivity
import com.example.movieapp.utils.adapters.*
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject


class OverviewFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var viewModel: OverviewViewModel
    lateinit var binding: OverviewFragmentBinding

    lateinit var recViewingMovieAdapter: RecViewingMovieAdapter
    lateinit var topRatedMovieAdapter: TopRatedMovieAdapter
    lateinit var popularMovieAdapter: PopularMovieAdapter
    lateinit var nowPlayingMovieAdapter: NowPlayingMovieAdapter

    private var errorSnackbar: Snackbar? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        App.appComponent.inject(this)
        binding = OverviewFragmentBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, viewModelFactory).get(OverviewViewModel::class.java)

        //Listener of recycler view click

        recViewingRvViewing()
        topRatedRvViewing()
        popularRvViewing()
        nowPlayingRvViewing()




        val snapHelperStart = GravitySnapHelper(Gravity.START)
        snapHelperStart.attachToRecyclerView(binding.recyclerRecViewing)

        //Navigate to Detail Activity
        viewModel.navigateToSelectProperty.observe(viewLifecycleOwner, Observer {
            it?.let {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("movie", it.id)
                startActivity(intent)
                viewModel.displayPropertyDetailsCompleted()
            }
        })


        //Looking for the internet connection
        viewModel.eventNetworkError.observe(viewLifecycleOwner, Observer {
                if (it) onNetworkError()
            })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray(
            "ARTICLE_SCROLL_POSITION",
            intArrayOf(binding.scrollView.scrollX, binding.scrollView.scrollY)
        )
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val position = savedInstanceState?.getIntArray("ARTICLE_SCROLL_POSITION")
        if (position != null) binding.scrollView.post {
            binding.scrollView.scrollTo(
                position[0],
                position[1]
            )
        }
    }

    //Function will show a toast when there is no internet
    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            errorSnackbar = Snackbar.make(binding.root, "Ошибка сети", Snackbar.LENGTH_INDEFINITE)
            errorSnackbar?.setAction(R.string.retry, viewModel.errorClickListener)
            errorSnackbar?.show()
        }
    }

    private fun recViewingRvViewing() {
        binding.recyclerRecViewing.adapter =
            RecViewingMovieAdapter(RecViewingMovieAdapter.ClickListener {
                viewModel.displayPropertyDetails(it)
            }, mutableListOf())

        recViewingMovieAdapter = binding.recyclerRecViewing.adapter as RecViewingMovieAdapter

        viewModel.recViewingPlayList.observe(viewLifecycleOwner, Observer {
            recViewingMovieAdapter.appendMovies(it)
        })

    }


    private fun topRatedRvViewing(){
        binding.recyclerTopRated.adapter = TopRatedMovieAdapter(MovieListener{
            viewModel.displayPropertyDetails(it)
        })

        topRatedMovieAdapter = binding.recyclerTopRated.adapter as TopRatedMovieAdapter

        viewModel.topRatedPlayList.observe(viewLifecycleOwner, Observer {
            topRatedMovieAdapter.addHeaderAndSubmitList(it)
        })
    }


    private fun popularRvViewing() {
        binding.recyclerPopular.adapter = PopularMovieAdapter(PopularMovieAdapter.ClickListener {
            viewModel.displayPropertyDetails(it)
        }, mutableListOf())

        popularMovieAdapter = binding.recyclerPopular.adapter as PopularMovieAdapter

        viewModel.popularPlayList.observe(viewLifecycleOwner, Observer {
            popularMovieAdapter.appendMovies(it)
        })

    }

    private fun nowPlayingRvViewing() {
        binding.recyclerNowPlaying.adapter =
            NowPlayingMovieAdapter(NowPlayingMovieAdapter.ClickListener {
                viewModel.displayPropertyDetails(it)
            }, mutableListOf())
        nowPlayingMovieAdapter = binding.recyclerNowPlaying.adapter as NowPlayingMovieAdapter

        viewModel.nowPlayingPlayList.observe(viewLifecycleOwner, Observer {
            nowPlayingMovieAdapter.appendMovies(it)
        })
    }

}