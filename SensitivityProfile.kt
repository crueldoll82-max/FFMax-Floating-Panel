package com.example.ffpanel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class SensitivityProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val general: Int,
    val redDot: Int,
    val scope2x: Int,
    val scope4x: Int,
    val sniper: Int,
    val freeLook: Int
)
