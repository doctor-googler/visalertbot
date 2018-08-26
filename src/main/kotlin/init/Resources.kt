package init

import java.util.*

class Resources {
    private val resourceBundle: ResourceBundle = PropertyResourceBundle(
            javaClass.getResourceAsStream("../settings.properties"))

    fun <T> getResourse(key: String): T {
        return resourceBundle.getObject(key) as T
    }
}