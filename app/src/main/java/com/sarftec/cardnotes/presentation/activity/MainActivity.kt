package com.sarftec.cardnotes.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sarftec.cardnotes.R
import com.sarftec.cardnotes.databinding.*
import com.sarftec.cardnotes.model.Todo
import com.sarftec.cardnotes.presentation.dialog.SortDialog
import com.sarftec.cardnotes.presentation.listener.NoteListener
import com.sarftec.cardnotes.presentation.listener.TodoListener
import com.sarftec.cardnotes.presentation.listener.ToolbarListener
import com.sarftec.cardnotes.presentation.notification.note.MainToNote
import com.sarftec.cardnotes.presentation.notification.note.NoteToMain
import com.sarftec.cardnotes.presentation.notification.todo.MainToTodo
import com.sarftec.cardnotes.presentation.viewmodel.MainViewModel
import com.sarftec.cardnotes.rateApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity(), NoteListener, ToolbarListener, TodoListener {

    private val layoutBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mainViewModel by viewModels<MainViewModel>()

    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutBinding.root)
        setupButtonListeners()
        setupBottomNavigation()
        setupActivityResultLauncher()
    }

    private fun setupButtonListeners() {
        layoutBinding.deleteClose.setOnClickListener {
            mainViewModel.setNoteNotification(MainToNote.CloseDelete)
        }
        layoutBinding.deleteLayout.setOnClickListener {
            mainViewModel.setNoteNotification(MainToNote.DeleteNotes)
        }
        layoutBinding.selectAll.setOnCheckedChangeListener { _, isChecked ->
            mainViewModel.setNoteNotification(MainToNote.CheckAll(isChecked))
        }
        layoutBinding.more.setOnClickListener {
            createRemoveCompletedSheet()
        }
        layoutBinding.thumbUp.setOnClickListener {
            rateApp()
        }
    }

    private fun setupBottomNavigation() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_container) as NavHostFragment
        layoutBinding.bottomNavigationView.setupWithNavController(
            navHost.findNavController()
        )
    }

    private fun setupActivityResultLauncher() {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.getParcelableExtra<Parcelable>(ACTIVITY_PARCEL)?.let { parcel ->
                mainViewModel.setResultParcel(parcel)
            }
        }
    }

    private fun switchToNormal(isNormal: Boolean) {
        if (!isNormal) {
            layoutBinding.deleteToolbar.visibility = View.VISIBLE
            layoutBinding.deleteLayout.visibility = View.VISIBLE
            layoutBinding.mainToolbar.visibility = View.GONE
            layoutBinding.bottomNavigationView.visibility = View.GONE
        } else {
            layoutBinding.deleteToolbar.visibility = View.GONE
            layoutBinding.deleteLayout.visibility = View.GONE
            layoutBinding.bottomNavigationView.visibility = View.VISIBLE
            layoutBinding.mainToolbar.visibility = View.VISIBLE
        }
    }

    private fun setDeleteCount(count: Int) {
        val title = when {
            count > 1 -> "$count notes selected"
            count == 1 -> "$count note selected"
            else -> "Select at least one note"
        }
        layoutBinding.selectText.text = title
    }

    private fun createSortDialog() : SortDialog {
        return SortDialog(
            layoutBinding.root,
            LayoutTodoSortDialogBinding.inflate(
                layoutInflater,
                layoutBinding.root,
                false
            ),
            onDefault = {
                mainViewModel.setTodoNotification(MainToTodo.Sort(MainToTodo.Sort.DATE))
            },
            onAscending = {
                mainViewModel.setTodoNotification(MainToTodo.Sort(MainToTodo.Sort.ASC))
            },
            onDescending = {
                mainViewModel.setTodoNotification(MainToTodo.Sort(MainToTodo.Sort.DESC))
            }
        )
    }

    override fun navigate(intent: Intent) {
        launcher.launch(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun notification(notification: NoteToMain) {
        when (notification) {
            is NoteToMain.Mode -> switchToNormal(notification.isNormal)
            is NoteToMain.NoteCount -> setDeleteCount(notification.count)
        }
    }

    override fun showNoteToolbar() {
        layoutBinding.mainTitle.text = getString(R.string.note_title)
        layoutBinding.more.visibility = View.GONE
        layoutBinding.thumbUp.visibility = View.VISIBLE
    }

    override fun showTodoToolbar() {
        layoutBinding.mainTitle.text = getString(R.string.todo_title)
        layoutBinding.more.visibility = View.VISIBLE
        layoutBinding.thumbUp.visibility = View.GONE
    }

    override fun createDeleteSheet(todo: Todo) {
        val sheet = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val binding = LayoutTodoDeleteBinding.inflate(
            layoutInflater
        )
        binding.deleteTodo.setOnClickListener {
            mainViewModel.setTodoNotification(MainToTodo.DeleteTodo(todo))
            sheet.cancel()
        }
        sheet.setContentView(binding.root)
        sheet.show()
        sheet.setCancelable(true)
    }

    //This is method is called by the activity itself and not anywhere
    override fun createRemoveCompletedSheet() {
        val sheet = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val binding = LayoutTodoDeleteCompletedBinding.inflate(
            layoutInflater
        )
        binding.deleteCompleted.setOnClickListener {
            sheet.cancel()
            mainViewModel.setTodoNotification(MainToTodo.DeleteCompleted)
        }
        binding.sort.setOnClickListener {
            sheet.cancel()
            createSortDialog().show()
        }
        sheet.setContentView(binding.root)
        sheet.show()
        sheet.setCancelable(true)
    }

    override fun createTodoSheet() {

    }
}