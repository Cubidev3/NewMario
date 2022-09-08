package Renderer

import org.lwjgl.opengl.GL20.*
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class Shader {
    private var vertexSource = ""
    private var fragmentSource = ""
    private var filePath = ""
    private var programId = 0

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
        glUseProgram(programId)
    }

    fun detach() {
        glUseProgram(0)
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
}