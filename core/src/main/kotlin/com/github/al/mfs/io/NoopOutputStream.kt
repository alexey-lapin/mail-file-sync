package com.github.al.mfs.io

import java.io.OutputStream

object NoopOutputStream : OutputStream() {
    override fun write(b: Int) {}
}
