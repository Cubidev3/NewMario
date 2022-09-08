package jade

import Util.Time
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL

object Window {
    private const val width = 640
    private const val height = 360
    private const val title = "Mario"

    private var glfwWindow: Long = NULL

    var currentScene: Scene = EmptyScene()

    var r = 1.0f
    var g = 1.0f
    var b = 1.0f
    var a = 1.0f

    fun changeScene(newSceneIdx: Int) {
        when (newSceneIdx) {
            0 -> {
                currentScene = LevelScene()
            }
            1 -> {
                currentScene = LevelEditorScene()
            }
            else -> {
                assert(false) { "Unknown Scene '$newSceneIdx'" }
            }
        }

        currentScene.init()
    }

    fun run() {
        println("Hello LWJGL" + Version.getVersion() + "!")

        init()
        loop()

        // Free Memory
        glfwFreeCallbacks(glfwWindow)
        glfwDestroyWindow(glfwWindow)

        // Terminate GLFW and free error callback
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }

    fun init() {
        // Setup Error Callback
        GLFWErrorCallback.createPrint(System.err).set()

        // Initializing GLFW
        if (!glfwInit()) {
            throw IllegalStateException("Could not initialize GLFW.")
        }

        // Setup Window Configs
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // Window will be invisible until it1 created
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // Create Window
        glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL)
        if (glfwWindow == NULL) {
            throw IllegalStateException("Could not create GLFW window")
        }

        // Setup Listeners Callback
        glfwSetCursorPosCallback(glfwWindow, MouseListener::cursorPositionCallback)
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback)
        glfwSetScrollCallback(glfwWindow, MouseListener::scrollCallback)
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback)
        glfwSetJoystickCallback(GamepadListener::gamepadCallback)

        GamepadListener.getAlreadyConnectedGamepads()

        glfwMakeContextCurrent(glfwWindow)

        // Enable v-sync
        glfwSwapInterval(1)

        // Make window visible
        glfwShowWindow(glfwWindow)

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()
    }

    fun loop() {
        changeScene(1)

        // Delta Time calculation values
        var beginTime = Time.getTime()
        var endTime = 0f
        var deltaTime = -1f

        while (!glfwWindowShouldClose(glfwWindow)) {
            glClear(GL_COLOR_BUFFER_BIT)
            glClear(GL_DEPTH_BUFFER_BIT)

            glClearColor(r, g, b, a)

            glfwPollEvents()

            // if (KeyListener.isKeyDown(GLFW_KEY_SPACE)) println("SPAAAAAAAAAAAAAAACE !!!!!")
            // if (MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) println("mouse position: (x: " + MouseListener.getX() + ", y: " + MouseListener.getY() + ")")
            // if (GamepadListener.isButtonDown(GLFW_JOYSTICK_1, GLFW_GAMEPAD_BUTTON_A)) println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB")

            if (deltaTime >= 0) {
                currentScene.update(deltaTime)
            }

            glfwSwapBuffers(glfwWindow)

            endTime = Time.getTime()
            deltaTime = endTime - beginTime
            beginTime = endTime
        }
    }
}