package services

import com.amazonaws.AmazonClientException
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder
import model.ConnectionSettings


class ConnectionService(
    private val connectionSettings: ConnectionSettings,
    private val variableStore: VariableStore
) {

    private val selectedRegionName = connectionSettings.serverRegion.getName()
    lateinit var dynamoDb: AmazonDynamoDBAsync

    init {
        try {
            dynamoDb = AmazonDynamoDBAsyncClientBuilder
                .standard()
                .withCredentials(
                    AWSStaticCredentialsProvider(
                        connectionSettings.credentialType.credential(connectionSettings)
                    )
                )
                .withEndpointConfiguration(
                    AwsClientBuilder.EndpointConfiguration(
                        connectionSettings.serverUrl,
                        selectedRegionName
                    )
                )
                .build()
            if (dynamoDb.listTables() != null) {
                variableStore.connected = true
                variableStore.connecting = false
            }

        } catch (ex: AmazonClientException) {
            variableStore.errorMessage = ex.message ?: ""
            variableStore.connected = false
            variableStore.connecting = false
        }


    }

    fun disconnect() {
        dynamoDb.shutdown()
    }
}
