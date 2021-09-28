package com.sarftec.simplenotes.presentation.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.sarftec.simplenotes.R
import com.sarftec.simplenotes.databinding.LayoutTodoBinding
import com.sarftec.simplenotes.entity.TodoNotify
import com.sarftec.simplenotes.model.Todo
import com.sarftec.simplenotes.presentation.fragment.TodoFragment
import kotlinx.coroutines.launch
import java.util.*

class TodoAdapter(
    private val fragmentContainer: TodoFragment.TodoFragmentContainer
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var todos = emptyList<Todo>()

    private var completedStartPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = LayoutTodoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(todos[position], position)
    }

    override fun getItemCount(): Int = todos.size

    fun submitData(items: List<Todo>) {
        todos = items
        completedStartPosition = items.firstOrNull { it.isCompleted }
            ?.let { items.indexOf(it) }
            ?: -1
        notifyDataSetChanged()
    }

    //Todo View Holder
    inner class TodoViewHolder(
        private val layoutBinding: LayoutTodoBinding
    ) : RecyclerView.ViewHolder(layoutBinding.root) {

        fun bind(todo: Todo, position: Int) {
            layoutBinding.completed.visibility =
                if (position == completedStartPosition) View.VISIBLE else View.GONE
            layoutBinding.todoTitle.text = todo.title
            setTodoColor(todo)
            layoutBinding.todoMarkCard.setOnClickListener {
                todo.isCompleted = !todo.isCompleted
                fragmentContainer.todoFragment.todoViewModel.updateAdapterTodo(todo)
                fragmentContainer.todoFragment.lifecycleScope.launch {
                    if(todo.isCompleted) {
                        if(todo.shouldAlert)
                            fragmentContainer.todoFragment.notifyManager.removeTodoNotify(todo.id)
                    }
                    else {
                        if(todo.shouldAlert && todo.date > Calendar.getInstance().timeInMillis) {
                            fragmentContainer.todoFragment.notifyManager.addTodoNotify(
                                TodoNotify(todo.id, todo.date)
                            )
                        }
                    }
                }
                fragmentContainer.viewModel.reload()
                // setTodoColor(todo)
            }
            layoutBinding.todoLayout.setOnClickListener {
                fragmentContainer.onTodoClicked(todo)
            }
            layoutBinding.todoLayout.setOnLongClickListener {
                fragmentContainer.todoListener.createDeleteSheet(todo)
                true
            }
        }

        private fun setTodoColor(todo: Todo) {
            if (todo.isCompleted) switchCompleted(todo) else switchNormal(todo)
        }

        private fun switchCompleted(todo: Todo) {
            layoutBinding.todoTitle.setTextColor(
                ContextCompat.getColor(itemView.context, R.color.color_todo_title_cancel)
            )
            layoutBinding.todoTitle.setBackgroundResource(R.drawable.todo_title_strike_through)
            layoutBinding.todoMark.setImageResource(R.drawable.ic_check_cancel)
            layoutBinding.todoMarkCard.strokeWidth = 0
            layoutBinding.todoMarkCard.setCardBackgroundColor(
                ContextCompat.getColor(itemView.context, R.color.color_todo_mark_cancel_background)
            )
            if (todo.shouldAlert) {
                val color = ContextCompat.getColor(itemView.context, R.color.color_note_search_hint)
                layoutBinding.alertLayout.visibility = View.VISIBLE
                layoutBinding.alertText.text = fragmentContainer.todoFragment.getAlertText(
                    true,
                    todo.alertTime
                )
                layoutBinding.alertText.setTextColor(color)
                ImageViewCompat.setImageTintList(
                    layoutBinding.dateIcon,
                    ColorStateList.valueOf(color)
                )
            }
            else layoutBinding.alertLayout.visibility = View.GONE
        }

        private fun switchNormal(todo: Todo) {
            layoutBinding.todoTitle.setTextColor(
                ContextCompat.getColor(itemView.context, R.color.color_todo_title)
            )
            layoutBinding.todoMark.setImageDrawable(null)
            layoutBinding.todoMarkCard.strokeColor = ContextCompat.getColor(
                itemView.context,
                R.color.color_todo_mark_stroke
            )
            layoutBinding.todoMarkCard.strokeWidth = 4
            layoutBinding.todoMarkCard.setCardBackgroundColor(
                ContextCompat.getColor(itemView.context, R.color.color_todo_card_background)
            )
            layoutBinding.todoTitle.background = null
            if (todo.shouldAlert) {
                val color = ContextCompat.getColor(
                    itemView.context,
                    if (Calendar.getInstance().timeInMillis > todo.alertTime) R.color.color_todo_icon_red
                    else R.color.color_note_search_hint
                )
                layoutBinding.alertLayout.visibility = View.VISIBLE
                layoutBinding.alertText.text = fragmentContainer.todoFragment.getAlertText(
                    true,
                    todo.alertTime
                )
                layoutBinding.alertText.setTextColor(color)
                ImageViewCompat.setImageTintList(
                    layoutBinding.dateIcon,
                    ColorStateList.valueOf(color)
                )
            }
            else layoutBinding.alertLayout.visibility = View.GONE
        }
    }
}