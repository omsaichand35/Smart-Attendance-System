package com.example.smartattendance.utils

import android.content.Context
import android.content.Intent
import com.example.smartattendance.activities.DashboardActivity
import com.example.smartattendance.activities.StudentDashboardActivity

object RoleNavigator {

    fun dashboardIntent(context: Context, role: String?): Intent {
        val destination = if (role.equals("TEACHER", ignoreCase = true)) {
            DashboardActivity::class.java
        } else {
            StudentDashboardActivity::class.java
        }
        return Intent(context, destination)
    }
}
