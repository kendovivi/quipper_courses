package com.courses.model

import com.google.gson.annotations.SerializedName

/**
 * 講座の進捗
 */
class CourseProgress {
    @SerializedName("course_id")
    val courseId: String? = null
    @SerializedName("progress")
    val progress: Int? = null
}