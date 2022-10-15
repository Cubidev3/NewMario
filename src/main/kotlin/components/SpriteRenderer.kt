package components

import Renderer.Texture
import jade.Component
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f

data class SpriteRenderer(
    private var texture: Texture,
    private var dimensions: Vector2f = Vector2f(1f,1f),
    private var color: Vector4f = Vector4f(1f,1f,1f,1f),
) : Component() {
    private var needsUpdate = true
    private var oldPosition: Vector3f = Vector3f()
    override fun start() {
        oldPosition = gameObject!!.transform.position
    }
    override fun update(deltaTime: Float) {
        val currentPosition = gameObject!!.transform.position
        needsUpdate = needsUpdate || (currentPosition != oldPosition)
        oldPosition = currentPosition
    }

    fun getPosition() : Vector3f {return (gameObject?.transform?.position ?: Vector3f())}
    fun getColor() : Vector4f {return color}
    fun getDimensions() : Vector2f {return dimensions}
    fun getTexture() : Texture {return texture}

    fun needsUpdate() : Boolean {return needsUpdate}

    fun setPosition(pos: Vector3f) {
        gameObject!!.transform.position = pos
        needsUpdate = true
    }
    fun setColor(col: Vector4f) {
        color = col
        needsUpdate = true
    }

    fun setDimensions(dim: Vector2f) {
        dimensions = dim
        needsUpdate = true
    }

    fun setTexture(tex: Texture) {
        texture = tex
        needsUpdate = true
    }

    fun gotUpdated() {needsUpdate = false}
}