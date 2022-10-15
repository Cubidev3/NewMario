package jade

import org.joml.Vector2f
import org.joml.Vector3f

data class Transform(var position: Vector3f = Vector3f(0f,0f,0f), var scale: Vector2f = Vector2f(1f,1f))