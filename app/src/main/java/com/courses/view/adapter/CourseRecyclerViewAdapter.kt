package com.courses.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.courses.R
import com.courses.model.Course
import kotlinx.android.synthetic.main.item_course.view.*

class CourseRecyclerViewAdapter(val context: Context, var courseList: List<Course>, val courseListener: CourseListener): RecyclerView.Adapter<CourseRecyclerViewAdapter.ViewHolder>() {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setup(courseList[position])
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun setup(course: Course) {
            itemView.tv_name.text = course.name
            itemView.tv_teacher_name.text = course.teacherName

            itemView.img_bookmark.setOnClickListener {
                courseListener.onBookmarkClick()
            }
        }

    }

    fun updateContents(newList: List<Course>) {
        // TODO check diff
        this.courseList = newList
        notifyDataSetChanged()
    }

    interface CourseListener {
        fun onBookmarkClick()
    }

}