package components

import jade.Component
import org.joml.Vector4f

data class SpriteRenderer(private var color: Vector4f = Vector4f()) : Component() {
    override fun start() {}
    override fun update(deltaTime: Float) {}
    fun getColor(): Vector4f {return color}
}