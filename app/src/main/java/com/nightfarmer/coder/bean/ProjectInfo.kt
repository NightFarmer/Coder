package com.nightfarmer.coder.bean

/**
 * Created by zhangfan on 16-8-26.
 */
class ProjectInfo {
    var id = ""
    var name = ""
    var apk = ""

    override fun toString(): String {
        return "{name:$name  id:$id apk:$apk}"
    }
}