package com.github.al.mfs

data class PartMarker(val current: Int, val total: Int) {
    override fun toString(): String {
        return "$current/$total"
    }

    companion object Factory {
        fun from(string: String): PartMarker {
            val (current, total) = string.split("/")
            return PartMarker(current.toInt(), total.toInt())
        }

        fun whole(): PartMarker {
            return PartMarker(1, 1)
        }
    }
}

data class FilePartMarker(val files: PartMarker, val parts: PartMarker) {
    override fun toString(): String {
        return "$files|$parts"
    }

    companion object Factory {
        fun from(string: String): FilePartMarker {
            val (files, parts) = string.split("|")
            return FilePartMarker(PartMarker.from(files), PartMarker.from(parts))
        }
    }
}

data class ChunkMetadata(val sourceFileName: String, val marker: PartMarker) {
    override fun toString(): String {
        return "$sourceFileName:$marker"
    }

    companion object Factory {
        fun from(string: String): ChunkMetadata {
            val (sourceFileName, markerString) = string.split(":")
            return ChunkMetadata(sourceFileName, PartMarker.from(markerString))
        }
    }
}

data class TransmissionMetadata(val id: String, val chunkMetadata: ChunkMetadata) {
    override fun toString(): String {
        return "$id.$chunkMetadata"
    }

//    companion object Factory {
//        fun from(string: String): TransmissionMetadata {
//            val (id, chunkMetadataString) = string.split(".")
//            return TransmissionMetadata(id, )
//        }
//    }
}
