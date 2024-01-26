package services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.sqs.model.Message
import model.ConnectionSettings
import model.CredentialType

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
    var selectedRegion by mutableStateOf(connectionSettings.serverRegion.name)
    var selectedCredential by mutableStateOf(connectionSettings.credentialType.name)
    var expandedRegion by mutableStateOf(false)
    var expandedCredential by mutableStateOf(false)

    var listedTables by mutableStateOf(listOf<String>())
    var listedItems by mutableStateOf(listOf<Item>())

    var connecting by mutableStateOf(false)
    var connected by mutableStateOf(false)
}