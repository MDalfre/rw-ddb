package services

import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.internal.InternalUtils
import com.amazonaws.services.dynamodbv2.model.ScanRequest

class DynamoService(private val connectionService: ConnectionService?) {

    fun scanTable(tableName: String): MutableList<Item>? {
        val scanResult = connectionService?.dynamoDb?.scan(
            ScanRequest()
                .withLimit(100)
                .withTableName(tableName)
        )

        return InternalUtils.toItemList(scanResult?.items)
    }
}