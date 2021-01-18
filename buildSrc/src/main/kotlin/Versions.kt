import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate
import kotlin.reflect.KProperty

class Versions(private val project: Project) {
    private val properties = object {
        operator fun getValue(receiver: Any?, property: KProperty<*>) = get(property.name)
    }

    val kt by properties
    val mn by properties
    val jakartaMail by properties
    val logback by properties
    val slf4j by properties
    val junit by properties
    val ktlint by properties

    operator fun get(name: String): String = project.extra.get("version.$name") as String
}

val Project.versions: Versions
    get() {
        var versions: Versions? by rootProject.extra
        return versions ?: Versions(rootProject).also { versions = it }
    }
