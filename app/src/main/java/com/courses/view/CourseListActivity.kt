package com.courses.view

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.courses.R
import com.courses.viewmodel.CourseViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_course_list.*
import kotlinx.android.synthetic.main.layout_loading_error.*

class CourseListActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private var courseViewModel: CourseViewModel? = null
    private var fragmentManager: FragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_list)

        registerViewModel()

        val connManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connManager.activeNetworkInfo
        courseViewModel?.setIsDeviceOnline(netInfo != null && netInfo.isConnected)

        val courseListFragment: CourseListFragment = CourseListFragment.newInstance()
        val myCourseFragment: MyCourseFragment = MyCourseFragment.newInstance()

        fragmentManager.beginTransaction().add(R.id.fragment_container, courseListFragment , "fg_course_list").commit()
        fragmentManager.beginTransaction().add(R.id.fragment_container, myCourseFragment, "fg_my_course").hide(myCourseFragment).commit()

        navigation.setOnNavigationItemSelectedListener(this)

        btn_reload_progress.setOnClickListener {
            courseViewModel?.loadProgressFromCourseList()
        }

        btn_retry.setOnClickListener {
            courseViewModel?.loadCourseList()
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
                toolbarTitle.text = "Course List"
            }
            R.id.navigation_my_course -> {
                fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("fg_course_list")!!).show(fragmentManager.findFragmentByTag("fg_my_course")!!).commit()
                courseViewModel?.updateMyBookmarkedCourseList()
                toolbarTitle.text = "Bookmarked Courses"
            }
        }
        return true
    }

    private fun registerViewModel() {
        courseViewModel = ViewModelProviders.of(this).get(CourseViewModel::class.java)
        courseViewModel?.let { model ->
            model.getLoadingStatus().observe(this, Observer { loadingStatus ->
                when (loadingStatus.first) {
                    CourseViewModel.LoadingStatus.Loading -> {
                        layout_loading.visibility = View.VISIBLE
                        layout_error.visibility = View.GONE
                        layout_body.visibility = View.GONE
                    }
                    CourseViewModel.LoadingStatus.Success -> {
                        layout_loading.visibility = View.GONE
                        layout_error.visibility = View.GONE
                        layout_body.visibility = View.VISIBLE
                    }
                    CourseViewModel.LoadingStatus.Error -> {
                        layout_loading.visibility = View.GONE
                        layout_error.visibility = View.VISIBLE
                        tv_error_msg.text = getString(R.string.error_msg, loadingStatus.second)
                        layout_body.visibility = View.GONE
                    }
                    else -> {
                        layout_loading.visibility = View.GONE
                        layout_error.visibility = View.VISIBLE
                        layout_body.visibility = View.GONE
                    }
                }
            })
        }

    }
}
