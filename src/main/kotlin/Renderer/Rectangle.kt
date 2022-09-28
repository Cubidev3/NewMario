package Renderer

class Rectangle {
    var verticies : Array<Vertex>

    constructor(x: Float, y: Float, width: Float, height: Float, texId: Float) {
        val br = Vertex(floatArrayOf(x + width,y,0f), floatArrayOf(0f,0f,0f,1f), floatArrayOf(1f,0f), texId)
        val tl = Vertex(floatArrayOf(x,y + height,0f), floatArrayOf(0f,0f,0f,1f), floatArrayOf(0f,1f), texId)
        val tr = Vertex(floatArrayOf(x + width,y + height,0f), floatArrayOf(0f,0f,0f,1f), floatArrayOf(1f,1f), texId)
        val bl = Vertex(floatArrayOf(x,y,0f), floatArrayOf(0f,0f,0f,1f), floatArrayOf(0f,0f), texId)

        verticies = arrayOf(br, tl, tr, bl)
    }

    fun toFloatArray() : FloatArray {
        return verticies[0].toFloatArray().plus(verticies[1].toFloatArray()).plus(verticies[2].toFloatArray()).plus(verticies[3].toFloatArray())
    }
}