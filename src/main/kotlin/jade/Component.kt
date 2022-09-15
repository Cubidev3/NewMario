package jade

abstract class Component {
    var gameObject: GameObject? = null

    abstract fun update(deltaTime: Float)
    open fun start() {}
}