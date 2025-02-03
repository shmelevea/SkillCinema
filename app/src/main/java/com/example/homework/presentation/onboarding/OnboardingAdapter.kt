package com.example.homework.presentation.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.homework.R
import com.example.homework.databinding.ItemOnboardingBinding
import javax.inject.Inject

class OnboardingAdapter @Inject constructor(private val pageChangeListener: OnboardingFragment) :
    RecyclerView.Adapter<OnboardingAdapter.ViewHolder>() {

    private val images = intArrayOf(R.drawable.start_1, R.drawable.start_2, R.drawable.start_3)
    private val titles = arrayOf(
        R.string.find_out_about_premieres,
        R.string.create_collections,
        R.string.share_with_friends
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(images[position], titles[position])
        holder.itemView.setOnClickListener {
            pageChangeListener.onPageChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class ViewHolder(private val binding: ItemOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageResource: Int, titleResource: Int) {
            binding.imageView.setImageResource(imageResource)
            binding.textView.text =
                getMultiLineText(itemView.context.resources.getString(titleResource))
        }
    }

    private fun getMultiLineText(text: String): CharSequence {
        val parts = text.split(" ")
        val firstLine = parts.firstOrNull() ?: ""
        val otherLines = parts.drop(1).joinToString(" ")
        return "$firstLine\n$otherLines"
    }
}
