package org.antoined.vaadin.nvl

import elemental.json.Json
import elemental.json.JsonObject

/**
 * Represents a directed relationship (edge) between two nodes.
 *
 * [id], [from], and [to] are required. All other properties are optional.
 *
 * @property id Unique identifier for this relationship.
 * @property from ID of the source node.
 * @property to ID of the target node.
 * @property caption Simple text label displayed on the relationship line.
 * @property color CSS color string for the relationship stroke.
 * @property selected Whether this relationship is visually selected.
 * @property disabled Whether this relationship is rendered in a disabled/dimmed style.
 * @property hovered Whether this relationship is rendered in its hover state.
 * @property captionSize Caption font size level (1 = small, 2 = medium, 3 = large).
 * @property captionAlign Vertical alignment of the caption relative to the relationship line.
 * @property captions Styled caption segments for multi-style labels.
 * @property overlayIcon An icon overlay displayed on the relationship.
 * @property type A semantic type label (e.g. `"KNOWS"`, `"ACTED_IN"`).
 * @property width Stroke width of the relationship line in pixels.
 */
data class NvlRelationship(
    val id: String,
    val from: String,
    val to: String,
    val caption: String? = null,
    val color: String? = null,
    val selected: Boolean? = null,
    val disabled: Boolean? = null,
    val hovered: Boolean? = null,
    val captionSize: Double? = null,
    val captionAlign: NvlCaptionAlign? = null,
    val captions: List<NvlStyledCaption>? = null,
    val overlayIcon: NvlOverlayIcon? = null,
    val type: String? = null,
    val width: Double? = null,
) {
    fun toJson(): JsonObject = Json.createObject().apply {
        put("id", id)
        put("from", from)
        put("to", to)
        caption?.let { put("caption", it) }
        color?.let { put("color", it) }
        selected?.let { put("selected", it) }
        disabled?.let { put("disabled", it) }
        hovered?.let { put("hovered", it) }
        captionSize?.let { put("captionSize", it) }
        captionAlign?.let { put("captionAlign", it.jsValue) }
        captions?.let { c ->
            put("captions", Json.createArray().also { arr ->
                c.forEachIndexed { i, v -> arr.set(i, v.toJson()) }
            })
        }
        overlayIcon?.let { put("overlayIcon", it.toJson()) }
        this@NvlRelationship.type?.let { put("type", it) }
        width?.let { put("width", it) }
    }

    companion object {
        fun fromJson(json: JsonObject): NvlRelationship = NvlRelationship(
            id = json.getString("id"),
            from = json.getString("from"),
            to = json.getString("to"),
            caption = json.optString("caption"),
            color = json.optString("color"),
            selected = json.optBoolean("selected"),
            disabled = json.optBoolean("disabled"),
            captionSize = json.optDouble("captionSize"),
            captionAlign = json.optString("captionAlign")?.let { v ->
                NvlCaptionAlign.entries.find { it.jsValue == v }
            },
            type = json.optString("type"),
            width = json.optDouble("width"),
        )
    }
}
