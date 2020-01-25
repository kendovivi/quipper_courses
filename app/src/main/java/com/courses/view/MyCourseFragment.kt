package com.courses.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.courses.R
import com.courses.common.CourseItemDecoration
import com.courses.model.Course
import com.courses.view.adapter.CourseRecyclerViewAdapter
import com.courses.viewmodel.CourseViewModel
import kotlinx.android.synthetic.main.fragment_course_list.*

class MyCourseFragment: Fragment(), CourseRecyclerViewAdapter.CourseListener {

    companion object {
        fun newInstance(): MyCourseFragment {
            val fragment = MyCourseFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    lateinit var rootView: View
    private var recyclerView: RecyclerView? = null
    private var adapter: CourseRecyclerViewAdapter? = null
    private var layoutManager: LinearLayoutManager? = null

    private var courseViewModel: CourseViewModel?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_course_list, container, false)

        registerViewModel()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = recycler_view_course
        layoutManager = LinearLayoutManager(context)
        adapter = CourseRecyclerViewAdapter(context!!, emptyList(), this)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.addItemDecoration(CourseItemDecoration(15))
        recyclerView?.adapter = adapter
    }

    override fun onBookmarkClick(position: Int, course: Course) {
        courseViewModel?.handleBookMarkClick(position, course)
        courseViewModel?.updateMyBookmarkedCourseList()
    }

    private fun registerViewModel() {
        courseViewModel = activity?.let { ViewModelProviders.of(it).get(CourseViewModel::class.java) }
        courseViewModel?.let { model ->
            model.getMyBookmarkedCourseList().observe(this, Observer { bookmarkedList ->
                adapter?.updateContents(bookmarkedList)
            })
        }
    }

}