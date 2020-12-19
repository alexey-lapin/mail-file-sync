package com.github.al.mfs.receiver

import java.io.OutputStream

interface Receivable {

    fun getSubject(): String

    fun loadAttachment(output: OutputStream)

}