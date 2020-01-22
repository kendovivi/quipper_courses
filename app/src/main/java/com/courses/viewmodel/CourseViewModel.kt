package com.courses.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.courses.model.Course
import com.courses.model.getCourseList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseViewModel: ViewModel() {

    companion object {
        private const val LOGTAG = "CourseViewModel"
    }

    private var loadingStatus: MutableLiveData<LoadingStatus> = MutableLiveData()
    private var courseListData: MutableLiveData<List<Course>> = MutableLiveData()

    fun loadCourseList(): LiveData<List<Course>> {
        return courseListData
    }

    private fun requestCourseList() {
        Log.d(LOGTAG, "requestCourseList")
        getCourseList().enqueue(object: Callback<List<Course>> {
            override fun onFailure(call: Call<List<Course>>, t: Throwable) {
                loadingStatus.postValue(LoadingStatus.Error)
                // TODO エラー処理
            }

            override fun onResponse(call: Call<List<Course>>, response: Response<List<Course>>) {
                if (response.isSuccessful) {
                    loadingStatus.postValue(LoadingStatus.Success)
                    response.body()?.let {
                        courseListData.postValue(it)
                    }
                } else {
                    loadingStatus.postValue(LoadingStatus.Error)
                    // TODO エラー処理
                }
            }
        })
    }

    enum class LoadingStatus {
        Loading,
        Success,
        Error
    }
}