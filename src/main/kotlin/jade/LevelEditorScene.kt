package jade

import org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE

class LevelEditorScene : Scene() {
    var isChangingScene = false
    var timeToChangeScene = 2f
    private val fadeVelocity = 1 / timeToChangeScene

    override fun init() {
        println("Level Editor Scene Init")

        Window.r = 1f
        Window.g = 1f
        Window.b = 1f
        Window.a = 1f
    }

    override fun update(deltaTime: Float) {
        if (!isChangingScene && KeyListener.isKeyDown(GLFW_KEY_SPACE)) {
            isChangingScene = true
            return
        }

        if (isChangingScene) {
            timeToChangeScene -= deltaTime
            Window.r -= fadeVelocity * deltaTime
            Window.g -= fadeVelocity * deltaTime
            Window.b -= fadeVelocity * deltaTime
            Window.a -= fadeVelocity * deltaTime
        }

        if (timeToChangeScene <= 0) {
            Window.changeScene(0)
        }
    }
}