package Util

object Time {
    val startTime = System.nanoTime()

    fun getTime() : Float {
        return ((System.nanoTime() - startTime) * 1E-9).toFloat()
    }
}