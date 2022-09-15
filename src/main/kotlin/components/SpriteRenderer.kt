package components

import jade.Component

class SpriteRenderer : Component() {
    var isFirstTime = true

    override fun start() {
        println("Sprite Renderer Starting")
    }

    override fun update(deltaTime: Float) {
        if (isFirstTime) {
            println("Sprite Renderer Updating")
            isFirstTime = false
        }
    }
}