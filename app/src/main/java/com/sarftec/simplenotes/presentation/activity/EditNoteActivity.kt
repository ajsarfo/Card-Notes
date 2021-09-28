package com.sarftec.simplenotes.presentation.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.sarftec.simplenotes.R
import com.sarftec.simplenotes.databinding.ActivityEditNoteBinding
import com.sarftec.simplenotes.presentation.adapter.ColorAdapter
import com.sarftec.simplenotes.presentation.advertisement.AdCountManager
import com.sarftec.simplenotes.presentation.advertisement.BannerManager
import com.sarftec.simplenotes.presentation.advertisement.InterstitialManager
import com.sarftec.simplenotes.presentation.getDateString
import com.sarftec.simplenotes.presentation.message.note.MessageBox.Companion.NOTE_MESSAGE
import com.sarftec.simplenotes.presentation.viewmodel.NoteEditViewModel
import com.sarftec.simplenotes.presentation.viewmodel.NoteState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditNoteActivity : BaseActivity() {

    private val layoutBinding by lazy {
        ActivityEditNoteBinding.inflate(layoutInflater)
    }

    private val noteEditViewModel by viewModels<NoteEditViewModel>()

    private val colorAdapter by lazy {
        ColorAdapter(this) { color ->
            noteEditViewModel.setColor(color)
        }
    }

    private lateinit var interstitialManager: InterstitialManager

    override fun onBackPressed() {
        interstitialManager.show {
            val intent = Intent()
            noteEditViewModel.getResponse()
                ?.let { it as Parcelable }
                ?.let { intent.putExtra(ACTIVITY_PARCEL, it) }
            setResult(Activity.RESULT_OK, intent)
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutBinding.root)
        savedInstanceState ?: setParcel()
        //AdCountManager is declared in the companion object
        val adRequest = AdRequest.Builder().build()
        interstitialManager = InterstitialManager(this, adCountManager, adRequest)
        interstitialManager.load(getString(R.string.admob_edit_interstitial_id))
        BannerManager(this, adRequest).attachBannerAd(
            getString(R.string.admob_edit_banner_id),
            layoutBinding.editBanner
        )
        setupToolbar()
        setupButtons()
        setupAdapter()
        observeLiveData()
        noteEditViewModel.getState()?.let {
            setEditContent(it)
        }
        layoutBinding.viewModel = noteEditViewModel
        layoutBinding.executePendingBindings()
    }

    private fun setParcel() = intent?.apply {
        getParcelableExtra<Parcelable>(NOTE_MESSAGE)?.let {
            noteEditViewModel.setParcel(it)
        }
    }

    private fun setupToolbar() {
        layoutBinding.toolbarTitle.text = noteEditViewModel.getToolbarTitle()
        layoutBinding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupButtons() {
        layoutBinding.toolbarActionCard.setOnClickListener {
            noteEditViewModel.save()
            Toast.makeText(this, "Note Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAdapter() {
        layoutBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@EditNoteActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = colorAdapter
            setHasFixedSize(true)
        }
    }

    private fun setEditContent(state: NoteState) {
        state.let {
            layoutBinding.noteDate.text = getDateString(it.note.note.date)
            layoutBinding.noteContent.setText(it.note.note.content)
            layoutBinding.noteTitle.setText(it.note.note.title)
        }
    }

    private fun observeLiveData() {
        noteEditViewModel.noteState.observe(this) {
            layoutBinding.editContentCard.setCardBackgroundColor(it.note.meta.noteColor)
            layoutBinding.toolbarActionCard.isClickable = it.isSavable
            layoutBinding.toolbarActionCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    if (!it.isSavable) R.color.color_note_background else R.color.colorPrimary
                )
            )
        }
    }

    companion object {
        val adCountManager = AdCountManager(listOf(3))
    }
}