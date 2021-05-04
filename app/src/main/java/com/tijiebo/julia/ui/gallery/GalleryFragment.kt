package com.tijiebo.julia.ui.gallery

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.tijiebo.julia.MainActivity
import com.tijiebo.julia.databinding.GalleryFragmentBinding
import com.tijiebo.julia.ui.gallery.viewmodel.GalleryViewModel
import com.tijiebo.julia.ui.gallery.viewmodel.GalleryViewModel.GalleryStatus
import com.tijiebo.julia.ui.main.viewmodel.MainViewModel
import io.reactivex.disposables.CompositeDisposable

class GalleryFragment : Fragment() {

    companion object {
        fun newInstance() = GalleryFragment()
        const val TAG = "GalleryFragment"
    }

    private var activity: MainActivity? = null
    private lateinit var viewModel: GalleryViewModel
    private val disposables = CompositeDisposable()
    private var _binding: GalleryFragmentBinding? = null
    private val binding get() = _binding!!

    private var galleryAdapter: GalleryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GalleryFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        viewModel.getFromCloud()
        observeData()
        initialiseViews()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as? MainActivity
    }

    private fun initialiseViews() {
        binding.galleryList.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = GalleryAdapter(mutableListOf(), viewModel).apply {
                galleryAdapter = this
            }
        }
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.explore.setOnClickListener { activity?.onBackPressed() }
    }

    private fun observeData() {
        viewModel.imagesOnCloud.observe(viewLifecycleOwner, {
            when (it) {
                is GalleryStatus.Pending -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.emptyContainer.visibility = View.GONE
                    binding.galleryList.visibility = View.GONE
                }
                is GalleryStatus.Empty -> {
                    binding.progress.visibility = View.GONE
                    binding.emptyContainer.visibility = View.VISIBLE
                    binding.galleryList.visibility = View.GONE
                }
                is GalleryStatus.Success -> {
                    binding.progress.visibility = View.GONE
                    binding.emptyContainer.visibility = View.GONE
                    binding.galleryList.visibility = View.VISIBLE
                    galleryAdapter?.updateData(it.list)
                }
            }
        })
        disposables.add(
            viewModel.fullView.subscribe {
                activity?.showFullView(it)
            }
        )
    }
}