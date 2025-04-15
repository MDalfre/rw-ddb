package components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import commons.DefaultColors
import commons.DefaultStyle.MATERIAL_ICON_DIMENSION
import connection
import services.DynamoService
import services.VariableStore


@Composable
fun CollapsableLazyColumn(
    variableStore: VariableStore,
    modifier: Modifier = Modifier
) {
    variableStore.collapsableSection = variableStore.listedItems.map {
        CollapsableSection(
            it.toTitle(variableStore),
            listOf(it.toJSONPretty())
        )
    }.toMutableList()
    val sections = variableStore.collapsableSection
    val collapsedState = remember(sections) { sections.map { true }.toMutableStateList() }
    val deleteState = remember(sections) { sections.map { 1 }.toMutableStateList() }
    LazyColumn(modifier) {
        sections.forEachIndexed { i, dataItem ->
            item(key = "header_$i") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            collapsedState[i] = !collapsedState[i]
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.run {
                            if (collapsedState[i]) {
                                deleteState[i] = 1
                                KeyboardArrowDown
                            } else {
                                KeyboardArrowUp
                            }
                        },
                        contentDescription = "",
                        tint = DefaultColors.tintColor,
                    )
                    Text(
                        text = dataItem.title,
                        color = DefaultColors.secondaryTintColor,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .weight(1f)
                    )
                }
                Divider()
            }
            if (!collapsedState[i]) {
                items(dataItem.rows) { content ->
                    SelectionContainer {
                        Row {
                            Spacer(modifier = Modifier.size(MATERIAL_ICON_DIMENSION.dp))
                            Text(
                                text = content,
                                color = Color.LightGray,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit Item",
                            tint = if (deleteState[i] == 1) DefaultColors.tintColor else Color.Red,
                            modifier = Modifier.clickable {
                                if (deleteState[i] == 1) {
                                    deleteState[i] = 2
                                } else {
                                    val dynamoService = DynamoService(connection)
                                    dynamoService.deleteItem(
                                        tableName = variableStore.selectedTable,
                                        keySchemaElement = variableStore.selectedTableSchema,
                                        json = content
                                    )
                                    variableStore.listedItems.removeAt(i)
                                    variableStore.collapsableSection.removeAt(i)
                                    collapsedState[i] = !collapsedState[i]
                                }
                            }
                        )

                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete Item",
                            tint = if (deleteState[i] == 1) DefaultColors.tintColor else Color.Red,
                            modifier = Modifier.clickable {
                                if (deleteState[i] == 1) {
                                    deleteState[i] = 2
                                } else {
                                    val dynamoService = DynamoService(connection)
                                    dynamoService.deleteItem(
                                        tableName = variableStore.selectedTable,
                                        keySchemaElement = variableStore.selectedTableSchema,
                                        json = content
                                    )
                                    variableStore.listedItems.removeAt(i)
                                    variableStore.collapsableSection.removeAt(i)
                                    collapsedState[i] = !collapsedState[i]
                                }
                            }
                        )
                    }
                    Divider(color = DefaultColors.secondaryColor)
                }
            }
        }
    }
}

data class CollapsableSection(val title: String, val rows: List<String>)
