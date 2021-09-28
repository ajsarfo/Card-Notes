package com.sarftec.simplenotes.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sarftec.simplenotes.databinding.LayoutNoteBinding
import com.sarftec.simplenotes.entity.MetaNote
import com.sarftec.simplenotes.presentation.fragment.NoteFragment
import com.sarftec.simplenotes.presentation.getDateString
import com.sarftec.simplenotes.presentation.notification.note.NoteToMain

class NoteAdapter(
    private val noteFragmentCore: NoteFragment.NoteFragmentCore
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var notes = mutableListOf<MetaNote>()

    private val viewHolders = mutableSetOf<NoteViewHolder>()

    private val checkedNotes = mutableSetOf<MetaNote>()

    private var clickState: ClickState = Single()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val layoutBinding = LayoutNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(layoutBinding).also {
            viewHolders.add(it)
        }
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.showCheck(clickState is Hold)
        holder.bind(note)
    }

    override fun getItemCount(): Int = notes.size


    fun deleteNotes() {
        noteFragmentCore.noteViewModel.deleteNotes(
            checkedNotes.toList()
        )
        closeDelete()
    }

    fun closeDelete() {
        clickState = Single()
        noteFragmentCore.noteListener.notification(
            NoteToMain.Mode(true)
        )
    }

    fun checkAll(isChecked: Boolean) {
        viewHolders.forEach { it.check(isChecked) }
        if(isChecked) checkedNotes.addAll(notes) else checkedNotes.clear()
        noteFragmentCore.noteListener.notification(
            NoteToMain.NoteCount(checkedNotes.size)
        )
    }

    fun submitData(items: MutableList<MetaNote>) {
        checkedNotes.clear()
        clickState = Single()
        notes = items
        notifyDataSetChanged()
    }

    //Note Adapter ViewHolder
    inner class NoteViewHolder(
        private val layoutBinding: LayoutNoteBinding
    ) : RecyclerView.ViewHolder(layoutBinding.root) {

        fun bind(note: MetaNote) = with(layoutBinding) {
            title.text = note.note.title
            content.text = note.note.content
            date.text = getDateString(note.note.date)
            noteCard.setCardBackgroundColor(note.meta.noteColor)
            noteCard.setOnClickListener {
                clickState.click(ClickState.State(layoutBinding, note, adapterPosition))
            }
            noteCard.setOnLongClickListener {
                clickState.hold(ClickState.State(layoutBinding, note, adapterPosition))
                true
            }
            checkBox.setOnCheckedChangeListener {_, isChecked ->
                clickState.checkListen(ClickState.State(layoutBinding, note, adapterPosition), isChecked)
            }
             check(checkedNotes.contains(note))
        }

        fun check(isChecked: Boolean) {
            layoutBinding.checkBox.isChecked = isChecked
        }

        fun showCheck(show: Boolean) {
            layoutBinding.checkBox.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    //Click State Class
    interface ClickState {
        fun click(state: State)
        fun hold(state: State)
        fun checkListen(state: State, isChecked: Boolean) {}
        class State(
            val binding: LayoutNoteBinding,
            val note: MetaNote,
            val position: Int
        )
    }

    inner class Single : ClickState {

        init {
            noteFragmentCore.onShowFab(true)
            viewHolders.forEach {
                it.check(false)
                it.showCheck(false)
            }
            checkedNotes.clear()
        }

        override fun click(state: ClickState.State) {
            noteFragmentCore.onNoteClicked(state.position, state.note)
        }

        override fun hold(state: ClickState.State) {
            clickState = Hold(state)
        }
    }

    inner class Hold(state: ClickState.State) : ClickState {

        init {
            viewHolders.forEach { it.showCheck(true) }
            checkState(state)
            noteFragmentCore.onShowFab(false)
            noteFragmentCore.noteListener.notification(
                NoteToMain.Mode(false)
            )
            noteFragmentCore.noteListener.notification(
                NoteToMain.NoteCount(checkedNotes.size)
            )
        }

        override fun click(state: ClickState.State) {
            checkState(state, !state.binding.checkBox.isChecked)
            noteFragmentCore.noteListener.notification(
                NoteToMain.NoteCount(checkedNotes.size)
            )
        }

        override fun hold(state: ClickState.State) {

        }

        override fun checkListen(state: ClickState.State, isChecked: Boolean) {
            checkState(state, isChecked)
            noteFragmentCore.noteListener.notification(
                NoteToMain.NoteCount(checkedNotes.size)
            )
        }

        private fun checkState(state: ClickState.State, isChecked: Boolean = true) {
            state.binding.checkBox.isChecked = isChecked
            if (isChecked) checkedNotes.add(state.note) else checkedNotes.remove(state.note)
        }
    }
}