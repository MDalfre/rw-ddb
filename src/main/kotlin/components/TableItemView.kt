package components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.amazonaws.services.dynamodbv2.document.Item
import commons.DefaultColors
import services.VariableStore

@Composable
fun TableItemView(variableStore: VariableStore) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.background(DefaultColors.backgroundColor).fillMaxSize()
    ) {
        CollapsableLazyColumn(
            variableStore.listedItems.map { CollapsableSection(it.toTitle(), listOf(it.toJSONPretty())) }
        )
    }
}

fun Item.toTitle(): String {
    val text = this.toJSON()
    return if (text.length > 20) {
        "${text.take(20)}..."
    } else {
        text.take(20)
    }
}