package com.example.smartattendance.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartattendance.databinding.ItemCourseBinding
import com.example.smartattendance.models.Course

class CourseAdapter(
    private val courses: List<Course>,
    private val onClick: (Course) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(courses[position])
    }

    override fun getItemCount() = courses.size

    class CourseViewHolder(
        private val binding: ItemCourseBinding,
        private val onClick: (Course) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(course: Course) {
            binding.apply {
                courseName.text = course.courseName
                courseCode.text = course.inviteCode
                studentCount.text = (course.studentCount ?: 0).toString()
                sessionCount.text = (course.sessionCount ?: 0).toString()
                professorName.text = "Professor: ${course.teacherName ?: "Not assigned"}"
                courseCard.setOnClickListener { onClick(course) }
            }
        }
    }
}
