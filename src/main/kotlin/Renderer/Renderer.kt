package Renderer

import components.SpriteRenderer
import jade.Camera
import jade.GameObject

object Renderer {
    private val batches = mutableListOf<RenderBatch>()

    fun addGameObject(gameObject: GameObject) : Renderer {
        val spriteRenderer = gameObject.getComponent(SpriteRenderer::class.java)
        spriteRenderer?.let { addSpriteRenderer(it) }
        return this
    }

    private fun addSpriteRenderer(spriteRenderer: SpriteRenderer) {
        var lastBatch = batches.lastOrNull()
        if (lastBatch == null || lastBatch.isFull()) {
            lastBatch = RenderBatch()
            lastBatch.start()
            batches.add(lastBatch)
        }

        lastBatch.addSprite(spriteRenderer)
    }

    fun render() {
        batches.forEach { it.render() }
    }
}