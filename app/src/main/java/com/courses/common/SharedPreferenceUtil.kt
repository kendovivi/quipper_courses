package com.courses.common

import android.content.Context
import android.preference.PreferenceManager.getDefaultSharedPreferences
import com.courses.model.Course
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun saveBookmarkStatus(context: Context, list: List<String>) {
    val editor = getDefaultSharedPreferences(context.applicationContext).edit()
    val jsonBookmark = Gson().toJson(list)
    editor.putString("bookmarked_course", jsonBookmark)
    editor.apply()
}

fun getBookmarks(context: Context): List<String>? {
    val bookmarkStr = getDefaultSharedPreferences(context.applicationContext).getString("bookmarked_course", null)
    val type = object : TypeToken<List<String>>() {}.type
    bookmarkStr?.let {
        return Gson().fromJson(it, type)
    } ?: run {
        return null
    }
}

fun saveCourseList(context: Context, list: List<Course>) {
    val jsonCourse = Gson().toJson(list)

    val editor = getDefaultSharedPreferences(context.applicationContext).edit()

    editor.putString("course_list", jsonCourse)
    editor.apply()
}

fun getCourseList(context: Context): List<Course>? {
    val listStr = getDefaultSharedPreferences(context.applicationContext).getString("course_list", null)
    val type = object : TypeToken<List<Course>>() {}.type
    listStr?.let {
        return Gson().fromJson(it, type)
    } ?: run {
        return null
    }
}


