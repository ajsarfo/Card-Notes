package com.sarftec.cardnotes.presentation.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sarftec.cardnotes.R
import com.sarftec.cardnotes.databinding.LayoutColorBinding

class ColorAdapter(
    context: Context,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    private val colors = context.resources.getStringArray(R.array.note_colors).map {
        Color.parseColor(it)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val layoutBinding = LayoutColorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ColorViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position])
    }

    override fun getItemCount(): Int = colors.size

    //View holder declaration
    inner class ColorViewHolder(
        private val layoutBinding: LayoutColorBinding
    ) : RecyclerView.ViewHolder(layoutBinding.root) {

        fun bind(color: Int) {
            layoutBinding.colorCard.setCardBackgroundColor(color)
            layoutBinding.colorCard.setOnClickListener {
                onClick(color)
            }
        }
    }
}