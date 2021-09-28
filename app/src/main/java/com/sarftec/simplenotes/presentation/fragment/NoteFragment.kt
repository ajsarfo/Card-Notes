package com.sarftec.simplenotes.presentation.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sarftec.simplenotes.R
import com.sarftec.simplenotes.databinding.FragmentNotesBinding
import com.sarftec.simplenotes.entity.MetaNote
import com.sarftec.simplenotes.model.Meta
import com.sarftec.simplenotes.model.Note
import com.sarftec.simplenotes.presentation.activity.EditNoteActivity
import com.sarftec.simplenotes.presentation.adapter.NoteAdapter
import com.sarftec.simplenotes.presentation.listener.NoteListener
import com.sarftec.simplenotes.presentation.listener.ToolbarListener
import com.sarftec.simplenotes.presentation.message.note.MessageBox
import com.sarftec.simplenotes.presentation.message.note.MessageBox.Companion.NOTE_MESSAGE
import com.sarftec.simplenotes.presentation.message.note.request.NoteRequest
import com.sarftec.simplenotes.presentation.notification.note.MainToNote
import com.sarftec.simplenotes.presentation.viewmodel.MainViewModel
import com.sarftec.simplenotes.presentation.viewmodel.NoteViewModel
import com.sarftec.simplenotes.presentation.viewmodel.Notification
import com.sarftec.simplenotes.presentation.viewmodel.SearchNotification
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteFragment : Fragment() {

    private lateinit var layoutBinding: FragmentNotesBinding

    private lateinit var noteListener: NoteListener

    private lateinit var toolbarListener: ToolbarListener

    private val noteViewModel by viewModels<NoteViewModel>()

    private val mainViewModel by activityViewModels<MainViewModel>()

    private val noteAdapter by lazy {
        NoteAdapter(
            NoteFragmentCore(
                noteViewModel,
                noteListener,
                onNoteClicked = { position, note ->
                    sendUpdate(position, note)
                },
                onShowFab = { show ->
                    layoutBinding.addNote.visibility = if (show) View.VISIBLE else View.GONE
                }
            )
        )
    }

    override fun onAttach(context: Context) {
        if (context is NoteListener) noteListener = context
        if (context is ToolbarListener) toolbarListener = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layoutBinding = FragmentNotesBinding.inflate(
            inflater,
            container,
            false
        )
        toolbarListener.showNoteToolbar()
        noteViewModel.fetch()
        setupButtons()
        setupAdapter()
        setupIntro()
        observeLiveData()
        layoutBinding.viewModel = noteViewModel
        layoutBinding.executePendingBindings()
        return layoutBinding.root
    }

    private fun setupIntro() {
        layoutBinding.introCard.introTitle.text = getString(R.string.note_intro_title)
        layoutBinding.introCard.introBody.text = getString(R.string.note_intro_body)
        layoutBinding.introCard.childCard.setCardBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.color_note_background)
        )
    }

    private fun setupAdapter() {
        layoutBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = noteAdapter
        }
    }

    private fun setupButtons() {
        layoutBinding.addNote.setOnClickListener {
            noteViewModel.onCloseSearchQuery()
            sendNew(createNote())
        }
        layoutBinding.searchClose.setOnClickListener {
            noteViewModel.onCloseSearchQuery()
        }
    }

    private fun displayRecycleViewOrIntro(isEmpty: Boolean) {
        if (isEmpty) {
            layoutBinding.introCard.parent.visibility = View.VISIBLE
            layoutBinding.recyclerView.visibility = View.GONE
        } else {
            layoutBinding.introCard.parent.visibility = View.GONE
            layoutBinding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun observeLiveData() {
        noteViewModel.notes.observe(viewLifecycleOwner) {
            displayRecycleViewOrIntro(!it.isSearch && it.notes.isEmpty())
            noteAdapter.submitData(it.notes)
        }
        mainViewModel.parcel.observe(viewLifecycleOwner) {
            noteViewModel.setRequestResponse(it)
        }
        noteViewModel.notification.observe(viewLifecycleOwner) {
            handleNotification(it)
        }
        mainViewModel.noteNotification.observe(viewLifecycleOwner) {
            handleMainToNote(it)
        }
        noteViewModel.searchNotification.observe(viewLifecycleOwner) {
            handleSearchQuery(it)
        }
    }

    private fun clearAndHideSearchField() {
        layoutBinding.categorySearch.apply {
            text.clear()
            clearFocus()
        }
        layoutBinding.dummyView.requestFocus()
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE).let { service ->
            (service as InputMethodManager).hideSoftInputFromWindow(
                layoutBinding.categorySearch.windowToken, 0
            )
        }
    }

    private fun handleSearchQuery(notification: SearchNotification) {
        when (notification) {
            is SearchNotification.Notify -> {
                if (notification.clearField) clearAndHideSearchField()
                layoutBinding.searchClose.visibility =
                    if (notification.isVisible) View.VISIBLE else View.GONE
                noteViewModel.neutralizeSearchNotify()
            }
            else -> {

            }
        }
    }

    private fun handleNotification(notification: Notification) {
        when (notification) {
            is Notification.Update -> {
                noteAdapter.notifyItemChanged(notification.position)
                noteViewModel.neutralizeNotification()
            }
            is Notification.Insert -> {
                displayRecycleViewOrIntro(false)
                noteAdapter.notifyItemInserted(0)
                noteViewModel.neutralizeNotification()
            }
            else -> {
            }
        }
    }

    private fun handleMainToNote(mainToNote: MainToNote) {
        when (mainToNote) {
            is MainToNote.CheckAll -> {
                noteAdapter.checkAll(mainToNote.checked)
                mainViewModel.setNoteNotification(MainToNote.Neutral)
            }
            is MainToNote.CloseDelete -> {
                noteAdapter.closeDelete()
                mainViewModel.setNoteNotification(MainToNote.Neutral)
            }
            is MainToNote.DeleteNotes -> {
                noteAdapter.deleteNotes()
                mainViewModel.setNoteNotification(MainToNote.Neutral)
            }
            else -> {

            }
        }
    }

    private fun sendNew(note: MetaNote) {
        val intent = Intent(requireContext(), EditNoteActivity::class.java).apply {
            putExtra(NOTE_MESSAGE, NoteRequest.New(note, MessageBox.NEW))
        }
        noteListener.navigate(intent)
    }

    private fun sendUpdate(position: Int, note: MetaNote) {
        val intent = Intent(requireContext(), EditNoteActivity::class.java).apply {
            putExtra(NOTE_MESSAGE, NoteRequest.Update(note, MessageBox.UPDATE, position))
        }
        noteListener.navigate(intent)
    }

    private fun createNote(): MetaNote {
        val color = ContextCompat.getColor(requireContext(), R.color.color_toolbar)
        return MetaNote(Note(title = "", content = ""), Meta(noteColor = color))
    }

    inner class NoteFragmentCore(
        val noteViewModel: NoteViewModel,
        val noteListener: NoteListener,
        val onNoteClicked: (Int, MetaNote) -> Unit,
        val onShowFab: (Boolean) -> Unit
    )
}