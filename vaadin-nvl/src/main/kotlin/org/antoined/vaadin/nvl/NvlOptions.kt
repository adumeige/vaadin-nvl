package org.antoined.vaadin.nvl

import elemental.json.Json
import elemental.json.JsonObject

data class NvlOptions(
    val layout: NvlLayout? = null,
    val minZoom: Double? = null,
    val maxZoom: Double? = null,
    val allowDynamicMinZoom: Boolean? = null,
    val panX: Double? = null,
    val panY: Double? = null,
    val initialZoom: Double? = null,
    val renderer: NvlRenderer? = null,
    val styling: NvlStyling? = null,
    val disableTelemetry: Boolean? = null,
    val disableWebWorkers: Boolean? = null,
    val disableAria: Boolean? = null,
    val layoutOptions: NvlLayoutOptions? = null,
) {
    fun toJson(): JsonObject = Json.createObject().apply {
        layout?.let { put("layout", it.jsValue) }
        minZoom?.let { put("minZoom", it) }
        maxZoom?.let { put("maxZoom", it) }
        allowDynamicMinZoom?.let { put("allowDynamicMinZoom", it) }
        panX?.let { put("panX", it) }
        panY?.let { put("panY", it) }
        initialZoom?.let { put("initialZoom", it) }
        renderer?.let { put("renderer", it.jsValue) }
        styling?.let { put("styling", it.toJson()) }
        disableTelemetry?.let { put("disableTelemetry", it) }
        disableWebWorkers?.let { put("disableWebWorkers", it) }
        disableAria?.let { put("disableAria", it) }
        layoutOptions?.let { put("layoutOptions", it.toJson()) }
    }
}

data class NvlStyling(
    val defaultNodeColor: String? = null,
    val defaultRelationshipColor: String? = null,
    val nodeDefaultBorderColor: String? = null,
    val selectedBorderColor: String? = null,
    val selectedInnerBorderColor: String? = null,
    val dropShadowColor: String? = null,
    val disabledItemColor: String? = null,
    val disabledItemFontColor: String? = null,
    val minimapViewportBoxColor: String? = null,
) {
    fun toJson(): JsonObject = Json.createObject().apply {
        defaultNodeColor?.let { put("defaultNodeColor", it) }
        defaultRelationshipColor?.let { put("defaultRelationshipColor", it) }
        nodeDefaultBorderColor?.let { put("nodeDefaultBorderColor", it) }
        selectedBorderColor?.let { put("selectedBorderColor", it) }
        selectedInnerBorderColor?.let { put("selectedInnerBorderColor", it) }
        dropShadowColor?.let { put("dropShadowColor", it) }
        disabledItemColor?.let { put("disabledItemColor", it) }
        disabledItemFontColor?.let { put("disabledItemFontColor", it) }
        minimapViewportBoxColor?.let { put("minimapViewportBoxColor", it) }
    }
}

sealed interface NvlLayoutOptions {
    fun toJson(): JsonObject
}

data class NvlForceDirectedOptions(
    val intelWorkaround: Boolean? = null,
    val enableCytoscape: Boolean? = null,
) : NvlLayoutOptions {
    override fun toJson(): JsonObject = Json.createObject().apply {
        intelWorkaround?.let { put("intelWorkaround", it) }
        enableCytoscape?.let { put("enableCytoscape", it) }
    }
}

data class NvlHierarchicalOptions(
    val direction: Direction? = null,
    val packing: Packing? = null,
) : NvlLayoutOptions {
    enum class Direction(val jsValue: String) {
        UP("up"), DOWN("down"), LEFT("left"), RIGHT("right")
    }

    enum class Packing(val jsValue: String) {
        BIN("bin"), STACK("stack")
    }

    override fun toJson(): JsonObject = Json.createObject().apply {
        direction?.let { put("direction", it.jsValue) }
        packing?.let { put("packing", it.jsValue) }
    }
}

data class NvlZoomOptions(
    val noPan: Boolean? = null,
    val outOnly: Boolean? = null,
    val minZoom: Double? = null,
    val maxZoom: Double? = null,
    val animated: Boolean? = null,
) {
    fun toJson(): JsonObject = Json.createObject().apply {
        noPan?.let { put("noPan", it) }
        outOnly?.let { put("outOnly", it) }
        minZoom?.let { put("minZoom", it) }
        maxZoom?.let { put("maxZoom", it) }
        animated?.let { put("animated", it) }
    }
}
