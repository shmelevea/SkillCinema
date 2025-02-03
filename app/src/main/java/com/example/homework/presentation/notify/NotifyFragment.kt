package com.example.homework.presentation.notify

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.homework.R
import com.example.homework.databinding.FragmentNotifyBinding
import com.example.homework.presentation.LocalCollectionsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotifyFragment : DialogFragment() {

    private lateinit var binding: FragmentNotifyBinding
    private var onDismissListener: (() -> Unit)? = null
    private val localCollectionsViewModel: LocalCollectionsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotifyBinding.inflate(layoutInflater)

        setupListeners()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            resources.getDimension(R.dimen.dialog_width).toInt(),
            resources.getDimension(R.dimen.dialog_height).toInt()
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupListeners() {
        binding.apply {
            applyBtn.setOnClickListener {
                val name = inputNameTv.text.toString()
                if (name.isBlank() || name.length > 30) {
                    Toast.makeText(requireContext(),
                        getString(R.string.name_cannot_be_empty), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                localCollectionsViewModel.createCategory(name)
                Toast.makeText(requireContext(),
                    getString(R.string.category_saved), Toast.LENGTH_SHORT).show()
                dismiss()
            }

            closeBtn.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }

    fun setOnDismissListener(listener: () -> Unit) {
        onDismissListener = listener
    }
}