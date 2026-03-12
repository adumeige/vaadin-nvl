package org.antoined.vaadin.nvl

import elemental.json.Json
import elemental.json.JsonObject

data class NvlStyledCaption(
    val value: String? = null,
    val key: String? = null,
    val styles: List<String>? = null,
) {
    fun toJson(): JsonObject = Json.createObject().apply {
        value?.let { put("value", it) }
        key?.let { put("key", it) }
        styles?.let { s ->
            put("styles", Json.createArray().also { arr ->
                s.forEachIndexed { i, v -> arr.set(i, v) }
            })
        }
    }
}
