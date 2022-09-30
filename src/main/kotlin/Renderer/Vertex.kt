package Renderer

data class Vertex(
    var position: FloatArray = FloatArray(posSize),
    var color: FloatArray = FloatArray(colorSize),
    var uvCoordinates: FloatArray = FloatArray(uvCoordSize),
    var textureId: Float = 0f
) {

    companion object {
        const val posSize = 3
        const val colorSize = 4
        const val uvCoordSize = 2
        const val texIdSize = 1
        fun sizeOf(): Int {
            return (posSize + colorSize + uvCoordSize + texIdSize) * Float.SIZE_BYTES
        }

        fun positionOffset(): Long {
            return 0
        }

        fun colorOffset(): Long {
            return positionOffset() + posSize * Float.SIZE_BYTES
        }

        fun uvCoordinatesOffset(): Long {
            return colorOffset() + colorSize * Float.SIZE_BYTES
        }

        fun textureIdOffset(): Long {
            return uvCoordinatesOffset() + uvCoordSize * Float.SIZE_BYTES
        }
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
        return position + color + uvCoordinates + textureId
    }
}