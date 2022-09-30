package Renderer

class Rectangle {
    var verticies : Array<Vertex>

    companion object {
        val baseIndices = intArrayOf(2,1,0,0,1,3)
    }
    constructor(x: Float, y: Float, width: Float, height: Float, texId: Float) {
        val br = Vertex(floatArrayOf(x + width,y,0f), floatArrayOf(0f,0f,0f,1f), floatArrayOf(1f,0f), texId)
        val tl = Vertex(floatArrayOf(x,y + height,0f), floatArrayOf(0f,0f,0f,1f), floatArrayOf(0f,1f), texId)
        val tr = Vertex(floatArrayOf(x + width,y + height,0f), floatArrayOf(0f,0f,0f,1f), floatArrayOf(1f,1f), texId)
        val bl = Vertex(floatArrayOf(x,y,0f), floatArrayOf(0f,0f,0f,1f), floatArrayOf(0f,0f), texId)

        verticies = arrayOf(br, tl, tr, bl)
    }

    fun sizeOf() : Int {
        return verticies.size * Vertex.sizeOf()
    }
    fun toFloatArray() : FloatArray {
        return verticies[0].toFloatArray() + verticies[1].toFloatArray() + verticies[2].toFloatArray() + verticies[3].toFloatArray()
    }
}