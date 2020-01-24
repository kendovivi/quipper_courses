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

    private var loadingStatus: MutableLiveData<LoadingStatus> = MutableLiveData()
    private var localCourseListData: List<Course> = mutableListOf()
    private var courseListData: MutableLiveData<List<Course>> = MutableLiveData()
    private var progressMap: MutableMap<String, Int> = mutableMapOf()

    fun loadCourseList(): LiveData<List<Course>> {
        requestCourseList()
        return courseListData
    }

    fun getMyBookmarkedCourseList(): List<Course> {
        return localCourseListData.filter {
            it.isBookmark == true
        }
    }

    fun saveBookmarkStatusToPrefs() {
        val list = localCourseListData.filter {
            it.isBookmark == true
        }.map {
            it.id!!
        }
        saveBookmarkStatus(getApplication(), list)
    }

    private fun requestCourseList() {
        Log.d(LOGTAG, "requestCourseList")
        getCourseList().enqueue(object: Callback<List<Course>> {
            override fun onFailure(call: Call<List<Course>>, t: Throwable) {
                Log.d(LOGTAG, "requestCourseList onFailure")
                loadingStatus.postValue(LoadingStatus.Error)
                // TODO エラー処理
            }

            override fun onResponse(call: Call<List<Course>>, response: Response<List<Course>>) {
                if (response.isSuccessful) {
                    loadingStatus.postValue(LoadingStatus.Success)
                    response.body()?.let {
                        Log.d(LOGTAG, "requestCourseList Success")
//                        courseListData.postValue(it)
                        localCourseListData = it
                        loadProgressFromCourseList(it)
                    }
                } else {
                    loadingStatus.postValue(LoadingStatus.Error)
                    Log.d(LOGTAG, "requestCourseList Error")
                    // TODO エラー処理
                }
            }
        })
    }

    private fun requestCourseProgress(courseId: String) {
        Log.d(LOGTAG, "requestCourseProgress courseId: $courseId")
        progressMap[courseId] = -1
        getCourseProgress(courseId).enqueue(object: Callback<CourseProgress> {
            override fun onFailure(call: Call<CourseProgress>, t: Throwable) {
                Log.d(LOGTAG, "requestCourseProgress onFailure")
//                loadingStatus.postValue(LoadingStatus.Error)
                // TODO エラー処理
            }

            override fun onResponse(call: Call<CourseProgress>, response: Response<CourseProgress>) {
                if (response.isSuccessful) {
                    loadingStatus.postValue(LoadingStatus.Success)
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
                    }

                } else {
                    loadingStatus.postValue(LoadingStatus.Error)
                    Log.d(LOGTAG, "requestCourseProgress Error")
                    // TODO エラー処理
                }
            }
        })
    }

    private fun loadProgressFromCourseList(list: List<Course>) {
        list.forEach {
            it.id?.let { courseId ->
                requestCourseProgress(courseId)
            }
        }
    }

    fun handleBookMarkClick(position: Int, course: Course) {
//        localCourseListData[position].isBookmark = !localCourseListData[position].isBookmark
        course.isBookmark = !course.isBookmark
    }

    enum class LoadingStatus {
        Loading,
        Success,
        Error
    }
}