package com.happyhappyyay.android.dreamdiary.util

import com.happyhappyyay.android.dreamdiary.R

fun createThemeReference(bookColor: String?,themeColor: String?):Int{
    when(bookColor) {
        "0"->
        return when (themeColor) {
            "1" -> R.style.TealTheme
            "2" -> R.style.BumblebeeTheme
            "3" -> R.style.FireTruckTheme
            "4" -> R.style.ForestGreenTheme
            "5" -> R.style.SeaTheme
            "6" -> R.style.ProfessionalTheme
            else -> R.style.AppTheme
        }
        "1"->
            return when (themeColor) {
                "1" -> R.style.TealTheme_Red
                "2" -> R.style.BumblebeeTheme_Red
                "3" -> R.style.FireTruckTheme_Red
                "4" -> R.style.ForestGreenTheme_Red
                "5" -> R.style.SeaTheme_Red
                "6" -> R.style.ProfessionalTheme_Red
                else -> R.style.AppTheme_Red
            }
        "2"->
            return when (themeColor) {
                "1" -> R.style.TealTheme_Blue
                "2" -> R.style.BumblebeeTheme_Blue
                "3" -> R.style.FireTruckTheme_Blue
                "4" -> R.style.ForestGreenTheme_Blue
                "5" -> R.style.SeaTheme_Blue
                "6" -> R.style.ProfessionalTheme_Blue
                else -> R.style.AppTheme_Blue
            }
        "3"->
            return when (themeColor) {
                "1" -> R.style.TealTheme_Green
                "2" -> R.style.BumblebeeTheme_Green
                "3" -> R.style.FireTruckTheme_Green
                "4" -> R.style.ForestGreenTheme_Green
                "5" -> R.style.SeaTheme_Green
                "6" -> R.style.ProfessionalTheme_Green
                else -> R.style.AppTheme_Green
            }
        "4"->
            return when (themeColor) {
                "1" -> R.style.TealTheme_Yellow
                "2" -> R.style.BumblebeeTheme_Yellow
                "3" -> R.style.FireTruckTheme_Yellow
                "4" -> R.style.ForestGreenTheme_Yellow
                "5" -> R.style.SeaTheme_Yellow
                "6" -> R.style.ProfessionalTheme_Yellow
                else -> R.style.AppTheme_Yellow
            }
        "5"->
            return when (themeColor) {
                "1" -> R.style.TealTheme_Orange
                "2" -> R.style.BumblebeeTheme_Orange
                "3" -> R.style.FireTruckTheme_Orange
                "4" -> R.style.ForestGreenTheme_Orange
                "5" -> R.style.SeaTheme_Orange
                "6" -> R.style.ProfessionalTheme_Orange
                else -> R.style.AppTheme_Orange
            }
        "6"->
            return when (themeColor) {
                "1" -> R.style.TealTheme_Purple
                "2" -> R.style.BumblebeeTheme_Purple
                "3" -> R.style.FireTruckTheme_Purple
                "4" -> R.style.ForestGreenTheme_Purple
                "5" -> R.style.SeaTheme_Purple
                "6" -> R.style.ProfessionalTheme_Purple
                else -> R.style.AppTheme_Purple
            }
        "7"->
            return when (themeColor) {
                "1" -> R.style.TealTheme_Pink
                "2" -> R.style.BumblebeeTheme_Pink
                "3" -> R.style.FireTruckTheme_Pink
                "4" -> R.style.ForestGreenTheme_Pink
                "5" -> R.style.SeaTheme_Pink
                "6" -> R.style.ProfessionalTheme_Pink
                else -> R.style.AppTheme_Pink
            }
        "8"->
            return when (themeColor) {
                "1" -> R.style.TealTheme_White
                "2" -> R.style.BumblebeeTheme_White
                "3" -> R.style.FireTruckTheme_White
                "4" -> R.style.ForestGreenTheme_White
                "5" -> R.style.SeaTheme_White
                "6" -> R.style.ProfessionalTheme_White
                else -> R.style.AppTheme_White
            }
        else->
        return when (themeColor) {
            "1" -> R.style.TealTheme_Black
            "2" -> R.style.BumblebeeTheme_Black
            "3" -> R.style.FireTruckTheme_Black
            "4" -> R.style.ForestGreenTheme_Black
            "5" -> R.style.SeaTheme_Black
            "6" -> R.style.ProfessionalTheme_Black
            else -> R.style.AppTheme_Black
        }
    }
}