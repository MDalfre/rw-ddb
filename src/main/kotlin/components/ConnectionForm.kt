package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.amazonaws.regions.Regions
import commons.DefaultColors
import commons.DefaultStyle.MATERIAL_ICON_DIMENSION
import connection
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.ConnectionSettings
import model.CredentialType
import services.ConnectionService
import services.VariableStore

@Suppress("LongMethod")
@OptIn(DelicateCoroutinesApi::class)
@Composable
@Preview
fun connectionForm(
    variableStore: VariableStore
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.background(DefaultColors.backgroundColor).fillMaxSize()
    ) {
        Image(
            modifier =  Modifier.size(200.dp),
            painter = painterResource("rw-ddb4.png"),
            contentScale = ContentScale.Fit,
            contentDescription = "RW-DDB",
            alignment = Alignment.BottomStart,
//            colorFilter = ColorFilter.tint(DefaultColors.backgroundColor, BlendMode.Modulate)
        )
        Spacer(modifier = Modifier.size(MATERIAL_ICON_DIMENSION.dp))
        CredentialTypeDropdown(variableStore)
        DefaultTextField(
            text = "Server URL",
            value = variableStore.serverUrl,
            onValueChange = { variableStore.serverUrl = it }
        )
        DefaultTextField(
            text = "Access Key",
            value = variableStore.accessKey,
            onValueChange = { variableStore.accessKey = it }
        )
        DefaultTextField(
            text = "Secret Key",
            value = variableStore.secretKey,
            onValueChange = { variableStore.secretKey = it }
        )

        if (variableStore.selectedCredential == CredentialType.SESSION.name) {
            DefaultTextField(
                text = "Session Key",
                value = variableStore.sessionKey,
                onValueChange = { variableStore.sessionKey = it }
            )
        }

        DefaultTextField(
            text = "Region",
            value = variableStore.selectedRegion,
            onValueChange = { println(it) }
        )

        Spacer(modifier = Modifier.size(MATERIAL_ICON_DIMENSION.dp))

        OutlinedButton(
            enabled = !variableStore.connecting,
            border = BorderStroke(1.dp, DefaultColors.tintColor),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent,
                contentColor = DefaultColors.contentButtonColor
            ),
            onClick = {
                val settings = ConnectionSettings(
                    credentialType = CredentialType.valueOf(variableStore.selectedCredential),
                    serverUrl = variableStore.serverUrl,
                    accessKey = variableStore.accessKey,
                    secretKey = variableStore.secretKey,
                    sessionKey = variableStore.sessionKey,
                    serverRegion = Regions.valueOf(variableStore.selectedRegion)
                )
                GlobalScope.launch {
                    variableStore.connecting = true
                    connection = ConnectionService(connectionSettings = settings, variableStore = variableStore)
                }
            }
        ) {
            Text(text = "Connect", fontFamily = FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.size(MATERIAL_ICON_DIMENSION.dp))

        Text(
            text = variableStore.errorMessage,
            fontFamily = FontFamily.Monospace,
            color = Color.Red
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CredentialTypeDropdown(variableStore: VariableStore) {
    Text(
        text = "Credential Type",
        color = DefaultColors.secondaryColor,
        style = TextStyle(fontFamily = FontFamily.Monospace),
        modifier = Modifier.padding(bottom = 5.dp)
    )
    ExposedDropdownMenuBox(
        expanded = variableStore.expandedCredential,
        onExpandedChange = { variableStore.expandedCredential = !variableStore.expandedCredential },
    ) {
        BasicTextField(
            value = variableStore.selectedCredential,
            readOnly = true,
            textStyle = LocalTextStyle.current.copy(color = DefaultColors.tintColor),
            onValueChange = {},
            decorationBox = {
                Column(
                    Modifier
                        .width(300.dp)
                        .border(0.5.dp, color = DefaultColors.secondaryColor, shape = RoundedCornerShape(5.dp))
                        .height(30.dp)
                        .padding(5.dp)
                ) {
                    it()
                }
            }
        )
        ExposedDropdownMenu(
            variableStore.expandedCredential,
            onDismissRequest = { variableStore.expandedCredential = !variableStore.expandedCredential }) {
            CredentialType.entries.forEach {
                DropdownMenuItem(
                    onClick = {
                        variableStore.selectedCredential = it.name
                        variableStore.expandedCredential = !variableStore.expandedCredential
                    }
                ) {
                    Text(
                        fontFamily = FontFamily.Monospace,
                        text = it.name
                    )
                }
            }
        }
    }
}
