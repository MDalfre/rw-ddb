package components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import commons.DefaultColors

@Composable
fun DefaultTextField(
    modifier: Modifier = Modifier,
    text: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    Text(
        text = text,
        color = DefaultColors.secondaryColor,
        style = TextStyle(fontFamily = FontFamily.Monospace),
        modifier = Modifier.padding(bottom = 5.dp)
    )
    BasicTextField(
        cursorBrush = SolidColor(DefaultColors.tintColor),
        value = value,
        enabled = enabled,
        textStyle = LocalTextStyle.current.copy(color = DefaultColors.tintColor, fontFamily = FontFamily.Monospace),
        decorationBox = {
            Column(
                modifier
                    .width(300.dp)
                    .border(0.5.dp, color = DefaultColors.secondaryColor, shape = RoundedCornerShape(5.dp))
                    .height(30.dp)
                    .padding(5.dp)
            ) {
                it()
            }
        },
        onValueChange = onValueChange
    )
}
