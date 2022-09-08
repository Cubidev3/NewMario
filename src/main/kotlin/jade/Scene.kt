package jade

abstract class Scene {
    open fun init() {}
    open fun update(deltaTime: Float) {}
}