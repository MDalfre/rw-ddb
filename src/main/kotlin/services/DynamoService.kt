package services

import com.amazonaws.services.dynamodbv2.document.Attribute
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.internal.InternalUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import com.amazonaws.services.dynamodbv2.model.ScanRequest

class DynamoService(private val connectionService: ConnectionService?) {

    fun scanTable(tableName: String, limit: Int = 100): MutableList<Item>? {
        val scanResult = connectionService?.dynamoDb?.scan(
            ScanRequest()
                .withLimit(limit)
                .withTableName(tableName)
        )

        return InternalUtils.toItemList(scanResult?.items)
    }

    fun searchHashKey(tableName: String, hashKey: String, value: String): Item {
        val notFoundItem = Item.fromMap(
            mapOf(
                "Item" to "Not Found",
                "Table" to tableName,
                "HashKey" to hashKey,
                "Value" to value
            )
        )
        val request = GetItemRequest()
            .withTableName(tableName)
            .withKey(
                mapOf(
                    hashKey to AttributeValue().withS(value)
                )
            )
        val item = connectionService?.dynamoDb?.getItem(request)?.item?.let {
            val convertedMap = it.mapValues { it.value.toPrimitive() }
            return Item.fromMap(convertedMap)
        } ?: notFoundItem
        return item
    }

    fun AttributeValue.toPrimitive(): Any? {
        return when {
            this.s != null -> this.s
            this.n != null -> this.n.toDoubleOrNull() ?: this.n
            this.bool != null -> this.bool
            this.m != null -> this.m.mapValues { it.value.toPrimitive() }
            this.l != null -> this.l.map { it.toPrimitive() }
            else -> null
        }
    }
}
