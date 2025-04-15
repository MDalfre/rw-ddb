package services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import components.CollapsableSection
import model.ConnectionSettings
import model.CredentialType
import model.QueryMode

class VariableStore {

    private val connectionSettings = ConnectionSettings(
        CredentialType.BASIC,
        "http://localhost:4566",
        "docker",
        "docker",
        "docker",
        Regions.US_EAST_1
    )
    var serverUrl by mutableStateOf(connectionSettings.serverUrl)
    var accessKey by mutableStateOf(connectionSettings.accessKey)
    var secretKey by mutableStateOf(connectionSettings.secretKey)
    var sessionKey by mutableStateOf(connectionSettings.sessionKey)
    var selectedRegion by mutableStateOf(connectionSettings.serverRegion)
    var selectedCredential by mutableStateOf(connectionSettings.credentialType.name)
    var expandedRegion by mutableStateOf(false)
    var expandedCredential by mutableStateOf(false)
    var expandedQueryMode by mutableStateOf(false)
    var selectedQueryMode by mutableStateOf(QueryMode.NONE)
    var errorMessage by mutableStateOf("")
    var deleteButtonCounter by mutableStateOf(1)

    var listedTables by mutableStateOf(listOf<String>())
    var listedItems by mutableStateOf(mutableListOf<Item>())
    var collapsableSection by mutableStateOf(mutableListOf<CollapsableSection>())
    var selectedTable by mutableStateOf("")
    var selectedTableSchema by mutableStateOf(listOf<KeySchemaElement>())

    var connecting by mutableStateOf(false)
    var connected by mutableStateOf(false)

    var queryModeScan by mutableStateOf("5")
    var queryModeHashKey by mutableStateOf("id")
    var queryModeHashKeyValue by mutableStateOf("")
}
