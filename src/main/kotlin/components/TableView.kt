package components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import commons.DefaultColors
import connection
import services.DynamoService
import services.VariableStore

@Composable
fun TableView(variableStore: VariableStore) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.background(DefaultColors.thirdColor).fillMaxHeight().padding(start = 5.dp, end = 5.dp)
            .widthIn(50.dp, 150.dp)
    ) {
        Text(text = "Collections", fontFamily = FontFamily.Monospace, color = DefaultColors.secondaryColor)
        LazyColumn {
            items(variableStore.listedTables) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
                        .clickable {
                            val dynamoService = DynamoService(connection)
                            variableStore.listedItems = dynamoService.scanTable(it) ?: mutableListOf()
                        },
                    backgroundColor = DefaultColors.backgroundColor,
                    elevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(
                            style = TextStyle(fontFamily = FontFamily.Monospace),
                            color = DefaultColors.secondaryColor,
                            text = it
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxHeight().padding(bottom = 10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedButton(
                border = BorderStroke(1.dp, DefaultColors.tintColor),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = DefaultColors.contentButtonColor
                ),
                onClick = {
                    val tables = connection?.dynamoDb?.listTables()
                    tables?.tableNames?.map {
                        if (!variableStore.listedTables.any { current -> current == it }) {
                            variableStore.listedTables = variableStore.listedTables.plus(it)
                        }
                    }
                }
            ) {
                Text(text = "Fetch Tables", fontFamily = FontFamily.Monospace)
            }
        }
    }
    TableItemView(variableStore)
}