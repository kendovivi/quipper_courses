package com.courses.common

import android.content.Context
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.util.Log
import com.courses.model.Course
import com.google.gson.Gson

private const val LOGTAG = "SharedPrefsUtil"

fun saveBookmarkStatus(context: Context, list: List<String>) {
    val editor = getDefaultSharedPreferences(context.applicationContext).edit()
    var bookmarkStr = ""
    if (list.isNotEmpty()) {
        list.forEach { courseId ->
            bookmarkStr += "$courseId,"
        }
    }
    editor.putString("bookmark_course", bookmarkStr)
    editor.commit()
}

fun getBookmarks(context: Context): List<String>? {
    val bookmarkStr = getDefaultSharedPreferences(context.applicationContext).getString("bookmark_course", null)
    if (!bookmarkStr.isNullOrEmpty()) {
        return bookmarkStr.split(",")
    }
    return null
}