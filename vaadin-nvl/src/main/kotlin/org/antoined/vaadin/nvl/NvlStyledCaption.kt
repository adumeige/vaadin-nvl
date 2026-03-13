package org.antoined.vaadin.nvl

import elemental.json.Json
import elemental.json.JsonObject

/**
 * A styled caption segment for nodes or relationships, allowing fine-grained text styling.
 *
 * Multiple [NvlStyledCaption]s can be combined to render multi-style labels.
 *
 * @property value The text content of this caption segment.
 * @property key An optional key used by NVL to resolve the value from the element data.
 * @property styles CSS class names or NVL style tokens applied to this segment.
 */
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
