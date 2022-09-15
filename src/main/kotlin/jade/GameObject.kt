package jade

class GameObject {
    var name = ""
    val components = mutableListOf<Component>()

    constructor(name: String) {
        this.name = name
    }

    fun <T: Component> getComponent(componentClass: Class<T>) : T? {
        for (c in components) {
            if (componentClass.isAssignableFrom(c.javaClass)) {
                try {
                    return componentClass.cast(c)
                } catch (e: ClassCastException) {
                    e.printStackTrace()
                    assert(false) {"Error Casting Component"}
                }
            }
        }

        return null
    }

    fun <T: Component> removeComponent(componentClass: Class<T>) {
        val len = components.size - 1
        for (i in 0..len) {
            val component = components[i]
            if (componentClass.isAssignableFrom(component.javaClass)) {
                components.removeAt(i)
                return
            }
        }
    }

    fun addComponent(component: Component) {
        component.gameObject = this
        components.add(component)
    }

    fun update(deltaTime: Float) {
        for (component in components) {
            component.update(deltaTime)
        }
    }

    fun startComponents() {
        for (component in components) {
            component.start()
        }
    }
}