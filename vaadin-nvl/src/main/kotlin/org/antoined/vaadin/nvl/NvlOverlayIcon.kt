package org.antoined.vaadin.nvl

import elemental.json.Json
import elemental.json.JsonObject

/**
 * An icon overlay displayed on top of a node or relationship.
 *
 * @property url URL of the image to display as the overlay icon.
 * @property position Optional `[x, y]` offset of the icon relative to the element center.
 * @property size Optional size (in pixels) of the icon.
 */
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
