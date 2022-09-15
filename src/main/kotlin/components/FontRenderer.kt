package components

import jade.Component

class FontRenderer : Component() {
    override fun start() {
        if (gameObject?.getComponent(SpriteRenderer().javaClass) != null) {
            println("Found Sprite Renderer")
        }
    }

    override fun update(deltaTime: Float) {}
}