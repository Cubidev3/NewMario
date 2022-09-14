package Renderer

import org.joml.*
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Vector

class Shader {
    private var vertexSource = ""
    private var fragmentSource = ""
    private var filePath = ""
    private var programId = 0
    private var beingUsed = false
    constructor(file_path: String) {
        this.filePath = file_path

        try {
            val content = readFile(file_path)
            val types = getTypes(content)
            val shaderCodes = getShaderCodes(content)
            setSources(types, shaderCodes)
        } catch (e: IOException) {
            e.printStackTrace()
            assert(false) {"Could not read file '$file_path'"}
        } catch (e: Error) {
            e.printStackTrace()
            assert(false) {""}
        }
    }

    fun compile() {
        val vertexId = compileVertexShader()
        val fragmentId = compileFragmentShader()
        linkShaders(vertexId, fragmentId)
    }
    fun use() {
        if (beingUsed) return

        glUseProgram(programId)
        beingUsed = true
    }

    fun detach() {
        glUseProgram(0)
        beingUsed = false
    }

    private fun readFile(file_path: String) : String {
        return String(Files.readAllBytes(Paths.get(file_path)))
    }

    private fun getTypes(content: String) : List<String> {
        var start = 0
        val result = mutableListOf<String>()

        while (true) {
            start = content.indexOf("#type", start)
            if (start == -1) break // -1 means the is no "#type" left

            start += 6 // + 6 so we get type's first letter
            val endOfLine = content.indexOf("\n", start)

            val type = content.substring(start..endOfLine).trim()
            result.add(type)
        }

        return result
    }

    private fun getShaderCodes(content: String) : List<String> {
        return content.split(Regex("(#type).*")).drop(1)
    }

    private fun setSources(types: List<String>, codes: List<String>) {
        val pairs = types zip codes

        for (pair in pairs) {
            when (pair.first) {
                "vertex" -> vertexSource = pair.second
                "fragment" -> fragmentSource = pair.second
                else -> throw Error("Unknown shader type '${pair.first}'")
            }
        }
    }

    private fun compileVertexShader() : Int {return compileShader(vertexSource, GL_VERTEX_SHADER)}
    private fun compileFragmentShader() : Int {return compileShader(fragmentSource, GL_FRAGMENT_SHADER)}

    private fun linkShaders(vararg ids: Int) {
        // Program Creation and Linking
        programId = glCreateProgram()
        attackShaders(programId, ids)
        glLinkProgram(programId)

        // Error Checking
        val success = glGetProgrami(programId, GL_LINK_STATUS)
        if (success == GL_FALSE) {
            val len = glGetProgrami(programId, GL_INFO_LOG_LENGTH)
            println("Error in file '$filePath'\nCould not link shaders of ids: $ids")
            println(glGetShaderInfoLog(programId, len))
            assert(false)
        }
    }

    private fun compileShader(source: String, type: Int) : Int {
        // Shader Compilation
        val shaderId = glCreateShader(type)
        glShaderSource(shaderId, source)
        glCompileShader(shaderId)

        // Error Checking
        val success = glGetShaderi(shaderId, GL_COMPILE_STATUS)
        if (success == GL_FALSE) {
            val len = glGetShaderi(shaderId, GL_INFO_LOG_LENGTH)
            println("Error in file '$filePath'\nCould not compile shader of type: $type")
            println(glGetShaderInfoLog(shaderId, len))
            assert(false)
        }

        return shaderId
    }

    private fun attackShaders(programId: Int, ids: IntArray) {
        for (shaderId in ids) {
            glAttachShader(programId, shaderId)
        }
    }

    fun uploadMatrix4f(varName: String, mat4: Matrix4f) {
        val location = getUniformLocation(varName)
        use()
        val matBuffer = BufferUtils.createFloatBuffer(16)
        mat4.get(matBuffer)
        glUniformMatrix4fv(location, false, matBuffer)
    }

    fun uploadMatrix3f(varName: String, mat3: Matrix3f) {
        val location = getUniformLocation(varName)
        use()
        val matBuffer = BufferUtils.createFloatBuffer(9)
        mat3.get(matBuffer)
        glUniformMatrix3fv(location, false, matBuffer)
    }

    fun uploadMatrix2f(varName: String, mat2: Matrix2f) {
        val location = getUniformLocation(varName)
        use()
        val matBuffer = BufferUtils.createFloatBuffer(4)
        mat2.get(matBuffer)
        glUniformMatrix2fv(location, false, matBuffer)
    }
    fun uploadVec4f(varName: String, vec4: Vector4f) {
        val location = getUniformLocation(varName)
        use()
        glUniform4f(location, vec4.x, vec4.y, vec4.z, vec4.w)
    }

    fun uploadVec3f(varName: String, vec3: Vector3f) {
        val location = getUniformLocation(varName)
        use()
        glUniform3f(location, vec3.x, vec3.y, vec3.z)
    }

    fun uploadVec2f(varName: String, vec2: Vector2f) {
        val location = getUniformLocation(varName)
        use()
        glUniform2f(location, vec2.x, vec2.y)
    }

    fun uploadFloat(varName: String, f: Float) {
        val location = getUniformLocation(varName)
        use()
        glUniform1f(location, f)
    }

    fun uploadInt(varName: String, i: Int) {
        val location = getUniformLocation(varName)
        use()
        glUniform1i(location, i)
    }

    fun uploadTexture(varName: String, tex: Int) {
        uploadInt(varName, tex)
    }
    private fun getUniformLocation(varName: String) : Int {
        return glGetUniformLocation(programId, varName)
    }
}