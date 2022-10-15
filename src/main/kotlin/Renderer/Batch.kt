package Renderer

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glGenVertexArrays

class Batch(val size: Int = 1024) {
    private val rects = mutableListOf<Rectangle>()

    private var vaoId = 0
    private var vboId = 0
    private var eboId = 0

    private var auxiliarBatch: Batch? = null

    fun init() {
        // Generate VAO, VBO and EBO buffer objects, and send to gpu
        vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        // Create VBO
        vboId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, Rectangle.sizeOf().toLong() * size, GL_DYNAMIC_DRAW)

        eboId = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, getIndicesArray(), GL_STATIC_DRAW)

        // Add vertex attribute pointers

        // Position Attribute
        glVertexAttribPointer(
            0,
            Vertex.posSize,
            GL_FLOAT,
            false,
            Vertex.sizeOf(),
            Vertex.positionOffset()
        )
        glEnableVertexAttribArray(0)

        // Color Attribute
        glVertexAttribPointer(1, Vertex.colorSize, GL_FLOAT, false, Vertex.sizeOf(), Vertex.colorOffset())
        glEnableVertexAttribArray(1)

        // Uv Attribute
        glVertexAttribPointer(
            2,
            Vertex.uvCoordSize,
            GL_FLOAT,
            false,
            Vertex.sizeOf(),
            Vertex.uvCoordinatesOffset()
        )
        glEnableVertexAttribArray(2)

        // Texture Id Attribute
        glVertexAttribPointer(
            3,
            Vertex.texIdSize,
            GL_FLOAT,
            false,
            Vertex.sizeOf(),
            Vertex.textureIdOffset()
        )
        glEnableVertexAttribArray(3)
    }

    fun put(rectangle: Rectangle) {
        if (auxiliarBatch != null) {
            auxiliarBatch!!.put(rectangle)
            return
        }

        if (rects.size >= size) {
            auxiliarBatch = Batch()
            auxiliarBatch!!.init()
            auxiliarBatch!!.put(rectangle)
            return
        }

        rects.add(rectangle)
    }

    fun update() {
        // Set dynamic vertex buffer data
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferSubData(GL_ARRAY_BUFFER, 0, getVertexArray())

        // Bind vao
        glBindVertexArray(vaoId)

        // Enable Attributes
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glEnableVertexAttribArray(2)
        glEnableVertexAttribArray(3)

        // Draw
        glDrawElements(GL_TRIANGLES, getIndicesSize(), GL_UNSIGNED_INT, 0)
        auxiliarBatch?.update()
    }

    fun finish() {
        auxiliarBatch = null
        rects.clear()

        // Unbind Everything
        GL30.glDisableVertexAttribArray(0)
        GL30.glDisableVertexAttribArray(1)
        GL30.glDisableVertexAttribArray(2)
        GL30.glDisableVertexAttribArray(3)

        glBindVertexArray(0)

        auxiliarBatch?.finish()
    }

    fun getVertexArray() : FloatArray {
        return rects.flatMap { rect -> rect.toFloatArray().asList() }.toFloatArray()
    }

    fun getIndicesArray() : IntArray {
        return List(size) { i -> Rectangle.baseIndices.map { it + 4*i }}.flatten().toIntArray()
    }

    fun getIndicesSize() : Int {
        return size * Rectangle.baseIndices.size
    }
}
