package com.courses.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
            itemView.tv_course_count.text = context.getString(R.string.course_count, course.numberOfTopics)
            itemView.tv_progress.text =
                when (course.myProgress) {
                    0 -> "開始前"
                    in 1..99 -> "未完了"
                    100 -> "完了済み"
                    else -> ""
                }

            when (course.isBookmark) {
                true -> {
                    itemView.img_bookmark.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent))
                }
                false -> {
                    itemView.img_bookmark.setColorFilter(ContextCompat.getColor(context, R.color.Grey_400))
                }
            }

            itemView.img_bookmark.setOnClickListener {
                courseListener.onBookmarkClick(adapterPosition, course)
            }

            var myOptions = RequestOptions()

            myOptions.apply(RequestOptions.circleCropTransform())
            Glide.with(context).asBitmap().load(course.iconUrl).apply(myOptions).into(itemView.img_teacher)
        }

    }

    fun updateContents(newList: List<Course>) {
        // TODO check diff
        this.courseList = newList
        notifyDataSetChanged()
    }

    interface CourseListener {
        fun onBookmarkClick(position: Int, course: Course)
    }

}