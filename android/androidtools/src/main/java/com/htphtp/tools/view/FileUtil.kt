package com.htphtp.tools.view

import java.io.File

/**
 *
 * Create by htp on 2019/3/9
 */
class FileUtil private constructor() {
    companion object {
        fun deleteDir(dir: File): Boolean {
            if (dir.isDirectory) {
                val children = dir.list()
                //递归删除目录中的子目录下
                for (f in children) {
                    val success = deleteDir(File(dir, f))
                    if (!success) {
                        return false
                    }
                }
            }
            // 目录此时为空，可以删除
            return dir.delete()
        }
    }
}