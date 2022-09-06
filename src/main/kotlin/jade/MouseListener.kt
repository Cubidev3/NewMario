package jade

import org.lwjgl.glfw.GLFW.*

object MouseListener {
    private var scrollX: Double = 0.0
    private var scrollY: Double = 0.0

    private var xPos: Double = 0.0
    private var yPos: Double = 0.0
    private var lastX: Double = 0.0
    private var lastY: Double = 0.0

    private var mouseButtonPressed = BooleanArray(GLFW_MOUSE_BUTTON_LAST + 1)

    private var isDragging = false

    fun cursorPositionCallback(window: Long, x: Double, y: Double) {
        lastX = xPos
        lastY = yPos
        xPos = x
        yPos = y

        isDragging = isAnyButtonPressed()
    }

    fun mouseButtonCallback(window: Long, button: Int, action: Int, mod: Int) {
        if (!isValidButton(button)) return

        if (action == GLFW_PRESS) {
            mouseButtonPressed[button] = true

        } else if (action == GLFW_RELEASE) {
            mouseButtonPressed[button] = false
            isDragging = false
        }
    }

    fun scrollCallback(window: Long, xOffset: Double, yOffset: Double) {
        scrollX = xOffset
        scrollY = yOffset
    }

    fun isButtonDown(button: Int) : Boolean {
        if (!isValidButton(button)) return false
        return mouseButtonPressed[button]
    }

    fun endFrame() {
        scrollX = 0.0
        scrollY = 0.0
        lastX = xPos
        lastY = yPos
    }

    fun getX() : Float {
        return xPos.toFloat()
    }

    fun getY() : Float {
        return yPos.toFloat()
    }

    fun getDx() : Float {
        return (lastX - xPos).toFloat()
    }

    fun getDy() : Float {
        return (lastY - yPos).toFloat()
    }

    fun getScrollX() : Float {
        return scrollX as Float
    }

    fun getScrollY() : Float {
        return scrollY as Float
    }

    fun isDragging() : Boolean {
        return isDragging
    }

    private fun isAnyButtonPressed() : Boolean {
        for (isPressed in mouseButtonPressed) {
            if (isPressed) return true
        }
        return false
    }

    private fun isValidButton(button: Int) : Boolean {
        return button >= 0 && button < mouseButtonPressed.size
    }
}