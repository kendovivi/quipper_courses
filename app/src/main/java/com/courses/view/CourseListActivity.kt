package com.courses.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.courses.R
import com.courses.common.CourseItemDecoration
import com.courses.common.saveBookmarkStatus
import com.courses.model.Course
import com.courses.view.adapter.CourseRecyclerViewAdapter
import com.courses.viewmodel.CourseViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_course_list.*

class CourseListActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, CourseRecyclerViewAdapter.CourseListener {

    private var recyclerView: RecyclerView? = null
    private var adapter: CourseRecyclerViewAdapter? = null
    private var layoutManager: LinearLayoutManager? = null

    private var courseViewModel: CourseViewModel ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_list)

        recyclerView = recycler_view_course
        layoutManager = LinearLayoutManager(this)
        adapter = CourseRecyclerViewAdapter(this, emptyList(), this)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.addItemDecoration(CourseItemDecoration(15))
        recyclerView?.adapter = adapter

        navigation.setOnNavigationItemSelectedListener(this)

        registerViewModel()

        btn_my_course.setOnClickListener {
            courseViewModel?.getMyBookmarkedCourseList()?.let {
                adapter?.updateContents(it)
            }
        }

        btn_reload_progress.setOnClickListener {
            courseViewModel?.loadProgressFromCourseList()
        }
    }

    override fun onPause() {
        super.onPause()
        // save bookmark data
        courseViewModel?.saveBookmarkStatusToPrefs()

    }

    override fun onBookmarkClick(position: Int, course: Course) {
        courseViewModel?.handleBookMarkClick(position, course)
        adapter?.notifyItemChanged(position)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.navigation_course_list -> {

            }
            R.id.navigation_my_course -> {

            }
        }
        return true
    }

    private fun registerViewModel() {
        courseViewModel = ViewModelProviders.of(this).get(CourseViewModel::class.java)
        courseViewModel?.let { model ->
            model.loadCourseList().observe(this, Observer { courseList ->
                adapter?.updateContents(courseList)
            })
        }
    }
}
