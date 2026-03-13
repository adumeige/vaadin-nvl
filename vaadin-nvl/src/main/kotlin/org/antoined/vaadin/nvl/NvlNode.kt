package org.antoined.vaadin.nvl

import elemental.json.Json
import elemental.json.JsonObject

/**
 * Represents a node in the graph visualization.
 *
 * Only [id] is required. All other properties are optional and let NVL use its defaults when `null`.
 *
 * @property id Unique identifier for this node.
 * @property caption Simple text label displayed on the node (ignored when [captions] is set).
 * @property color CSS color string for the node fill (e.g. `"#4C8BF5"`).
 * @property size Diameter of the node in pixels.
 * @property selected Whether this node is visually selected.
 * @property disabled Whether this node is rendered in a disabled/dimmed style.
 * @property hovered Whether this node is rendered in its hover state.
 * @property captionSize Caption font size level (1 = small, 2 = medium, 3 = large).
 * @property captionAlign Vertical alignment of the caption relative to the node.
 * @property captions Styled caption segments; when set, [caption] is ignored.
 * @property overlayIcon An icon overlay displayed on the node.
 * @property pinned Whether this node is pinned (excluded from layout forces).
 * @property x Horizontal position in graph coordinates.
 * @property y Vertical position in graph coordinates.
 * @property activated Whether the node is in an "activated" visual state.
 * @property icon URL of an icon displayed inside the node.
 */
data class NvlNode(
    val id: String,
    val caption: String? = null,
    val color: String? = null,
    val size: Int? = null,
    val selected: Boolean? = null,
    val disabled: Boolean? = null,
    val hovered: Boolean? = null,
    val captionSize: Double? = null,
    val captionAlign: NvlCaptionAlign? = null,
    val captions: List<NvlStyledCaption>? = null,
    val overlayIcon: NvlOverlayIcon? = null,
    val pinned: Boolean? = null,
    val x: Double? = null,
    val y: Double? = null,
    val activated: Boolean? = null,
    val icon: String? = null) {

    fun toJson(): JsonObject = Json.createObject().apply {
        put("id", id)

        // If we have an explicit captions array, use it directly
        if (captions != null) {
            put("captions", Json.createArray().also { arr ->
                captions.forEachIndexed { i, v -> arr.set(i, v.toJson()) }
            })
        } else if (caption != null) {
            // Use simple caption when no styled captions are provided
            put("caption", caption)
        }

        color?.let { put("color", it) }
        size?.let { put("size", it.toDouble()) }
        selected?.let { put("selected", it) }
        disabled?.let { put("disabled", it) }
        hovered?.let { put("hovered", it) }
        captionSize?.let { put("captionSize", it) }
        captionAlign?.let { put("captionAlign", it.jsValue) }
        overlayIcon?.let { put("overlayIcon", it.toJson()) }
        pinned?.let { put("pinned", it) }
        x?.let { put("x", it) }
        y?.let { put("y", it) }
        activated?.let { put("activated", it) }
        icon?.let { put("icon", it) }
    }

    companion object {
        fun fromJson(json: JsonObject): NvlNode = NvlNode(
            id = json.getString("id"),
            caption = json.optString("caption"),
            color = json.optString("color"),
            size = json.optInt("size"),
            captionSize = json.optDouble("captionSize"),
            selected = json.optBoolean("selected"),
            disabled = json.optBoolean("disabled"),
            pinned = json.optBoolean("pinned"),
            x = json.optDouble("x"),
            y = json.optDouble("y"),
            activated = json.optBoolean("activated"),
            icon = json.optString("icon"),
        )
    }
}
