package com.nightfarmer.coder.bean

import java.io.Serializable

/**
 * Created by zhangfan on 16-8-26.
 */
class ProjectInfo : Serializable {
    var id = ""
    var name = ""
    var apk = ""
    var describe = ""
    var gitHub = ""

    override fun toString(): String {
        return "{name:$name  id:$id apk:$apk}"
    }
}