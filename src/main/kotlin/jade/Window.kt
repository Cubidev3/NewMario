package jade

import Renderer.Shader
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
        currentScene.isRunning = false

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
        currentScene.start()
    }

    fun run() {
        println("Hello LWJGL" + Version.getVersion() + "!")

        init()
        loop()

        // This Happens when Window Should Close
        freeMemory()
        terminateGLFW()
    }

    private fun init() {
        setupErrorCallback()

        initializeGLFW()

        setupWindowHints()
        createWindow()

        setupMouseListener()
        setupKeyListener()
        setupGamepadListener()

        makeContextCurrent()

        enableVsync()

        showWindow()

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()
    }

    private fun loop() {
        changeScene(1)

        // Delta Time calculation values
        var beginTime = Time.getTime()
        var deltaTime = -1f

        while (!glfwWindowShouldClose(glfwWindow)) {
            glClear(GL_COLOR_BUFFER_BIT)
            glClear(GL_DEPTH_BUFFER_BIT)

            glClearColor(r, g, b, a)

            glfwPollEvents()

            if (deltaTime >= 0) {
                currentScene.update(deltaTime)
            }

            glfwSwapBuffers(glfwWindow)

            val endTime = Time.getTime()
            deltaTime = endTime - beginTime
            beginTime = endTime
        }
    }

    private fun setupErrorCallback() {
        GLFWErrorCallback.createPrint(System.err).set()
    }
    private fun initializeGLFW() {
        if (!glfwInit()) {
            throw IllegalStateException("Could not initialize GLFW.")
        }
    }

    private fun setupWindowHints() {
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // Window will be invisible until it1 created
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    }

    private fun setupMouseListener() {
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback)
        glfwSetCursorPosCallback(glfwWindow, MouseListener::cursorPositionCallback)
        glfwSetScrollCallback(glfwWindow, MouseListener::scrollCallback)
    }

    private fun setupKeyListener() {
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback)
    }

    private fun setupGamepadListener() {
        glfwSetJoystickCallback(GamepadListener::gamepadCallback)
        GamepadListener.getAlreadyConnectedGamepads()
    }
    private fun createWindow() {
        glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL)
        if (glfwWindow == NULL) {
            throw IllegalStateException("Could not create GLFW window.")
        }
    }

    private fun makeContextCurrent() {
        glfwMakeContextCurrent(glfwWindow)
    }
    private fun enableVsync() {
        glfwSwapInterval(1)
    }

    private fun showWindow() {
        glfwShowWindow(glfwWindow)
    }
    private fun freeMemory() {
        glfwFreeCallbacks(glfwWindow)
        glfwDestroyWindow(glfwWindow)
    }

    private fun terminateGLFW() {
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }
}