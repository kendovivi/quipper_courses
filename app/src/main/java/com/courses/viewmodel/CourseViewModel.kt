package com.courses.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.courses.common.getBookmarks
import com.courses.common.saveBookmarkStatus
import com.courses.model.Course
import com.courses.model.CourseProgress
import com.courses.model.getCourseList
import com.courses.model.getCourseProgress
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        private const val LOGTAG = "CourseViewModel"
    }

    private var loadingStatus: MutableLiveData<Pair<LoadingStatus, String?>> = MutableLiveData()
    private var localCourseListData: List<Course> = mutableListOf()
    private var courseListData: MutableLiveData<List<Course>> = MutableLiveData()
    private var bookmarkedCourseList: MutableLiveData<List<Course>> = MutableLiveData()
    private var progressMap: MutableMap<String, Int> = mutableMapOf()
    private var firstBoot: Boolean = true

    fun getLoadingStatus(): LiveData<Pair<LoadingStatus, String?>> {
        return loadingStatus
    }

    fun loadCourseList(): LiveData<List<Course>> {
        requestCourseList()
        return courseListData
    }

    fun getMyBookmarkedCourseList(): LiveData<List<Course>> {
        return bookmarkedCourseList
    }

    fun updateMyBookmarkedCourseList() {
        val list = localCourseListData.filter {
            it.isBookmark == true
        }
        return bookmarkedCourseList.postValue(list)
    }

    fun loadLocalCourseList() {
        courseListData.postValue(localCourseListData)
    }

    fun saveBookmarkStatusToPrefs() {
        if (localCourseListData.isNotEmpty()) { // if load error, do not overwrite prefs
            val list = localCourseListData.filter {
                it.isBookmark == true
            }.map {
                it.id!!
            }
            saveBookmarkStatus(getApplication(), list)
        }
    }

    private fun requestCourseList() {
        loadingStatus.postValue(Pair(LoadingStatus.Loading, null))
        getCourseList().enqueue(object: Callback<List<Course>> {
            override fun onFailure(call: Call<List<Course>>, t: Throwable) {
                loadingStatus.postValue(Pair(LoadingStatus.Error, t.localizedMessage))
            }

            override fun onResponse(call: Call<List<Course>>, response: Response<List<Course>>) {
                if (response.isSuccessful) {
                    loadingStatus.postValue(Pair(LoadingStatus.Success, null))
                    response.body()?.let {
                        localCourseListData = it
                        loadProgressFromCourseList()
                    }
                } else {
                    loadingStatus.postValue(Pair(LoadingStatus.Error, response.code().toString()))
                }
            }
        })
    }

    private fun requestCourseProgress(courseId: String) {
        progressMap[courseId] = -1
        getCourseProgress(courseId).enqueue(object: Callback<CourseProgress> {
            override fun onFailure(call: Call<CourseProgress>, t: Throwable) {
                Log.d(LOGTAG, "requestCourseProgress onFailure")
            }

            override fun onResponse(call: Call<CourseProgress>, response: Response<CourseProgress>) {
                if (response.isSuccessful) {
                    response.body()?.let { courseProgress ->
                        Log.d(LOGTAG, "requestCourseProgress Success")
                        courseProgress.courseId?.let {
                            progressMap[it] = courseProgress.progress ?: -1
                        }

                    }
                    if (progressMap.keys.size == localCourseListData.size) {
                        val bookmarkedCourses = getBookmarks(getApplication())
                        localCourseListData.forEach {
                            if (bookmarkedCourses?.contains(it.id) == true) {
                                it.isBookmark = true
                            }
                            it.setProgress(progressMap[it.id] ?: -1)
                        }
                        courseListData.postValue(localCourseListData)
                        updateMyBookmarkedCourseList()
                    }

                } else {
                    Log.d(LOGTAG, "requestCourseProgress Error")
                }
            }
        })
    }

    fun loadProgressFromCourseList() {
        if (firstBoot) {
            firstBoot = false
        } else {
            saveBookmarkStatusToPrefs()
        }
        localCourseListData.forEach {
            it.id?.let { courseId ->
                requestCourseProgress(courseId)
            }
        }
    }

    fun handleBookMarkClick(course: Course) {
        course.isBookmark = !course.isBookmark
    }

    enum class LoadingStatus {
        Loading,
        Success,
        Error
    }
}