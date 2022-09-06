package jade

import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL

object Window {
    private const val width = 1920
    private const val height = 1080
    private const val title = "Mario"

    private var glfwWindow: Long = NULL

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
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE)

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
        glClearColor(1.0f, 1.0f, 1.0f, 0.1f)

        while (!glfwWindowShouldClose(glfwWindow)) {
            glClear(GL_COLOR_BUFFER_BIT)
            glClear(GL_DEPTH_BUFFER_BIT)

            glfwSwapBuffers(glfwWindow)

            // if (KeyListener.isKeyDown(GLFW_KEY_SPACE)) println("SPAAAAAAAAAAAAAAACE !!!!!")
            // if (MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) println("mouse position: (x: " + MouseListener.getX() + ", y: " + MouseListener.getY() + ")")

            glfwPollEvents()
        }
    }
}