package com.courses.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.courses.common.*
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
    private var isDeviceOnline: Boolean = false

    /**
     * get loading status live data
     */
    fun getLoadingStatus(): LiveData<Pair<LoadingStatus, String?>> {
        return loadingStatus
    }

    /**
     * load course list live data
     */
    fun loadCourseList(): LiveData<List<Course>> {
        requestCourseList()
        return courseListData
    }

    /**
     * get bookmarked course list live data
     */
    fun getMyBookmarkedCourseList(): LiveData<List<Course>> {
        return bookmarkedCourseList
    }

    /**
     * set bookmarked course list
     */
    fun updateMyBookmarkedCourseList() {
        val list = localCourseListData.filter {
            it.isBookmark == true
        }
        return bookmarkedCourseList.postValue(list)
    }

    /**
     * set local course list
     */
    fun loadLocalCourseList() {
        courseListData.postValue(localCourseListData)
    }

    /**
     * save bookmark status
     */
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

    /**
     * set device network connection status
     */
    fun setIsDeviceOnline(isOnline: Boolean) {
        isDeviceOnline = isOnline
    }

    /**
     * call request course list api
     */
    private fun requestCourseList() {
        if (!isDeviceOnline) {
            firstBoot = false
            localCourseListData = getCourseList(getApplication()) ?: mutableListOf()
            if (localCourseListData.isNotEmpty()) { // if device is offline and course list is not empty, show the list
                courseListData.postValue(localCourseListData)
            } else {
                loadingStatus.postValue(Pair(LoadingStatus.Error, "device offline"))
            }
        } else {
            loadingStatus.postValue(Pair(LoadingStatus.Loading, null))
            getCourseList().enqueue(object : Callback<List<Course>> {
                override fun onFailure(call: Call<List<Course>>, t: Throwable) {
                    loadingStatus.postValue(Pair(LoadingStatus.Error, t.localizedMessage))
                }

                override fun onResponse(
                    call: Call<List<Course>>,
                    response: Response<List<Course>>
                ) {
                    if (response.isSuccessful) {
                        loadingStatus.postValue(Pair(LoadingStatus.Success, null))
                        response.body()?.let {
                            localCourseListData = it
                            loadProgressFromCourseList()
                        }
                    } else {
                        loadingStatus.postValue(
                            Pair(
                                LoadingStatus.Error,
                                response.code().toString()
                            )
                        )
                    }
                }
            })
        }
    }

    /**
     * call request progress api
     */
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
                            it.myProgress = progressMap[it.id] ?: -1
                        }
                        saveCourseList(getApplication(), localCourseListData)
                        courseListData.postValue(localCourseListData)
                        updateMyBookmarkedCourseList()
                    }

                } else {
                    Log.d(LOGTAG, "requestCourseProgress Error")
                }
            }
        })
    }

    /**
     * load progress according to the local course list
     */
    fun loadProgressFromCourseList() {
        if (isDeviceOnline) {
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
    }

    /**
     * handle book mark click event
     */
    fun handleBookMarkClick(course: Course) {
        course.isBookmark = !course.isBookmark
    }

    /**
     * loading status enum
     */
    enum class LoadingStatus {
        Loading,
        Success,
        Error
    }
}