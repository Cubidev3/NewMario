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
    private val testTexture = Texture("assets/textures/sample.png")
    private val testTexture2 = Texture("assets/textures/sample2.jpeg")

    private val moveSpeed = 150f
    private val runSpeed = 459f

    private var posTest = Vector3f()

    val object1 = GameObject("1").addComponent(SpriteRenderer(testTexture, Vector2f(1280f, 672f)))
    val object2 = GameObject("2").addComponent(SpriteRenderer(testTexture2, Vector2f(1280f,672f)))

    var camMovementMode = false

    override fun init() {
        Renderer.addGameObject(object1).addGameObject(object2)
    }

    override fun update(deltaTime: Float) {
        move(deltaTime)
        Renderer.render()
        updateGameObjects(deltaTime)
    }

    fun move(deltaTime: Float) {
        val direction = Vector2f(
            KeyListener.getKetStrength(GLFW_KEY_D) - KeyListener.getKetStrength(GLFW_KEY_A),
            KeyListener.getKetStrength(GLFW_KEY_W) - KeyListener.getKetStrength(GLFW_KEY_S)
        )
        if (!direction.isZero()) direction.normalize()

        val speed = if (KeyListener.isKeyDown(GLFW_KEY_LEFT_SHIFT)) runSpeed else moveSpeed
        if (KeyListener.isKeyDown(GLFW_KEY_C)) {
            camera.cameraPosition.x += direction.x * speed * deltaTime
            camera.cameraPosition.y += direction.y * speed * deltaTime
        } else {
            object1.transform.position.x += direction.x * speed * deltaTime
            object2.transform.position.y += direction.y * speed * deltaTime
        }
    }

    private fun Vector2f.isZero() : Boolean {
        return x == 0f && y == 0f
    }
}