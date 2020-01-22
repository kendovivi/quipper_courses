package com.courses.model

import com.google.gson.annotations.SerializedName

/**
 * 講座
 */
class Course {
    @SerializedName("id")
    val id: String? = null
    @SerializedName("name")
    val name: String? = null
    @SerializedName("teacher_name")
    val teacherName: String? = null
    @SerializedName("number_of_topics")
    val numberOfTopics: Int? = null
    @SerializedName("icon_url")
    val iconUrl: String? = null
}