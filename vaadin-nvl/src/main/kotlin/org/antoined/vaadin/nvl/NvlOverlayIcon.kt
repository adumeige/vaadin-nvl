package org.antoined.vaadin.nvl

import elemental.json.Json
import elemental.json.JsonObject

data class NvlOverlayIcon(
    val url: String,
    val position: List<Double>? = null,
    val size: Double? = null,
) {
    fun toJson(): JsonObject = Json.createObject().apply {
        put("url", url)
        position?.let { p ->
            put("position", Json.createArray().also { arr ->
                p.forEachIndexed { i, v -> arr.set(i, v) }
            })
        }
        size?.let { put("size", it) }
    }
}
