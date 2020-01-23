package com.courses.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.courses.model.Course
import com.courses.model.CourseProgress
import com.courses.model.getCourseList
import com.courses.model.getCourseProgress
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseViewModel: ViewModel() {

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
                        localCourseListData.forEach {
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

    enum class LoadingStatus {
        Loading,
        Success,
        Error
    }
}