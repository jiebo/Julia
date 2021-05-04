package com.tijiebo.julia.ui.main

import android.content.Context
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tijiebo.julia.MainActivity
import com.tijiebo.julia.R
import com.tijiebo.julia.databinding.MainFragmentBinding
import com.tijiebo.julia.ui.gallery.viewmodel.GalleryViewModel
import com.tijiebo.julia.ui.julia.models.JuliaHighlights
import com.tijiebo.julia.ui.julia.models.JuliaImage
import com.tijiebo.julia.ui.main.viewmodel.MainViewModel
import io.reactivex.disposables.CompositeDisposable

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        const val TAG = "MainFragment"
    }

    private var activity: MainActivity? = null
    private lateinit var mainVm: MainViewModel
    private lateinit var galleryVm: GalleryViewModel
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVm = ViewModelProvider(this).get(MainViewModel::class.java)
        galleryVm = ViewModelProvider(this).get(GalleryViewModel::class.java)
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
        binding.shutter.setOnClickListener {
            binding.juliaView.apply {
                galleryVm.getJuliaImage(this, JuliaImage.getNameFrom(this.getC()))
            }
        }
        binding.highlights.apply {
            adapter = HighlightsAdapter(JuliaHighlights.getAll()) { c: PointF ->
                mainVm.updateJuliaView(c)
            }
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        binding.more.setOnClickListener {
            showCustomInputDialog()
        }
        binding.gallery.setOnClickListener {
            activity?.showGallery()
            binding.newIndicator.visibility = View.GONE
        }
        binding.reset.setOnClickListener {
            mainVm.resetJuliaView()
        }
        binding.zoomIn.setOnClickListener {
            mainVm.zoomJuliaView(2f)
        }
        binding.zoomOut.setOnClickListener {
            mainVm.zoomJuliaView(0.5f)
        }
    }

    private fun observeData() {
        disposables.add(galleryVm.uploadProgress.subscribe {
            setUploadProgress(it)
        })
        disposables.add(mainVm.resetJuliaView.subscribe {
            binding.juliaView.reset()
        })
        mainVm.updateJuliaView.observe(viewLifecycleOwner, {
            binding.juliaView.updateConstant(it)
        })
        mainVm.zoomJuliaView.observe(viewLifecycleOwner, {
            binding.juliaView.updateView(zoom = it)
        })
        mainVm.enableZoomIn.observe(viewLifecycleOwner, {
            binding.zoomIn.apply {
                isEnabled = it
                alpha = if (it) 1f else 0.12f
            }
        })
        mainVm.enableZoomOut.observe(viewLifecycleOwner, {
            binding.zoomOut.apply {
                isEnabled = it
                alpha = if (it) 1f else 0.12f
            }
        })
    }

    private fun setUploadProgress(uploadStatus: GalleryViewModel.UploadStatus) {
        binding.progressTextContainer.visibility = View.VISIBLE
        binding.progressIndicator.visibility = View.VISIBLE
        when (uploadStatus) {
            is GalleryViewModel.UploadStatus.Failure -> {
                binding.progressIndicator.progress = 0
                binding.progressText.text = getString(R.string.upload_failed)
            }
            is GalleryViewModel.UploadStatus.Pending -> {
                binding.progressIndicator.progress = (uploadStatus.progress * 100).toInt()
                binding.progressText.text = getString(R.string.upload_pending)
            }
            is GalleryViewModel.UploadStatus.Success -> {
                binding.progressIndicator.progress = 100
                binding.progressText.text = getString(R.string.upload_complete)
                binding.newIndicator.visibility = View.VISIBLE
                binding.progressTextContainer.postDelayed({
                    binding.progressTextContainer.visibility = View.GONE
                }, 2000)
                binding.progressIndicator.postDelayed({
                    binding.progressIndicator.visibility = View.GONE
                }, 2000)
            }
        }
    }

    private fun showCustomInputDialog() {
        var coefficientX: EditText
        var coefficientY: EditText
        MaterialAlertDialogBuilder(requireContext())
            .setView(LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_custom_input, null, false).apply {
                    coefficientX = this.findViewById(R.id.coefficient_x)
                    coefficientY = this.findViewById(R.id.coefficient_y)
                })
            .setTitle(R.string.custom_input_title)
            .setPositiveButton(R.string.custom_input_cta) { dialog, _ ->
                val cX = coefficientX.text.toString().toFloatOrNull() ?: 0f
                val cY = coefficientY.text.toString().toFloatOrNull() ?: 0f
                mainVm.updateJuliaView(PointF(cX, cY))
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

}