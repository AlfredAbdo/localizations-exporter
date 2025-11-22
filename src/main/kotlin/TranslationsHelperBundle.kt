import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.function.Supplier

@NonNls
private const val BUNDLE = "messages.TranslationsHelperBundle"

internal object TranslationsHelperBundle {
    private val instance = DynamicBundle(TranslationsHelperBundle::class.java, BUNDLE)

    @JvmStatic
    @Nls
    fun message(key: @PropertyKey(resourceBundle = BUNDLE) String, vararg params: Any): String {
        return instance.getMessage(key, *params)
    }

    @JvmStatic
    fun lazyMessage(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): Supplier<String> {
        return instance.getLazyMessage(key, *params)
    }
}