package com.courses.model

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://native-team-code-test-api.herokuapp.com/"

/**
 * 講義リストを取得
 */
fun getCourseList(): Call<List<Course>> {
    return getRetrofit().create(CourseApi::class.java).getCourseList()
}

/**
 * 講義進捗を取得
 *
 * @param courseId 講義id
 */
fun getCourseProgress(courseId: String): Call<CourseProgress> {
    return getRetrofit().create(CourseApi::class.java).getCourseProgress(courseId)
}

private fun getRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}


