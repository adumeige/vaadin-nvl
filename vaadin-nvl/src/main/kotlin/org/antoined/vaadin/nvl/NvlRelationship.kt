package org.antoined.vaadin.nvl

import elemental.json.Json
import elemental.json.JsonObject

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
