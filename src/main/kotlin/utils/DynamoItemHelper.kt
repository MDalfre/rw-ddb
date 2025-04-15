package utils

import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType

fun findSchemaKey(schema: List<KeySchemaElement>): KeySchemaElement {
    return schema.find { keySchemaElement ->
        keySchemaElement.keyType == KeyType.HASH.name
    } ?: throw RuntimeException("KeySchema HASH not found")
}