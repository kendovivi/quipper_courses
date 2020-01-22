package com.courses.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.courses.R
import com.courses.viewmodel.CourseViewModel

class CourseListActivity : AppCompatActivity() {

    private var courseViewModel: CourseViewModel ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_list)

        registerViewModel()
    }

    private fun registerViewModel() {
        courseViewModel = ViewModelProviders.of(this).get(CourseViewModel::class.java)
        courseViewModel?.let { model ->
            model.loadCourseList().observe(this, Observer { courseList ->
                // TODO set data to adapter
            })
        }
    }
}
