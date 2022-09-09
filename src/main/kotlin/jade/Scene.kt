package jade

import org.joml.Vector2f

abstract class Scene {
    protected var camera: Camera = Camera(Vector2f())
    open fun init() {}
    open fun update(deltaTime: Float) {}
}