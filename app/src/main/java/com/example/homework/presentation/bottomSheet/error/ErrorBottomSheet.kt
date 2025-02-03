package com.example.homework.presentation.bottomSheet.error

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.homework.databinding.BottomSheetErrorMessageBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ErrorBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetErrorMessageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetErrorMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeBtn.setOnClickListener {
            dismiss()
        }

        val errorMessage = arguments?.getString(ARG_ERROR_MESSAGE)
        if (!errorMessage.isNullOrEmpty()) {
            binding.errorTextTv.text = errorMessage
        }
    }

    companion object {
        private const val ARG_ERROR_MESSAGE = "error_message"

        fun newInstance(errorMessage: String): ErrorBottomSheet {
            val fragment = ErrorBottomSheet()
            val args = Bundle()
            args.putString(ARG_ERROR_MESSAGE, errorMessage)
            fragment.arguments = args
            return fragment
        }
    }
}