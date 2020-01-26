package com.courses

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.platform.app.InstrumentationRegistry
import com.courses.common.getBookmarks
import com.courses.common.getCourseList
import com.courses.common.saveBookmarkStatus
import com.courses.common.saveCourseList
import com.courses.model.Course
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

import org.junit.Assert.*
import org.mockito.Mock

class SharedPreferenceUtilTest {

    private val bookmarkList = listOf("courseId_1", "courseId_2", "courseId_3")

    private val course1 = Course(id = "course1", name = "コース1", teacherName= "teacher1", numberOfTopics=10, iconUrl="", myProgress = 0, isBookmark = false)
    private val course2 = Course(id = "course2", name = "コース2", teacherName= "teacher2", numberOfTopics=20, iconUrl="", myProgress = 10, isBookmark = true)
    private val courseList = listOf(course1, course2)

    @Test
    fun validBookmarkSavedTest() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        saveBookmarkStatus(appContext, bookmarkList)
        val savedBookmark = getBookmarks(getApplicationContext())
        assertEquals(savedBookmark, bookmarkList)
    }

    @Test
    fun validCourseListSavedTest() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        saveCourseList(appContext, courseList)
        val courses = getCourseList(appContext)
        assertEquals(courses, courseList)
    }
}