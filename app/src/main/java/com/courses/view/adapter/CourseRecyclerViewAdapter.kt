package com.courses.view.adapter

import android.content.Context
import android.graphics.Matrix
import android.graphics.Shader
import android.graphics.SweepGradient
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.courses.R
import com.courses.model.Course
import kotlinx.android.synthetic.main.item_course.view.*
import kotlinx.android.synthetic.main.layout_progress_circle.view.*

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

            setSubscriptionProgressColor(itemView.progress_circle_layout, itemView.progress_circle_layout2, startColorRes = R.color.colorProgress, endColorRes = R.color.colorProgress, completePercent = course.myProgress?.toFloat() ?: 0f)
        }

    }

    /**
     * update list
     */
    fun updateContents(newList: List<Course>) {
        courseList = newList.toMutableList()
        notifyDataSetChanged()
    }

    /**
     * progress color設定
     *
     * @param startColorRes start color (same color in this case)
     * @param endColorRes end color (same color in this case)
     * @param completePercent course progress
     *
     */
    private fun setSubscriptionProgressColor(bottomLayout1: RelativeLayout, bottomLayout2: RelativeLayout, startColorRes: Int, endColorRes: Int, completePercent: Float) {

        val sf = object: ShapeDrawable.ShaderFactory(){
            override fun resize(width: Int, height: Int): Shader {
                val shader = SweepGradient(width.toFloat()/2, height.toFloat()/2,
                    intArrayOf(ContextCompat.getColor(context, startColorRes), ContextCompat.getColor(context, endColorRes), ContextCompat.getColor(context, startColorRes)),
                    floatArrayOf(0f, 0.4f, 1f))
                val matrix = Matrix()
                matrix.setRotate(270f, width.toFloat()/2, height.toFloat()/2)
                shader.setLocalMatrix(matrix)
                return shader
            }
        }

        val paintDrawable = PaintDrawable()
        paintDrawable.shape = OvalShape()
        paintDrawable.shaderFactory = sf

        bottomLayout1.background = paintDrawable


        var progress: Float =
            if (completePercent >= 100) {
                1f
            } else if (completePercent < 0) {
                0f
            } else {
                completePercent / 100f
            }

        val sf2 = object: ShapeDrawable.ShaderFactory(){
            override fun resize(width: Int, height: Int): Shader {
                val shader = SweepGradient(width.toFloat()/2, height.toFloat()/2,
                    intArrayOf(ContextCompat.getColor(context, R.color.transparent), ContextCompat.getColor(context, R.color.transparent), ContextCompat.getColor(context, R.color.Grey_100), ContextCompat.getColor(context, R.color.Grey_100)),
                    floatArrayOf(0f, progress, progress, 1f))
                val matrix = Matrix()
                matrix.setRotate(270f, width.toFloat()/2, height.toFloat()/2)
                shader.setLocalMatrix(matrix)
                return shader
            }
        }

        val paintDrawable2 = PaintDrawable()
        paintDrawable2.shape = OvalShape()
        paintDrawable2.shaderFactory = sf2

        bottomLayout2.background = paintDrawable2
    }


    interface CourseListener {
        fun onBookmarkClick(position: Int, course: Course)
    }

}