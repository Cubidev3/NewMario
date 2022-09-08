package jade

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30.*

class LevelEditorScene : Scene() {
    private val vertexShaderScr = "#version 330 core\n" +
            "\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}"

    private val fragmentShaderScr = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    color = fColor;\n" +
            "}"

    private var vertexId = 0
    private var fragmentId = 0
    private var shaderProgram = 0

    private val vertexArray = floatArrayOf(
        // Positions                 // Color
        0.5f, -0.5f, 0f,             1f, 1f, 0f, 1f, // Bottom Right
        -0.5f, 0.5f, 0f,             0f, 1f, 1f, 1f, // Top Left
        0.5f, 0.5f, 0f,              1f, 0f, 1f, 1f, // Top Right
        -0.5f, -0.5f, 0f,            0f, 1f, 0f, 1f // Bottom Left
    )

    // IMPORTANT: This must be in count-clockwise order
    private val elementArray = intArrayOf(
        2,1,0, // top Right triangle
        0,1,3 // bottom Left Triangle
    )

    private var vaoId = 0
    private var vboId = 0
    private var eboId = 0
    override fun init() {
        // Compiling and Linking shaders
        // First load and compile vertex shader
        vertexId = glCreateShader(GL_VERTEX_SHADER)
        // Pass shader source to GPU
        glShaderSource(vertexId, vertexShaderScr)

        // Check for errors
        var success = glGetShaderi(vertexId, GL_COMPILE_STATUS)
        if (success == GL_FALSE) {
            val len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH)
            println("Error: 'shaders/default.dlsl'\nVertex shader compilation failed.")
            println(glGetShaderInfoLog(vertexId, len))
            assert(false) {""}
        }

        // First load and compile fragment shader
        fragmentId = glCreateShader(GL_FRAGMENT_SHADER)
        // Pass shader source to GPU
        glShaderSource(fragmentId, fragmentShaderScr)

        // Check for errors
        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS)
        if (success == GL_FALSE) {
            val len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH)
            println("Error: 'shaders/default.dlsl'\nFragment shader compilation failed.")
            println(glGetShaderInfoLog(fragmentId, len))
            assert(false) {""}
        }

        // Linking Shaders
        shaderProgram = glCreateProgram()
        glAttachShader(shaderProgram, vertexId)
        glAttachShader(shaderProgram, fragmentId)
        glLinkProgram(shaderProgram)

        // Check for linking errors
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS)
        if (success == GL_FALSE) {
            val len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH)
            println("Error: 'shaders/default.dlsl'\nLinking of shaders failed.")
            println(glGetProgramInfoLog(shaderProgram, len))
            assert(false) {""}
        }

        // Generate VAO, VBO and EBO buffer objects, and send to gpu
        vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        // Create a float buffer of vertices
        val vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.size)
        vertexBuffer.put(vertexArray).flip()

        // Create VBO
        vboId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)

        // Create Indices and Upload
        val elementBuffer = BufferUtils.createIntBuffer(elementArray.size)
        elementBuffer.put(elementArray).flip()

        eboId = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW)

        // Add vertex attribute pointers
        val positionsSize = 3
        val colorsSize = 4
        val floatSizeInBytes = 4
        val vertexSizeInBytes = (positionsSize + colorsSize) * floatSizeInBytes

        // Position Attribute
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeInBytes, 0)
        glEnableVertexAttribArray(0)

        // Color Attribute
        glVertexAttribPointer(1, colorsSize, GL_FLOAT, false, vertexSizeInBytes, (positionsSize * floatSizeInBytes).toLong())
        glEnableVertexAttribArray(1)
    }

    override fun update(deltaTime: Float) {
        // Bind Program
        glUseProgram(shaderProgram)

        // Bind vao
        glBindVertexArray(vaoId)

        // Enable Attributes
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)

        // Draw
        glDrawElements(GL_TRIANGLES, elementArray.size, GL_UNSIGNED_INT, 0)

        // Unbind Everything
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)

        glBindVertexArray(0)

        glUseProgram(0)
    }
}