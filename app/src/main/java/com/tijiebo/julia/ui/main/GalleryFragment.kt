package com.tijiebo.julia.ui.main

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

class GalleryFragment : Fragment() {

    companion object {
        fun newInstance() = GalleryFragment()
        const val TAG = "GalleryFragment"
    }

    private var activity: MainActivity? = null
    private lateinit var viewModel: MainViewModel
    private var _binding: GalleryFragmentBinding? = null
    private val binding get() = _binding!!

    private val galleryAdapter = GalleryAdapter(mutableListOf())

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
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.getFromCloud()
        observeData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as? MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseViews()
    }

    private fun initialiseViews() {
        binding.galleryList.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = galleryAdapter
        }
    }

    private fun observeData() {
        viewModel.imagesOnCloud.observe(viewLifecycleOwner, {
            galleryAdapter.updateData(it)
        })
    }
}