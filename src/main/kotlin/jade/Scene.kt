package jade

import org.joml.Vector2f

abstract class Scene {
    open var camera: Camera = Camera(Vector2f())
    var isRunning = false

    val gameObjects = ArrayList<GameObject>()
    open fun init() {}
    open fun update(deltaTime: Float) {}

    fun start() {
        for (go in gameObjects) {
            go.startComponents()
        }

        isRunning = true
    }
    fun updateGameObjects(deltaTime: Float) {
        for (go in gameObjects) {
            go.update(deltaTime)
        }
    }
    fun addGameObject(go: GameObject) {
        gameObjects.add(go)
        if (isRunning) go.startComponents()
    }
}