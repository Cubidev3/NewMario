package jade

import org.lwjgl.glfw.GLFW

class LevelScene : Scene() {
    var isChangingScene = false
    var timeToChangeScene = 2f
    private val fadeVelocity = 1 / timeToChangeScene

    override fun init() {
        println("Level Scene Init")

        Window.r = 0f
        Window.g = 0f
        Window.b = 0f
        Window.a = 0f
    }

    override fun update(deltaTime: Float) {
        if (!isChangingScene && KeyListener.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
            isChangingScene = true
            return
        }

        if (isChangingScene) {
            timeToChangeScene -= deltaTime
            Window.r += fadeVelocity * deltaTime
            Window.g += fadeVelocity * deltaTime
            Window.b += fadeVelocity * deltaTime
            Window.a += fadeVelocity * deltaTime
        }

        if (timeToChangeScene <= 0) {
            Window.changeScene(1)
        }
    }
}