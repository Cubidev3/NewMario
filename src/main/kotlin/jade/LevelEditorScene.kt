package jade

import Renderer.*
import Util.Time
import components.FontRenderer
import components.SpriteRenderer
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*

class LevelEditorScene : Scene() {

    override var camera: Camera = Camera(Vector2f(100f,100f))
    private val defaultShader = Shader("assets/shaders/default.glsl")
    private val testTexture = Texture("assets/textures/sample.png")
    private val testTexture2 = Texture("assets/textures/sample2.jpeg")

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

    private var posTest = Vector2f()

    private var firstTime = true
    val batch: Batch = Batch()
    override fun init() {
        val testObject = GameObject("test object")
        testObject.addComponent(SpriteRenderer())

        defaultShader.compile()
        batch.init()
    }

    override fun update(deltaTime: Float) {
        move(deltaTime)

        /*
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
        */

        val rect1 = Rectangle(posTest.x,posTest.y, 1280f, 672f, (if (KeyListener.isKeyDown(GLFW_KEY_L)) {1f} else {0f}))
        val rect2 = Rectangle(1280f,0f, 1280f, 672f, 1f)
        batch.put(rect1)
        batch.put(rect2)

        // Bind Program
        defaultShader.use()
        defaultShader.uploadMatrix4f("uProjection", camera.getProjectionMatrix())
        defaultShader.uploadMatrix4f("uView", camera.getViewMatrix())

        glActiveTexture(GL_TEXTURE0)
        testTexture.bind()
        glActiveTexture(GL_TEXTURE1)
        testTexture2.bind()
        defaultShader.uploadIntArray("tex_sampler", intArrayOf(0,1))

        batch.update()
        batch.finish()

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

        posTest.x += direction.x * speed * deltaTime
        posTest.y += direction.y * speed * deltaTime
    }

    private fun Vector2f.isZero() : Boolean {
        return x == 0f && y == 0f
    }
}