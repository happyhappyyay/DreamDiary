package com.happyhappyyay.android.dreamdiary.speech

enum class Instruction(val instruction: String){
    ADD_ENTRY("add entry "),
    GO_TO("go to "),
    DELETE_ENTRY("delete entry "),
    ADD_PAGE("add page "),
    DELETE_PAGE("delete page "),
    FIRST_PAGE("first page"),
    LAST_PAGE("last page"),
    DELETE_ENTRY_ALL("delete entry all")
}