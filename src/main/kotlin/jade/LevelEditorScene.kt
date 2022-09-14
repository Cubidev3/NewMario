package jade

import Renderer.Shader
import Renderer.Texture
import Util.Time
import org.joml.Vector2f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*

class LevelEditorScene : Scene() {
    private val defaultShader = Shader("assets/shaders/default.glsl")
    private val testTexture = Texture("assets/textures/sample.png")

    private val vertexArray = floatArrayOf(
        // Positions                 // Color                  // UV Coordinates
        1280f, 0f, 0f,             1f, 0f, 0f, 1f,             1f, 0f, // Bottom Right
        0f, 672f, 0f,             0f, 1f, 0f, 1f,              0f, 1f, // Top Left
        1280f, 672f, 0f,              0f, 0f, 1f, 1f,          1f, 1f, // Top Right
        0f, 0f, 0f,            0f, 0f, 0f, 1f,                 0f, 0f  // Bottom Left
    )

    // IMPORTANT: This must be in count-clockwise order
    private val elementArray = intArrayOf(
        2,1,0, // top Right triangle
        0,1,3 // bottom Left Triangle
    )

    private var vaoId = 0
    private var vboId = 0
    private var eboId = 0

    private val moveSpeed = 100f
    override fun init() {
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
        val floatSizeInBytes = Float.SIZE_BYTES
        val vertexSizeInBytes = (positionsSize + colorsSize + uvSize) * floatSizeInBytes

        // Position Attribute
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeInBytes, 0)
        glEnableVertexAttribArray(0)

        // Color Attribute
        glVertexAttribPointer(1, colorsSize, GL_FLOAT, false, vertexSizeInBytes, (positionsSize * floatSizeInBytes).toLong())
        glEnableVertexAttribArray(1)

        // Uv Attribute
        GL20.glVertexAttribPointer( 2, uvSize, GL_FLOAT, false, vertexSizeInBytes, ((positionsSize + colorsSize) * floatSizeInBytes).toLong())
        glEnableVertexAttribArray(2)
    }

    override fun update(deltaTime: Float) {
        move(deltaTime)
        // Bind Program
        defaultShader.use()
        defaultShader.uploadMatrix4f("uProjection", camera.getProjectionMatrix())
        defaultShader.uploadMatrix4f("uView", camera.getViewMatrix())

        glActiveTexture(GL_TEXTURE0)
        defaultShader.uploadTexture("tex_sampler", 0)
        testTexture.bind()
        // Bind vao
        glBindVertexArray(vaoId)

        // Enable Attributes
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)

        // Draw
        glDrawElements(GL_TRIANGLES, elementArray.size, GL_UNSIGNED_INT, 0)

        // Unbind Everything
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)

        glBindVertexArray(0)

        defaultShader.detach()

        if (GamepadListener.isButtonDown(GLFW_JOYSTICK_1, GLFW_GAMEPAD_BUTTON_B)) println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
    }

    fun move(deltaTime: Float) {
        val direction = Vector2f(
            KeyListener.getKetStrength(GLFW_KEY_D) - KeyListener.getKetStrength(GLFW_KEY_A),
            KeyListener.getKetStrength(GLFW_KEY_W) - KeyListener.getKetStrength(GLFW_KEY_S)
        )
        if (!direction.isZero()) direction.normalize()

        camera.cameraPosition.x += direction.x * moveSpeed * deltaTime
        camera.cameraPosition.y += direction.y * moveSpeed * deltaTime
    }

    private fun Vector2f.isZero() : Boolean {
        return x == 0f && y == 0f
    }
}