package jade

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f

class Camera {
    private var view = Matrix4f()
    private var projection = Matrix4f()
    var cameraPosition = Vector2f()

    constructor(position: Vector2f) {
        cameraPosition = position
        adjustProjection()
    }

    fun adjustProjection() {
        projection.identity()
        projection.ortho(0f, 32f * 40f, 0f, 32f * 21f, 0f, 100f)
    }

    fun getViewMatrix() : Matrix4f {
        val cameraFront = Vector3f(0f,0f,-1f)
        val cameraUp = Vector3f(0f,1f,0f)

        view.identity()
        view.lookAt(Vector3f(cameraPosition, 20f),
            cameraFront.add(Vector3f(cameraPosition, 0f)),
            cameraUp
        )

        return view
    }

    fun getProjectionMatrix() : Matrix4f {
        return projection
    }
}