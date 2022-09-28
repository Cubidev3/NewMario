package Renderer

data class Vertex(
    var position: FloatArray = floatArrayOf(0f,0f,0f),
    var color: FloatArray = floatArrayOf(0f,0f,0f,0f),
    var uvCoordinates: FloatArray = floatArrayOf(0f,0f),
    var textureId: Float = 0f
) {

    companion object {
        fun sizeOf(): Int {
            return (3 + 4 + 2 + 1) * Float.SIZE_BYTES
        }

        fun positionOffset(): Long {
            return 0
        }

        fun colorOffset(): Long {
            return positionOffset() + 3 * Float.SIZE_BYTES
        }

        fun uvCoordinatesOffset(): Long {
            return colorOffset() + 4 * Float.SIZE_BYTES
        }

        fun textureIdOffset(): Long {
            return uvCoordinatesOffset() + 2 * Float.SIZE_BYTES
        }

        fun positionSize() : Int {return 3}
        fun colorSize() : Int {return 4}
        fun uvCoordinatesSize() : Int {return 3}
        fun textureIdSize() : Int {return 3}
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vertex

        if (!position.contentEquals(other.position)) return false
        if (!color.contentEquals(other.color)) return false
        if (!uvCoordinates.contentEquals(other.uvCoordinates)) return false
        if (textureId != other.textureId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = position.contentHashCode()
        result = 31 * result + color.contentHashCode()
        result = 31 * result + uvCoordinates.contentHashCode()
        result = 31 * result + textureId.hashCode()
        return result
    }

    fun toFloatArray() : FloatArray {
        return position.plus(color).plus(uvCoordinates).plus(textureId)
    }
}