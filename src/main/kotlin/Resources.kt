import java.util.*

class Resources {
    val resourceBundle: ResourceBundle = PropertyResourceBundle(
            javaClass.getResourceAsStream("settings.properties"))

    fun <T> getResourse(key: String): T {
        return resourceBundle.getObject(key) as T
    }
}