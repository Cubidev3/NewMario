package jade

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWGamepadState

object GamepadListener {
    var gamepadConnected = BooleanArray(GLFW_JOYSTICK_LAST)

    fun getAlreadyConnectedGamepads() {
        for (joystick in 0..GLFW_JOYSTICK_LAST) {
            if (glfwJoystickIsGamepad(joystick)) gamepadConnected[joystick] = true
        }
    }

    fun gamepadCallback(gamepad: Int, action: Int) {
        if (!isValidGamepad(gamepad)) return

        if (action == GLFW_CONNECTED) gamepadConnected[gamepad] = true
        else if (action == GLFW_DISCONNECTED) gamepadConnected[gamepad] = false
    }

    fun isButtonDown(gamepad: Int, button: Int) : Boolean {
        if (!isValidGamepad(gamepad)) return false

        var state: GLFWGamepadState = GLFWGamepadState.create()

        if (glfwGetGamepadState(gamepad, state)) {
            if (!isValidButton(state, button)) return false
            return state.buttons()[gamepad] == 1.toByte() // 1 <- Pressed, 0 <- Not Pressed
        }

        return false
    }

    fun getGamepadAxis(gamepad: Int, axis: Int) : Float {
        if (!isValidGamepad(gamepad)) return 0f

        var state = GLFWGamepadState.create()

        if (glfwGetGamepadState(gamepad, state)) {
            if (!isValidAxis(state, axis)) return 0f
            return state.axes(axis)
        }

        return 0f
    }

    private fun isValidGamepad(gamepad: Int) : Boolean {
        return gamepad >= 0 && gamepad < gamepadConnected.size && glfwJoystickIsGamepad(gamepad)
    }

    private fun isValidButton(state: GLFWGamepadState, button: Int) : Boolean {
        return button >= 0 && button < state.buttons().capacity()
    }

    private fun isValidAxis(state: GLFWGamepadState, axis: Int) : Boolean {
        return axis >= 0 && axis < state.axes().capacity()
    }
}