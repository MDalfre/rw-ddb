package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import commons.DefaultColors
import connection
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.QueryMode
import services.DynamoService
import services.VariableStore

@Preview
@Composable
fun QuerySelector(variableStore: VariableStore) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(DefaultColors.thirdColor).fillMaxWidth().padding(top = 5.dp, bottom = 5.dp)
    ) {
        if (variableStore.selectedTable != "") QueryModeDropdown(variableStore)
        when (variableStore.selectedQueryMode) {
            QueryMode.SCAN -> QueryModeScan(variableStore)
            QueryMode.QUERY -> Unit
            QueryMode.SEARCH_HASH -> QueryModeSearchHash(variableStore)
            QueryMode.NONE -> Unit
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QueryModeDropdown(variableStore: VariableStore) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Query Mode:",
            color = DefaultColors.secondaryTintColor,
            style = TextStyle(fontFamily = FontFamily.Monospace)
        )
        Spacer(Modifier.padding(start = 10.dp))
        ExposedDropdownMenuBox(
            expanded = variableStore.expandedQueryMode,
            onExpandedChange = { variableStore.expandedQueryMode = !variableStore.expandedQueryMode },
        ) {
            BasicTextField(
                value = variableStore.selectedQueryMode.pretty,
                readOnly = true,
                textStyle = LocalTextStyle.current.copy(color = DefaultColors.tintColor, fontSize = 12.sp),
                onValueChange = {},
                decorationBox = {
                    Column(
                        Modifier
                            .border(0.5.dp, color = DefaultColors.secondaryColor, shape = RoundedCornerShape(5.dp))
                            .height(30.dp)
                            .padding(5.dp)
                            .widthIn(min = 150.dp)
                    ) {
                        it()
                    }
                }
            )
            ExposedDropdownMenu(
                modifier = Modifier.width(IntrinsicSize.Max),
                expanded = variableStore.expandedQueryMode,
                onDismissRequest = { variableStore.expandedQueryMode = !variableStore.expandedQueryMode }) {
                QueryMode.entries.forEach {
                    if (it != QueryMode.NONE) {
                        DropdownMenuItem(
                            contentPadding = PaddingValues(10.dp),
                            onClick = {
                                variableStore.selectedQueryMode = it
                                variableStore.expandedQueryMode = !variableStore.expandedQueryMode
                            }
                        ) {
                            Text(
                                fontFamily = FontFamily.Monospace,
                                text = it.pretty,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun QueryModeSearchHash(variableStore: VariableStore) {
    val dynamoService = DynamoService(connection)
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.padding(start = 10.dp))
        Text(
            text = "HashKey:",
            color = DefaultColors.tintColor,
            style = TextStyle(fontFamily = FontFamily.Monospace),
        )
        Spacer(Modifier.padding(start = 5.dp))
        DefaultTextField(
            modifier = Modifier.width(150.dp),
            text = "",
            value = variableStore.queryModeHashKey,
            onValueChange = { variableStore.queryModeHashKey = it },
            enabled = true
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = "Value:",
            color = DefaultColors.tintColor,
            style = TextStyle(fontFamily = FontFamily.Monospace),
            modifier = Modifier.padding(bottom = 5.dp)
        )
        Spacer(Modifier.padding(start = 5.dp))
        DefaultTextField(
            modifier = Modifier.width(150.dp),
            text = "",
            value = variableStore.queryModeHashKeyValue,
            onValueChange = { variableStore.queryModeHashKeyValue = it },
            enabled = true
        )
        Spacer(Modifier.width(30.dp))
        OutlinedButton(
            onClick = {
                GlobalScope.launch {
                    dynamoService.searchHashKey(
                        tableName = variableStore.selectedTable,
                        hashKey = variableStore.queryModeHashKey,
                        value = variableStore.queryModeHashKeyValue
                    ).also { variableStore.listedItems = listOf(it) }
                }
            },
            border = BorderStroke(1.dp, DefaultColors.tintColor),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent,
                contentColor = DefaultColors.contentButtonColor
            ),
            enabled = variableStore.queryModeHashKey != "" && variableStore.queryModeHashKeyValue != ""
        ) {
            Text("Scan")
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun QueryModeScan(variableStore: VariableStore) {
    val dynamoService = DynamoService(connection)
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.padding(start = 10.dp))
        Text(
            text = "Max results:",
            color = DefaultColors.tintColor,
            style = TextStyle(fontFamily = FontFamily.Monospace),
        )
        Spacer(Modifier.width(5.dp))
        DefaultTextField(
            modifier = Modifier.width(60.dp),
            text = "",
            value = variableStore.queryModeScan,
            onValueChange = {
                if (it.matches(Regex("^\\d+$"))) {
                    variableStore.queryModeScan = it
                } else {
                    variableStore.queryModeScan = "0"
                }
            },
            enabled = true
        )
        Spacer(Modifier.width(30.dp))
        OutlinedButton(
            onClick = {
                GlobalScope.launch {
                    dynamoService.scanTable(variableStore.selectedTable, variableStore.queryModeScan.toInt())
                        .also { variableStore.listedItems = it?.toList() ?: emptyList() }
                }
            },
            border = BorderStroke(1.dp, DefaultColors.tintColor),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent,
                contentColor = DefaultColors.contentButtonColor
            ),
            enabled = variableStore.queryModeScan != "0"
        ) {
            Text("Scan")
        }
    }
}
