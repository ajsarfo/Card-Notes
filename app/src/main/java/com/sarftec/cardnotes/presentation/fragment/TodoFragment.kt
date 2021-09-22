package com.sarftec.cardnotes.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.sarftec.cardnotes.R
import com.sarftec.cardnotes.databinding.FragmentTodosBinding
import com.sarftec.cardnotes.databinding.LayoutCreateTodoBinding
import com.sarftec.cardnotes.model.Todo
import com.sarftec.cardnotes.presentation.adapter.TodoAdapter
import com.sarftec.cardnotes.presentation.getExtendedDateString
import com.sarftec.cardnotes.presentation.getTimeString
import com.sarftec.cardnotes.presentation.listener.TodoListener
import com.sarftec.cardnotes.presentation.listener.ToolbarListener
import com.sarftec.cardnotes.presentation.notification.todo.MainToTodo
import com.sarftec.cardnotes.presentation.notify.NotifyManager
import com.sarftec.cardnotes.presentation.viewmodel.MainViewModel
import com.sarftec.cardnotes.presentation.viewmodel.SheetTodo
import com.sarftec.cardnotes.presentation.viewmodel.TodoViewModel
import com.sarftec.cardnotes.presentation.viewmodel.TodoViewModelNotification
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TodoFragment : Fragment() {

    private lateinit var layoutBinding: FragmentTodosBinding

    private lateinit var toolbarListener: ToolbarListener

    private lateinit var todoListener: TodoListener

    val todoViewModel by viewModels<TodoViewModel>()

    private val mainViewModel by activityViewModels<MainViewModel>()

    @Inject
    lateinit var notifyManager: NotifyManager

    private val todoAdapter by lazy {
        TodoAdapter(
            TodoFragmentContainer(todoViewModel, todoListener, this) {
                todoViewModel.updateTodo(it)
                launchTodoSheet()
            }
        )
    }

    private var todoObserve: TodoObserve? = null

    private var canSaveTodo: Boolean = false

    override fun onAttach(context: Context) {
        toolbarListener = context as ToolbarListener
        todoListener = context as TodoListener
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layoutBinding = FragmentTodosBinding.inflate(
            inflater,
            container,
            false
        )
        toolbarListener.showTodoToolbar()
        todoViewModel.fetch()
        setupRecyclerView()
        layoutBinding.addTodo.setOnClickListener {
            todoViewModel.createTodo()
            launchTodoSheet()
        }
        observeLiveData()
        return layoutBinding.root
    }

    private fun observeLiveData() {
        todoViewModel.todos.observe(viewLifecycleOwner) {
            todoAdapter.submitData(it.todos)
        }
        mainViewModel.todoNotification.observe(viewLifecycleOwner) {
            observeTodoNotification(it)
        }
        todoViewModel.sheetTodo.observe(viewLifecycleOwner) {
            observeTodoSheet(it)
        }
        todoViewModel.viewModelNotification.observe(viewLifecycleOwner) {
            observeTodoNotification(it)
        }
    }

    private fun observeTodoNotification(notification: TodoViewModelNotification) {
        when (notification) {
            is TodoViewModelNotification.DisplaySaved -> {
                Toast.makeText(requireContext(), notification.message, Toast.LENGTH_SHORT).show()
                todoViewModel.neutralize(TodoViewModelNotification::class)
            }
            else -> {

            }
        }
    }

    private fun observeTodoSheet(todoSheetTodo: SheetTodo) {
        when (todoSheetTodo) {
            is SheetTodo.Update, is SheetTodo.New -> {
                todoObserve?.observe(todoSheetTodo)
            }
            else -> {
            }
        }
    }

    private fun observeTodoNotification(mainToTodo: MainToTodo) {
        when (mainToTodo) {
            is MainToTodo.DeleteTodo -> {
                todoViewModel.deleteTodo(mainToTodo.todo)
                mainViewModel.setTodoNotification(MainToTodo.Neutral)
            }
            is MainToTodo.DeleteCompleted -> {
                todoViewModel.deleteCompleted()
                mainViewModel.setTodoNotification(MainToTodo.Neutral)
            }
            is MainToTodo.Sort -> {
                when (mainToTodo.type) {
                    MainToTodo.Sort.ASC -> todoViewModel.sortAscending()
                    MainToTodo.Sort.DESC -> todoViewModel.sortDescending()
                    MainToTodo.Sort.DATE -> todoViewModel.sortByDate()
                }
                mainViewModel.setTodoNotification(MainToTodo.Neutral)
            }
            else -> {

            }
        }
    }

    private fun setupRecyclerView() {
        layoutBinding.recyclerView.apply {
            adapter = todoAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun createLayoutCreateTodo(onColor: Int, offColor: Int): LayoutCreateTodoBinding {
        val binding = LayoutCreateTodoBinding.inflate(layoutInflater)
        binding.viewModel = todoViewModel
        binding.executePendingBindings()
        binding.todoText.setText(todoViewModel.getTodo()?.title)
        binding.todoText.requestFocus()
        todoObserve = object : TodoObserve {
            override fun observe(sheetTodo: SheetTodo) {
                canSaveTodo = sheetTodo.canSave
                binding.saveTodo.setTextColor(if (sheetTodo.canSave) onColor else offColor)
            }
        }
        return binding
    }

    private fun createBottomSheet(layoutBinding: LayoutCreateTodoBinding): BottomSheetDialog {
        val sheet = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        sheet.setContentView(layoutBinding.root)
        return sheet
    }

    private fun launchTodoSheet() {
        fun setAlertText(binding: LayoutCreateTodoBinding, todo: Todo) {
            binding.cancelAlert.visibility = if (todo.shouldAlert) View.VISIBLE else View.GONE
            binding.alertText.text = getAlertText(todo.shouldAlert, todo.alertTime)
        }

        val binding = createLayoutCreateTodo(
            ContextCompat.getColor(requireContext(), R.color.colorPrimary),
            ContextCompat.getColor(requireContext(), R.color.color_note_search_hint)
        )
        val sheet = createBottomSheet(binding)
        todoViewModel.getTodo()?.let {
            setAlertText(binding, it)
        }
        var shouldAlert = false
        binding.saveTodo.setOnClickListener {
            if (canSaveTodo) {
                todoViewModel.saveTodo(shouldAlert)
                sheet.cancel()
            }
        }
        binding.cancelAlert.setOnClickListener { _ ->
            todoViewModel.getTodo()?.let { it ->
                it.shouldAlert = false
                setAlertText(binding, it)
                todoViewModel.setSavable(true)
            }
        }
        binding.setAlerts.setOnClickListener { _ ->
            getTimeFromPicker { time ->
                todoViewModel.getTodo()?.let {
                    it.alertTime = time
                    it.shouldAlert = true
                    setAlertText(binding, it)
                    todoViewModel.setSavable(true)
                    shouldAlert = true
                }
            }
        }
        sheet.setOnCancelListener {
            todoObserve = null
            todoViewModel.neutralize(SheetTodo::class)

        }
        sheet.show()
    }

    fun getAlertText(showAlert: Boolean, time: Long): String {
        class DateInfo(
            val year: Int,
            val month: Int,
            val day: Int
        ) {
            fun inYearAndMonth(dateInfo: DateInfo): Boolean {
                return year == dateInfo.year && month == dateInfo.month
            }
        }

        fun getDateInfo(calendar: Calendar): DateInfo {
            return DateInfo(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }

        val calendar = Calendar.getInstance()

        val stored = getDateInfo(
            Calendar.getInstance().also {
                it.timeInMillis = time
            }
        )
        val current = getDateInfo(Calendar.getInstance())
        if (!showAlert) return "Set Alerts"
        return if (stored.inYearAndMonth(current) && (current.day == stored.day)) {
            "Today ${getTimeString(calendar.timeInMillis)}"
        } else if (stored.inYearAndMonth(current) && (current.day + 1) == stored.day) {
            "Tomorrow ${getTimeString(calendar.timeInMillis)}"
        }
        else if (stored.inYearAndMonth(current) && stored.day == (current.day - 1)) {
            "Yesterday ${getTimeString(calendar.timeInMillis)}"
        } else {
            getExtendedDateString(time)
        }
    }

    private fun getTimeFromPicker(onTime: (Long) -> Unit) {
        class TimeInfo(
            val hour: Int,
            val minute: Int
        )

        class DateInfo(
            val year: Int,
            val month: Int,
            val day: Int,
        )


        showDatePickerDialog { year, month, day ->
            val dateInfo = DateInfo(year, month, day)
            showTimePickerDialog { hour, minute ->
                val timeInfo = TimeInfo(hour, minute)
                timeInfo.let { time ->
                    dateInfo.let { date ->
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.YEAR, date.year)
                        calendar.set(Calendar.MONTH, date.month)
                        calendar.set(Calendar.DAY_OF_MONTH, date.day)
                        calendar.set(Calendar.HOUR_OF_DAY, time.hour)
                        calendar.set(Calendar.MINUTE, time.minute)
                        calendar.set(Calendar.SECOND, 0)
                        onTime(calendar.timeInMillis)
                    }
                }
            }
        }
    }

    private fun showDatePickerDialog(onResponse: (Int, Int, Int) -> Unit) {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Set Notification Date")
            .build()
        picker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = Date(it)
            onResponse(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }
        picker.show(requireActivity().supportFragmentManager, picker.toString())
    }

    private fun showTimePickerDialog(onResponse: (Int, Int) -> Unit) {
        val picker = Calendar.getInstance().let {
            MaterialTimePicker
                .Builder()
                .setHour(it.get(Calendar.HOUR_OF_DAY))
                .setMinute(it.get(Calendar.MINUTE))
                .setTitleText("Set Notification Time")
                .build()
        }
        picker.addOnPositiveButtonClickListener {
            onResponse(picker.hour, picker.minute)
        }
        picker.show(requireActivity().supportFragmentManager, picker.toString())
    }

    interface TodoObserve {
        fun observe(sheetTodo: SheetTodo)
    }

    class TodoFragmentContainer(
        val viewModel: TodoViewModel,
        val todoListener: TodoListener,
        val todoFragment: TodoFragment,
        var onTodoClicked: (Todo) -> Unit
    )
}