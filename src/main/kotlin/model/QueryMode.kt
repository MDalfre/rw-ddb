package model

enum class QueryMode(val pretty: String) {
    NONE(""), SCAN("Scan"), QUERY("Query"), SEARCH_HASH("Search HashKey")
}
