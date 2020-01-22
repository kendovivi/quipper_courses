package com.courses.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CourseApi {
    @GET("api/courses")
    fun getCourseList(): Call<List<Course>>
    @GET("api/{course_id}/usage")
    fun getCourseProgress(@Path("course_id") courseId: String): Call<CourseProgress>
}