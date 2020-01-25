package com.courses.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.FragmentManager
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

class CourseListActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private var courseViewModel: CourseViewModel? = null
    private var fragmentManager: FragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_list)

        val courseListFragment: CourseListFragment = CourseListFragment.newInstance()
        val myCourseFragment: MyCourseFragment = MyCourseFragment.newInstance()

        fragmentManager.beginTransaction().add(R.id.fragment_container, courseListFragment , "fg_course_list").commit()
        fragmentManager.beginTransaction().add(R.id.fragment_container, myCourseFragment, "fg_my_course").hide(myCourseFragment).commit()


        navigation.setOnNavigationItemSelectedListener(this)

        registerViewModel()

//        btn_my_course.setOnClickListener {
//            courseViewModel?.getMyBookmarkedCourseList()?.let {
//                adapter?.updateContents(it)
//            }
//        }

        btn_reload_progress.setOnClickListener {
            courseViewModel?.loadProgressFromCourseList()
        }
    }

    override fun onPause() {
        super.onPause()
        // save bookmark data
        courseViewModel?.saveBookmarkStatusToPrefs()

    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.navigation_course_list -> {
                fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("fg_my_course")!!).show(fragmentManager.findFragmentByTag("fg_course_list")!!).commit()
                courseViewModel?.loadLocalCourseList()
            }
            R.id.navigation_my_course -> {
                fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("fg_course_list")!!).show(fragmentManager.findFragmentByTag("fg_my_course")!!).commit()
                courseViewModel?.updateMyBookmarkedCourseList()
            }
        }
        return true
    }

    private fun registerViewModel() {
        courseViewModel = ViewModelProviders.of(this).get(CourseViewModel::class.java)
    }
}
