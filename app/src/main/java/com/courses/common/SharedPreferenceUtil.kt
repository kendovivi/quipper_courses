package com.courses.common

import android.content.Context
import android.preference.PreferenceManager.getDefaultSharedPreferences
import com.courses.model.Course
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun saveBookmarkStatus(context: Context, list: List<String>) {
    val editor = getDefaultSharedPreferences(context.applicationContext).edit()
    var bookmarkStr = ""
    if (list.isNotEmpty()) {
        list.forEach { courseId ->
            bookmarkStr += "$courseId,"
        }
    }
    editor.putString("bookmarked_course", bookmarkStr)
    editor.apply()
}

fun getBookmarks(context: Context): List<String>? {
    val bookmarkStr = getDefaultSharedPreferences(context.applicationContext).getString("bookmarked_course", null)
    if (!bookmarkStr.isNullOrEmpty()) {
        return bookmarkStr.split(",")
    }
    return null
}

fun saveCourseList(context: Context, list: List<Course>) {
    val jsonCurProduct = Gson().toJson(list)

    val editor = getDefaultSharedPreferences(context.applicationContext).edit()

    editor.putString("course_list", jsonCurProduct)
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


