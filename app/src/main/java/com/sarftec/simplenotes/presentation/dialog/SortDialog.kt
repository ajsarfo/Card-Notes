package com.sarftec.simplenotes.presentation.dialog

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import com.sarftec.simplenotes.databinding.LayoutTodoSortDialogBinding

class SortDialog(
    parent: View,
    private val binding: LayoutTodoSortDialogBinding,
    private val onDefault: () -> Unit,
    private val onAscending: () -> Unit,
    private val onDescending: () -> Unit
) : AlertDialog(parent.context) {

    init {
        LayoutTodoSortDialogBinding.inflate(layoutInflater)
        with(binding) {
            sortAscend.setOnClickListener {
                onAscending()
                cancel()
            }
            sortDescend.setOnClickListener {
                onDescending()
                cancel()
            }
            sortDefault.setOnClickListener {
                onDefault()
                cancel()
            }
            cancel.setOnClickListener { cancel() }
             requestWindowFeature(Window.FEATURE_NO_TITLE)
             window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            setView(binding.root)
        }
    }

}