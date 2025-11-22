package alfredabdo.ide.plugins.translations.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.component.styling.TextFieldStyle
import org.jetbrains.jewel.ui.theme.textFieldStyle

@Composable
fun IntTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE,
    keyboardStep: Int = 1,
    onValueChanged: (value: Int?) -> Unit = {},
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = JewelTheme.defaultTextStyle,
    onKeyboardAction: KeyboardActionHandler? = null,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    style: TextFieldStyle = JewelTheme.textFieldStyle,
    outline: Outline = Outline.None,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    outputTransformation: OutputTransformation? = null,
    undecorated: Boolean = false,
) {
    LaunchedEffect(state.text) {
        val value = state.text.toString().toIntOrNull()
        if (value != null) {
            val coercedValue = value.coerceIn(range)
            if (coercedValue != value) {
                state.setTextAndPlaceCursorAtEnd(coercedValue.toString())
            }
            onValueChanged(coercedValue)
        } else {
            onValueChanged(null)
        }
    }

    TextField(
        state,
        modifier
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    val currentValue = state.text.toString().toIntOrNull()

                    if (currentValue != null) {
                        val newValue = when (event.key) {
                            Key.DirectionUp -> (currentValue + keyboardStep).coerceIn(range)
                            Key.DirectionDown -> (currentValue - keyboardStep).coerceIn(range)
                            else -> null
                        }
                        if (newValue != null && newValue != currentValue) {
                            state.setTextAndPlaceCursorAtEnd(newValue.toString())
                            return@onPreviewKeyEvent true
                        }
                    }
                }
                return@onPreviewKeyEvent false
            },
        enabled,
        readOnly,
        inputTransformation = object : InputTransformation {
            override val keyboardOptions: KeyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            )

            override fun TextFieldBuffer.transformInput() {
                val newText = asCharSequence().toString()

                if (newText.isEmpty()) {
                    return
                }

                val isNegativeSign = newText.startsWith('-')
                val digitsOnly = if (isNegativeSign) newText.substring(1) else newText

                if (digitsOnly.any { !it.isDigit() }) {
                    revertAllChanges()
                    return
                }

                if (newText.length > 1 && newText.startsWith('0') && !isNegativeSign) {
                    val correctedText = newText.dropWhile { it == '0' }.ifEmpty { "0" }
                    if (correctedText != newText) {
                        revertAllChanges()
                        replace(0, length, correctedText)
                    }
                }
            }
        },
        textStyle,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
        onKeyboardAction,
        onTextLayout,
        interactionSource,
        style,
        outline,
        placeholder,
        leadingIcon,
        trailingIcon,
        outputTransformation,
        undecorated,
    )
}