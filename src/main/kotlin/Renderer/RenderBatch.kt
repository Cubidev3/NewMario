package Renderer

import components.SpriteRenderer
import jade.Camera
import jade.Window
import org.joml.Vector2f
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glGenVertexArrays
import kotlin.math.sign

class RenderBatch(var shader: Shader = Shader("assets/shaders/default.glsl"), private val size: Int = 128) {
    val vertexSize = 10 * Float.SIZE_BYTES
    val spriteSize = 4 * vertexSize
    val attribQuantities: Int = 4

    private val sprites = mutableListOf<SpriteRenderer>()
    private val vertexInfo = FloatArray(size * 4 * 10)

    private var vaoId = 0
    private var vboId = 0
    private var eboId = 0

    fun addSprite(spriteRenderer: SpriteRenderer) {
        if (isFull()) throw IllegalStateException("Batch is full")
        sprites.add(spriteRenderer)
    }

    fun start() {
        shader.compile()

        vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        vboId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, (size * spriteSize).toLong(), GL_DYNAMIC_DRAW)

        eboId = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, getIndices(), GL_STATIC_DRAW)

        // Position Attribute
        glVertexAttribPointer(
            0,
            3,
            GL_FLOAT,
            false,
            10 * Float.SIZE_BYTES,
            0
        )
        glEnableVertexAttribArray(0)

        // Color Attribute
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 10 * Float.SIZE_BYTES, 3 * Float.SIZE_BYTES.toLong())
        glEnableVertexAttribArray(1)

        // Uv Attribute
        glVertexAttribPointer(
            2,
            2,
            GL_FLOAT,
            false,
            10 * Float.SIZE_BYTES,
            7 * Float.SIZE_BYTES.toLong()
        )
        glEnableVertexAttribArray(2)

        // Texture Id Attribute
        glVertexAttribPointer(
            3,
            1,
            GL_FLOAT,
            false,
            10 * Float.SIZE_BYTES,
            9 * Float.SIZE_BYTES.toLong()
        )
        glEnableVertexAttribArray(3)
    }

    fun render() {
        val camera = Window.getCurrentCamera()
        shader.use()
        shader.uploadMatrix4f("uProjection", camera.getProjectionMatrix())
        shader.uploadMatrix4f("uView", camera.getViewMatrix())

        sprites.forEachIndexed { index, spriteRenderer ->
            val texture = spriteRenderer.getTexture()
            glActiveTexture(GL_TEXTURE0 + texture.texId - 1)
            texture.bind()
        }
        shader.uploadIntArray("tex_sampler", intArrayOf(0,1,2,3,0,0))

        updateVertexInfo()

        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertexInfo)

        glBindVertexArray(vaoId)

        (0 until attribQuantities).forEach { glEnableVertexAttribArray(it) }

        glDrawElements(GL_TRIANGLES, size * 6, GL_UNSIGNED_INT, 0)

        (0 until attribQuantities).forEach { glDisableVertexAttribArray(it) }
        glBindVertexArray(0)

        shader.detach()
        sprites.forEach {
            val texture = it.getTexture()
            texture.unbind()
        }
    }

    private fun updateVertexInfo() {
        sprites.forEachIndexed { index, spriteRenderer ->
            if (spriteRenderer.needsUpdate()) {
                loadVertexProperties(index, spriteRenderer)
                println("redone")
            }
        }
    }

    private fun loadVertexProperties(index: Int, spriteRenderer: SpriteRenderer) {
        val br = createVertexInfoArray(spriteRenderer, Vector2f(1f,0f))
        val tl = createVertexInfoArray(spriteRenderer, Vector2f(0f,1f))
        val tr = createVertexInfoArray(spriteRenderer, Vector2f(1f,1f))
        val bl = createVertexInfoArray(spriteRenderer, Vector2f(0f,0f))

        br.forEachIndexed { ind, info ->
            vertexInfo[index * 40 + ind] = info
        }
        tl.forEachIndexed { ind, info ->
            vertexInfo[index * 40 + ind + 10] = info
        }
        tr.forEachIndexed { ind, info ->
            vertexInfo[index * 40 + ind + 10 * 2] = info
        }
        bl.forEachIndexed { ind, info ->
            vertexInfo[index * 40 + ind + 10 * 3] = info
        }

        spriteRenderer.gotUpdated()
    }

    private fun getIndices() : IntArray {
        val rectangleBaseIndices = intArrayOf(0,1,2,3,1,0)
        return IntArray(size * 6) { i ->
            val rectIndex: Int = i / 6
            rectangleBaseIndices[i % 6]+ 4*rectIndex
        }
    }

    private fun createVertexInfoArray(spriteRenderer: SpriteRenderer, offset: Vector2f) : FloatArray {
        val position = spriteRenderer.getPosition()
        val color = spriteRenderer.getColor()
        val dimensions = spriteRenderer.getDimensions()
        val texture = spriteRenderer.getTexture()

        return floatArrayOf(
            position.x + offset.x * dimensions.x,
            position.y + offset.y * dimensions.y,
            position.z,
            color.x,
            color.y,
            color.z,
            color.w,
            offset.x.sign,
            offset.y.sign,
            texture.texId.toFloat() - 1f
        )
    }

    fun isFull() : Boolean {
        return sprites.size >= size
    }
}