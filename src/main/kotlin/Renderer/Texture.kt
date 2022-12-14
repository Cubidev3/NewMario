package Renderer

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage.*

class Texture {
    var filepath = ""
    var texId = 0

    constructor(filepath: String) {
        this.filepath = filepath

        // Generate Texture ID
        texId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, texId)

        // Set Texture Parameters
        // Repeat Images In Both Directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

        // Pixelate when stretching
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)

        // Pixelate when Shrinking
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        val width = BufferUtils.createIntBuffer(1)
        val height = BufferUtils.createIntBuffer(1)
        val channels = BufferUtils.createIntBuffer(1)
        stbi_set_flip_vertically_on_load(true)
        val image = stbi_load(filepath, width, height, channels, 0)

        if (image != null) {
            when (channels.get(0)) {
                3 -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image)
                4 -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image)
                else -> assert(false) {"Unknown number of Channels ${channels.get(0)} from texture '$filepath'"}
            }
        } else {
            assert(false) {"Could not load Texture file: '$filepath'"}
        }

        stbi_image_free(image)
    }

    fun bind() {
        glBindTexture(GL_TEXTURE_2D, texId)
    }

    fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }
}