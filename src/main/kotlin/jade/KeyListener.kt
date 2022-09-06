package jade

import org.lwjgl.glfw.GLFW.*

object KeyListener {
    var keyPressed = BooleanArray(GLFW_KEY_LAST + 1)

    fun keyCallback(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if (!isKeyValid(key)) return

        if (action == GLFW_PRESS) keyPressed[key] = true
        else if (action == GLFW_RELEASE) keyPressed[key] = false
    }

    fun isKeyDown(key: Int) : Boolean {
        if (!isKeyValid(key)) return false
        return keyPressed[key]
    }

    private fun isKeyValid(key: Int) : Boolean {
        return key >= 0 && key <= keyPressed.size
    }
}