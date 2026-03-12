package org.antoined.vaadin.nvl

import elemental.json.Json
import elemental.json.JsonObject

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
    val icon: String? = null,
) {
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
