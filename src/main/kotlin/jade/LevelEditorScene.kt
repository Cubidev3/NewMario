package jade

import Renderer.Shader
import Renderer.Texture
import Util.Time
import components.FontRenderer
import components.SpriteRenderer
import org.joml.Vector2f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*

class LevelEditorScene : Scene() {

    override var camera: Camera = Camera(Vector2f(100f,100f))
    private val defaultShader = Shader("assets/shaders/default.glsl")
    private val testTexture = Texture("assets/textures/sample.png")
    private val testTexture2 = Texture("assets/textures/sample2.jpeg")

    private val vertexArray = floatArrayOf(
        // Positions                 // Color                  // UV Coordinates
        1280f, 0f, 0f,               1f, 0f, 0f, 1f,           1f, 0f,    0f,// Bottom Right
        0f, 672f, 0f,                0f, 1f, 0f, 1f,           0f, 1f,    0f,// Top Left
        1280f, 672f, 0f,             0f, 0f, 1f, 1f,           1f, 1f,    0f,// Top Right
        0f, 0f, 0f,                  0f, 0f, 0f, 1f,           0f, 0f,    0f,// Bottom Left

        2560f, 0f, 0f,               1f, 0f, 0f, 1f,           1f, 0f,    1f,// Bottom Right
        1280f, 672f, 0f,             0f, 1f, 0f, 1f,           0f, 1f,    1f,// Top Left
        2560f, 672f, 0f,             0f, 0f, 1f, 1f,           1f, 1f,    1f,// Top Right
        1280f, 0f, 0f,               0f, 0f, 0f, 1f,           0f, 0f,    1f // Bottom Left
    )

    // IMPORTANT: This must be in count-clockwise order
    private val elementArray = intArrayOf(
        2,1,0, // top Right triangle
        0,1,3, // bottom Left Triangle

        6,5,4,
        4,5,7
    )

    private var vaoId = 0
    private var vboId = 0
    private var eboId = 0

    private val moveSpeed = 150f
    private val runSpeed = 459f

    private var firstTime = true
    override fun init() {
        val testObject = GameObject("test object")
        testObject.addComponent(SpriteRenderer())

        defaultShader.compile()

        // Generate VAO, VBO and EBO buffer objects, and send to gpu
        vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        // Create a float buffer of vertices
        val vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.size)
        vertexBuffer.put(vertexArray).flip()

        // Create VBO
        vboId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)

        // Create Indices and Upload
        val elementBuffer = BufferUtils.createIntBuffer(elementArray.size)
        elementBuffer.put(elementArray).flip()

        eboId = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW)

        // Add vertex attribute pointers
        val positionsSize = 3
        val colorsSize = 4
        val uvSize = 2
        val textureIdSize = 1
        val floatSizeInBytes = Float.SIZE_BYTES
        val vertexSizeInBytes = (positionsSize + colorsSize + uvSize + textureIdSize) * floatSizeInBytes

        // Position Attribute
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeInBytes, 0)
        glEnableVertexAttribArray(0)

        // Color Attribute
        glVertexAttribPointer(1, colorsSize, GL_FLOAT, false, vertexSizeInBytes, (positionsSize * floatSizeInBytes).toLong())
        glEnableVertexAttribArray(1)

        // Uv Attribute
        GL20.glVertexAttribPointer( 2, uvSize, GL_FLOAT, false, vertexSizeInBytes, ((positionsSize + colorsSize) * floatSizeInBytes).toLong())
        glEnableVertexAttribArray(2)

        // Texture Id Attribute
        GL20.glVertexAttribPointer(3, textureIdSize, GL_FLOAT, false, vertexSizeInBytes, ((positionsSize + colorsSize + uvSize) * floatSizeInBytes).toLong())
        glEnableVertexAttribArray(3)
    }

    override fun update(deltaTime: Float) {
        move(deltaTime)
        // Bind Program
        defaultShader.use()
        defaultShader.uploadMatrix4f("uProjection", camera.getProjectionMatrix())
        defaultShader.uploadMatrix4f("uView", camera.getViewMatrix())

        glActiveTexture(GL_TEXTURE0)
        testTexture.bind()
        glActiveTexture(GL_TEXTURE1)
        testTexture2.bind()
        defaultShader.uploadIntArray("tex_sampler", intArrayOf(0,1))
        // Bind vao
        glBindVertexArray(vaoId)

        // Enable Attributes
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glEnableVertexAttribArray(2)

        // Draw
        glDrawElements(GL_TRIANGLES, elementArray.size, GL_UNSIGNED_INT, 0)

        // Unbind Everything
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glDisableVertexAttribArray(2)

        glBindVertexArray(0)

        defaultShader.detach()
        testTexture.unbind()
        testTexture2.unbind()

        updateGameObjects(deltaTime)
    }

    fun move(deltaTime: Float) {
        val direction = Vector2f(
            KeyListener.getKetStrength(GLFW_KEY_D) - KeyListener.getKetStrength(GLFW_KEY_A),
            KeyListener.getKetStrength(GLFW_KEY_W) - KeyListener.getKetStrength(GLFW_KEY_S)
        )
        if (!direction.isZero()) direction.normalize()

        val speed = if (KeyListener.isKeyDown(GLFW_KEY_LEFT_SHIFT)) runSpeed else moveSpeed

        camera.cameraPosition.x += direction.x * speed * deltaTime
        camera.cameraPosition.y += direction.y * speed * deltaTime
    }

    private fun Vector2f.isZero() : Boolean {
        return x == 0f && y == 0f
    }
}