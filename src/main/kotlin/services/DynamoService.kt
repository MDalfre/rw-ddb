package services

import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.internal.InternalUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import utils.findSchemaKey

class DynamoService(private val connectionService: ConnectionService?) {

    fun tableKeySchema(tableName: String): MutableList<KeySchemaElement> {
        return connectionService?.dynamoDb?.describeTable(tableName)
            ?.table
            ?.keySchema
            ?: throw RuntimeException("Schema not found")
    }

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
        val item = connectionService?.dynamoDb?.getItem(request)?.item?.let { itemMap ->
            val convertedMap = itemMap.mapValues { it.value.toPrimitive() }
            return Item.fromMap(convertedMap)
        } ?: notFoundItem
        return item
    }

    fun deleteItem(tableName: String, keySchemaElement: List<KeySchemaElement>, json: String) {
        val id = findSchemaKey(keySchemaElement).attributeName
        val value = jacksonObjectMapper().readTree(json).findValue(id).textValue()
        val idValueMap = mapOf<String, AttributeValue>(id to AttributeValue().withS(value))
        connectionService?.dynamoDb?.deleteItem(tableName, idValueMap)
    }

    fun updateItem(tableName: String, keySchemaElement: List<KeySchemaElement>, json: String) {
        val id = findSchemaKey(keySchemaElement).attributeName
        val value = jacksonObjectMapper().readTree(json).findValue(id).textValue()
        val idValueMap = mapOf<String, AttributeValue>(id to AttributeValue().withS(value))
        Item.fromJSON(json).attributes()
        val updateRequest = UpdateItemRequest().run {
            key = idValueMap
        }
        connectionService?.dynamoDb?.updateItem(tableName, idValueMap, attributes)
    }

    private fun AttributeValue.toPrimitive(): Any? {
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
