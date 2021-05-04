package com.tijiebo.julia.ui.gallery

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tijiebo.julia.databinding.FullViewDialogFragmentBinding

class FullViewDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(uri: Uri) = FullViewDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(X_ARG, uri)
            }
        }

        const val TAG = "GalleryFragment"
        private val X_ARG = "x_arg"
    }

    private var _binding: FullViewDialogFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FullViewDialogFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uri = arguments?.getParcelable(X_ARG) as? Uri
        uri?.let {
            Glide.with(binding.image)
                .load(uri)
                .into(binding.image)
        }
    }
}